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
import cat.padelgest.escriptori.model.EditarUsuariRequest;
import cat.padelgest.escriptori.model.NovaContrasenyaRequest;
import cat.padelgest.escriptori.model.Usuari;
import cat.padelgest.escriptori.model.UsuariRequest;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

// Controlador de la pantalla de gestió d'usuaris (usuaris.fxml)
public class UsuarisController {

    // Elements de la pantalla
    @FXML
    private TableView<Usuari> taulaUsuaris;

    @FXML
    private TableColumn<Usuari, String> columnaNom;

    @FXML
    private TableColumn<Usuari, String> columnaEmail;

    @FXML
    private TableColumn<Usuari, String> columnaRol;

    @FXML
    private TableColumn<Usuari, String> columnaEstat;

    @FXML
    private TextField campNom;

    @FXML
    private TextField campCognoms;

    @FXML
    private TextField campEmail;

    @FXML
    private ComboBox<String> campRol;

    @FXML
    private PasswordField campContrasenya;

    @FXML
    private Button botoEstat;

    @FXML
    private Button botoContrasenya;

    @FXML
    private Label etiquetaMissatge;

    // Inicialització
    @FXML
    private void initialize() {
        // Què ha d'ensenyar cada columna per a cada usuari
        columnaNom.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().nom() + " " + fila.getValue().cognoms()));
        columnaEmail.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().email()));
        columnaRol.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().rol()));
        columnaEstat.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().actiu() ? "Actiu" : "Desactivat"));

        campRol.getItems().setAll("USER", "ADMIN");

        // Quan l'usuari tria una fila de la taula, omplim el formulari
        taulaUsuaris.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, usuari) -> mostrarUsuari(usuari));

        mostrarUsuari(null);
        carregarUsuaris();
    }

    // Accions
    // Botó Nou: deixa el formulari buit per crear un usuari
    @FXML
    private void nou() {
        taulaUsuaris.getSelectionModel().clearSelection();
        etiquetaMissatge.setText("");
    }

    // Botó Desar: crea l'usuari si no n'hi ha cap de triat, o modifica el triat
    @FXML
    private void desar() {
        String nom = campNom.getText().trim();
        String cognoms = campCognoms.getText().trim();
        String email = campEmail.getText().trim();
        String rol = campRol.getValue();

        if (nom.isEmpty() || cognoms.isEmpty() || email.isEmpty() || rol == null) {
            etiquetaMissatge.setText("Omple el nom, els cognoms, l'email i el rol");
            return;
        }

        Usuari triat = taulaUsuaris.getSelectionModel().getSelectedItem();
        try {
            if (triat == null) {
                // Un usuari nou necessita una contrasenya inicial
                String contrasenya = campContrasenya.getText();
                if (contrasenya.isEmpty()) {
                    etiquetaMissatge.setText("Escriu una contrasenya inicial");
                    return;
                }
                ApiClient.post("/usuaris", new UsuariRequest(nom, cognoms, email, contrasenya, rol), Usuari.class);
                carregarUsuaris();
                etiquetaMissatge.setText("Usuari creat");
            } else {
                ApiClient.put("/usuaris/" + triat.id(),
                        new EditarUsuariRequest(nom, cognoms, email, rol, triat.actiu()), Usuari.class);
                carregarUsuaris();
                etiquetaMissatge.setText("Usuari modificat");
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Botó Desactivar / Reactivar
    @FXML
    private void canviarEstat() {
        Usuari triat = taulaUsuaris.getSelectionModel().getSelectedItem();
        if (triat == null) {
            etiquetaMissatge.setText("Tria un usuari de la taula");
            return;
        }

        try {
            if (triat.actiu()) {
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                        "Segur que vols desactivar " + triat.nom() + " " + triat.cognoms()
                        + "? No podrà iniciar sessió.");
                alerta.setTitle("Desactivar usuari");
                alerta.setHeaderText(null);
                Optional<ButtonType> resposta = alerta.showAndWait();
                if (resposta.isEmpty() || resposta.get() != ButtonType.OK) {
                    return;
                }
                ApiClient.delete("/usuaris/" + triat.id());
                carregarUsuaris();
                etiquetaMissatge.setText("Usuari desactivat");
            } else {
                ApiClient.put("/usuaris/" + triat.id(), new EditarUsuariRequest(triat.nom(),
                        triat.cognoms(), triat.email(), triat.rol(), true), Usuari.class);
                carregarUsuaris();
                etiquetaMissatge.setText("Usuari reactivat");
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Botó Restablir contrasenya: l'admin posa una contrasenya nova a l'usuari triat
    @FXML
    private void restablirContrasenya() {
        Usuari triat = taulaUsuaris.getSelectionModel().getSelectedItem();
        if (triat == null) {
            etiquetaMissatge.setText("Tria un usuari de la taula");
            return;
        }

        TextInputDialog dialeg = new TextInputDialog();
        dialeg.setTitle("Restablir contrasenya");
        dialeg.setHeaderText(null);
        dialeg.setContentText("Contrasenya nova per a " + triat.nom() + " " + triat.cognoms() + ":");
        Optional<String> resposta = dialeg.showAndWait();
        if (resposta.isEmpty() || resposta.get().isEmpty()) {
            return;
        }

        try {
            ApiClient.put("/usuaris/" + triat.id() + "/contrasenya",
                    new NovaContrasenyaRequest(resposta.get()), Void.class);
            etiquetaMissatge.setText("Contrasenya restablerta. Comunica-la a l'usuari.");
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Mètodes auxiliars
    // Demana els usuaris al servidor i els posa a la taula
    private void carregarUsuaris() {
        try {
            Usuari[] usuaris = ApiClient.get("/usuaris", Usuari[].class);
            taulaUsuaris.getItems().setAll(usuaris);
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Omple el formulari amb l'usuari triat (o el buida si no n'hi ha cap)
    private void mostrarUsuari(Usuari usuari) {
        campContrasenya.setText("");
        if (usuari == null) {
            campNom.setText("");
            campCognoms.setText("");
            campEmail.setText("");
            campRol.setValue("USER");
            // La contrasenya només es posa en crear un usuari
            campContrasenya.setDisable(false);
            botoEstat.setText("Desactivar");
            botoEstat.setDisable(true);
            botoContrasenya.setDisable(true);
        } else {
            campNom.setText(usuari.nom());
            campCognoms.setText(usuari.cognoms());
            campEmail.setText(usuari.email());
            campRol.setValue(usuari.rol());
            campContrasenya.setDisable(true);
            botoEstat.setText(usuari.actiu() ? "Desactivar" : "Reactivar");
            botoEstat.setDisable(false);
            botoContrasenya.setDisable(false);
        }
    }
}
