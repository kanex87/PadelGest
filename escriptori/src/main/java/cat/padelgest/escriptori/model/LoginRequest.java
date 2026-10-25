/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
// Dades que enviem al servidor per iniciar sessió
public record LoginRequest(String email, String contrasenya) {
}
