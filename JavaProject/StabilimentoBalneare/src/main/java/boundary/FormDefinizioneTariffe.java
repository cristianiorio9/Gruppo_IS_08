package boundary;

import java.util.List;
import controller.ControllerOmbrelloni;
import controller.ControllerServizi;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Map;

public class FormDefinizioneTariffe {

    private JPanel defTariffePanel;
    private JLabel esito;
    private JTabbedPane tabTariffe;
    private JButton impostaTariffaButton;
    private JComboBox<String> periodoComboBox;

    private JTable tableServizi;
    private JTable tablePosizioni;

    public static final int TAB_SERVIZI = 0;
    public static final int TAB_POSIZIONI = 1;


    //Inizializzazione dei componenti grafici in base alla selezione del gestore tra servizi e posizioni,
    // aggiornando le tabelle in base al periodo selezionato, e richiama i due metodi
    // che si occupano della gestione delle tariffe dei servizi e delle posizioni
    public FormDefinizioneTariffe() {

        inizializzaComponenti();

        // Listener: Quando il gestore cambia periodo nella ComboBox, aggiorna le tabelle
        periodoComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                aggiornaTabelle();
                esito.setText(""); // Pulisce l'esito precedente
            }
        });

        impostaTariffaButton.addActionListener(e -> {
            int tabSelezionato = tabTariffe.getSelectedIndex();

            if (tabSelezionato == TAB_SERVIZI) {
                gestisciImpostazioneTariffaServizi();
            } else if (tabSelezionato == TAB_POSIZIONI) {
                gestisciImpostazioneTariffaPosizioni();
            }
        });
    }

    //Metodo che svuota e ripopola la comboBox dei periodi tramite il Controller e aggiorna le tabelle
    private void inizializzaComponenti() {
        periodoComboBox.removeAllItems();

        for (String p : ControllerServizi.getPeriodiServizi()) {
            periodoComboBox.addItem(p);
        }

        // Popola le tabelle per la prima volta
        aggiornaTabelle();
    }

    // Metodo che riempie le tabelle con i prezzi dal DB in base al periodo
    // recupera i dati dai controller e li converte in matrici per i modelli delle JTable
    private void aggiornaTabelle() {
        String periodo = (String) periodoComboBox.getSelectedItem();
        if (periodo == null) return;

        // 1. Aggiorna Tabella Servizi
        List<String> servizi = ControllerServizi.getServizi();
        Map<String, Float> datiServizi = ControllerServizi.getTariffeServiziPerPeriodo(servizi, periodo);
        String[] colonneServizi = {"Servizio", "Prezzo Attuale"};

        // Convertiamo la mappa in una matrice bidimensionale (richiesta da DefaultTableModel)
        Object[][] matriceServizi = new Object[datiServizi.size()][2];
        int i = 0;
        for (Map.Entry<String, Float> entry : datiServizi.entrySet()) {
            matriceServizi[i][0] = entry.getKey();
            // Se il prezzo è 0.0f impostiamo "non impostata"
            matriceServizi[i][1] = (entry.getValue() == 0.0f) ? "non impostata" : entry.getValue();
            i++;
        }

        tableServizi.setModel(new DefaultTableModel(matriceServizi, colonneServizi) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rende la tabella non editabile direttamente
            }
        });

        // 2. Aggiorna Tabella Posizione
        String[] posizioni = ControllerOmbrelloni.getPosizioni();
        Map<String, Float> datiPosizioni = ControllerOmbrelloni.getTutteTariffePosizioniPerPeriodo(periodo, posizioni);
        String[] colonnePosizioni = {"Posizione", "Prezzo Attuale"};

        // Convertiamo la mappa in una matrice bidimensionale
        Object[][] matricePosizioni = new Object[datiPosizioni.size()][2];
        int j = 0;
        for (Map.Entry<String, Float> entry : datiPosizioni.entrySet()) {
            matricePosizioni[j][0] = entry.getKey();
            // Se il prezzo è 0.0 impostiamo "non impostata"
            matricePosizioni[j][1] = (entry.getValue() == 0.0f) ? "Non impostata" : entry.getValue();
            j++;
        }

        tablePosizioni.setModel(new DefaultTableModel(matricePosizioni, colonnePosizioni) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
    }

    // Gestisce l’impostazione della tariffa per un servizio selezionato:
    // valida la selezione del servizio e del periodo, richiede il nuovo prezzo, verifica il formato,
    // salva la tariffa tramite il controller e aggiorna la tabella mostrando
    // l’esito dell’operazione.

    private void gestisciImpostazioneTariffaServizi() {
        try {
            int selectedRow = tableServizi.getSelectedRow();
            if (selectedRow == -1) {
                esito.setText("Seleziona un servizio!");
                esito.setForeground(Color.RED);
                JOptionPane.showMessageDialog(defTariffePanel,
                        "Seleziona un servizio dalla tabella!",
                        "Nessuna Selezione",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String servizio = tableServizi.getValueAt(selectedRow, 0).toString();
            String periodo = (String) periodoComboBox.getSelectedItem();
            String prezzoAttuale = tableServizi.getValueAt(selectedRow, 1).toString();

            String prezzoStr = JOptionPane.showInputDialog(
                    "Inserisci il nuovo prezzo per il servizio " + servizio + " (" + periodo + "):\n" +
                            "Attuale: " + prezzoAttuale
            );

            if (prezzoStr == null || prezzoStr.trim().isEmpty()) {
                return;
            }

            String prezzoNormalizzato = prezzoStr.replace(",", ".");

            if (!prezzoNormalizzato.matches("\\d+(\\.\\d{1,2})?")) {
                esito.setText("Formato prezzo non valido (es. 25,00 o 25.00)");
                esito.setForeground(Color.RED);
                JOptionPane.showMessageDialog(defTariffePanel,
                        "Formato prezzo non valido (es. 25,00 o 25.00).",
                        "Errore Formato",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            float prezzo = Float.parseFloat(prezzoNormalizzato);

            boolean esito = ControllerServizi.definisciTariffaServizio(servizio, periodo, prezzo);

            if (esito) {
                this.esito.setText("Tariffa impostata con successo per il servizio " + servizio + " (" + periodo + ") (€" + prezzo + ")");
                this.esito.setForeground(Color.GREEN);
                aggiornaTabelle();
            } else {
                this.esito.setText("Errore durante il salvataggio della tariffa.");
                this.esito.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            esito.setText("Errore durante l'impostazione della tariffa.");
            esito.setForeground(Color.RED);
        }
    }

    // Gestisce l’impostazione della tariffa per una posizione selezionata:
    // verifica la selezione della posizione e del periodo, richiede il nuovo prezzo, controlla il formato,
    // salva la tariffa tramite il controller e aggiorna la tabella mostrando
    // l’esito dell’operazione.

    private void gestisciImpostazioneTariffaPosizioni() {
        try {
            int selectedRow = tablePosizioni.getSelectedRow();
            if (selectedRow == -1) {
                esito.setText("Seleziona una posizione!");
                esito.setForeground(Color.RED);
                JOptionPane.showMessageDialog(defTariffePanel,
                        "Seleziona una posizione dalla tabella!",
                        "Nessuna Selezione",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String posizione = tablePosizioni.getValueAt(selectedRow, 0).toString();
            String periodo = (String) periodoComboBox.getSelectedItem();
            String prezzoAttuale = tablePosizioni.getValueAt(selectedRow, 1).toString();

            String prezzoStr = JOptionPane.showInputDialog(
                    "Inserisci il nuovo prezzo per la posizione " + posizione + " (" + periodo + "):\n" +
                            "Attuale: " + prezzoAttuale
            );

            if (prezzoStr == null || prezzoStr.trim().isEmpty()) {
                return;
            }

            String prezzoNormalizzato = prezzoStr.replace(",", ".");

            if (!prezzoNormalizzato.matches("\\d+(\\.\\d{1,2})?")) {
                esito.setText("Formato prezzo non valido (es. 25,00 o 25.00)");
                esito.setForeground(Color.RED);
                JOptionPane.showMessageDialog(defTariffePanel,
                        "Formato prezzo non valido (es. 25,00 o 25.00).",
                        "Errore Formato",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            float prezzo = Float.parseFloat(prezzoNormalizzato);

            boolean esito = ControllerOmbrelloni.definisciTariffaPosizione(posizione, periodo, prezzo);

            if (esito) {
                this.esito.setText("Tariffa impostata con successo per la posizione " + posizione + " (€" + prezzo + ")");
                this.esito.setForeground(Color.GREEN);
                aggiornaTabelle();
            } else {
                this.esito.setText("Errore durante il salvataggio della tariffa.");
                this.esito.setForeground(Color.RED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            esito.setText("Errore durante l'impostazione della tariffa.");
            esito.setForeground(Color.RED);
        }
    }

    public JFrame apriFormDefinizioneTariffe() {
        JFrame frame = new JFrame("Benvenuto nella pagina di Definizione Tariffe!");
        frame.setContentPane(this.defTariffePanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }
}