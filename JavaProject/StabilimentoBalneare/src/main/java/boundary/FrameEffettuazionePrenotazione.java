package boundary;

import controller.ControllerPrenotazioni;
import controller.ControllerOmbrelloni;
import controller.ControllerServizi;
import controller.ControllerUtenti;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FrameEffettuazionePrenotazione {

    private JPanel effettuaPrenotazionePanel;
    private JButton calcolaPreventivoButton;
    private JTable ombrelloneTable;
    private JLabel stagionelbl;
    private JLabel datalbl;
    private JPanel serviziPanel;

    private String dataPrenotazione;
    private String formatoData;
    private String periodo;
    private float tariffaOmbrellone;
    private String usernameClienteLoggato;

    private Long idOmbrelloneSelezionato;
    private Map<JCheckBox, Float> mappaCheckBoxPrezzi = new HashMap<>();

    public FrameEffettuazionePrenotazione() {
    }


    private void inizializzaDatiPeriodo() {
        this.periodo = ControllerPrenotazioni.getPeriodoPerData(formatoData, dataPrenotazione);
        datalbl.setText("Data prenotazione: " + dataPrenotazione);

        if (this.periodo.equals("ALTA_STAGIONE")) {
            stagionelbl.setText("Stagione selezionata: Alta");
        } else if (this.periodo.equals("BASSA_STAGIONE")) {
            stagionelbl.setText("Stagione selezionata: Bassa");
        } else {
            stagionelbl.setText("Stagione selezionata: ERROR");
        }
    }

    private void popolaSezioneOmbrellone() {
        String[] dati = ControllerOmbrelloni.caricaOmbrellone(idOmbrelloneSelezionato);

        String[] colonne = {"Fila", "Numero", "Posizione", "Tariffa"};
        DefaultTableModel model = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (dati != null) {

            String filaOmbrellone   = dati[1];
            String numeroOmbrellone = dati[2];
            String posizione        = dati[3];

            tariffaOmbrellone = ControllerOmbrelloni.getTariffaPosizionePerPeriodo(periodo, posizione);

            model.addRow(new String[]{
                    filaOmbrellone,
                    numeroOmbrellone,
                    posizione,
                    "€ " + String.format("%.2f", tariffaOmbrellone)
            });

        } else {
            // Se dati è null, significa che l'ombrellone non è nel db
            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                    "Attenzione: L'ombrellone selezionato non esiste nel database.",
                    "Ombrellone Non Trovato",
                    JOptionPane.WARNING_MESSAGE);

            SwingUtilities.invokeLater(() -> {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(effettuaPrenotazionePanel);
                if (frame != null) frame.dispose();
            });
        }

        ombrelloneTable.setModel(model);
        ombrelloneTable.getTableHeader().setReorderingAllowed(false);
    }

    private void popolaSezioneServizi() {
        serviziPanel.setLayout(new BoxLayout(serviziPanel, BoxLayout.Y_AXIS));

        List<String[]> serviziDisponibili = ControllerServizi.getServiziDisponibili(dataPrenotazione);

        List<String> nomiServizi = new ArrayList<>();
        for (String[] servizio : serviziDisponibili) {
            nomiServizi.add(servizio[1]);
        }

        Map<String, Float> mappaTariffe = ControllerServizi.getTariffeServiziPerPeriodo(nomiServizi, periodo);

        for (String[] datiServizio : serviziDisponibili) {
            String idServizio   = datiServizio[0];
            String nomeServizio = datiServizio[1];
            Float prezzo        = mappaTariffe.getOrDefault(nomeServizio, 0.0f);

            JPanel rigaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            rigaPanel.setOpaque(false);

            JCheckBox cb = new JCheckBox(nomeServizio);
            cb.setActionCommand(idServizio);

            JLabel prezzoLabel = new JLabel(" - € " + String.format("%.2f", prezzo));
            rigaPanel.add(cb);
            rigaPanel.add(prezzoLabel);
            serviziPanel.add(rigaPanel);

            mappaCheckBoxPrezzi.put(cb, prezzo);
        }
    }

    private void impostaListener() {
        calcolaPreventivoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double totale = tariffaOmbrellone;
                List<Long> idServiziSelezionati = new ArrayList<>();

                // Calcolo il totale e recupero gli ID dei servizi selezionati
                for (Map.Entry<JCheckBox, Float> entry : mappaCheckBoxPrezzi.entrySet()) {
                    JCheckBox cb = entry.getKey();
                    if (cb.isSelected()) {
                        totale += entry.getValue();
                        idServiziSelezionati.add(Long.parseLong(cb.getActionCommand()));
                    }
                }

                String messaggio = "Il totale del preventivo è: € " + String.format("%.2f", totale)
                        + "\nVuoi confermare la prenotazione?";

                int scelta = JOptionPane.showConfirmDialog(
                        effettuaPrenotazionePanel,
                        messaggio,
                        "Conferma Prenotazione",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );


                if (scelta == JOptionPane.YES_OPTION) {

                    // se l'utente clicca si chiamo effettuaPrenotazione e controllo l'esito
                    int esito = ControllerPrenotazioni.effettuaPrenotazione(
                            idOmbrelloneSelezionato, dataPrenotazione, idServiziSelezionati, usernameClienteLoggato);

                    if (esito == ControllerPrenotazioni.SUCCESSO) {

                        JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                                "Prenotazione salvata con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);

                        SistemaNotifiche notifiche = SistemaNotificheJakarta.getInstance();
                        boolean emailInviata = notifiche.inviaConfermaPrenotazione(
                                ControllerUtenti.getEmailClientePerUsername(usernameClienteLoggato), dataPrenotazione, totale);

                        if (!emailInviata) {
                            JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                                    "Prenotazione salvata, ma l'invio della email di conferma è fallito.",
                                    "Avviso Notifica",
                                    JOptionPane.WARNING_MESSAGE);
                        }

                        JFrame frameTop = (JFrame) SwingUtilities.getWindowAncestor(effettuaPrenotazionePanel);
                        if (frameTop != null) frameTop.dispose();

                    } else {
                        JOptionPane.showMessageDialog(effettuaPrenotazionePanel,
                                "Si è verificato un errore: " + esito, "Errore", JOptionPane.ERROR_MESSAGE);

                    }


                } else {
                    // se l'utente clicca no
                    JOptionPane.showMessageDialog(effettuaPrenotazionePanel, "Prenotazione annullata.");
                }
            }
        });
    }


    public JFrame apriFormEffettuazionePrenotazione(long idOmbrellone, String dataPrenotazione,
                                                    String formatoData, String username) {
        this.idOmbrelloneSelezionato = idOmbrellone;
        this.dataPrenotazione = dataPrenotazione;
        this.formatoData = formatoData;
        this.usernameClienteLoggato = username;

        inizializzaDatiPeriodo();
        popolaSezioneOmbrellone();
        popolaSezioneServizi();
        impostaListener();

        JFrame frame = new JFrame("Effettua una Prenotazione");
        frame.setContentPane(effettuaPrenotazionePanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }
}