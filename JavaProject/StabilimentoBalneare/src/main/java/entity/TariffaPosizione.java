package entity;


import jakarta.persistence.*;


@Entity
public class TariffaPosizione extends TariffaGiornaliera {

    @Enumerated(EnumType.STRING)
    private Posizione posizione;

    public TariffaPosizione() {}

    public TariffaPosizione(float prezzo, Periodo periodo, Posizione posizione) {
        super(prezzo, periodo);
        this.posizione = posizione;
    }

    public Posizione getPosizione() {
        return posizione;
    }

    public void setPosizione(Posizione posizione) {
        this.posizione = posizione;
    }


}
