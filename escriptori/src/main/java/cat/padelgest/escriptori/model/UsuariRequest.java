/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
// Dades per crear un usuari nou (POST /api/usuaris)
public record UsuariRequest(String nom, String cognoms, String email, String contrasenya, String rol) {
}
