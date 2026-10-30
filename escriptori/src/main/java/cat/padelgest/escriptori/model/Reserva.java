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

// Una reserva tal com l'envia el servidor (amb l'usuari i la pista sencers)
public record Reserva(Long id, Usuari usuari, Pista pista, LocalDate data,
        LocalTime horaInici, LocalTime horaFi, String estat) {
}
