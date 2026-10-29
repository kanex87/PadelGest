/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
// Dades per modificar un usuari (PUT /api/usuaris/{id}); la contrasenya no hi és
public record EditarUsuariRequest(String nom, String cognoms, String email, String rol, Boolean actiu) {
}
