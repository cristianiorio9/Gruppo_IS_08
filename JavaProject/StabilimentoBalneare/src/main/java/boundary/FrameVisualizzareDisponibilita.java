package boundary;

import com.toedter.calendar.JDateChooser;
import controller.ControllerOmbrelloni;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameVisualizzareDisponibilita {
    private JPanel disponibilitaPane;
    private JButton visualizzaButton;
    private JDateChooser JDateChooser1;
    private JPanel mappaPanel;

    private JFrame formEffettuazionePrenotazione;
    private String usernameClienteLoggato;

    // Salvo il nome utente del cliente attualmente loggato
    public void caricaDati(String username) {
        this.usernameClienteLoggato = username;
    }

    public FrameVisualizzareDisponibilita() {
        visualizzaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Prelevo la data selezionata dall'utente nel calendario
                java.util.Date dataSelezionata = JDateChooser1.getDate();

                if (dataSelezionata != null) {
                    // Formatto la data nel pattern "dd/MM/yyyy" per passarla al controller
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    String formatoData = JDateChooser1.getDateFormatString();
                    String dataCorretta = sdf.format(dataSelezionata);

                    // Genero la mappa degli ombrelloni con la data scelta
                    generaMappa(dataCorretta, formatoData);
                } else {
                    // Avviso l'utente se ha premuto il bottone senza selezionare una data
                    JOptionPane.showMessageDialog(null, "Seleziona una data prima di procedere!");
                }
            }
        });
    }

    // Inizializzo e mostro il frame, restituendo il riferimento al chiamante
    public JFrame apriFrameVisualizzareDisponibilita() {
        JFrame frame = new JFrame("Frame Visualizzare Disponibilità");
        frame.setContentPane(disponibilitaPane);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }

    private void generaMappa(String data, String formatoData) {
        // Recupero le dimensioni massime della spiaggia (righe e colonne)
        int[] dimensioni = ControllerOmbrelloni.getDimensioni();
        int maxFila = dimensioni[0];
        int maxNumero = dimensioni[1];

        // Se non ci sono ombrelloni configurati, interrompo e avviso l'utente
        if (maxFila == 0 || maxNumero == 0) {
            JOptionPane.showMessageDialog(null, "Nessun ombrellone configurato per lo stabilimento.");
            mappaPanel.removeAll();
            mappaPanel.revalidate();
            mappaPanel.repaint();
            return;
        }

        // Richiedo al controller lo stato di tutti gli ombrelloni per la data scelta
        Map<String, List<String[]>> statoOmbrelloni = ControllerOmbrelloni.visualizzaDisponibilita(data);
        List<String[]> disponibili = statoOmbrelloni.get("disponibili");
        List<String[]> prenotati = statoOmbrelloni.get("prenotati");

        // Costruisco due mappe indicizzate per coordinate ("fila-numero") per consultazione rapida
        Map<String, String[]> mappaDisponibili = new HashMap<>();
        if (disponibili != null) {
            for (String[] o : disponibili) {
                mappaDisponibili.put(o[1] + "-" + o[2], o);
            }
        }

        Map<String, String[]> mappaPrenotati = new HashMap<>();
        if (prenotati != null) {
            for (String[] o : prenotati) {
                mappaPrenotati.put(o[1] + "-" + o[2], o);
            }
        }

        // Pulisco il pannello precedente e imposto il layout a griglia
        mappaPanel.removeAll();
        mappaPanel.setLayout(new GridLayout(maxFila, maxNumero, 10, 10));

        // Scorro tutte le celle della griglia e creo il bottone corrispondente
        for (int riga = 1; riga <= maxFila; riga++) {
            for (int colonna = 1; colonna <= maxNumero; colonna++) {

                String chiaveCoordinate = riga + "-" + colonna;

                if (mappaPrenotati.containsKey(chiaveCoordinate)) {
                    // Ombrellone già prenotato: mostro un bottone rosso non cliccabile
                    String[] dati = mappaPrenotati.get(chiaveCoordinate);
                    JButton btnOmbrellone = new JButton("F" + dati[1] + " - N" + dati[2]);
                    btnOmbrellone.setBackground(Color.RED);
                    btnOmbrellone.setEnabled(false);
                    mappaPanel.add(btnOmbrellone);

                } else if (mappaDisponibili.containsKey(chiaveCoordinate)) {
                    // Ombrellone disponibile: mostro un bottone verde cliccabile
                    String[] dati = mappaDisponibili.get(chiaveCoordinate);

                    // --- NOVITÀ: Estraiamo l'ID dall'array ---
                    final Long idOmbrelloneSelezionato = Long.parseLong(dati[0]);

                    final int filaSelezionata = Integer.parseInt(dati[1]);
                    final int numeroSelezionato = Integer.parseInt(dati[2]);

                    JButton btnOmbrellone = new JButton("F" + filaSelezionata + " - N" + numeroSelezionato);
                    btnOmbrellone.setBackground(Color.GREEN);

                    btnOmbrellone.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Se il form di prenotazione non è già aperto, lo apro passando l'ID dell'ombrellone
                            if (formEffettuazionePrenotazione == null || !formEffettuazionePrenotazione.isDisplayable()) {
                                FrameEffettuazionePrenotazione formIstanza = new FrameEffettuazionePrenotazione();


                                formEffettuazionePrenotazione = formIstanza.apriFormEffettuazionePrenotazione(
                                        idOmbrelloneSelezionato, data, formatoData, usernameClienteLoggato);

                                // Aggiorno la mappa alla chiusura del form, per riflettere eventuali nuove prenotazioni
                                formEffettuazionePrenotazione.addWindowListener(new java.awt.event.WindowAdapter() {
                                    @Override
                                    public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                                        generaMappa(data, formatoData);
                                    }
                                });

                                formEffettuazionePrenotazione.setLocationRelativeTo(null);
                                formEffettuazionePrenotazione.setVisible(true);

                            } else {
                                // Il form è già aperto: lo porto in primo piano
                                formEffettuazionePrenotazione.toFront();
                                formEffettuazionePrenotazione.requestFocus();
                            }
                        }
                    });
                    mappaPanel.add(btnOmbrellone);

                } else {
                    mappaPanel.add(new JLabel());
                }
            }
        }

        // Aggiorno il pannello per mostrare la nuova mappa
        mappaPanel.revalidate();
        mappaPanel.repaint();
    }

    private void createUIComponents() {
        JDateChooser1 = new com.toedter.calendar.JDateChooser();
        ((com.toedter.calendar.JTextFieldDateEditor) JDateChooser1.getDateEditor()).setEditable(false);
        JDateChooser1.setDateFormatString("dd/MM/yyyy");

        // 1. Cattura la data odierna per il limite minimo
        java.util.Calendar calMin = java.util.Calendar.getInstance();
        JDateChooser1.setMinSelectableDate(calMin.getTime());
        JDateChooser1.setDate(calMin.getTime());

        // 2. Cattura di nuovo la data odierna per calcolare il limite massimo
        java.util.Calendar calMax = java.util.Calendar.getInstance();

        // --- AGGIUNGE ESATTAMENTE 1 ANNO ALLA DATA ATTUALE ---
        calMax.add(java.util.Calendar.YEAR, 1);

        // Imposta la data massima
        JDateChooser1.setMaxSelectableDate(calMax.getTime());
    }
}