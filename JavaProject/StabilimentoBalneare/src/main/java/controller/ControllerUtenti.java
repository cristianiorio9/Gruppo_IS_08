package controller;

import entity.Cliente;
import entity.Gestore;
import entity.RegistroUtenti;

import java.util.*;

public class ControllerUtenti {

    public static final int RUOLO_CLIENTE = 0;
    public static final int RUOLO_GESTORE = 1;

    //ZONA Globale
    public static boolean autenticazione(int ruolo, String username, String password) {

        RegistroUtenti registroUtenti = new RegistroUtenti();

        if (ruolo == RUOLO_CLIENTE) {

            Cliente cliente = registroUtenti.cercaCliente(username, password);

            return cliente != null;

        } else if (ruolo == RUOLO_GESTORE) {

            Gestore gestore = registroUtenti.cercaGestore(username, password);

            return gestore != null;

        } else {

            return false;

        }
    }

    //DCC
    /**
     * Recupera l'indirizzo email di un cliente a partire dal suo username.
     *
     * @param username Lo username del cliente da cercare.
     * @return L'indirizzo email del cliente, oppure null se il cliente non esiste.
     */
    public static String getEmailClientePerUsername(String username) {
        RegistroUtenti registroUtenti = new RegistroUtenti();

        return registroUtenti.cercaClientePerUsername(username).getEmail();
    }
}
