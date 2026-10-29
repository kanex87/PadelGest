/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
// Contrasenya nova que posa l'admin (PUT /api/usuaris/{id}/contrasenya)
public record NovaContrasenyaRequest(String contrasenyaNova) {
}
