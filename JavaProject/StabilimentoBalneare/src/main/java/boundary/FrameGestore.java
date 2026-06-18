package boundary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FrameGestore {
    private JButton definisciTariffeButton;
    private JButton configuraOmbrelloniButton;
    private JPanel gestorePanel;

    private JFrame formDefinizioneTariffe;
    private JFrame formConfigurazioneOmbrelloni;

    public FrameGestore() {

        //Se il gestore clicca su DefinisciTariffe -> si apre FormDefinizioneTariffe
        definisciTariffeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (formDefinizioneTariffe == null || !formDefinizioneTariffe.isDisplayable()){

                    FormDefinizioneTariffe formDefinizioneTariffeIstanza = new FormDefinizioneTariffe();

                    formDefinizioneTariffe = formDefinizioneTariffeIstanza.apriFormDefinizioneTariffe();

                    formDefinizioneTariffe.setLocationRelativeTo(null);

                    formDefinizioneTariffe.setVisible(true);

                } else {

                    formDefinizioneTariffe.toFront();

                    formDefinizioneTariffe.requestFocus();

                }
            }
        });

        //Se il gestore clicca su ConfiguraOmbrelloni -> si apre FormConfigurazioneOmbrelloni
        configuraOmbrelloniButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (formConfigurazioneOmbrelloni == null || !formConfigurazioneOmbrelloni.isDisplayable()){

                    FormConfigurazioneOmbrelloni formConfigurazioneOmbrelloniIstanza = new FormConfigurazioneOmbrelloni();

                    formConfigurazioneOmbrelloni = formConfigurazioneOmbrelloniIstanza.apriFormConfigurazioneOmbrelloni();

                    formConfigurazioneOmbrelloni.setLocationRelativeTo(null);

                    formConfigurazioneOmbrelloni.setVisible(true);

                } else {

                    formConfigurazioneOmbrelloni.toFront();
                    formConfigurazioneOmbrelloni.requestFocus();

                }

            }
        });
    }

    //Apertura del frame se si accede come Gestore
    public JFrame apriFrameGestore() {

        JFrame frame = new JFrame("Benvenuto nella pagina di Gestore!");
        FrameGestore GestoreFrame = new FrameGestore();

        frame.setContentPane(GestoreFrame.gestorePanel);


        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 1200);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return frame;
    }
}
