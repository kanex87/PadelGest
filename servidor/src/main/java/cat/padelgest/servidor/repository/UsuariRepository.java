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
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Repositori d'acces a dades dels usuaris
public interface UsuariRepository extends JpaRepository<Usuari, Long> {

    // Comprova si ja hi ha un usuari amb aquest email
    boolean existsByEmail(String email);

    // Busca un usuari pel seu email
    Optional<Usuari> findByEmail(String email);

    // Busca usuaris que tinguin el text al nom, als cognoms o a l'email (sense distingir majúscules)
    @Query("SELECT u FROM Usuari u "
            + "WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :text, '%')) "
            + "OR LOWER(u.cognoms) LIKE LOWER(CONCAT('%', :text, '%')) "
            + "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :text, '%')) "
            + "ORDER BY u.cognoms, u.nom")
    List<Usuari> cercar(@Param("text") String text);
}