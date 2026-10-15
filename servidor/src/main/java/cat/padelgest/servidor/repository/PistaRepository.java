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

public interface PistaRepository extends JpaRepository<Pista, Long> {

    boolean existsByNom(String nom);
}
