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
import cat.padelgest.escriptori.api.ApiException;
import cat.padelgest.escriptori.model.LoginRequest;
import cat.padelgest.escriptori.model.LoginResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Controlador de la pantalla de login (login.fxml)
public class LoginController {

    // Elements de la pantalla 
    // El nom de cada atribut ha de coincidir amb el fx:id del FXML
    @FXML
    private TextField campEmail;

    @FXML
    private PasswordField campContrasenya;

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

        try {
            LoginResponse resposta = ApiClient.post("/auth/login",
                    new LoginRequest(email, contrasenya), LoginResponse.class);
            ApiClient.setToken(resposta.token());
            etiquetaMissatge.setText("Connectat com a " + resposta.nom() + " (" + resposta.rol() + ")");
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }
}
