package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Ombrellone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int fila;
    private int numero;

    @Enumerated(EnumType.STRING)
    private Posizione posizione;

    @OneToMany(mappedBy = "ombrellone", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Posizione getPosizione() {
        return posizione;
    }

    public void setPosizione(Posizione posizione) {
        this.posizione = posizione;
    }

    public Ombrellone() {
    }

    public Ombrellone(Long id, int fila, int numero, Posizione posizione) {
        this.id = id;
        this.fila = fila;
        this.numero = numero;
        this.posizione = posizione;
    }

    public Ombrellone(int fila, int numero, Posizione posizione) {
        this.fila = fila;
        this.numero = numero;
        this.posizione = posizione;
    }

    @Override
    public String toString() {
        return "Ombrellone{" +
                "id=" + id +
                ", fila=" + fila +
                ", numero=" + numero +
                ", posizione=" + posizione +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        // Se puntano alla stessa area di memoria, sono sicuramente uguali
        if (this == o) return true;

        // Se l'altro oggetto è nullo o appartiene a un'altra classe, non sono uguali
        if (o == null || getClass() != o.getClass()) return false;


        Ombrellone that = (Ombrellone) o;

        // Se l'ID non è nullo e coincide, allora per noi è lo stesso ombrellone
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        // Quando si sovrascrive equals(), è obbligatorio sovrascrivere anche hashCode().
        // Per le entità JPA, restituire un valore fisso per la classe è la pratica più sicura.
        return getClass().hashCode();
    }



}