/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.padelgest.servidor.model;

/**
 *
 * @author fer_c
 */
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

//Entitat que representa la reserva d'una pista per part d'un usuari en una data i franja horària.
@Entity
@Table(name = "reserves")
public class Reserva {
    
//Atributs
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuari", nullable = false)
    private Usuari usuari;

    @ManyToOne
    @JoinColumn(name = "id_pista", nullable = false)
    private Pista pista;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "hora_inici", nullable = false)
    private LocalTime horaInici;

    @Column(name = "hora_fi", nullable = false)
    private LocalTime horaFi;

    @Column(name = "estat", nullable = false)
    private String estat = "ACTIVA";

    //Constructor
    
    public Reserva() {
    }

    //Geters i setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuari getUsuari() {
        return usuari;
    }

    public void setUsuari(Usuari usuari) {
        this.usuari = usuari;
    }

    public Pista getPista() {
        return pista;
    }

    public void setPista(Pista pista) {
        this.pista = pista;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInici() {
        return horaInici;
    }

    public void setHoraInici(LocalTime horaInici) {
        this.horaInici = horaInici;
    }

    public LocalTime getHoraFi() {
        return horaFi;
    }

    public void setHoraFi(LocalTime horaFi) {
        this.horaFi = horaFi;
    }

    public String getEstat() {
        return estat;
    }

    public void setEstat(String estat) {
        this.estat = estat;
    }
}
