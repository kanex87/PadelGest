/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.dto;

/**
 *
 * @author fer_c
 */
// Dades que envia un usuari per canviar la seva contrasenya
public record CanviContrasenyaRequest(String contrasenyaActual, String contrasenyaNova) {
}
