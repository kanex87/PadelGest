/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.dto;

/**
 *
 * @author fer_c
 */
// Dades que envia l'admin per modificar un usuari (sense contrasenya)
public record EditarUsuariRequest(String nom, String cognoms, String email, String rol, Boolean actiu) {
}
