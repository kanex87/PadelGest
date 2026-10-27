/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori;

/**
 *
 * @author fer_c
 */
import javafx.application.Application;
import javafx.stage.Stage;

// Aplicació d'escriptori de PadelGest (administració del club)
public class App extends Application {

    // JavaFX crida aquest mètode quan l'aplicació ja està preparada
    @Override
    public void start(Stage finestra) {
        // Guardem la finestra perquè Navegacio hi pugui canviar les pantalles
        Navegacio.setFinestra(finestra);
        Navegacio.obrir("login.fxml", "PadelGest - Inici de sessió");
        finestra.show();
    }
}
