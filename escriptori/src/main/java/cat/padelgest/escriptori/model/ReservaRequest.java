/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
import java.time.LocalDate;
import java.time.LocalTime;

// Dades per crear una reserva (POST /api/reserves); l'admin indica el jugador
public record ReservaRequest(Long idUsuari, Long idPista, LocalDate data,
        LocalTime horaInici, LocalTime horaFi) {
}
