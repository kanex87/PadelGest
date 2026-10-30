/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
// Dades d'un usuari tal com les envia el servidor (sense la contrasenya)
public record Usuari(Long id, String nom, String cognoms, String email, String rol, Boolean actiu) {

    // Text que surt quan l'usuari es mostra en un desplegable
    @Override
    public String toString() {
        return nom + " " + cognoms + " (" + email + ")";
    }
}
