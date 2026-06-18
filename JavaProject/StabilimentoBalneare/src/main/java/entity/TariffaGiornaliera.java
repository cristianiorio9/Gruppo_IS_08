package entity;

import jakarta.persistence.*;

@Entity
public abstract class TariffaGiornaliera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float prezzo;

    @Enumerated(EnumType.STRING)
    private Periodo periodo;

    public TariffaGiornaliera() {}

    public TariffaGiornaliera(float prezzo, Periodo periodo) {
        this.prezzo = prezzo;
        this.periodo = periodo;
    }

    public Long getId() {
        return id;
    }

    public float getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(float prezzo) {
        this.prezzo = prezzo;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }


}
