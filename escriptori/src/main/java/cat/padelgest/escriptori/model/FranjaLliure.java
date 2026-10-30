/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
import java.time.LocalTime;

// Una franja d'una hora lliure d'una pista (GET /api/pistes/{id}/disponibilitat)
public record FranjaLliure(LocalTime horaInici, LocalTime horaFi) {

    // Text que surt al desplegable, per exemple "10:00 - 11:00"
    @Override
    public String toString() {
        return horaInici + " - " + horaFi;
    }
}
