/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.exception;

/**
 *
 * @author fer_c
 */
import org.springframework.http.HttpStatus;


//Excepcio de l'API. Guarda el codi HTTP que s'ha de retornar al client juntament amb el missatge d'error.

public class ApiException extends RuntimeException {

    // Atributs

    private final HttpStatus estat;

    //Constructor

    public ApiException(HttpStatus estat, String missatge) {
        super(missatge);
        this.estat = estat;
    }

    public HttpStatus getEstat() {
        return estat;
    }
}
