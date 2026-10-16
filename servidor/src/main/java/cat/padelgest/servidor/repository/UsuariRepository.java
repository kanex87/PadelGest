/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.repository;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.model.Usuari;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositori d'acces a dades dels usuaris
public interface UsuariRepository extends JpaRepository<Usuari, Long> {

    // Comprova si ja hi ha un usuari amb aquest email
    boolean existsByEmail(String email);

    // Busca un usuari pel seu email
    Optional<Usuari> findByEmail(String email);
}
