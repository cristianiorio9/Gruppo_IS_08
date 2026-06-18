package database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Map;

public class GestorePersistenza {

    /*
     * Cerca tutti gli oggetti persistenti che soddisfano un insieme di condizioni.
     *
     * La query JPQL viene costruita nel livello database.
     */
    public <T> List<T> cercaPerCampi(Class<T> classe,
                                     Map<String, Object> campi) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            StringBuilder jpql = new StringBuilder();

            jpql.append("SELECT e FROM ")
                    .append(classe.getSimpleName())
                    .append(" e");

            if (!campi.isEmpty()) {
                jpql.append(" WHERE ");

                int contatore = 0;

                for (String nomeCampo : campi.keySet()) {
                    if (contatore > 0) {
                        jpql.append(" AND ");
                    }

                    String nomeParametro = nomeCampo.replace(".", "_");

                    jpql.append("e.")
                            .append(nomeCampo)
                            .append(" = :")
                            .append(nomeParametro);

                    contatore++;
                }
            }

            TypedQuery<T> query = em.createQuery(
                    jpql.toString(),
                    classe
            );

            for (String nomeCampo : campi.keySet()) {
                String nomeParametro = nomeCampo.replace(".", "_");
                query.setParameter(nomeParametro, campi.get(nomeCampo));
            }

