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
import cat.padelgest.escriptori.api.EnSegonPla;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

// Controlador de la finestra principal (principal.fxml)
public class PrincipalController {

    // Elements de la pantalla
    @FXML
    private BorderPane arrel;
    @FXML
    private Label etiquetaUsuari;

    // Atributs
    // Rellotge que cada 10 minuts fa una petició perquè el servidor no s'adormi
    private Timeline mantenirDespert;

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

                // La primera pantalla que veu l'admin és el panell del dia
        obrirPanell();

        // Mentre l'aplicació estigui oberta, cada 10 minuts avisem el servidor
        mantenirDespert = new Timeline(new KeyFrame(Duration.minutes(10), e -> ferPing()));
        mantenirDespert.setCycleCount(Animation.INDEFINITE);
        mantenirDespert.play();
    }

    // Petició petita perquè Render vegi activitat i no aturi el servidor.
    // La resposta no ens importa, per això els dos últims arguments no fan res.
    private void ferPing() {
        EnSegonPla.executar(
                () -> ApiClient.get("/auth/jo", Usuari.class),
                jo -> { },
                missatge -> { });
    }

    // Accions del menú
    // Posa la pantalla de pistes al centre de la finestra
    @FXML
    private void obrirPistes() {
        arrel.setCenter(Navegacio.carregar("pistes.fxml"));
    }

    // Posa la pantalla d'usuaris al centre de la finestra
    @FXML
    private void obrirUsuaris() {
        arrel.setCenter(Navegacio.carregar("usuaris.fxml"));
    }

    // Posa la pantalla de reserves al centre de la finestra
    @FXML
    private void obrirReserves() {
        arrel.setCenter(Navegacio.carregar("reserves.fxml"));
    }

    // Posa el panell del dia al centre de la finestra
    @FXML
    private void obrirPanell() {
        arrel.setCenter(Navegacio.carregar("panell.fxml"));
    }

    // Posa la pantalla de recepció al centre de la finestra
    @FXML
    private void obrirRecepcio() {
        arrel.setCenter(Navegacio.carregar("recepcio.fxml"));
    }

    // Oblida el token i torna a la pantalla de login
    @FXML
        private void tancarSessio() {
        mantenirDespert.stop();
        ApiClient.setToken(null);
        Navegacio.obrir("login.fxml", "PadelGest - Inici de sessió");
    }

    // Tanca l'aplicació
    @FXML
    private void sortir() {
        Platform.exit();
    }
}
