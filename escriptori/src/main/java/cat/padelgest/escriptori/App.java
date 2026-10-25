/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori;

/**
 *
 * @author fer_c
 */
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Aplicació d'escriptori de PadelGest (administració del club)
public class App extends Application {

    // JavaFX crida aquest mètode quan l'aplicació ja està preparada
    @Override
    public void start(Stage finestra) throws IOException {
        // Carreguem la pantalla de login des del fitxer FXML
        FXMLLoader carregador = new FXMLLoader(App.class.getResource("login.fxml"));
        Scene escena = new Scene(carregador.load());

        finestra.setTitle("PadelGest - Administració");
        finestra.setScene(escena);
        finestra.show();
    }
}
