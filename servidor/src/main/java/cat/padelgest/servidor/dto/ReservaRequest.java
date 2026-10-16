/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.dto;

/**
 *
 * @author fer_c
 */
import java.time.LocalDate;
import java.time.LocalTime;


//DTO amb les dades que envia l'app client per crear una reserva.

public record ReservaRequest(
        Long idUsuari,
        Long idPista,
        LocalDate data,
        LocalTime horaInici,
        LocalTime horaFi) {
}
