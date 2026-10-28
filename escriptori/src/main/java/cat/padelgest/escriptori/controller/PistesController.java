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
import cat.padelgest.escriptori.model.Pista;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

// Controlador de la pantalla de gestió de pistes (pistes.fxml)
public class PistesController {

    // Elements de la pantalla
    @FXML
    private TableView<Pista> taulaPistes;

    @FXML
    private TableColumn<Pista, String> columnaNom;

    @FXML
    private TableColumn<Pista, String> columnaTipus;

    @FXML
    private TableColumn<Pista, String> columnaEstat;

    @FXML
    private TextField campNom;

    @FXML
    private ComboBox<String> campTipus;

    @FXML
    private Button botoEstat;

    @FXML
    private Label etiquetaMissatge;

    // Inicialització
    @FXML
    private void initialize() {
        // Què ha d'ensenyar cada columna per a cada pista
        columnaNom.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().nom()));
        columnaTipus.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().tipus()));
        columnaEstat.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().activa() ? "Activa" : "Desactivada"));

        campTipus.getItems().setAll("INTERIOR", "EXTERIOR");

        // Quan l'usuari tria una fila de la taula, omplim el formulari
        taulaPistes.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, pista) -> mostrarPista(pista));

        carregarPistes();
    }

    // Accions
    // Botó Nova: deixa el formulari buit per crear una pista
    @FXML
    private void nova() {
        taulaPistes.getSelectionModel().clearSelection();
        etiquetaMissatge.setText("");
    }

    // Botó Desar: crea la pista si no n'hi ha cap de triada, o modifica la triada
    @FXML
    private void desar() {
        String nom = campNom.getText().trim();
        String tipus = campTipus.getValue();

        if (nom.isEmpty() || tipus == null) {
            etiquetaMissatge.setText("Escriu el nom i tria el tipus");
            return;
        }

        Pista triada = taulaPistes.getSelectionModel().getSelectedItem();
        try {
            if (triada == null) {
                ApiClient.post("/pistes", new Pista(null, nom, tipus, true), Pista.class);
                carregarPistes();
                etiquetaMissatge.setText("Pista creada");
            } else {
                ApiClient.put("/pistes/" + triada.id(), new Pista(triada.id(), nom, tipus, triada.activa()), Pista.class);
                carregarPistes();
                etiquetaMissatge.setText("Pista modificada");
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Botó Desactivar / Reactivar
    @FXML
    private void canviarEstat() {
        Pista triada = taulaPistes.getSelectionModel().getSelectedItem();
        if (triada == null) {
            etiquetaMissatge.setText("Tria una pista de la taula");
            return;
        }

        try {
            if (triada.activa()) {
                // Abans de desactivar, demanem confirmació
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION,
                        "Segur que vols desactivar la " + triada.nom() + "? No es podrà reservar.");
                alerta.setTitle("Desactivar pista");
                alerta.setHeaderText(null);
                Optional<ButtonType> resposta = alerta.showAndWait();
                if (resposta.isEmpty() || resposta.get() != ButtonType.OK) {
                    return;
                }
                ApiClient.delete("/pistes/" + triada.id());
                carregarPistes();
                etiquetaMissatge.setText("Pista desactivada");
            } else {
                ApiClient.put("/pistes/" + triada.id(), new Pista(triada.id(), triada.nom(), triada.tipus(), true), Pista.class);
                carregarPistes();
                etiquetaMissatge.setText("Pista reactivada");
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Mètodes auxiliars
    // Demana les pistes al servidor i les posa a la taula
    private void carregarPistes() {
        try {
            Pista[] pistes = ApiClient.get("/pistes", Pista[].class);
            taulaPistes.getItems().setAll(pistes);
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Omple el formulari amb la pista triada (o el buida si no n'hi ha cap)
    private void mostrarPista(Pista pista) {
        if (pista == null) {
            campNom.setText("");
            campTipus.setValue(null);
            botoEstat.setText("Desactivar");
            botoEstat.setDisable(true);
        } else {
            campNom.setText(pista.nom());
            campTipus.setValue(pista.tipus());
            botoEstat.setText(pista.activa() ? "Desactivar" : "Reactivar");
            botoEstat.setDisable(false);
        }
    }
}
