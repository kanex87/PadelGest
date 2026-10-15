/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.controller;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.model.Usuari;
import cat.padelgest.servidor.repository.UsuariRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuaris")
public class UsuariController {

    private final UsuariRepository usuariRepository;

    public UsuariController(UsuariRepository usuariRepository) {
        this.usuariRepository = usuariRepository;
    }

    @GetMapping
    public List<Usuari> llistarUsuaris() {
        return usuariRepository.findAll();
    }
}
