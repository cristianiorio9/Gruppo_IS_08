package controller;

import entity.Cliente;
import entity.Gestore;
import database.GestorePersistenza;
import database.JpaUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AutenticazioneTest{

    private static final String USR_CLIENTE_VALIDO = "hojlund";
    private static final String PWD_CLIENTE_VALIDA = "attaccante11";

    private static final String USR_GESTORE_VALIDO = "rrhamani";
    private static final String PWD_GESTORE_VALIDA = "difensore13";

    @BeforeAll
    static void setupDatabase() {

        JpaUtil.getInstance();
        GestorePersistenza gestore = new GestorePersistenza();

        // 1. Creiamo e salviamo manualmente il Cliente necessario per le classi valide
        Cliente clienteTest = new Cliente(
                USR_CLIENTE_VALIDO,
                PWD_CLIENTE_VALIDA,
                "Rasmus",
                "Hojlund",
                "rasmus.hojlund@email.com",
                "333111222"
        );
        gestore.salva(clienteTest);

        // Creiamo e salviamo manualmente il Gestore necessario per le classi valide
        Gestore gestoreTest = new Gestore(
                USR_GESTORE_VALIDO,
                PWD_GESTORE_VALIDA,
                "Amir",
                "Rrahmani",
                "amir.rrahmani@email.com",
                "333444555"
        );
        gestore.salva(gestoreTest);

        System.out.println("Setup completato: Dati di test (Hojlund e Rrhamani) caricati nel DB.");
    }


    // CASI DI SUCCESSO
    @Test
    void LoginClienteValido() {
        // Copertura: CErv1 (0), CEuv1 (hojlund), CEpv1 (attaccante11)
        boolean esito = ControllerUtenti.autenticazione(0, USR_CLIENTE_VALIDO, PWD_CLIENTE_VALIDA);
        assertTrue(esito, "TCBB1 fallito: Il login del cliente Hojlund con dati validi deve restituire true.");
    }

    @Test
    void LoginGestoreValido() {
        // Copertura: CErv2 (1), CEuv1 (rrhamani), CEpv1 (difensore13)
        boolean esito = ControllerUtenti.autenticazione(1, USR_GESTORE_VALIDO, PWD_GESTORE_VALIDA);
        assertTrue(esito, "TCBB2 fallito: Il login del gestore Rrhamani con dati validi deve restituire true.");
    }


    // CASI DI FALLIMENTO

    @Test
    void RuoloNonValidoMinimo() {
        // Copertura: CEnvr1 (-1)
        // Valori validi inseriti: CEuv1 (hojlund), CEpv1 (attaccante11)
        boolean esito = ControllerUtenti.autenticazione(-1, USR_CLIENTE_VALIDO, PWD_CLIENTE_VALIDA);
        assertFalse(esito, "TCBB3 fallito: Il sistema deve rifiutare un ruolo inferiore a 0.");
    }

    @Test
    void RuoloNonValidoMassimo() {
        // Copertura: CEnvr2 (2)
        // Valori validi inseriti: CEuv1 (rrhamani), CEpv1 (difensore13)
        boolean esito = ControllerUtenti.autenticazione(2, USR_GESTORE_VALIDO, PWD_GESTORE_VALIDA);
        assertFalse(esito, "TCBB4 fallito: Il sistema deve rifiutare un ruolo superiore a 1.");
    }

    @Test
    void UsernameInesistente() {
        // Copertura: CEnvu1 (Username inesistente)
        // Valori validi inseriti: CErv2 (1), CEpv1 (difensore13)
        boolean esito = ControllerUtenti.autenticazione(1, "fantasma", PWD_GESTORE_VALIDA);
        assertFalse(esito, "TCBB5 fallito: Il sistema deve rifiutare un utente non presente nel DB.");
    }

    @Test
    void PasswordErrata() {
        // Copertura: CEnvp1 (Password non corrispondente)
        // Valori validi inseriti: CErv1 (0), CEuv1 (hojlund)
        boolean esito = ControllerUtenti.autenticazione(0, USR_CLIENTE_VALIDO, "errata_123");
        assertFalse(esito, "TCBB6 fallito: Il sistema deve rifiutare il login se la password è sbagliata.");
    }

  //PULIZIA DB

    @AfterAll
    static void puliziaDatabase() {
        // Usiamo l'EntityManager direttamente per non dover modificare il GestorePersistenza
        jakarta.persistence.EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            // Cerchiamo il Cliente usando direttamente la sua chiave primaria (String) e lo eliminiamo
            Cliente clienteDaCancellare = em.find(Cliente.class, USR_CLIENTE_VALIDO);
            if (clienteDaCancellare != null) {
                em.remove(clienteDaCancellare);
            }

            // Cerchiamo il Gestore usando direttamente la sua chiave primaria (String) e lo eliminiamo
            Gestore gestoreDaCancellare = em.find(Gestore.class, USR_GESTORE_VALIDO);
            if (gestoreDaCancellare != null) {
                em.remove(gestoreDaCancellare);
            }

            em.getTransaction().commit();
            System.out.println("Teardown completato: Dati di test (Hojlund e Rrhamani) rimossi in modo pulito dal DB.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}