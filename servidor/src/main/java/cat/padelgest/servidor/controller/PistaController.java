/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.model.Pista;
import cat.padelgest.servidor.repository.PistaRepository;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pistas")
public class PistaController {

    private final PistaRepository pistaRepository;

    public PistaController(PistaRepository pistaRepository) {
        this.pistaRepository = pistaRepository;
    }

    @GetMapping
    public List<Pista> listarPistas() {
        return pistaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pista> obtenerPista(@PathVariable Long id) {
        return pistaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crearPista(@RequestBody Pista pista) {
        String error = validarPista(pista);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (pistaRepository.existsByNombre(pista.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ja existeix una pista amb aquest nom");
        }

        pista.setId(null);
        if (pista.getActiva() == null) {
            pista.setActiva(true);
        }
        Pista guardada = pistaRepository.save(pista);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarPista(@PathVariable Long id, @RequestBody Pista datos) {
        Pista pista = pistaRepository.findById(id).orElse(null);
        if (pista == null) {
            return ResponseEntity.notFound().build();
        }

        String error = validarPista(datos);
        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }
        if (!pista.getNombre().equals(datos.getNombre())
                && pistaRepository.existsByNombre(datos.getNombre())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ja existeix una pista amb aquest nom");
        }

        pista.setNombre(datos.getNombre());
        pista.setTipo(datos.getTipo());
        if (datos.getActiva() != null) {
            pista.setActiva(datos.getActiva());
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
        if (pista.getNombre() == null || pista.getNombre().isBlank()) {
            return "El nom de la pista és obligatori";
        }
        if (!"INTERIOR".equals(pista.getTipo()) && !"EXTERIOR".equals(pista.getTipo())) {
            return "El tipus ha de ser INTERIOR o EXTERIOR";
        }
        return null;
    }
}
