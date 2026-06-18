package boundary;

import controller.ControllerOmbrelloni;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.List;

public class FormConfigurazioneOmbrelloni {
    private JPanel configOmbrelloniPanel;
    private JTable table1;
    private JSpinner filaInserita;
    private JSpinner numeroInserito;
    private JComboBox<String> comboBox1;
    private JButton aggiungiOmbrelloneButton;
    private JButton rimuoviOmbrelloneButton;
    private JButton salvaConfigurazioneButton;

    public FormConfigurazioneOmbrelloni(){

        //Impostazioni spinner (fila e numero): valore iniziale 1, valore minimo 1, massimo 1000, step 1
        filaInserita.setModel(new SpinnerNumberModel(1, 1, 1000, 1));
        numeroInserito.setModel(new SpinnerNumberModel(1, 1, 1000, 1));

        caricaDatiTabella();

        caricaDatiComboBox();

        // Click del bottone aggiungi ombrellone
        aggiungiOmbrelloneButton.addActionListener(_ -> {

            // Prelevo i dati dagli spinner e dalla combobox posizione
            int fila = (int) filaInserita.getValue();
            int numero = (int) numeroInserito.getValue();
            String posizione = (String) comboBox1.getSelectedItem();

            // Controllo che la posizione sia stata selezionata
            if (posizione == null || posizione.isEmpty()) {
                JOptionPane.showMessageDialog(configOmbrelloniPanel,
                        "Per favore, seleziona una posizione valida.",
                        "Errore Inserimento",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mando i dati in ingresso al controller
            boolean successo = ControllerOmbrelloni.aggiungiNuovoOmbrellone(fila, numero, posizione);

            if (successo) { // Se l'aggiunta va a buon fine mostro messaggio di successo

                JOptionPane.showMessageDialog(configOmbrelloniPanel, "Ombrellone aggiunto con successo!");
                // ricarico la tabella per mostrare il nuovo ombrellone
                caricaDatiTabella();

            } else { // altrimenti messaggio di errore

                JOptionPane.showMessageDialog(configOmbrelloniPanel,
                        "Impossibile aggiungere l'ombrellone. Verifica che non esista già.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Click del bottone rimuovi ombrellone
        rimuoviOmbrelloneButton.addActionListener(_ -> {

            // Prendo la riga selezionata dal gestore
            int rigaSelezionata = table1.getSelectedRow();

            if (rigaSelezionata == -1) { // Se clicca il bottone senza aver selezionato una riga -> messaggio di errore

                JOptionPane.showMessageDialog(configOmbrelloniPanel,
                        "Seleziona un ombrellone dalla tabella prima di procedere con la rimozione.",
                        "Nessuna Selezione",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Poichè non mostro l'id, converto l'indice della riga selezionta nell'indice del modello
            int rigaModel = table1.convertRowIndexToModel(rigaSelezionata);

            // Salvo le informazioni dell'ombrellone selezionato
            String idStringa = table1.getModel().getValueAt(rigaModel, 0).toString();
            long idOmbrellone = Long.parseLong(idStringa);
            String filaSelezionata = table1.getModel().getValueAt(rigaModel, 1).toString();
            String numeroSelezionato = table1.getModel().getValueAt(rigaModel, 2).toString();

            // Chiedo conferma rimozione (SI/NO)
            int conferma = JOptionPane.showConfirmDialog(configOmbrelloniPanel,
                    "Sei sicuro di voler rimuovere l'ombrellone in Fila " + filaSelezionata + ", Numero " + numeroSelezionato + "?",
                    "Conferma Eliminazione",
                    JOptionPane.YES_NO_OPTION);

            if (conferma == JOptionPane.YES_OPTION) { // Se clicca si chiamo controller passandogli l'id

                boolean successo = ControllerOmbrelloni.rimuoviOmbrellone(idOmbrellone);

                if (successo) { // Se viene rimosso correttamente -> messaggio successo e aggiorno tabella

                    JOptionPane.showMessageDialog(configOmbrelloniPanel, "Ombrellone rimosso con successo!");
                    caricaDatiTabella();

                } else { // altrimenti messaggio di errore e non aggiorno
                    JOptionPane.showMessageDialog(configOmbrelloniPanel,
                            "Errore durante la rimozione dell'ombrellone.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Click del bottone salva configurazione
        salvaConfigurazioneButton.addActionListener(_ -> {

            if (table1.isEditing()) {
                table1.getCellEditor().stopCellEditing();
            }

            DefaultTableModel model = (DefaultTableModel) table1.getModel();
            int numeroRighe = model.getRowCount();

            java.util.ArrayList<String[]> datiTabella = new java.util.ArrayList<>();

            for (int i = 0; i < numeroRighe; i++) {

                // Leggo i dati della tabella rispettando l'ordine del modello
                String id = model.getValueAt(i, 0).toString();
                String fila = model.getValueAt(i, 1).toString();
                String numero = model.getValueAt(i, 2).toString();
                String posizione = model.getValueAt(i, 3).toString();

                datiTabella.add(new String[]{id, fila, numero, posizione});
            }

            // Chiamo il metodo del controller ombrelloni passandogli tutta la tabella
            boolean successo = ControllerOmbrelloni.salvaConfigurazioneComplessiva(datiTabella);

            if (successo) {

                JOptionPane.showMessageDialog(configOmbrelloniPanel, "Configurazione salvata con successo nel sistema!");
                caricaDatiTabella(); // Aggiorno per vedere le modifiche effettuate

            } else { // Se il gestore tenta di salvare due ombrelloni con stessa fila - numero ->errore

                JOptionPane.showMessageDialog(configOmbrelloniPanel,
                        "Errore: hai inserito coordinate (Fila/Numero) già occupate da un altro ombrellone.",
                        "Errore di Consistenza",
                        JOptionPane.ERROR_MESSAGE);
                caricaDatiTabella(); // Aggiorno per annullare le modifiche non consentite
            }
        });

    }

    // Metodo dedicato per il popolamento della tabella.
    private void caricaDatiTabella() {

        // Prendo dal controller gli ombrelloni
        List<String[]> righe = ControllerOmbrelloni.getTuttiOmbrelloni();

        DefaultTableModel model = getDefaultTableModel();

        for (String[] riga : righe) {
            model.addRow(riga);
        }

        if (table1 != null) {
            table1.setModel(model);

            // Nascondo l'id dell'ombrellone (indice 0)
            table1.removeColumn(table1.getColumnModel().getColumn(0));

            //Istanzio dinamicamente la combobox presente in tutte le righe nella colonna Posizione
            String[] posizioni = ControllerOmbrelloni.getPosizioni();
            JComboBox<String> comboBoxPosizione = new JComboBox<>(posizioni);
            TableColumn colonnaPosizione = table1.getColumnModel().getColumn(2);
            colonnaPosizione.setCellEditor(new DefaultCellEditor(comboBoxPosizione));
        }
    }

    // In questo metodo configuro e restituisco il modello di base per la mia tabella
    private static DefaultTableModel getDefaultTableModel() {
        // Definisco i nomi delle colonne che voglio mostrare.
        // Ho inserito "ID_Nascosto" per tenere traccia del record nel database senza necessariamente mostrarlo/farlo toccare all'utente.
        String[] colonne = {
                "ID_Nascosto",
                "Fila",
                "Numero",
                "Posizione"
        };

        // Ritorno un nuovo DefaultTableModel inizializzato con le mie colonne e 0 righe di partenza
        return new DefaultTableModel(colonne, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                // Sovrascrivo questo metodo per assicurarmi di bloccare la modifica della prima colonna (l'ID_Nascosto, indice 0).
                // Tutte le altre colonne (Fila, Numero, Posizione) le lascio modificabili.
                return column != 0;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                // Qui intercetto il momento in cui l'utente cerca di inserire un nuovo valore in una cella.
                // Controllo se sta modificando la colonna 1 ("Fila") o la colonna 2 ("Numero").
                if (column == 1 || column == 2) {
                    String input = aValue.toString();

                    // Utilizzo un'espressione regolare ("\\d+") per verificare che l'input contenga esclusivamente numeri interi.
                    if (!input.matches("\\d+")) {

                        // Se il gestore ha inserito lettere o spazi, blocco l'inserimento e gli mostro un popup di avviso.
                        JOptionPane.showMessageDialog(null,
                                "Formato non valido! Inserisci solo numeri interi senza spazi.",
                                "Errore di inserimento",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Se l'input è un numero, controllo che sia maggiore o uguale a 1.
                    int valoreInserito = Integer.parseInt(input);
                    if (valoreInserito < 1) {
                        JOptionPane.showMessageDialog(null,
                                "Valore non valido! La fila e il numero devono essere maggiori o uguali a 1.",
                                "Errore di inserimento",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                }
                // Se i controlli passano (o se sta modificando altre colonne), procedo con il normale salvataggio del dato.
                super.setValueAt(aValue, row, column);
            }
        };
    }

    public void caricaDatiComboBox() {

        // Chiedo le posizioni al controller
        String[] opzioni = ControllerOmbrelloni.getPosizioni();

        // Faccio un controllo di sicurezza per accertarmi che l'elemento grafico esista
        if (comboBox1 != null) {

            // Svuoto la combobox rimuovendo tutti gli elementi precedenti.
            comboBox1.removeAllItems();

            // Aggiungo gli elementi alla combobox
            for (String opzione : opzioni) {
                comboBox1.addItem(opzione);
            }

            // Infine, imposto l'indice selezionato a -1, così all'apertura della schermata non appare la combobox
            comboBox1.setSelectedIndex(-1);
        }
    }

    public JFrame apriFormConfigurazioneOmbrelloni() {

        JFrame frame = new JFrame("Benvenuto nella pagina di Configurazione Ombrelloni!");
        FormConfigurazioneOmbrelloni ConfigOmbrelloniForm = new FormConfigurazioneOmbrelloni();

        frame.setContentPane(ConfigOmbrelloniForm.configOmbrelloniPanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 1200);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }
}