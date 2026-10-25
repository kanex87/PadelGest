/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.escriptori.api;

/**
 *
 * @author fer_c
 */
// Error en parlar amb el servidor; el missatge ja es pot ensenyar a l'usuari
public class ApiException extends RuntimeException {

    public ApiException(String missatge) {
        super(missatge);
    }
}
