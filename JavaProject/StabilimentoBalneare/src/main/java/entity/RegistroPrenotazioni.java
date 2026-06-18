package entity;

import database.GestorePersistenza;
import java.util.List;

public class RegistroPrenotazioni {


    public static final int SUCCESSO = 1;
    public static final int OMBRELLONE_NON_ESISTENTE = 2;
    public static final int OMBRELLONE_NON_DISPONIBILE = 3;
    public static final int CLIENTE_NON_ESISTENTE = 4;
    public static final int SERVIZIO_NON_ESISTENTE = 5;
    public static final int ERRORE_DB = 6;

    private GestorePersistenza gestorePersistenza;

    public RegistroPrenotazioni() {
        this.gestorePersistenza = new GestorePersistenza();
    }

    public int salvaPrenotazione(Long idOmbrelloneSelezionato, String dataPrenotazione, List<Long> idServiziSelezionati, String idClienteSelezionato) {


        RegistroOmbrelloni registroOmbrelloni = new RegistroOmbrelloni();
        Ombrellone ombrellone = registroOmbrelloni.cercaOmbrellonePerId(idOmbrelloneSelezionato);

        if (ombrellone == null) {return OMBRELLONE_NON_ESISTENTE;}
        if (!registroOmbrelloni.isOmbrelloneDisponibile(dataPrenotazione, ombrellone)){ return OMBRELLONE_NON_DISPONIBILE;}


        RegistroUtenti registroUtenti = new RegistroUtenti();
        Cliente cliente = registroUtenti.cercaClientePerUsername(idClienteSelezionato);

        if (cliente == null) return CLIENTE_NON_ESISTENTE;


        RegistroServizi registroServizi = new RegistroServizi();
        List<ServizioAggiuntivo> servizi = registroServizi.getTuttiServiziPerId(idServiziSelezionati);



        if (!idServiziSelezionati.isEmpty() && servizi.isEmpty()) {
            return SERVIZIO_NON_ESISTENTE;
        }

        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setData(dataPrenotazione);
        prenotazione.setOmbrellone(ombrellone);
        prenotazione.setCliente(cliente);

        for (ServizioAggiuntivo servizio : servizi) {

            prenotazione.addServizioAggiuntivo(servizio);
        }


        prenotazione.setStatoPrenotazione(StatoPrenotazione.CONFERMATA);
        boolean salvato = gestorePersistenza.salva(prenotazione);

        if (salvato) {
            return SUCCESSO;
        } else {
            return ERRORE_DB;
        }
    }
}