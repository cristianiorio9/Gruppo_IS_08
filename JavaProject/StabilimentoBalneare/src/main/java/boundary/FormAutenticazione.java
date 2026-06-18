package boundary;

import controller.ControllerUtenti;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormAutenticazione {
    private JTextField textField_username;
    private JPasswordField textField_password;
    private JButton loginButton;
    private JPanel autenticazionePanel;
    private JTabbedPane tabbedPane;
    private JFrame frameCliente;
    private JFrame frameGestore;

    public FormAutenticazione() {

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int ruoloSelezionato = tabbedPane.getSelectedIndex();

                // 2. Prendi le credenziali scritte dall'utente
                String username = textField_username.getText();
                String password = new String(textField_password.getPassword());

                boolean esito = ControllerUtenti.autenticazione(ruoloSelezionato, username, password);

                if (esito) {

                    if (ruoloSelezionato == ControllerUtenti.RUOLO_CLIENTE) {

                        if (frameCliente == null || !frameCliente.isDisplayable()) {

                            FrameCliente frameClienteIstanza = new FrameCliente();


                            frameClienteIstanza.caricaDati(username);

                            frameCliente = frameClienteIstanza.apriFrameCliente();
                            frameCliente.setLocationRelativeTo(null);
                            frameCliente.setVisible(true);

                        } else {


                            frameCliente.toFront();
                            frameCliente.requestFocus();

                        }

                    } else if (ruoloSelezionato == ControllerUtenti.RUOLO_GESTORE) {

                        if (frameGestore == null || !frameGestore.isDisplayable()) {

                            FrameGestore frameGestoreIstanza = new FrameGestore();

                            frameGestore = frameGestoreIstanza.apriFrameGestore();

                            frameGestore.setLocationRelativeTo(null);

                            frameGestore.setVisible(true);

                        } else {

                            frameGestore.toFront();
                            frameGestore.requestFocus();

                        }
                    }
                } else {

                    JOptionPane.showMessageDialog(null, "Username o password errata","Accesso negato", JOptionPane.ERROR_MESSAGE);

                }
            }
        });
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Benvenuto nella pagina di Autenticazione!");
        FormAutenticazione AutenticazioneFrame = new FormAutenticazione();

        frame.setContentPane(AutenticazioneFrame.autenticazionePanel);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1200);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void apriFormAutenticazione(){

        JFrame frame = new JFrame("Benvenuto nella pagina di Autenticazione!");
        FormAutenticazione AutenticazioneFrame = new FormAutenticazione();

        frame.setContentPane(AutenticazioneFrame.autenticazionePanel);


        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1200);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}