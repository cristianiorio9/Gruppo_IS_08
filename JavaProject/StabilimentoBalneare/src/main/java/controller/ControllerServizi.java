package controller;

import entity.ServizioAggiuntivo;
import entity.Periodo;
import entity.RegistroServizi;
import entity.RegistroTariffe;
import entity.TariffaServizio;
import entity.TipoServizio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerServizi {
    //DCC
    /**
     * Recupera l'elenco dei servizi aggiuntivi disponibili per una specifica data.
     * Prepara i dati per la Boundary convertendo gli oggetti in array di stringhe.
     *
     * @param data La data nel formato testuale (dd/MM/yyyy) per cui verificare la disponibilità.
     * @return Una lista di array di stringhe. Ogni array contiene: [ID, Nome, Descrizione].
     */
    public static List<String[]> getServiziDisponibili(String data) {

        List<String[]> disponibili = new ArrayList<>();
        RegistroServizi registroServizi = new RegistroServizi();

        List<ServizioAggiuntivo> serviziDisponibili = registroServizi.getServiziDisponibiliPerData(data);

        for (ServizioAggiuntivo disponibile : serviziDisponibili) {

            disponibili.add(convertiServizioInArray( disponibile ));
        }

        return disponibili;
    }

    /**
     * Converte un oggetto ServizioAggiuntivo in un array di stringhe.
     * @param servizio L'entità ServizioAggiuntivo da convertire.
     * @return Un array di stringhe contenente i dettagli del servizio nel seguente ordine: [ID, Nome, Descrizione].
     */
    private static String[] convertiServizioInArray(ServizioAggiuntivo servizio) {
        String[] riga = new String[]{
                String.valueOf(servizio.getId()),  // [0] ID
                servizio.getServizio().toString(), // [1] Nome
                servizio.getDescrizione()          // [2] Descrizione
        };
        return  riga;
    }

    /**
     * Recupera i prezzi per un elenco di servizi in base al periodo specificato.
     * Interroga il RegistroTariffe per estrarre la tariffa corrispondente. Se per un determinato
     * servizio non è configurata alcuna tariffa nel database per quel periodo, il sistema
     * gestisce l'assenza assegnando un prezzo sicuro di default pari a 0.0f per evitare errori a runtime.
     *
     * @param nomeServizio Una lista di stringhe rappresentanti i nomi dei servizi (es. "CABINA", "PARCHEGGIO").
     * @param periodo      Il periodo di interesse ("ALTA_STAGIONE", "BASSA_STAGIONE").
     * @return Una mappa (Map) che associa al nome di ogni servizio (Chiave) il relativo prezzo (Valore Float).
     */
    public static Map<String, Float> getTariffeServiziPerPeriodo(List<String> nomeServizio, String periodo) {
        Map<String, Float> prezzi = new HashMap<>();
        RegistroTariffe registroTariffe = new RegistroTariffe();

        for (String servizio : nomeServizio) {

            List<TariffaServizio> tariffe = registroTariffe.getTariffaServizioPerPeriodo(TipoServizio.valueOf(servizio), Periodo.valueOf(periodo));

            if (!tariffe.isEmpty()) {
                prezzi.put(servizio, tariffe.getFirst().getPrezzo());
            } else {
                // Gestione sicura se manca la tariffa per un servizio
                prezzi.put(servizio, 0.0f);
            }
        }
        return prezzi;
    }


    //ZONA Giuseppe Damiano

    // Definisce e salva la tariffa di un servizio per il periodo indicato:
    // converte le stringhe nei rispettivi enum e delega al RegistroTariffe
    // la creazione o l’aggiornamento della tariffa, restituendo l’esito
    // dell’operazione.
    public static boolean definisciTariffaServizio(String tipoServizio, String periodo, float prezzo) {
        RegistroTariffe registroTariffe = new RegistroTariffe();
        return registroTariffe.setTariffaServizio(prezzo, Periodo.valueOf(periodo), TipoServizio.valueOf(tipoServizio));
    }

    // Restituisce l’elenco dei periodi disponibili per i servizi,
    // convertendo i valori dell’enum Periodo in un array di stringhe
    // utilizzabile dalla GUI (es. per popolare le ComboBox).
    public static String[] getPeriodiServizi() {

        Periodo[] valoriEnum = Periodo.values();

        String[] nomi = new String[valoriEnum.length];
        for (int i = 0; i < valoriEnum.length; i++) {
            nomi[i] = valoriEnum[i].toString();
        }
        return nomi;
    }


    // Restituisce la lista dei servizi disponibili convertendo i valori
    // dell’enum TipoServizio in una lista di stringhe utilizzabile dalla GUI.
    public static List<String> getServizi() {

        TipoServizio[] valoriEnum = TipoServizio.values();

        List<String> nomi = new ArrayList<>();
        for (TipoServizio valori : valoriEnum) {
            nomi.add(valori.name());
        }
        return nomi;
    }


}


