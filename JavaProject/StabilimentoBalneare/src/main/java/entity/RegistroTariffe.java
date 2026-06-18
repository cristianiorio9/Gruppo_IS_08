package entity;

import database.GestorePersistenza;

import java.util.List;
import java.util.Map;

public class RegistroTariffe {

    private GestorePersistenza gestorePersistenza;

    public RegistroTariffe(){

        this.gestorePersistenza = new GestorePersistenza();

    }

    //ZONA Giuseppe Damiano

    // Imposta la tariffa per una determinata posizione e periodo:
    // verifica se esiste già una tariffa con la stessa combinazione
    // (periodo + posizione) e, se presente, aggiorna solo il prezzo;
    // altrimenti crea una nuova tariffa e la salva nel database.
    public boolean setTariffaPosizione(float prezzo,  Periodo periodo, Posizione posizione) {
        
        // 1. Creiamo la mappa per cercare se esiste già questa combinazione
        Map<String, Object> parametriRicerca = Map.of(
                "periodo", periodo,
                "posizione", posizione
        );


        // Cerchiamo nel DB
        List<TariffaPosizione> esistenti = gestorePersistenza.cercaPerCampi(TariffaPosizione.class, parametriRicerca);

        if (!esistenti.isEmpty()) {
            // 2. Esiste già! Prendiamo l'ID della tariffa trovata
            Long idEsistente = esistenti.getFirst().getId();

            // Creiamo la mappa con i campi da aggiornare (solo il prezzo)
            Map<String, Object> campiDaAggiornare = Map.of(
                    "prezzo", prezzo
            );

            // Aggiorniamo la tupla esistente
            return gestorePersistenza.aggiornaCampi(TariffaPosizione.class, idEsistente, campiDaAggiornare);

        } else {
            // 3. Non esiste, è una tariffa del tutto nuova. La creiamo.
            TariffaPosizione t = new TariffaPosizione(prezzo, periodo, posizione);
            return gestorePersistenza.salva(t);
        }
    }

    // Imposta la tariffa per un determinato servizio e periodo:
    // verifica se esiste già una tariffa con la stessa combinazione
    // (servizio + posizione) e, se presente, aggiorna solo il prezzo;
    // altrimenti crea una nuova tariffa e la salva nel database.
    public boolean setTariffaServizio(float prezzo, Periodo periodo, TipoServizio tipoServizio) {


        // 1. Creiamo la mappa per cercare se esiste già questa combinazione
        Map<String, Object> parametriRicerca = Map.of(
                "periodo", periodo,
                "servizio", tipoServizio
        );

        // Cerchiamo nel DB
        List<TariffaServizio> esistenti = gestorePersistenza.cercaPerCampi(TariffaServizio.class, parametriRicerca);

        if (!esistenti.isEmpty()) {
            // 2. Esiste già! Prendiamo l'ID della tariffa trovata
            Long idEsistente = esistenti.get(0).getId();

            // Creiamo la mappa con i campi da aggiornare (solo il prezzo)
            Map<String, Object> campiDaAggiornare = Map.of(
                    "prezzo", prezzo
            );

            // Aggiorniamo la tupla esistente
            return gestorePersistenza.aggiornaCampi(TariffaServizio.class, idEsistente, campiDaAggiornare);

        } else {
            // 3. Non esiste, è una tariffa del tutto nuova. La creiamo.
            TariffaServizio t = new TariffaServizio(prezzo, periodo, tipoServizio);
            return gestorePersistenza.salva(t);
        }
    }

    //ZONA Christian Di Costanzo e Giuseppe Damiano

    /**
     * Estrae la tariffa per un determinato servizio  in base alla stagione.
     *
     * @param servizio La tipologia di servizio (es. CABINA, LETTINO_EXTRA).
     * @param periodo  la stagione (es. ALTA_STAGIONE, BASSA_STAGIONE)
     * @return Una lista contenente la TariffaServizio corrispondente.
     */
    public List<TariffaServizio> getTariffaServizioPerPeriodo(TipoServizio servizio, Periodo periodo){

        List<TariffaServizio> tariffa = gestorePersistenza.cercaPerCampi(
                TariffaServizio.class,
                Map.of(
                        "servizio", servizio,
                        "periodo",periodo
                )

        );
        return tariffa;
    }

    //DCC
    /**
     * Estrae la tariffa di una specifica posizione in base alla stagione.
     *
     * @param periodo   Il periodo stagionale (es. ALTA_STAGIONE, BASSA_STAGIONE).
     * @param posizione La locazione dell'ombrellone sulla spiaggia (es. PRIMA_FILA).
     * @return Una lista contenente la TariffaPosizione (solitamente di un solo elemento).
     */
    public List<TariffaPosizione> getTariffaPosizionePerPeriodo(Periodo periodo, Posizione posizione){
        return gestorePersistenza.cercaPerCampi(
                TariffaPosizione.class,
                Map.of(
                        "posizione", posizione,
                        "periodo", periodo
                )
        );
    }

}
