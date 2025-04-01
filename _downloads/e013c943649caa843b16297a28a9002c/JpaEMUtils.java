package edu.acceso.test_hibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaEMUtils {

    private static Map<Integer, EntityManagerFactory> instances = new HashMap<>();

    private JpaEMUtils() { super(); }

    /**
     * Genera siempre el mismo EntityManagerFactory a partir del nombre de la unidad de persistencia
     * y un mapa que modifica sus propiedades.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @param props El mapa que define las propiedades definidas en tiempo de ejecución.
     * @return El objeto resultante.
     */
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnit, Map<String, String> props) {
        if(persistenceUnit == null) return getEntityManagerFactory();
        int hashCode = Objects.hash(persistenceUnit, props);
        EntityManagerFactory instance = instances.get(hashCode);
        if(instance == null || !instance.isOpen()) {
            instance = Persistence.createEntityManagerFactory(persistenceUnit, props);
            instances.put(hashCode, instance);
        }
        return instance;
    }

    /**
     * Genera siempre el mismo EntityManagerFactory a partir del nombre de la unidad de persistencia.
     * Se sobreentiende que no se modifica o añade ninguna propiedad.ñ
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @return El objeto resultante.
     */
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {
        return getEntityManagerFactory(persistenceUnit, null);
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente. Sólo funciona si se generó uno.
     * @return El objeto resultante.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory instance = null;

        switch(instances.size()) {
            case 1:
                instance = instances.values().iterator().next();
                if(instance.isOpen()) return instance;
                else instances.clear();
            case 0:
                throw new IllegalStateException("No hay disponible ninguna instancia con parámetros");
            default:
                throw new IllegalStateException("Invocación ambigua: hay varios candidatos");
        }
    }

    /**
     * Elimina todos los objetos previamente creados.
     */
    public static void reset() {
        instances.clear();
    }

    // Transacciones.
    public static void transaction(Consumer<EntityManager> action, String persistenceUnit, Map<String, String> props) {
        EntityManagerFactory emf = getEntityManagerFactory(persistenceUnit, props);
        try(EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                action.accept(em);
                tx.commit();
            }
            catch(Exception e) {
                if(tx != null && tx.isActive()) tx.rollback();
                throw new RuntimeException("Fallo en la transacción", e);
            }
        }
    }

    public static void transaction(Consumer<EntityManager> action, String persistenceUnit) {
        transaction(action, persistenceUnit, null);
    }

    public static void transaction(Consumer<EntityManager> action) {
        transaction(action, null, null);
    }
    // Fin transacciones.
}
