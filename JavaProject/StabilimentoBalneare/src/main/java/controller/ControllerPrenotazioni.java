package controller;

import entity.Periodo;
import entity.RegistroPrenotazioni;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ControllerPrenotazioni {


    public static final int SUCCESSO = RegistroPrenotazioni.SUCCESSO;
    /*
    public static final int OMBRELLONE_NON_ESISTENTE = RegistroPrenotazioni.OMBRELLONE_NON_ESISTENTE;
    public static final int OMBRELLONE_NON_DISPONIBILE = RegistroPrenotazioni.OMBRELLONE_NON_DISPONIBILE;
    public static final int CLIENTE_NON_ESISTENTE = RegistroPrenotazioni.CLIENTE_NON_ESISTENTE;
    public static final int SERVIZIO_NON_ESISTENTE = RegistroPrenotazioni.SERVIZIO_NON_ESISTENTE;
    public static final int ERRORE_DB = RegistroPrenotazioni.ERRORE_DB;

    se in futuro staccassimo la boundary e ne mettessimo una che restituisce un messaggio specifico
    sulla base dell'errore, si potrebbe considerare di usare queste variabili
    */
    public static int effettuaPrenotazione(Long idOmbrelloneSelezionato, String dataPrenotazione, List<Long> idServiziSelezionati, String idCliente){

        RegistroPrenotazioni registroPrenotazioni = new RegistroPrenotazioni();

        return registroPrenotazioni.salvaPrenotazione(idOmbrelloneSelezionato, dataPrenotazione, idServiziSelezionati, idCliente);
    }

    // Restituisce la stringa ("ALTA_STAGIONE" o "BASSA_STAGIONE") sulla base della data
    public static String getPeriodoPerData(String formatoData, String data){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatoData);
        LocalDate dataParsata = LocalDate.parse(data, formatter);
        int mese = dataParsata.getMonthValue();

        // Usa l'enum per generare la stringa in modo sicuro
        if (mese >= 6 && mese <= 8) {
            return Periodo.ALTA_STAGIONE.name();
        } else {
            return Periodo.BASSA_STAGIONE.name();
        }
    }
}