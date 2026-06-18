package boundary;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

/*
 * Implementazione di SistemaNotifiche via email con Jakarta Mail.
 * Contiene tutta la logica di Jakarta Mail: nessun altro file del progetto
 * importa questa classe direttamente.
 */
class SistemaNotificheJakarta implements SistemaNotifiche {

    private static SistemaNotificheJakarta instance;



    private SistemaNotificheJakarta() {}
    public static SistemaNotificheJakarta getInstance() {
        if (instance == null) {
            instance = new SistemaNotificheJakarta();
        }
        return instance;
    }

    // Configurazione SMTP
    private static final String MITTENTE  = "frederique.schimmel@ethereal.email";
    private static final String PASSWORD  = "HsZw2teEyMg6ZvARBQ";
    private static final String SMTP_HOST = "smtp.ethereal.email";
    private static final String SMTP_PORT = "587";

    // Oggetti delle email: magari in futuro potrei averne uno per gli annullamenti
    private static final String OGGETTO_CONFERMA     = "Conferma Prenotazione Ombrellone";



    @Override
    public boolean inviaConfermaPrenotazione(String destinatario, String dataPrenotazione, double totale) {
        return inviaEmail(destinatario, OGGETTO_CONFERMA,
                costruisciCorpoConferma(dataPrenotazione, totale));
    }

    // Composizione del messaggio

    private String costruisciCorpoConferma(String dataPrenotazione, double totale) {
        return "Gentile cliente,\n\n"
                + "la tua prenotazione per la data " + dataPrenotazione + " è confermata.\n"
                + "Totale addebitato: € " + String.format("%.2f", totale) + "\n\n"
                + "Grazie per aver scelto il nostro stabilimento!";
    }

    //metodo generico per inviare un'email
    private boolean inviaEmail(String destinatario, String oggetto, String corpo) {
        Properties props = new Properties();
        props.put("mail.smtp.host",            SMTP_HOST);
        props.put("mail.smtp.port",            SMTP_PORT);
        props.put("mail.smtp.auth",            "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MITTENTE, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MITTENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(oggetto);
            message.setText(corpo);
            Transport.send(message);
            System.out.println("[SistemaNotifiche] Email inviata a: " + destinatario);
            return true;
        } catch (MessagingException e) {
            System.err.println("[SistemaNotifiche] Errore invio email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}