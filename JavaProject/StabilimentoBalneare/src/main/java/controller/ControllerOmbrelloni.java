package controller;

import entity.RegistroOmbrelloni;
import entity.Ombrellone;
import entity.Periodo;
import entity.Posizione;
import entity.RegistroTariffe;
import entity.TariffaPosizione;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerOmbrelloni {

    // DCC
    /**
     * Carica le informazioni di un ombrellone per id
     * @param idOmbrellone id dell'ombrellone
     * @return Una lista di array di stringhe contenenti i dati dell'ombrellone [ID, Fila, Numero, Posizione].
     */
    public static String[] caricaOmbrellone(Long idOmbrellone) {
        RegistroOmbrelloni registro = new RegistroOmbrelloni();
        Ombrellone ombrellone = registro.cercaOmbrellonePerId(idOmbrellone);

        if (ombrellone != null) {
            return convertiOmbrelloneInArray(ombrellone);
        }else{
            return null;
        }
    }

    /**
     * Recupera il prezzo di una tariffa associata a una specifica posizione dell'ombrellone
     * e a un determinato periodo.
     * @param periodo   Il periodo sotto forma di stringa ("ALTA_STAGIONE").
     * @param posizione La posizione dell'ombrellone sotto forma di stringa ("PRIMA_FILA").
     * @return Il prezzo (float) corrispondente alla tariffa trovata, oppure 0.0f se la tariffa non è presente.
     */
    public static float getTariffaPosizionePerPeriodo(String periodo, String posizione) {
        RegistroTariffe registroTariffe = new RegistroTariffe();

        List<TariffaPosizione> tariffa = registroTariffe.getTariffaPosizionePerPeriodo(
                Periodo.valueOf(periodo), Posizione.valueOf(posizione));
        float prezzo = 0.0f;
        if (!tariffa.isEmpty()) {
            prezzo = tariffa.getFirst().getPrezzo();
        }
        return prezzo;
    }

    /**
     * Converte un oggetto Ombrellone in un array di stringhe.
     * @param ombrellone L'oggetto Ombrellone da convertire.
     * @return Un array di stringhe contenente le informazioni dell'ombrellone nel seguente ordine:
     * [0] ID univoco, [1] Fila, [2] Numero, [3] Posizione.
     */
    private static String[] convertiOmbrelloneInArray(Ombrellone ombrellone) {
        return new String[]{
                String.valueOf(ombrellone.getId()),       // [0] ID
                Integer.toString(ombrellone.getFila()),   // [1] Fila
                Integer.toString(ombrellone.getNumero()), // [2] Numero
                ombrellone.getPosizione().toString()      // [3] Posizione
        };
    }

    //ZONA Cristian Iorio
    public static List<String[]> getTuttiOmbrelloni() {

        RegistroOmbrelloni registroOmbrelloni = new RegistroOmbrelloni();

        List<Ombrellone> ombrelloni = registroOmbrelloni.cercaTuttiOmbrelloni();

        List<String[]> risultato = new ArrayList<>();

        for (Ombrellone o : ombrelloni) {

            risultato.add(convertiOmbrelloneInArray(o));
        }

        return risultato;
    }

    public static boolean salvaConfigurazioneComplessiva(ArrayList<String[]> datiTabella) {

        List<Ombrellone> ombrelloniDaSalvare = new ArrayList<>();

        try {
            for (String[] riga : datiTabella) {

                // L'array ora contiene l'ID in posizione 0
                Long id = Long.parseLong(riga[0]);
                int fila = Integer.parseInt(riga[1]);
                int numero = Integer.parseInt(riga[2]);
                Posizione posizione = Posizione.valueOf(riga[3]);

                // Usiamo il nuovo costruttore che include l'ID
                Ombrellone ombrellone = new Ombrellone(id, fila, numero, posizione);

                ombrelloniDaSalvare.add(ombrellone);
            }

            RegistroOmbrelloni registroOmbrelloni = new RegistroOmbrelloni();
            return registroOmbrelloni.salvaModificheOmbrelloni(ombrelloniDaSalvare);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean aggiungiNuovoOmbrellone(int fila, int numero, String posizione) {

        RegistroOmbrelloni registroOmbrelloni = new RegistroOmbrelloni();

        Ombrellone ombrellone = new Ombrellone(fila, numero, Posizione.valueOf(posizione));

        return registroOmbrelloni.inserisciOmbrellone(ombrellone);

    }

    public static boolean rimuoviOmbrellone(long idOmbrellone) {

        RegistroOmbrelloni registroOmbrelloni = new RegistroOmbrelloni();

        //List<Ombrellone> ombrelloni = registroOmbrelloni.cercaOmbrellone(filaSelezionata, numeroSelezionato);

        //return registroOmbrelloni.rimuoviOmbrellone(ombrelloni.getFirst());
        return registroOmbrelloni.rimuoviOmbrellone(idOmbrellone);

    }

    //ZONA Giuseppe Damiano

    //  Restituisce l’elenco delle posizioni disponibili convertendo i valori
    // dell’enum Posizione in un array di stringhe, utile per popolare
    // componenti della GUI come le tabelle o le ComboBox.
    public static String[] getPosizioni() {

        Posizione[] valoriEnum = Posizione.values();

        String[] nomi = new String[valoriEnum.length];
        for (int i = 0; i < valoriEnum.length; i++) {
            nomi[i] = valoriEnum[i].toString();
        }
        return nomi;

    }

    // Restituisce una mappa contenente le tariffe di tutte le posizioni
    // per il periodo indicato: per ogni posizione richiama il metodo
    // dedicato al recupero della tariffa e inserisce il valore ottenuto
    // nella mappa risultante.
    public static Map<String, Float> getTutteTariffePosizioniPerPeriodo(String periodo, String[] posizioni) {
        Map<String, Float> dati = new HashMap<>();

        for (String posizione : posizioni) {
            dati.put(posizione, getTariffaPosizionePerPeriodo(periodo, posizione));
        }
        return dati;
    }

    // Definisce e salva la tariffa per una specifica posizione nel periodo indicato:
    // converte i parametri stringa nei rispettivi enum e delega al RegistroTariffe
    // l’aggiornamento o la creazione della tariffa, restituendo l’esito
    // dell’operazione.
    public static boolean definisciTariffaPosizione(String posizione, String periodo, float prezzo) {
        RegistroTariffe registroTariffe = new RegistroTariffe();
        return registroTariffe.setTariffaPosizione(prezzo, Periodo.valueOf(periodo), Posizione.valueOf(posizione));
    }

    //ZONA Giuseppe Luca Acampa
    public static int[] getDimensioni() {

        // Recupero tutti gli ombrelloni per calcolare l'estensione massima della griglia
        RegistroOmbrelloni registro = new RegistroOmbrelloni();
        List<Ombrellone> ombrelloni = registro.cercaTuttiOmbrelloni();

        int maxFila = 0;
        int maxNumero = 0;

        // Scansiono tutti gli ombrelloni tenendo traccia del valore più alto di fila e numero
        for (Ombrellone o : ombrelloni) {
            if (o.getFila() > maxFila) maxFila = o.getFila();
            if (o.getNumero() > maxNumero) maxNumero = o.getNumero();
        }

        // Posizione 0 = maxFila, posizione 1 = maxNumero
        return new int[]{maxFila, maxNumero};
    }





    // Restituisco i nomi di tutti i periodi disponibili come array di stringhe
    public static String[] getPeriodiPosizioni() {
        Periodo[] valoriEnum = Periodo.values();
        String[] nomi = new String[valoriEnum.length];

        for (int i = 0; i < valoriEnum.length; i++) {
            nomi[i] = valoriEnum[i].toString();
        }
        return nomi;
    }

    public static Map<String, List<String[]>> visualizzaDisponibilita(String dataSelezionata) {

        RegistroOmbrelloni registro = new RegistroOmbrelloni();

        // 1. Recupero gli ombrelloni liberi
        List<Ombrellone> disponibiliList = registro.getDisponibilitaOmbrelloni(dataSelezionata);

        // 2. Recupero TUTTI gli ombrelloni
        List<Ombrellone> tuttiGliOmbrelloni = registro.cercaTuttiOmbrelloni();

        // 3. Calcolo i prenotati per differenza: GRAZIE ALL'EQUALS ORA FUNZIONA!
        List<Ombrellone> prenotatiList = new ArrayList<>(tuttiGliOmbrelloni);
        prenotatiList.removeAll(disponibiliList);

        // Preparo le liste di stringhe per la Boundary
        List<String[]> prenotati = new ArrayList<>();
        List<String[]> disponibili = new ArrayList<>();

        for (Ombrellone ombrellone : disponibiliList) {
            disponibili.add(convertiOmbrelloneInArray(ombrellone));
        }
        for (Ombrellone ombrellone : prenotatiList) {
            prenotati.add(convertiOmbrelloneInArray(ombrellone));
        }

        // Costruisco la mappa risultato
        Map<String, List<String[]>> risultato = new HashMap<>();
        risultato.put("disponibili", disponibili);
        risultato.put("prenotati", prenotati);

        return risultato;
    }

}
