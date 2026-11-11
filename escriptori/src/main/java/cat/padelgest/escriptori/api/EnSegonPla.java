/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.api;

/**
 *
 * @author fer_c
 */
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.concurrent.Task;

// Fa les peticions al servidor en un fil a part, perquè la finestra no es quedi congelada
public class EnSegonPla {

    // Executa la feina en un fil a part. Quan acaba, crida siVaBe amb el resultat
    // o siFalla amb el missatge d'error. Totes dues s'executen al fil de JavaFX.
    public static <T> void executar(Supplier<T> feina, Consumer<T> siVaBe, Consumer<String> siFalla) {
        Task<T> tasca = new Task<>() {
            @Override
            protected T call() {
                return feina.get();
            }
        };

        // Ha anat bé: passem el resultat
        tasca.setOnSucceeded(e -> siVaBe.accept(tasca.getValue()));

        // Ha fallat: passem el missatge de l'error
        tasca.setOnFailed(e -> {
            Throwable error = tasca.getException();
            if (error instanceof ApiException) {
                siFalla.accept(error.getMessage());
            } else {
                siFalla.accept("Error inesperat: " + error.getMessage());
            }
        });

        // Creem el fil i l'engeguem
        Thread fil = new Thread(tasca);
        fil.setDaemon(true);
        fil.start();
    }
}