/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.repository;

/**
 *
 * @author fer_c
 */
import cat.padelgest.servidor.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PistaRepository extends JpaRepository<Pista, Long> {

    boolean existsByNom(String nom);

    // Totes les pistes, ordenades per id (1, 2, 3...)
    List<Pista> findAllByOrderByIdAsc();
}
