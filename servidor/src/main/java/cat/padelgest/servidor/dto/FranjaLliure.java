/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.dto;

/**
 *
 * @author fer_c
 */
import java.time.LocalTime;

// Una franja horària lliure d'una pista (de horaInici a horaFi)
public record FranjaLliure(LocalTime horaInici, LocalTime horaFi) {
}
