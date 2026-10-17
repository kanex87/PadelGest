/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.service;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.LoginRequest;
import cat.padelgest.servidor.dto.LoginResponse;
import cat.padelgest.servidor.dto.RegistreRequest;
import cat.padelgest.servidor.exception.ApiException;
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.repository.UsuariRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Servei d'autenticació: registre i login
@Service
public class AuthService {

    // Constants
    public static final int LONGITUD_MINIMA_CONTRASENYA = 8;

    // Dependències
    private final UsuariRepository usuariRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    // Constructor
    public AuthService(UsuariRepository usuariRepository, PasswordEncoder passwordEncoder,
            TokenService tokenService) {
        this.usuariRepository = usuariRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    // Registra un jugador nou amb rol USER i la contrasenya xifrada
    @Transactional
    public Usuari registrar(RegistreRequest dades) {
        if (esBuit(dades.nom()) || esBuit(dades.cognoms())
                || esBuit(dades.email()) || esBuit(dades.contrasenya())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Falten dades del registre");
        }

        String email = dades.email().trim().toLowerCase();
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "L'email no és vàlid");
        }
        if (dades.contrasenya().length() < LONGITUD_MINIMA_CONTRASENYA) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "La contrasenya ha de tenir almenys " + LONGITUD_MINIMA_CONTRASENYA + " caràcters");
        }
        if (usuariRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ja existeix un usuari amb aquest email");
        }

        Usuari usuari = new Usuari();
        usuari.setNom(dades.nom().trim());
        usuari.setCognoms(dades.cognoms().trim());
        usuari.setEmail(email);
        usuari.setContrasenyaHash(passwordEncoder.encode(dades.contrasenya()));
        usuari.setRol("USER");
        usuari.setActiu(true);
        return usuariRepository.save(usuari);
    }

    // Comprova l'email i la contrasenya i, si són correctes, retorna un token
    public LoginResponse login(LoginRequest dades) {
        if (esBuit(dades.email()) || esBuit(dades.contrasenya())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Falten l'email o la contrasenya");
        }

        String email = dades.email().trim().toLowerCase();
        Usuari usuari = usuariRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Email o contrasenya incorrectes"));

        if (!passwordEncoder.matches(dades.contrasenya(), usuari.getContrasenyaHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Email o contrasenya incorrectes");
        }
        if (!usuari.getActiu()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "L'usuari està desactivat");
        }

        String token = tokenService.generarToken(usuari);
        return new LoginResponse(token, usuari.getId(), usuari.getNom(), usuari.getRol());
    }

    // Retorna l'usuari que ha iniciat sessió a partir del seu email
    public Usuari usuariActual(String email) {
        return usuariRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuari no trobat"));
    }

    // Comprova si un text és null o està en blanc
    private boolean esBuit(String text) {
        return text == null || text.isBlank();
    }
}