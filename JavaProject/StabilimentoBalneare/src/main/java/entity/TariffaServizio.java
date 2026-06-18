package entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Map;

@Entity
public class TariffaServizio extends TariffaGiornaliera {

    @Enumerated(EnumType.STRING)
    private TipoServizio servizio;

    public TariffaServizio() {}

    public TariffaServizio(float prezzo, Periodo periodo, TipoServizio servizio) {
        super(prezzo, periodo);
        this.servizio = servizio;
    }

    public TipoServizio getServizio() {
        return servizio;
    }

    public void setServizio(TipoServizio servizio) {
        this.servizio = servizio;
    }
    

    @Override
    public String toString() {
        return "TariffaServizio{" +
                "servizio=" + servizio +
                '}';
    }
}
