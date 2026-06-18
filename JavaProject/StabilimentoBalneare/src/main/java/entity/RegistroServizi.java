package entity;

import database.GestorePersistenza;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegistroServizi {
    private GestorePersistenza gestorePersistenza;
    public RegistroServizi(){

        this.gestorePersistenza = new GestorePersistenza();

    }
    //DCC
    /**
     * Valuta l'insieme dei servizi aggiuntivi e  restituisce solo quelli
     * che non sono prenotati per la data specificata (ovvero sono disponibili).
     * * @param data La data della prenotazione.
     * @return Una lista di servizi aggiuntivi disponibili per la data della prenotazione.
     */
    public List<ServizioAggiuntivo> getServiziDisponibiliPerData(String data) {

        return gestorePersistenza.cercaDisponibiliConLimite(
                ServizioAggiuntivo.class,
                Prenotazione.class,
                "serviziAggiuntivi",
                Map.of(
                        "data", data,
                        "statoPrenotazione", StatoPrenotazione.CONFERMATA
                ),
                "disponibilita",          // Object limite: riconosce la Stringa (campo)
                true
        );
    }


    // DCC
    /**
     * Recupera una lista di oggetti ServizioAggiuntivo partendo da una lista di ID.
     * Ignora eventuali ID non validi o inesistenti.
     *
     * @param idServiziSelezionati Lista degli ID dei servizi scelti.
     * @return Lista di entità ServizioAggiuntivo o null se non sono presenti servizi con gli ID specificati.
     */
    public List<ServizioAggiuntivo> getTuttiServiziPerId(List<Long> idServiziSelezionati) {
        List<ServizioAggiuntivo> servizi = new ArrayList<>();

        for (Long id : idServiziSelezionati) {
            ServizioAggiuntivo servizio = gestorePersistenza.trovaPerId(ServizioAggiuntivo.class, id);
            if (servizio != null) {
                servizi.add(servizio);
            }
        }

        return servizi;
    }

}
