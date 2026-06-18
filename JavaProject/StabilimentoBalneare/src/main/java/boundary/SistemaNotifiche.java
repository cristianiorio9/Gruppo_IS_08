package boundary;

public interface SistemaNotifiche {
      boolean inviaConfermaPrenotazione(String destinatario, String dataPrenotazione, double totale);
}