/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.escriptori.api.ApiClient;
import cat.padelgest.escriptori.api.EnSegonPla;
import cat.padelgest.escriptori.model.LoginRequest;
import cat.padelgest.escriptori.model.LoginResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import cat.padelgest.escriptori.Navegacio;

// Controlador de la pantalla de login (login.fxml)
public class LoginController {

    // Elements de la pantalla 
    // El nom de cada atribut ha de coincidir amb el fx:id del FXML
    @FXML
    private TextField campEmail;

    @FXML
    private PasswordField campContrasenya;

    @FXML
    private Button botoEntrar;

    @FXML
    private Label etiquetaMissatge;

    // Accions 
    // S'executa en prémer el botó Entrar (onAction="#entrar" al FXML)
    @FXML
    private void entrar() {
        String email = campEmail.getText().trim();
        String contrasenya = campContrasenya.getText();

        if (email.isEmpty() || contrasenya.isEmpty()) {
            etiquetaMissatge.setText("Escriu l'email i la contrasenya");
            return;
        }

        // Mentre esperem, avisem l'usuari i desactivem el botó perquè no el premi dues vegades
        etiquetaMissatge.setText("Connectant amb el servidor... La primera vegada pot trigar fins a un minut.");
        botoEntrar.setDisable(true);

        EnSegonPla.executar(
                // 1. La feina (en un fil a part): fer el login
                () -> ApiClient.post("/auth/login", new LoginRequest(email, contrasenya), LoginResponse.class),
                // 2. Si ha anat bé (al fil de JavaFX)
                resposta -> {
                    botoEntrar.setDisable(false);

                    // Aquesta aplicació només és per a l'administració del club
                    if (!"ADMIN".equals(resposta.rol())) {
                        etiquetaMissatge.setText("Aquesta aplicació és només per als administradors del club");
                        return;
                    }

                    ApiClient.setToken(resposta.token());
                    Navegacio.obrir("principal.fxml", "PadelGest - Administració");
                },
                // 3. Si ha fallat (al fil de JavaFX)
                missatge -> {
                    botoEntrar.setDisable(false);
                    etiquetaMissatge.setText(missatge);
                });
    }
}