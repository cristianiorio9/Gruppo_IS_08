package setup;

import boundary.FormAutenticazione;
import database.GestorePersistenza;
import database.JpaUtil;

import javax.swing.*;

/*
 * Questa classe prepara il database con i dati iniziali dell'applicazione.
 *
 * Può essere usata in due modi:
 *
 * 1. solo per inizializzare il database;
 * 2. per inizializzare il database e poi aprire la GUI.
 *
 * La seconda modalità è utile quando nel persistence.xml usiamo:
 *
 * hibernate.hbm2ddl.auto = create
 *
 * perché in quel caso Hibernate ricrea le tabelle ogni volta che
 * l'applicazione viene avviata. Per evitare di perdere i dati appena
 * inseriti, inizializzazione e apertura della GUI devono avvenire
 * nello stesso avvio dell'applicazione.
 */
public class MainSetup {

    public static void main(String[] args) {

        /*
         * Avvia Hibernate.
         *
         * Con hibernate.hbm2ddl.auto = update, Hibernate crea o aggiorna
         * le tabelle senza cancellare i dati già presenti.
         *
         * Con hibernate.hbm2ddl.auto = create, Hibernate elimina e ricrea
         * le tabelle a ogni avvio dell'applicazione.
         */
        JpaUtil.getInstance();

        /*
         * Inserisce i dati iniziali nel database.
         */
        GestorePersistenza gestore = new GestorePersistenza();
        DatiTestStabilimento.popola(gestore);



        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                // Sostituisci MainFrame con il nome corretto del tuo form di avvio se necessario
                FormAutenticazione mainFrame = new FormAutenticazione();

                mainFrame.apriFormAutenticazione(); // Assicurati che questo metodo esista nella tua boundary
            }
        });

        System.out.println("Database inizializzato e GUI avviata.");
    }
}