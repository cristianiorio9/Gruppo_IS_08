package entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazione statoPrenotazione;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "ombrellone_id")
    private Ombrellone ombrellone;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "Prenotazione_Servizio",
            joinColumns = @JoinColumn(name = "prenotazione_id"),
            inverseJoinColumns = @JoinColumn(name = "servizio_id")
    )

    private Set<ServizioAggiuntivo> serviziAggiuntivi = new HashSet<>();

    public Prenotazione() {}

    public Prenotazione(Long id, String data, StatoPrenotazione statoPrenotazione, Cliente cliente, Ombrellone ombrellone) {
        this.id = id;
        this.data = data;
        this.statoPrenotazione = statoPrenotazione;
        this.cliente = cliente;
        this.ombrellone = ombrellone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatoPrenotazione getStatoPrenotazione() {
        return statoPrenotazione;
    }

    public void setStatoPrenotazione(StatoPrenotazione statoPrenotazione) {
        this.statoPrenotazione = statoPrenotazione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Set<ServizioAggiuntivo> getServiziAggiuntivi() {
        return serviziAggiuntivi;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setServiziAggiuntivi(Set<ServizioAggiuntivo> serviziAggiuntivi) {
        this.serviziAggiuntivi = serviziAggiuntivi;
    }

    public void addServizioAggiuntivo(ServizioAggiuntivo servizioAggiuntivo) {
        this.serviziAggiuntivi.add(servizioAggiuntivo);
    }

    public Ombrellone getOmbrellone() {
        return ombrellone;
    }

    public void setOmbrellone(Ombrellone ombrellone) {
        this.ombrellone = ombrellone;
    }

    @Override
    public String toString() {
        return "Prenotazione{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", statoPrenotazione=" + statoPrenotazione +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prenotazione)) return false;
        Prenotazione that = (Prenotazione) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}