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
import cat.padelgest.escriptori.model.Reserva;
import cat.padelgest.escriptori.model.Usuari;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

// Controlador de la pantalla de recepció (recepcio.fxml)
public class RecepcioController {

    // Elements de la pantalla
    @FXML
    private TextField campCerca;

    @FXML
    private ListView<Usuari> llistaJugadors;

    @FXML
    private Label etiquetaJugador;

    @FXML
    private Label etiquetaEstat;

    @FXML
    private TableView<Reserva> taulaReserves;

    @FXML
    private TableColumn<Reserva, String> columnaHora;

    @FXML
    private TableColumn<Reserva, String> columnaPista;

    @FXML
    private Label etiquetaMissatge;

    // Inicialització
    @FXML
    private void initialize() {
        columnaHora.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().horaInici() + " - " + fila.getValue().horaFi()));
        columnaPista.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().pista().nom()));

        // Quan es tria un jugador de la llista, n'ensenyem l'estat i les reserves d'avui
        llistaJugadors.getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, jugador) -> mostrarJugador(jugador));
    }

    // Accions
    // Botó Cercar: busca jugadors pel nom, els cognoms o l'email
    @FXML
    private void cercar() {
        String text = campCerca.getText().trim();
        if (text.isEmpty()) {
            etiquetaMissatge.setText("Escriu un nom, uns cognoms o un email");
            return;
        }

        try {
            // Els espais i les lletres com la ñ no poden anar tal qual a una URL
            String textUrl = URLEncoder.encode(text, StandardCharsets.UTF_8);
            Usuari[] trobats = ApiClient.get("/usuaris/cerca?text=" + textUrl, Usuari[].class);
            llistaJugadors.getItems().setAll(trobats);

            if (trobats.length == 0) {
                etiquetaMissatge.setText("No s'ha trobat cap jugador");
            } else if (trobats.length == 1) {
                etiquetaMissatge.setText("");
                llistaJugadors.getSelectionModel().selectFirst();
            } else {
                etiquetaMissatge.setText("S'han trobat " + trobats.length + " jugadors. Tria'n un de la llista.");
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Mètodes auxiliars
    // Ensenya el nom, l'estat del compte i les reserves d'avui del jugador triat
    private void mostrarJugador(Usuari jugador) {
        taulaReserves.getItems().clear();
        if (jugador == null) {
            etiquetaJugador.setText("");
            etiquetaEstat.setText("");
            return;
        }

        etiquetaJugador.setText(jugador.nom() + " " + jugador.cognoms());
        if (jugador.actiu()) {
            etiquetaEstat.setText("Compte actiu");
            etiquetaEstat.setStyle("-fx-text-fill: green;");
        } else {
            etiquetaEstat.setText("Compte desactivat: no pot jugar");
            etiquetaEstat.setStyle("-fx-text-fill: red;");
        }

        try {
            Reserva[] reserves = ApiClient.get("/reserves/usuari/" + jugador.id() + "?data=" + LocalDate.now(),
                    Reserva[].class);
            taulaReserves.getItems().setAll(reserves);
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }
}
