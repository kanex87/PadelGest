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
import cat.padelgest.escriptori.model.FranjaLliure;
import cat.padelgest.escriptori.model.Pista;
import cat.padelgest.escriptori.model.Reserva;
import cat.padelgest.escriptori.model.ReservaRequest;
import cat.padelgest.escriptori.model.Usuari;
import java.time.LocalDate;
import java.util.Optional;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// Controlador de la pantalla de reserves (reserves.fxml)
public class ReservesController {

    // Elements de la pantalla
    @FXML
    private DatePicker campData;

    @FXML
    private ComboBox<Pista> campPista;

    @FXML
    private TableView<Reserva> taulaReserves;

    @FXML
    private TableColumn<Reserva, String> columnaHora;

    @FXML
    private TableColumn<Reserva, String> columnaJugador;

    @FXML
    private TableColumn<Reserva, String> columnaEmail;

    @FXML
    private ComboBox<Usuari> campJugador;

    @FXML
    private ComboBox<FranjaLliure> campFranja;

    @FXML
    private Label etiquetaMissatge;

    // Inicialització
    @FXML
    private void initialize() {
        columnaHora.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().horaInici() + " - " + fila.getValue().horaFi()));
        columnaJugador.setCellValueFactory(fila -> new SimpleStringProperty(
                fila.getValue().usuari().nom() + " " + fila.getValue().usuari().cognoms()));
        columnaEmail.setCellValueFactory(fila -> new SimpleStringProperty(fila.getValue().usuari().email()));

        try {
            // Només es pot reservar a les pistes actives i per als jugadors actius
            for (Pista pista : ApiClient.get("/pistes", Pista[].class)) {
                if (pista.activa()) {
                    campPista.getItems().add(pista);
                }
            }
            for (Usuari usuari : ApiClient.get("/usuaris", Usuari[].class)) {
                if (usuari.actiu()) {
                    campJugador.getItems().add(usuari);
                }
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
            return;
        }

        // Cada vegada que canvia la data o la pista, tornem a carregar
        campData.valueProperty().addListener((observable, anterior, data) -> carregar());
        campPista.valueProperty().addListener((observable, anterior, pista) -> carregar());

        // Per defecte: avui i la primera pista
        campData.setValue(LocalDate.now());
        campPista.getSelectionModel().selectFirst();
    }

    // Accions
    // Botó Reservar: crea una reserva per al jugador i la franja triats
    @FXML
    private void reservar() {
        Usuari jugador = campJugador.getValue();
        FranjaLliure franja = campFranja.getValue();
        if (jugador == null || franja == null) {
            etiquetaMissatge.setText("Tria el jugador i la franja");
            return;
        }

        ReservaRequest dades = new ReservaRequest(jugador.id(), campPista.getValue().id(),
                campData.getValue(), franja.horaInici(), franja.horaFi());
        try {
            ApiClient.post("/reserves", dades, Reserva.class);
            carregar();
            etiquetaMissatge.setText("Reserva creada per a " + jugador.nom() + " a les " + franja.horaInici());
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Botó Cancel·lar reserva: cancel·la la reserva triada a la taula
    @FXML
    private void cancelar() {
        Reserva triada = taulaReserves.getSelectionModel().getSelectedItem();
        if (triada == null) {
            etiquetaMissatge.setText("Tria una reserva de la taula");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, "Segur que vols cancel·lar la reserva de "
                + triada.usuari().nom() + " " + triada.usuari().cognoms() + " a les " + triada.horaInici() + "?");
        alerta.setTitle("Cancel·lar reserva");
        alerta.setHeaderText(null);
        Optional<ButtonType> resposta = alerta.showAndWait();
        if (resposta.isEmpty() || resposta.get() != ButtonType.OK) {
            return;
        }

        try {
            // Aquest PUT no porta cos: tota la informació és a la ruta
            ApiClient.put("/reserves/" + triada.id() + "/cancelar", null, Reserva.class);
            carregar();
            etiquetaMissatge.setText("Reserva cancel·lada");
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Mètodes auxiliars
    // Carrega les reserves i les franges lliures de la pista i el dia triats
    private void carregar() {
        Pista pista = campPista.getValue();
        LocalDate data = campData.getValue();
        if (pista == null || data == null) {
            return;
        }

        etiquetaMissatge.setText("");
        taulaReserves.getItems().clear();
        campFranja.getItems().clear();
        try {
            Reserva[] reserves = ApiClient.get("/reserves/pista/" + pista.id() + "?data=" + data, Reserva[].class);
            taulaReserves.getItems().setAll(reserves);

            // Per a dies passats el servidor respon 400: no es pot reservar en el passat
            if (!data.isBefore(LocalDate.now())) {
                FranjaLliure[] franges = ApiClient.get("/pistes/" + pista.id() + "/disponibilitat?data=" + data,
                        FranjaLliure[].class);
                campFranja.getItems().setAll(franges);
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }
}
