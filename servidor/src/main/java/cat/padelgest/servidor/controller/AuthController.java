/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.RegistreRequest;
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controlador REST d'autenticació
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Dependències
    private final AuthService authService;

    // Constructor
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoints
    // Registra un jugador nou
    @PostMapping("/registre")
    public ResponseEntity<Usuari> registrar(@RequestBody RegistreRequest dades) {
        Usuari usuari = authService.registrar(dades);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }
}
