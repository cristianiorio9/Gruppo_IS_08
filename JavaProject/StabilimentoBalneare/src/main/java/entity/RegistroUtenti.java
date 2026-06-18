package entity;

import database.GestorePersistenza;

import java.util.List;
import java.util.Map;

public class RegistroUtenti {

    private GestorePersistenza gestorePersistenza;

    public RegistroUtenti(){

        this.gestorePersistenza = new GestorePersistenza();

    }


    public Cliente cercaCliente(String username, String password) {

        List<Cliente> risultati =  gestorePersistenza.cercaPerCampi(
                Cliente.class,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
        if (risultati.isEmpty()) {
            return null; // Se non trova niente, restituisce null
        }
        return risultati.getFirst();

    }

    public Gestore cercaGestore(String username, String password) {

        List<Gestore> risultati = gestorePersistenza.cercaPerCampi(
                Gestore.class,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
        // CORREZIONE: Controlliamo se la lista è vuota prima di fare getFirst()
        if (risultati.isEmpty()) {
            return null; // Se non trova niente, restituisce null
        }
        return risultati.getFirst();
    }

    public Cliente cercaClientePerUsername(String username) {
        List<Cliente> risultati = gestorePersistenza.cercaPerCampi(
                Cliente.class,
                Map.of(
                        "username", username

                )
        );
        if (risultati.isEmpty()) {
            return null;
        }
        return risultati.getFirst();
    }
}
