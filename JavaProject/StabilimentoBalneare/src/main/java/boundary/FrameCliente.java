package boundary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameCliente {
    private JButton visualizzareDisponibilitaButton;
    private JPanel clientePane;

    private JFrame frameVisualizzareDisponibilita;

    // 1. Aggiungiamo la variabile di memoria
    private String idClienteLoggato;

    public FrameCliente() {
        visualizzareDisponibilitaButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frameVisualizzareDisponibilita == null || !frameVisualizzareDisponibilita.isDisplayable()) {

                    FrameVisualizzareDisponibilita frameDisponibilita = new FrameVisualizzareDisponibilita();

                    // 3. IL PASSAGGIO DEL TESTIMONE!
                    // Passiamo l'ID al frame della disponibilità prima di aprirlo
                    frameDisponibilita.caricaDati(idClienteLoggato);

                    frameVisualizzareDisponibilita = frameDisponibilita.apriFrameVisualizzareDisponibilita();

                    frameVisualizzareDisponibilita.setLocationRelativeTo(null);

                    frameVisualizzareDisponibilita.setVisible(true);

                } else {

                    frameVisualizzareDisponibilita.toFront();
                    frameVisualizzareDisponibilita.requestFocus();

                }
            }
        });
    }

    // 2. Il metodo che verrà chiamato dal FormAutenticazione per passarci i dati
    public void caricaDati(String username) {
        this.idClienteLoggato = username;
    }

    public JFrame apriFrameCliente() {
        JFrame frame = new JFrame("Frame Cliente");

        frame.setContentPane(clientePane);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //questo fa si che si chiuda solo il frame e non l'intera app a differenza di EXIT_ON_CLOSE

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }
}