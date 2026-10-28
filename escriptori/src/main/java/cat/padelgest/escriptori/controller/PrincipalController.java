/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.escriptori.Navegacio;
import cat.padelgest.escriptori.api.ApiClient;
import cat.padelgest.escriptori.api.ApiException;
import cat.padelgest.escriptori.model.Usuari;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

// Controlador de la finestra principal (principal.fxml)
public class PrincipalController {

    // Elements de la pantalla
    @FXML
    private BorderPane arrel;
    @FXML
    private Label etiquetaUsuari;

    // Inicialització
    // JavaFX el crida sol quan la pantalla ja està carregada
    @FXML
    private void initialize() {
        try {
            // Demanem al servidor qui som (la petició porta el token)
            Usuari jo = ApiClient.get("/auth/jo", Usuari.class);
            etiquetaUsuari.setText("Connectat com a " + jo.nom() + " " + jo.cognoms() + " (" + jo.email() + ")");
        } catch (ApiException e) {
            etiquetaUsuari.setText(e.getMessage());
        }
    }

    // Accions del menú
    // Oblida el token i torna a la pantalla de login
    // Posa la pantalla de pistes al centre de la finestra
    @FXML
    private void obrirPistes() {
        arrel.setCenter(Navegacio.carregar("pistes.fxml"));
    }

    @FXML
    private void tancarSessio() {
        ApiClient.setToken(null);
        Navegacio.obrir("login.fxml", "PadelGest - Inici de sessió");
    }

    // Tanca l'aplicació
    @FXML
    private void sortir() {
        Platform.exit();
    }
}
