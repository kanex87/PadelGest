/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.service;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.EditarUsuariRequest;
import cat.padelgest.servidor.exception.ApiException;
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.repository.UsuariRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Servei d'usuaris: crear, editar, desactivar i canviar contrasenyes
@Service
public class UsuariService {

    // Constants
    public static final int LONGITUD_MINIMA_CONTRASENYA = 8;

    // Dependències
    private final UsuariRepository usuariRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor
    public UsuariService(UsuariRepository usuariRepository, PasswordEncoder passwordEncoder) {
        this.usuariRepository = usuariRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lògica de negoci
    // Retorna tots els usuaris
    public List<Usuari> llistar() {
        return usuariRepository.findAll();
    }

    // Busca usuaris pel nom, els cognoms o l'email (verificació a recepció)
    public List<Usuari> cercar(String text) {
        if (esBuit(text)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Escriu un text per buscar");
        }
        return usuariRepository.cercar(text.trim());
    }

    // Retorna un usuari pel seu id
    public Usuari obtenir(Long id) {
        return usuariRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "L'usuari no existeix"));
    }

    // Crea un usuari nou amb la contrasenya xifrada (el fan servir el registre i l'admin)
    @Transactional
    public Usuari crear(String nom, String cognoms, String email, String contrasenya, String rol) {
        if (esBuit(nom) || esBuit(cognoms) || esBuit(email) || esBuit(contrasenya)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Falten dades de l'usuari");
        }
        String emailNet = validarEmail(email);
        validarContrasenya(contrasenya);
        validarRol(rol);
        if (usuariRepository.existsByEmail(emailNet)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ja existeix un usuari amb aquest email");
        }

        Usuari usuari = new Usuari();
        usuari.setNom(nom.trim());
        usuari.setCognoms(cognoms.trim());
        usuari.setEmail(emailNet);
        usuari.setContrasenyaHash(passwordEncoder.encode(contrasenya));
        usuari.setRol(rol);
        usuari.setActiu(true);
        return usuariRepository.save(usuari);
    }

    // L'admin modifica les dades d'un usuari (no la contrasenya)
    @Transactional
    public Usuari editar(Long id, EditarUsuariRequest dades, Long idAdmin) {
        Usuari usuari = obtenir(id);
        if (esBuit(dades.nom()) || esBuit(dades.cognoms()) || esBuit(dades.email())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Falten dades de l'usuari");
        }
        String emailNet = validarEmail(dades.email());
        validarRol(dades.rol());
        if (!emailNet.equals(usuari.getEmail()) && usuariRepository.existsByEmail(emailNet)) {
            throw new ApiException(HttpStatus.CONFLICT, "Ja existeix un usuari amb aquest email");
        }
        // L'admin no es pot treure el rol ni desactivar-se a ell mateix
        if (id.equals(idAdmin) && (!"ADMIN".equals(dades.rol()) || Boolean.FALSE.equals(dades.actiu()))) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "No et pots treure el rol d'ADMIN ni desactivar-te a tu mateix");
        }

        usuari.setNom(dades.nom().trim());
        usuari.setCognoms(dades.cognoms().trim());
        usuari.setEmail(emailNet);
        usuari.setRol(dades.rol());
        if (dades.actiu() != null) {
            usuari.setActiu(dades.actiu());
        }
        return usuariRepository.save(usuari);
    }

    // L'admin desactiva un usuari (no l'esborra)
    @Transactional
    public void desactivar(Long id, Long idAdmin) {
        Usuari usuari = obtenir(id);
        if (id.equals(idAdmin)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No et pots desactivar a tu mateix");
        }
        usuari.setActiu(false);
        usuariRepository.save(usuari);
    }

    // Un usuari canvia la seva pròpia contrasenya (ha de saber l'actual)
    @Transactional
    public void canviarContrasenyaPropia(Long id, String contrasenyaActual, String contrasenyaNova) {
        Usuari usuari = obtenir(id);
        if (esBuit(contrasenyaActual) || !passwordEncoder.matches(contrasenyaActual, usuari.getContrasenyaHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La contrasenya actual no és correcta");
        }
        posarContrasenya(usuari, contrasenyaNova);
    }

    // L'admin posa una contrasenya nova a un usuari que l'ha oblidada
    @Transactional
    public void restablirContrasenya(Long id, String contrasenyaNova) {
        posarContrasenya(obtenir(id), contrasenyaNova);
    }

    // Mètodes auxiliars
    // Valida i desa una contrasenya nova xifrada
    private void posarContrasenya(Usuari usuari, String contrasenyaNova) {
        validarContrasenya(contrasenyaNova);
        usuari.setContrasenyaHash(passwordEncoder.encode(contrasenyaNova));
        usuariRepository.save(usuari);
    }

    // Comprova el format de l'email i el retorna net (sense espais i en minúscules)
    private String validarEmail(String email) {
        String emailNet = email.trim().toLowerCase();
        if (!emailNet.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "L'email no és vàlid");
        }
        return emailNet;
    }

    // Comprova la longitud mínima de la contrasenya
    private void validarContrasenya(String contrasenya) {
        if (contrasenya == null || contrasenya.length() < LONGITUD_MINIMA_CONTRASENYA) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "La contrasenya ha de tenir almenys " + LONGITUD_MINIMA_CONTRASENYA + " caràcters");
        }
    }

    // Comprova que el rol sigui ADMIN o USER
    private void validarRol(String rol) {
        if (!"ADMIN".equals(rol) && !"USER".equals(rol)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El rol ha de ser ADMIN o USER");
        }
    }

    // Comprova si un text és null o està en blanc
    private boolean esBuit(String text) {
        return text == null || text.isBlank();
    }
}
