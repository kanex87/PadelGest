/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.exception;

/**
 *
 * @author fer_c
 */
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Gestor global d'errors: converteix les excepcions en respostes HTTP
@RestControllerAdvice
public class GestorErrors {

    // Retorna el codi i el missatge de l'error
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<String> gestionarApiException(ApiException e) {
        return ResponseEntity.status(e.getEstat()).body(e.getMessage());
    }
}
