package entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class ServizioAggiuntivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descrizione;
    private int disponibilita;

    @Enumerated(EnumType.STRING)
    private TipoServizio servizio;

    @ManyToMany(mappedBy = "serviziAggiuntivi")
    private Set<Prenotazione> prenotazioni = new HashSet<>();

    public ServizioAggiuntivo() {}

    public ServizioAggiuntivo(String descrizione, int disponibilita) {
        this.descrizione = descrizione;
        this.disponibilita = disponibilita;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(int disponibilita) {
        this.disponibilita = disponibilita;
    }

    public TipoServizio getServizio() {
        return servizio;
    }

    public void setServizio(TipoServizio servizio) {
        this.servizio = servizio;
    }

    @Override
    public String toString() {
        return "ServizioAggiuntivo{" + "id=" + id + ", descrizione='" + descrizione + '\'' + ", disponibilita=" + disponibilita + '}';
    }
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        // Se l'altro oggetto è nullo o appartiene a un'altra classe, non sono uguali
        if (o == null || getClass() != o.getClass()) return false;

        // Facciamo il cast e confrontiamo gli ID
        ServizioAggiuntivo that = (ServizioAggiuntivo) o;

        // Due servizi sono uguali se e solo se il loro ID non è nullo e coincide
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        // Restituisce un hash fisso per la classe (best practice per le Entity JPA)
        return getClass().hashCode();
    }
}
