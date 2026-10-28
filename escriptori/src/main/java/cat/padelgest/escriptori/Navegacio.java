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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

// Canvia la pantalla que es veu a la finestra de l'aplicació
public class Navegacio {

    // Atributs
    // La finestra principal (la que ens dona JavaFX a App.start)
    private static Stage finestra;

    // Getters i setters
    public static void setFinestra(Stage novaFinestra) {
        finestra = novaFinestra;
    }

    // Accions
    // Carrega un fitxer FXML i el posa a la finestra amb el títol indicat
    public static void obrir(String fitxerFxml, String titol) {
        try {
            FXMLLoader carregador = new FXMLLoader(App.class.getResource(fitxerFxml));
            finestra.setScene(new Scene(carregador.load()));
        } catch (IOException e) {
            throw new RuntimeException("No es pot obrir la pantalla " + fitxerFxml, e);
        }
        finestra.setTitle(titol);
        // La finestra agafa la mida de la pantalla nova i es posa al mig
        finestra.sizeToScene();
        finestra.centerOnScreen();
    }
        // Carrega un fitxer FXML i en torna el contingut, per posar-lo dins una altra pantalla
    public static Parent carregar(String fitxerFxml) {
        try {
            FXMLLoader carregador = new FXMLLoader(App.class.getResource(fitxerFxml));
            return carregador.load();
        } catch (IOException e) {
            throw new RuntimeException("No es pot obrir la pantalla " + fitxerFxml, e);
        }
    }
}
