package setup;

import database.GestorePersistenza;
import entity.*;

public class DatiTestStabilimento {

    private DatiTestStabilimento() {
        // Classe di utilità: non deve essere istanziata.
    }

    /*
     * Metodo per popolare il database con una configurazione iniziale
     * di ombrelloni, utenti, servizi e prenotazioni per lo stabilimento.
     */
    public static void popola(GestorePersistenza gestore) {

        // --- 1. UTENTI ---
        Cliente clienteTest = new Cliente("scott8", "scott8", "Scott", "McTominay", "scott.mctominay@gmail.com", "888888");
        gestore.salva(clienteTest);

        Gestore gestoreTest = new Gestore("antonio25", "Conte2025", "Antonio", "Conte", "antonio.conte@gmail.com", "12345678");
        gestore.salva(gestoreTest);

        // --- 2. OMBRELLONI ---
        Ombrellone o1 = new Ombrellone(1, 1, Posizione.PRIMA_FILA);
        Ombrellone o2 = new Ombrellone(1, 2, Posizione.PRIMA_FILA);

        gestore.salva(o1);
        gestore.salva(o2);
        gestore.salva(new Ombrellone(1, 3, Posizione.PRIMA_FILA));

        gestore.salva(new Ombrellone(2, 1, Posizione.FILA_INTERMEDIA));
        gestore.salva(new Ombrellone(2, 2, Posizione.FILA_INTERMEDIA));
        gestore.salva(new Ombrellone(2, 3, Posizione.FILA_INTERMEDIA));

        gestore.salva(new Ombrellone(3, 1, Posizione.ULTIMA_FILA));
        gestore.salva(new Ombrellone(3, 2, Posizione.ULTIMA_FILA));

        // --- 3. SERVIZI AGGIUNTIVI ---
        ServizioAggiuntivo lettino = new ServizioAggiuntivo("Lettino supplementare", 5);
        lettino.setServizio(TipoServizio.LETTINO_EXTRA);
        gestore.salva(lettino);

        ServizioAggiuntivo cabina = new ServizioAggiuntivo("Cabina privata per cambio e doccia", 1);
        cabina.setServizio(TipoServizio.CABINA);
        gestore.salva(cabina);

        ServizioAggiuntivo parcheggio = new ServizioAggiuntivo("Posto auto riservato all'ombra", 10);
        parcheggio.setServizio(TipoServizio.PARCHEGGIO);
        gestore.salva(parcheggio);

        // --- 4. PRENOTAZIONI DI TEST ---
        // Recuperiamo un valore di default per lo stato della prenotazione (es. IN_ATTESA o CONFERMATA)
        StatoPrenotazione statoDefault = StatoPrenotazione.values()[0];

        // Prenotazione 1: Ombrellone 1 il 12/10/2026 con 1 Lettino e 1 Cabina
        Prenotazione p1 = new Prenotazione(null, "12/10/2026", statoDefault, clienteTest, o1);
        p1.getServiziAggiuntivi().add(lettino);
        p1.getServiziAggiuntivi().add(cabina);
        gestore.salva(p1);

        // Prenotazione 2: Ombrellone 2 il 12/10/2026 con 1 Lettino e 1 Parcheggio
        Prenotazione p2 = new Prenotazione(null, "12/10/2026", statoDefault, clienteTest, o2);
        p2.getServiziAggiuntivi().add(lettino);
        p2.getServiziAggiuntivi().add(parcheggio);
        gestore.salva(p2);

        // -------------------------------
        // 5. TARIFFE SERVIZI
        // -------------------------------
        gestore.salva(new TariffaServizio(5.0f, Periodo.ALTA_STAGIONE, TipoServizio.PARCHEGGIO));
        gestore.salva(new TariffaServizio(3.0f, Periodo.BASSA_STAGIONE, TipoServizio.PARCHEGGIO));

        gestore.salva(new TariffaServizio(12.0f, Periodo.ALTA_STAGIONE, TipoServizio.CABINA));

        gestore.salva(new TariffaServizio(7.0f, Periodo.ALTA_STAGIONE, TipoServizio.LETTINO_EXTRA));
        gestore.salva(new TariffaServizio(5.0f, Periodo.BASSA_STAGIONE, TipoServizio.LETTINO_EXTRA));


        // -------------------------------
        // 6. TARIFFE POSTAZIONI
        // -------------------------------
        gestore.salva(new TariffaPosizione(25.0f, Periodo.ALTA_STAGIONE, Posizione.PRIMA_FILA));
        gestore.salva(new TariffaPosizione(18.0f, Periodo.BASSA_STAGIONE, Posizione.PRIMA_FILA));

        gestore.salva(new TariffaPosizione(12.0f, Periodo.ALTA_STAGIONE, Posizione.ULTIMA_FILA));
        gestore.salva(new TariffaPosizione(8.0f, Periodo.BASSA_STAGIONE, Posizione.ULTIMA_FILA));

        System.out.println("✅ Setup iniziale completato. Dati e prenotazioni inseriti con successo nel DB!");
    }
}