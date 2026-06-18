package entity;

import database.GestorePersistenza;

import java.util.*;

public class RegistroOmbrelloni {

    private GestorePersistenza gestorePersistenza;

    public RegistroOmbrelloni() {
        this.gestorePersistenza = new GestorePersistenza();
    }
    //DCC
    /**
     * Cerca gli ombrelloni per fila e numero
     *
     * @param fila   La riga identificativa (es. Fila 1).
     * @param numero Il numero dell'ombrellone nella fila.
     * @return Lista di ombrelloni trovati.
     */
    public List<Ombrellone> cercaOmbrellonePerFilaNumero(int fila, int numero) {
        return gestorePersistenza.cercaPerCampi(
                Ombrellone.class,
                Map.of(
                        "fila", fila,
                        "numero", numero
                )
        );
    }

    /** Metodo che cerca tutti gli ombrelloni esistenti
     * @return Lista di tutti gli ombrelloni esistenti.*/
    public List<Ombrellone> cercaTuttiOmbrelloni() {

        List<Ombrellone> ombrelloni = gestorePersistenza.cercaTutti(Ombrellone.class);

        // Gli ombrelloni appaiono ordinati per fila e pe
        // r numero
        /* Esempio:
        * 1,1
        * 1,2
        * 1,3
        * 2,1
        * */
        ombrelloni.sort(Comparator.comparingInt(Ombrellone::getFila)
                .thenComparingInt(Ombrellone::getNumero));

        return ombrelloni;
    }

    // Metodo che manda al gestore un oggetto ombrellone, controllando prima che non ne esista già uno con stessi fila-numero
    public boolean inserisciOmbrellone(Ombrellone ombrellone) {
        // Controllo se esiste già un ombrellone in quella fila e numero
        List<Ombrellone> occupanti = cercaOmbrellonePerFilaNumero(ombrellone.getFila(), ombrellone.getNumero());

        // Se l'oggetto ombrellone esiste il posto è già occupato
        if (!occupanti.isEmpty()) {
            return false; // Questo farà scattare il messaggio di errore nella View
        } else
            return gestorePersistenza.salva(ombrellone);
    }

    // Metodo che rimuove ombrellone, non richiede nessun controllo logico
    // Nota off-topic: abbiamo scelto che quando si rimuove un ombrellone si cancellano tutte le prenotazioni a esso associate
    public boolean rimuoviOmbrellone(long idOmbrellone) {

        return gestorePersistenza.elimina(Ombrellone.class, idOmbrellone);
    }

    // Metodo che aggiorna i campi degli ombrelloni modificati
    public boolean salvaModificheOmbrelloni(List<Ombrellone> ombrelloniDalClient) {

        try {
            for (Ombrellone datiNuovi : ombrelloniDalClient) {

                // Cerco se esiste già un ombrellone con stesso fila-numero
                List<Ombrellone> occupanti = cercaOmbrellonePerFilaNumero(datiNuovi.getFila(), datiNuovi.getNumero());

                if (!occupanti.isEmpty() && !occupanti.getFirst().getId().equals(datiNuovi.getId())) { // Se esiste
                    // Se l'ombrellone esiste e l'id coincide
                    return false; // Il posto è effettivamente occupato da un altro, blocco tutto
                }

                // Il controllo è andato a buon fine -> posso chiamare gestore persistenza
                gestorePersistenza.aggiornaCampi(
                        Ombrellone.class,
                        datiNuovi.getId(),
                        Map.of(
                                "fila", datiNuovi.getFila(),
                                "numero", datiNuovi.getNumero(),
                                "posizione", datiNuovi.getPosizione()
                        )
                );
            }

            return true;

        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }

    //ACAMPA
    public List<Ombrellone> getDisponibilitaOmbrelloni(String dataSelezionata) {

        return gestorePersistenza.cercaDisponibiliConLimite(
                Ombrellone.class,
                Prenotazione.class,
                "ombrellone",
                Map.of(
                        "data", dataSelezionata,
                        "statoPrenotazione", StatoPrenotazione.CONFERMATA
                ),
                1,
                false
        );
    }

    //DCC
    public Ombrellone cercaOmbrellonePerId(Long id) {
        return gestorePersistenza.trovaPerId(Ombrellone.class, id);
    }
    //DCC
    /**
     * Verifica se un singolo ombrellone è disponibile per la data indicata,
     * riutilizzando la lista globale degli ombrelloni liberi.
     *
     * @param data       La data da verificare.
     * @param ombrellone L'entità Ombrellone da controllare.
     * @return true se l'ombrellone è libero, false se risulta già prenotato.
     */
    public boolean isOmbrelloneDisponibile(String data, Ombrellone ombrellone) {

        return this.getDisponibilitaOmbrelloni(data).contains(ombrellone);
    }

}