            return query.getResultList();

        } finally {
            em.close();
        }
    }

    /*
     * Cerca e restituisce tutti gli oggetti persistenti di una determinata classe.
     *
     * È l'equivalente di una query "SELECT * FROM tabella" in SQL,
     * ma scritta in JPQL lavorando direttamente con gli oggetti.
     * Il metodo è generico e può essere utilizzato per qualsiasi Entity.
     *
     * Esempio:
     * List<Proprietario> tuttiIProprietari = cercaTutti(Proprietario.class);
     */
    public <T> List<T> cercaTutti(Class<T> classe) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            /*
             * Costruiamo la stringa JPQL.
             * classe.getSimpleName() restituisce il nome della classe (es. "Proprietario"),
             * che in JPA corrisponde di default al nome dell'Entity.
             */
            String jpql = "SELECT e FROM " + classe.getSimpleName() + " e";

            /*
             * Creiamo una TypedQuery, che garantisce che il risultato
             * sia del tipo corretto (la classe passata come parametro).
             */
            TypedQuery<T> query = em.createQuery(jpql, classe);

            /*
             * Eseguiamo la query e restituiamo la lista dei risultati.
             */
            return query.getResultList();

        } finally {
            /*
             * Come per gli altri metodi, assicuriamoci di chiudere
             * l'EntityManager alla fine dell'operazione di lettura.
             */
            em.close();
        }
    }

    public <T> boolean elimina(Class<T> classe, Long id) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            /*
             * Cerchiamo nel database l'oggetto da eliminare,
             * usando la sua classe e il suo id.
             */

            T oggetto = em.find(classe, id);

            //se l'oggetto esiste, lo eliminiamo
            if (oggetto != null) {
                em.remove(oggetto);
                em.getTransaction().commit();
                return true;
            }

            em.getTransaction().commit();
            return false;

        } catch (RuntimeException e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
            return false;

        } finally {
            em.close();
        }
    }


    /*
     * Aggiorna specifici campi di un'entità tramite query JPQL di tipo UPDATE.
     * È il metodo più sicuro per modificare dati senza incorrere in problemi
     * con le liste collegate (evitando crash di Hibernate come AbstractVisitor).
     */
    public <T> boolean aggiornaCampi(Class<T> classe, Long id, Map<String, Object> campi) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            em.getTransaction().begin();

            StringBuilder jpql = new StringBuilder();
            jpql.append("UPDATE ")
                    .append(classe.getSimpleName())
                    .append(" e SET ");

            int contatore = 0;
            for (String nomeCampo : campi.keySet()) {
                if (contatore > 0) {
                    jpql.append(", ");
                }
                String nomeParametro = nomeCampo.replace(".", "_");
                jpql.append("e.").append(nomeCampo).append(" = :").append(nomeParametro);
                contatore++;
            }

            // Diciamo quale riga esatta aggiornare usando l'ID
            jpql.append(" WHERE e.id = :id");

            jakarta.persistence.Query query = em.createQuery(jpql.toString());

            // Inseriamo i valori dinamici
            for (String nomeCampo : campi.keySet()) {
                String nomeParametro = nomeCampo.replace(".", "_");
                query.setParameter(nomeParametro, campi.get(nomeCampo));
            }
            query.setParameter("id", id);

            int righeModificate = query.executeUpdate();

            em.getTransaction().commit();
            return righeModificate > 0;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /*
     * Salva nel database un oggetto persistente.
     *
     * Il parametro è di tipo Object perché il gestore della persistenza
     * deve rimanere generico: non deve conoscere direttamente le classi
     * specifiche del dominio, come Proprietario o Imbarcazione.
     *
     * L'oggetto passato deve però essere una Entity, cioè una classe
     * annotata con @Entity.
     */
    //public void salva(Object oggetto) {
    public boolean salva(Object oggetto) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            /*
             * Ogni operazione che modifica il database deve essere eseguita
             * all'interno di una transazione.
             */
            em.getTransaction().begin();

            /*
             * persist rende l'oggetto gestito da Hibernate.
             * Al commit della transazione, Hibernate tradurrà l'oggetto
             * in una riga della tabella corrispondente.
             */
            em.persist(oggetto);

            /*
             * Conferma la transazione.
             * Da questo momento le modifiche diventano effettive nel database.
             */
            em.getTransaction().commit();

            return true;

        } catch (RuntimeException e) {

            /*
             * Se qualcosa va storto durante l'operazione, annulliamo
             * la transazione per evitare modifiche parziali al database.
             */
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            //throw e;
            e.printStackTrace();
            return false;

        } finally {
            /*
             * L'EntityManager deve essere chiuso dopo l'operazione.
             * La EntityManagerFactory resta invece aperta e viene chiusa
             * solo alla fine dell'applicazione.
             */
            em.close();
        }
    }
    public <T> T trovaPerId(Class<T> classe, Long id) {

        EntityManager em = JpaUtil.getInstance().getEntityManager();

        try {
            /*
             * find cerca nel database una riga della tabella associata
             * alla classe indicata, usando l'id come chiave primaria.
             */
            return em.find(classe, id);

        } finally {
            em.close();
        }
    }

    /**
     * Cerca entità verificando che il conteggio delle loro associazioni sia inferiore a un limite.
     * * @param limite Può essere una String (nome del campo dell'entità target, es. "disponibilita")
     * oppure un Number (valore numerico fisso, es. 1).
     */
    public <T, J> List<T> cercaDisponibiliConLimite(
            Class<T> classeTarget,
            Class<J> classeJoin,
            String campoJoin,
            Map<String, Object> parametriJoin,
            Object limite,
            boolean manyToMany
    ) {
        EntityManager em = JpaUtil.getInstance().getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder();

            jpql.append("SELECT t FROM ").append(classeTarget.getSimpleName()).append(" t WHERE ");

            // 1. Risoluzione dinamica dell'Object 'limite'
            if (limite instanceof String nomeCampo && !nomeCampo.trim().isEmpty()) {
                jpql.append("t.").append(nomeCampo); // Es. t.disponibilita
            } else if (limite instanceof Number valoreFisso) {
                jpql.append(valoreFisso);            // Es. 1
            } else {
                throw new IllegalArgumentException("Il parametro 'limite' deve essere una String (nome campo) o un Number (valore fisso).");
            }

            // 2. Confrontiamo la soglia con il COUNT della subquery
            jpql.append(" > (SELECT COUNT(j) FROM ").append(classeJoin.getSimpleName()).append(" j ");

            // 3. Condizione di JOIN dinamica tra j e t
            if (manyToMany) {
                jpql.append("JOIN j.").append(campoJoin).append(" jt WHERE jt = t");
            } else {
                jpql.append("WHERE j.").append(campoJoin).append(" = t");
            }

            // 4. Aggiungiamo i filtri aggiuntivi per la subquery (es. data)
            if (parametriJoin != null && !parametriJoin.isEmpty()) {
                for (String param : parametriJoin.keySet()) {
                    String paramName = param.replace(".", "_");
                    jpql.append(" AND j.").append(param).append(" = :").append(paramName);
                }
            }
            jpql.append(")"); // Chiudiamo la subquery

            // Creiamo la query e settiamo i parametri
            TypedQuery<T> query = em.createQuery(jpql.toString(), classeTarget);
            if (parametriJoin != null && !parametriJoin.isEmpty()) {
                for (Map.Entry<String, Object> entry : parametriJoin.entrySet()) {
                    query.setParameter(entry.getKey().replace(".", "_"), entry.getValue());
                }
            }

            return query.getResultList();
        } finally {
            em.close();
        }
    }

}
