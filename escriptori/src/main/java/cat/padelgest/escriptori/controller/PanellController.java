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
import cat.padelgest.escriptori.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

// Controlador del panell del dia (panell.fxml): totes les pistes actives i les seves reserves
public class PanellController {

    // Elements de la pantalla
    @FXML
    private DatePicker campData;

    @FXML
    private TableView<LocalTime> taulaPanell;

    @FXML
    private TableColumn<LocalTime, String> columnaHora;

    @FXML
    private Label etiquetaMissatge;

    // Inicialització
    @FXML
    private void initialize() {
        columnaHora.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue() + " - " + fila.getValue().plusHours(1)));

        // Una fila per a cada hora en què el club està obert (de 08:00 a 23:00)
        for (int hora = 8; hora < 23; hora++) {
            taulaPanell.getItems().add(LocalTime.of(hora, 0));
        }

        campData.valueProperty().addListener((observable, anterior, data) -> carregar());
        campData.setValue(LocalDate.now());
    }

    // Accions
    // Botó Actualitzar (i canvi de dia): torna a demanar les reserves de totes les pistes
    @FXML
    private void carregar() {
        LocalDate data = campData.getValue();
        if (data == null) {
            return;
        }

        etiquetaMissatge.setText("");
        // Deixem només la columna Hora i traiem les columnes de pistes d'abans
        taulaPanell.getColumns().remove(1, taulaPanell.getColumns().size());
        try {
            for (Pista pista : ApiClient.get("/pistes", Pista[].class)) {
                if (pista.activa()) {
                    Reserva[] reserves = ApiClient.get("/reserves/pista/" + pista.id() + "?data=" + data,
                            Reserva[].class);

                    // Una columna nova per a aquesta pista
                    TableColumn<LocalTime, String> columna = new TableColumn<>(pista.nom());
                    columna.setPrefWidth(200);
                    columna.setCellValueFactory(fila ->
                            new SimpleStringProperty(ocupant(reserves, fila.getValue())));
                    taulaPanell.getColumns().add(columna);
                }
            }
        } catch (ApiException e) {
            etiquetaMissatge.setText(e.getMessage());
        }
    }

    // Mètodes auxiliars
    // Nom del jugador que té la pista reservada a aquella hora, o "Lliure"
    private String ocupant(Reserva[] reserves, LocalTime hora) {
        for (Reserva reserva : reserves) {
            if (reserva.horaInici().equals(hora)) {
                return reserva.usuari().nom() + " " + reserva.usuari().cognoms();
            }
        }
        return "Lliure";
    }
}
