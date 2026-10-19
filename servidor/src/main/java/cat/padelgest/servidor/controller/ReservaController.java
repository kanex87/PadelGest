/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.ReservaRequest;
import cat.padelgest.servidor.model.Reserva;
import cat.padelgest.servidor.service.ReservaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//Controlador REST de les reserves 
@RestController
@RequestMapping("/api/reserves")
public class ReservaController {

    //Dependencies
    private final ReservaService reservaService;

    //Constructor
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    //Endpoints
    //Retorna totes les reserves.
    @GetMapping
    public List<Reserva> llistarTotes() {
        return reservaService.llistarTotes();
    }

    //Retorna les reserves d'un usuari.
    @GetMapping("/usuari/{idUsuari}")
    public List<Reserva> llistarPerUsuari(@PathVariable Long idUsuari) {
        return reservaService.llistarPerUsuari(idUsuari);
    }

    //Retorna les reserves de l'usuari del token.
    @GetMapping("/meves")
    public List<Reserva> llistarMeves(@AuthenticationPrincipal Jwt token) {
        return reservaService.llistarPerUsuari(idUsuari(token));
    }

    //Retorna les reserves actives d'una pista en un dia.
    @GetMapping("/pista/{idPista}")
    public List<Reserva> llistarActivesPistaDia(@PathVariable Long idPista, @RequestParam LocalDate data) {
        return reservaService.llistarActivesPistaDia(idPista, data);
    }

    //Crea una reserva nova.
    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaRequest dades,
            @AuthenticationPrincipal Jwt token) {
        Reserva reserva = reservaService.crearReserva(dades, idUsuari(token), esAdmin(token));
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    //Cancel·la una reserva.
    @PutMapping("/{id}/cancelar")
    public Reserva cancelarReserva(@PathVariable Long id, @AuthenticationPrincipal Jwt token) {
        return reservaService.cancelarReserva(id, idUsuari(token), esAdmin(token));
    }

    //Metodes auxiliars
    //Llegeix l'id de l'usuari guardat dins del token.
    private Long idUsuari(Jwt token) {
        Number id = token.getClaim("id");
        return id.longValue();
    }

    //Comprova si el token és d'un administrador.
    private boolean esAdmin(Jwt token) {
        return "ADMIN".equals(token.getClaimAsString("rol"));
    }

    
}