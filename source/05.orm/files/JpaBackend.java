package edu.acceso.tarea_5_1.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

/**
 * Clase de utilidad para gestionar múltiples instancias de {@link EntityManagerFactory}.
 * Permite crear instancias a partir del nombre de la unidad de persistencia y un mapa de propiedades,
 * y recuperarlas posteriormente mediante el propio nombre de la unidad de persistencia.
 * También proporciona métodos para ejecutar transacciones de forma sencilla.
 */
public class JpaBackend {

    /**
     * Mapa que asocia las unidades de persistencias con los objetos {@link EntityManagerFactory} generados.
     */
    private static Map<String, EntityManagerFactory> emfs = new HashMap<>();

    /**
     * Constructor privado para evitar instanciación.
     */
    private JpaBackend() { super(); }

    /**
     * Genera un {@link EntityManagerFactory} a partir del nombre de la unidad de persistencia
     * y un mapa que modifica sus propiedades.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @param props El mapa que define las propiedades definidas en tiempo de ejecución.
     * @return El objeto resultante.
     * @throws IllegalArgumentException Si el nombre de la unidad de persistencia es nulo.
     * @throws IllegalStateException Si ya existe una instancia con esos parámetros.
     */
    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnit, Map<String, String> props) {
        if(persistenceUnit == null) throw new IllegalArgumentException("El nombre de la unidad de persistencia no puede ser nulo"); 

        EntityManagerFactory emf = emfs.get(persistenceUnit);
        if(emf != null && emf.isOpen()) throw new IllegalStateException("Ya existe una EntityManagerFactory con esos parámetros");

        emf = Persistence.createEntityManagerFactory(persistenceUnit, props);
        emfs.put(persistenceUnit, emf);
        return emf;
    }

    /**
     * Genera un {@link EntityManagerFactory} a partir del nombre de la unidad de persistencia
     * Se sobreentiende que no se modifica o añade ninguna propiedad.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @return El objeto resultante.
     * @throws IllegalArgumentException Si el nombre de la unidad de persistencia es nulo.
     * @throws IllegalStateException Si ya existe una instancia con esos parámetros.
     */
    public static EntityManagerFactory createEntityManagerFactory(String persistenceUnit) {
        return createEntityManagerFactory(persistenceUnit, null);
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @return La instancia de EntityManagerFactory correspondiente al índice.
     * @throws IllegalArgumentException Si el índice está fuera de rango.
     */
    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnit) {
        Objects.requireNonNull(persistenceUnit, "El nombre de la unidad de persistencia no puede ser nulo");
        EntityManagerFactory emf = emfs.get(persistenceUnit);
        if(emf == null) throw new IllegalArgumentException("No existe ningún objeto asociado a ese nombre de unidad de persistencia");

        if(emf.isOpen()) return emf;
        else {
            emfs.remove(persistenceUnit);
            throw new IllegalStateException("La instancia solicitada no está disponible. Pruebe a crearla de nuevo.");
        }
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente. Sólo funciona si se generó uno.
     * @return El objeto resultante.
     * @throws IllegalStateException Si no hay ninguna instancia o si hay varias.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory emf = null;

        switch(emfs.size()) {
            case 1:
                emf = emfs.values().iterator().next();
                if(emf.isOpen()) return emf;
                else {
                    reset();
                    throw new IllegalStateException("La instancia solicitada no está disponible");
                }
            case 0:
                throw new IllegalStateException("No hay disponible ninguna instancia");
            default:
                throw new IllegalStateException("Invocación ambigua: hay varios candidatos");
        }
    }

    /**
     * Cierra y elimina todos los objetos {@link EntityManagerFactory} previamente creados.
     */
    public static void reset() {
        emfs.values().forEach(emf -> emf.close());
        emfs.clear();
    }

    // Transacciones.
    /**
     * Ejecuta una acción dentro de una transacción, devolviendo un resultado.
     * @param <T> El tipo de resultado de la acción.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @param action La acción a ejecutar.
     * @return El resultado de la acción.
     */
    public static <T> T transactionR(String persistenceUnit, Function<EntityManager, T> action) {
        EntityManagerFactory emf = persistenceUnit != null ? getEntityManagerFactory(persistenceUnit) : getEntityManagerFactory();
        try(EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T result = action.apply(em);
                tx.commit();
                return result;
            }
            catch(Exception e) {
                try {
                    if(tx != null && tx.isActive()) tx.rollback();
                } catch(PersistenceException pe) {
                    e.addSuppressed(pe);
                }
                throw new RuntimeException("Fallo en la transacción", e);
            }
        }
    }

    /**
     * Versión sin índice de {@link #transactionR(String, Function)} para cuando sólo hay una instancia.
     * @param <T> El tipo de resultado de la acción.
     * @param action La acción a ejecutar.
     * @return El resultado de la acción.
     */
    public static <T> T transactionR(Function<EntityManager, T> action) {
        return transactionR(null, action);
    }

    /**
     * Ejecuta una acción dentro de una transacción, sin devolver resultado.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @param action La acción a ejecutar.
     */
    public static void transaction(String persistenceUnit, Consumer<EntityManager> action) {
        transactionR(persistenceUnit, em -> {
            action.accept(em);
            return null;
        });
    }

    /**
     * Versión sin índice de {@link #transaction(String, Consumer)} para cuando sólo hay una instancia.
     * @param action La acción a ejecutar.
     */
    public static void transaction(Consumer<EntityManager> action) {
        transaction(null, action);
    }
    // Fin transacciones.
}