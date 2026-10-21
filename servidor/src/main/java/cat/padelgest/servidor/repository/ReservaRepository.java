/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.repository;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repositori d'accés a dades de les reserves.
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    //Reserves d'un usuari, de la més recent a la més antiga.
    List<Reserva> findByUsuariIdOrderByDataDescHoraIniciDesc(Long idUsuari);

    
    //Reserves d'una pista en un dia i estat concrets, ordenades per hora.
   
    List<Reserva> findByPistaIdAndDataAndEstatOrderByHoraInici(Long idPista, LocalDate data, String estat);

    //Reserves d'un usuari en un dia i estat concrets, ordenades per hora.
    List<Reserva> findByUsuariIdAndDataAndEstatOrderByHoraInici(Long idUsuari, LocalDate data, String estat);

    
    //Compta les reserves actives d'una pista que se solapen amb la franja indicada.
    
    @Query("SELECT COUNT(r) FROM Reserva r "
            + "WHERE r.pista.id = :idPista "
            + "AND r.data = :data "
            + "AND r.estat = 'ACTIVA' "
            + "AND r.horaInici < :horaFi "
            + "AND r.horaFi > :horaInici")
    long comptarSolapaments(@Param("idPista") Long idPista,
            @Param("data") LocalDate data,
            @Param("horaInici") LocalTime horaInici,
            @Param("horaFi") LocalTime horaFi);
}