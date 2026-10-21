/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.EditarUsuariRequest;
import cat.padelgest.servidor.dto.NovaContrasenyaRequest;
import cat.padelgest.servidor.dto.UsuariRequest;
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.service.UsuariService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

// Controlador REST de gestió d'usuaris (només ADMIN, ho diu SeguretatConfig)
@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    // Dependències
    private final UsuariService usuariService;

    // Constructor
    public UsuariController(UsuariService usuariService) {
        this.usuariService = usuariService;
    }

    // Endpoints
    // Llista tots els usuaris
    @GetMapping
    public List<Usuari> llistarUsuaris() {
        return usuariService.llistar();
    }

    // Busca usuaris pel nom, els cognoms o l'email (recepció)
    @GetMapping("/cerca")
    public List<Usuari> cercarUsuaris(@RequestParam String text) {
        return usuariService.cercar(text);
    }

    // Retorna un usuari
    @GetMapping("/{id}")
    public Usuari obtenirUsuari(@PathVariable Long id) {
        return usuariService.obtenir(id);
    }

    // Crea un usuari (l'admin pot triar el rol)
    @PostMapping
    public ResponseEntity<Usuari> crearUsuari(@RequestBody UsuariRequest dades) {
        Usuari usuari = usuariService.crear(dades.nom(), dades.cognoms(), dades.email(),
                dades.contrasenya(), dades.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuari);
    }

    // Modifica les dades d'un usuari
    @PutMapping("/{id}")
    public Usuari editarUsuari(@PathVariable Long id, @RequestBody EditarUsuariRequest dades,
            @AuthenticationPrincipal Jwt token) {
        return usuariService.editar(id, dades, idUsuari(token));
    }

    // Desactiva un usuari
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarUsuari(@PathVariable Long id, @AuthenticationPrincipal Jwt token) {
        usuariService.desactivar(id, idUsuari(token));
        return ResponseEntity.noContent().build();
    }

    // Posa una contrasenya nova a un usuari que l'ha oblidada
    @PutMapping("/{id}/contrasenya")
    public ResponseEntity<Void> restablirContrasenya(@PathVariable Long id,
            @RequestBody NovaContrasenyaRequest dades) {
        usuariService.restablirContrasenya(id, dades.contrasenyaNova());
        return ResponseEntity.noContent().build();
    }

    // Mètodes auxiliars
    // Llegeix l'id de l'usuari guardat dins del token
    private Long idUsuari(Jwt token) {
        Number id = token.getClaim("id");
        return id.longValue();
    }
}
