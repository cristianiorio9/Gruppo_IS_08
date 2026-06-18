package entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Gestore {

    @Id
    private String username;

    private String password;
    private String nome;
    private String cognome;
    private String email;
    private String telefono;

    public Gestore() {
    }

    public Gestore(String username, String password, String nome, String cognome, String email, String telefono) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.telefono = telefono;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public String toString() {
        return "Gestore{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }
}
