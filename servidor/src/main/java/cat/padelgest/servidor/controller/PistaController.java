/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.dto.FranjaLliure;
import cat.padelgest.servidor.model.Pista;
import cat.padelgest.servidor.repository.PistaRepository;
import cat.padelgest.servidor.service.ReservaService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pistes")
public class PistaController {

    private final PistaRepository pistaRepository;
    private final ReservaService reservaService;

    public PistaController(PistaRepository pistaRepository, ReservaService reservaService) {
        this.pistaRepository = pistaRepository;
        this.reservaService = reservaService;
    }

        @GetMapping
    public List<Pista> llistarPistes() {
        return pistaRepository.findAllByOrderByIdAsc();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pista> obtenirPista(@PathVariable Long id) {
        return pistaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Retorna les franges lliures d'una pista en un dia (per a qualsevol usuari amb token)
    @GetMapping("/{id}/disponibilitat")
    public List<FranjaLliure> disponibilitat(@PathVariable Long id, @RequestParam LocalDate data) {
        return reservaService.disponibilitat(id, data);
    }

    @PostMapping
    public ResponseEntity<?> crearPista(@RequestBody Pista pista) {
        String error = validarPista(pista);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (pistaRepository.existsByNom(pista.getNom())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ja existeix una pista amb aquest nom");
        }

        pista.setId(null);
        if (pista.getActiva() == null) {
            pista.setActiva(true);
        }
        Pista desada = pistaRepository.save(pista);
        return ResponseEntity.status(HttpStatus.CREATED).body(desada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarPista(@PathVariable Long id, @RequestBody Pista dades) {
        Pista pista = pistaRepository.findById(id).orElse(null);
        if (pista == null) {
            return ResponseEntity.notFound().build();
        }

        String error = validarPista(dades);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (!pista.getNom().equals(dades.getNom())
                && pistaRepository.existsByNom(dades.getNom())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ja existeix una pista amb aquest nom");
        }

        pista.setNom(dades.getNom());
        pista.setTipus(dades.getTipus());
        if (dades.getActiva() != null) {
            pista.setActiva(dades.getActiva());
        }
        return ResponseEntity.ok(pistaRepository.save(pista));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarPista(@PathVariable Long id) {
        Pista pista = pistaRepository.findById(id).orElse(null);
        if (pista == null) {
            return ResponseEntity.notFound().build();
        }
        pista.setActiva(false);
        pistaRepository.save(pista);
        return ResponseEntity.noContent().build();
    }

    private String validarPista(Pista pista) {
        if (pista.getNom() == null || pista.getNom().isBlank()) {
            return "El nom de la pista és obligatori";
        }
        if (!"INTERIOR".equals(pista.getTipus()) && !"EXTERIOR".equals(pista.getTipus())) {
            return "El tipus ha de ser INTERIOR o EXTERIOR";
        }
        return null;
    }
}