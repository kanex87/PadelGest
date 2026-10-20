/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.CanviContrasenyaRequest;
import cat.padelgest.servidor.dto.LoginRequest;
import cat.padelgest.servidor.dto.LoginResponse;
import cat.padelgest.servidor.dto.RegistreRequest;
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.service.AuthService;
import cat.padelgest.servidor.service.UsuariService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controlador REST d'autenticació
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Dependències
    private final AuthService authService;
    private final UsuariService usuariService;

    // Constructor
    public AuthController(AuthService authService, UsuariService usuariService) {
        this.authService = authService;
        this.usuariService = usuariService;
    }

    // Endpoints
    // Registra un jugador nou
    @PostMapping("/registre")
    public ResponseEntity<Usuari> registrar(@RequestBody RegistreRequest dades) {
        Usuari usuari = authService.registrar(dades);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }

    // Inicia sessió i retorna el token
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest dades) {
        return ResponseEntity.ok(authService.login(dades));
    }

    // Retorna les dades de l'usuari del token (cal enviar el token)
    @GetMapping("/jo")
    public ResponseEntity<Usuari> jo(@AuthenticationPrincipal Jwt token) {
        return ResponseEntity.ok(authService.usuariActual(token.getSubject()));
    }

    // L'usuari del token canvia la seva contrasenya
    @PutMapping("/contrasenya")
    public ResponseEntity<Void> canviarContrasenya(@RequestBody CanviContrasenyaRequest dades,
            @AuthenticationPrincipal Jwt token) {
        Number id = token.getClaim("id");
        usuariService.canviarContrasenyaPropia(id.longValue(), dades.contrasenyaActual(), dades.contrasenyaNova());
        return ResponseEntity.noContent().build();
    }
}