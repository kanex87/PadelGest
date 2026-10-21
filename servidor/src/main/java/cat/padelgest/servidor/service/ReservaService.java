/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.service;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.FranjaLliure;
import cat.padelgest.servidor.dto.ReservaRequest;
import cat.padelgest.servidor.exception.ApiException;
import cat.padelgest.servidor.model.Pista;
import cat.padelgest.servidor.model.Reserva;
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.repository.PistaRepository;
import cat.padelgest.servidor.repository.ReservaRepository;
import cat.padelgest.servidor.repository.UsuariRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Servei amb la lògica de negoci de les reserves: validacions, control de solapaments i cancel·lacions.
@Service
public class ReservaService {

    //Constants
    public static final LocalTime HORA_OBERTURA = LocalTime.of(8, 0);
    public static final LocalTime HORA_TANCAMENT = LocalTime.of(23, 0);

    //Dependencies
    private final ReservaRepository reservaRepository;
    private final UsuariRepository usuariRepository;
    private final PistaRepository pistaRepository;

    //Constructor
    public ReservaService(ReservaRepository reservaRepository,
            UsuariRepository usuariRepository,
            PistaRepository pistaRepository) {
        this.reservaRepository = reservaRepository;
        this.usuariRepository = usuariRepository;
        this.pistaRepository = pistaRepository;
    }

    //Logica
    //Retorna totes les reserves.
    public List<Reserva> llistarTotes() {
        return reservaRepository.findAll();
    }

    
    //Retorna les reserves d'un usuari.
   
    public List<Reserva> llistarPerUsuari(Long idUsuari) {
        return reservaRepository.findByUsuariIdOrderByDataDescHoraIniciDesc(idUsuari);
    }

    
    //Retorna les reserves actives d'una pista en un dia.
   
    public List<Reserva> llistarActivesPistaDia(Long idPista, LocalDate data) {
        return reservaRepository.findByPistaIdAndDataAndEstatOrderByHoraInici(idPista, data, "ACTIVA");
    }

    //Retorna les reserves actives d'un usuari en un dia (verificació a recepció).
    public List<Reserva> llistarActivesUsuariDia(Long idUsuari, LocalDate data) {
        return reservaRepository.findByUsuariIdAndDataAndEstatOrderByHoraInici(idUsuari, data, "ACTIVA");
    }

    //Retorna les franges d'una hora que estan lliures en una pista i un dia, sense dir qui ha reservat.
    public List<FranjaLliure> disponibilitat(Long idPista, LocalDate data) {
        Pista pista = pistaRepository.findById(idPista)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La pista no existeix"));
        if (!pista.getActiva()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La pista no està activa");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No es pot consultar un dia passat");
        }

        boolean esAvui = data.equals(LocalDate.now());
        LocalTime ara = LocalTime.now();
        List<Reserva> reserves = reservaRepository.findByPistaIdAndDataAndEstatOrderByHoraInici(idPista, data, "ACTIVA");
        List<FranjaLliure> franges = new ArrayList<>();

        // Provem totes les franges d'una hora, de l'obertura al tancament
        LocalTime hora = HORA_OBERTURA;
        while (hora.isBefore(HORA_TANCAMENT)) {
            LocalTime fi = hora.plusHours(1);
            // Si és avui, les franges que ja han començat no es poden reservar
            boolean jaHaComencat = esAvui && hora.isBefore(ara);
            if (!jaHaComencat && !estaOcupada(reserves, hora, fi)) {
                franges.add(new FranjaLliure(hora, fi));
            }
            hora = fi;
        }
        return franges;
    }

    
    //Crea una reserva després de comprovar totes les regles de negoci.
   
    @Transactional
       public Reserva crearReserva(ReservaRequest dades, Long idUsuariToken, boolean esAdmin) {
        if (dades.idPista() == null || dades.data() == null
                || dades.horaInici() == null || dades.horaFi() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Falten dades de la reserva");
        }

        // Un jugador sempre reserva per a ell mateix; l'admin pot indicar un altre usuari
        Long idUsuari = (esAdmin && dades.idUsuari() != null) ? dades.idUsuari() : idUsuariToken;
        Usuari usuari = usuariRepository.findById(idUsuari)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "L'usuari no existeix"));
        if (!usuari.getActiu()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "L'usuari no està actiu");
        }

        Pista pista = pistaRepository.findById(dades.idPista())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La pista no existeix"));
        if (!pista.getActiva()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La pista no està activa");
        }

        if (!dades.horaFi().isAfter(dades.horaInici())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "L'hora de fi ha de ser posterior a la d'inici");
        }
        // Les reserves són d'una hora exacta i comencen en punt (10:00 a 11:00, 11:00 a 12:00...)
        if (dades.horaInici().getMinute() != 0 || dades.horaInici().getSecond() != 0
                || !dades.horaFi().equals(dades.horaInici().plusHours(1))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Les reserves són d'una hora exacta i en punt (per exemple, de 10:00 a 11:00)");
        }
        if (dades.horaInici().isBefore(HORA_OBERTURA) || dades.horaFi().isAfter(HORA_TANCAMENT)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El club obre de 08:00 a 23:00");
        }
        if (LocalDateTime.of(dades.data(), dades.horaInici()).isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No es pot reservar en el passat");
        }

        long solapaments = reservaRepository.comptarSolapaments(
                pista.getId(), dades.data(), dades.horaInici(), dades.horaFi());
        if (solapaments > 0) {
            throw new ApiException(HttpStatus.CONFLICT, "La pista ja està reservada en aquesta franja");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuari(usuari);
        reserva.setPista(pista);
        reserva.setData(dades.data());
        reserva.setHoraInici(dades.horaInici());
        reserva.setHoraFi(dades.horaFi());
        reserva.setEstat("ACTIVA");
        return reservaRepository.save(reserva);
    }

   
     //Cancel·la una reserva (canvia l'estat a CANCELLADA).
     
        @Transactional
    public Reserva cancelarReserva(Long idReserva, Long idUsuariToken, boolean esAdmin) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "La reserva no existeix"));
        if (!esAdmin && !reserva.getUsuari().getId().equals(idUsuariToken)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Només pots cancel·lar les teves reserves");
        }
        if ("CANCELLADA".equals(reserva.getEstat())) {
            throw new ApiException(HttpStatus.CONFLICT, "La reserva ja està cancel·lada");
        }
        reserva.setEstat("CANCELLADA");
        return reservaRepository.save(reserva);
    }

    //Metodes auxiliars
    //Comprova si alguna reserva se solapa amb la franja (la mateixa condició que comptarSolapaments).
    private boolean estaOcupada(List<Reserva> reserves, LocalTime inici, LocalTime fi) {
        for (Reserva reserva : reserves) {
            if (reserva.getHoraInici().isBefore(fi) && reserva.getHoraFi().isAfter(inici)) {
                return true;
            }
        }
        return false;
    }
}