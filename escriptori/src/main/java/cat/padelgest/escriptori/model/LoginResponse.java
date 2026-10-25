/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.model;

/**
 *
 * @author fer_c
 */
// Resposta del servidor al login: el token i les dades bàsiques de l'usuari
public record LoginResponse(String token, Long id, String nom, String rol) {
}
