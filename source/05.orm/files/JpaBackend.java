package edu.acceso.tarea_5_1.backend;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;

/**
 * Gestiona múltiples instancias de {@link EntityManagerFactory}.
 * Utiliza el patrón Multiton para crear instancias únicas asociadas al nombre de la unidad de persistencia.
 */
public class JpaBackend implements AutoCloseable {

    /**
     * Mapa que asocia las unidades de persistencias con los objetos {@link EntityManagerFactory} generados.
     */
    private static Map<String, JpaBackend> entities = new ConcurrentHashMap<>();

    private final EntityManagerFactory emf;
    private final String persistenceUnit;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Constructor privado para evitar instanciación.
     */
    private JpaBackend(String persistenceUnit, EntityManagerFactory emf) { 
        this.emf = emf;
        this.persistenceUnit = persistenceUnit;
    }

    /**
     * Genera un {@link EntityManagerFactory} a partir del nombre de la unidad de persistencia
     * y un mapa que modifica sus propiedades.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @param props El mapa que define las propiedades definidas en tiempo de ejecución.
     * @return La instancia de {@link JpaBackend} creada.
     * @throws IllegalArgumentException Si el nombre de la unidad de persistencia es nulo.
     * @throws IllegalStateException Si ya existe una instancia con esos parámetros.
     */
    public static JpaBackend createEntityManagerFactory(String persistenceUnit, Map<String, String> props) {
        Objects.requireNonNull(persistenceUnit, "El nombre de la unidad de persistencia no puede ser nulo");

        return entities.compute(persistenceUnit, (k, instance) -> {
            if(instance != null && instance.isOpen()) throw new IllegalStateException("Ya existe una EntityManagerFactory con esos parámetros");
            return new JpaBackend(persistenceUnit, Persistence.createEntityManagerFactory(persistenceUnit, props));
        });
    }

    /**
     * Indica si la fábrica de gestores de entidad asociada a la entidad está abierta
     * @return true si está abierta, false en caso contrario.
     */
    public boolean isOpen() {
        return !closed.get() && emf.isOpen();
    }

    /**
     * Genera un {@link EntityManagerFactory} a partir del nombre de la unidad de persistencia
     * Se sobreentiende que no se modifica o añade ninguna propiedad.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @throws IllegalArgumentException Si el nombre de la unidad de persistencia es nulo.
     * @throws IllegalStateException Si ya existe una instancia con esos parámetros.
     * @return La instancia de {@link JpaBackend} creada.
     */
    public static JpaBackend create(String persistenceUnit) {
        return createEntityManagerFactory(persistenceUnit, null);
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @return La instancia de {@link JpaBackend} correspondiente.
     * @throws IllegalArgumentException Si el índice está fuera de rango.
     */
    private static JpaBackend get(String persistenceUnit) {
        Objects.requireNonNull(persistenceUnit, "El nombre de la unidad de persistencia no puede ser nulo");

        JpaBackend instance = entities.get(persistenceUnit);
        if(instance == null) throw new IllegalArgumentException("No existe ningún objeto asociado a ese nombre de unidad de persistencia");

        if(instance.emf.isOpen()) return instance;
        else {
            entities.remove(persistenceUnit, instance);
            throw new IllegalStateException("La instancia solicitada no está disponible. Pruebe a crearla de nuevo.");
        }
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente. Sólo funciona si se generó uno.
     * @return La instancia de {@link JpaBackend} correspondiente.
     * @throws IllegalStateException Si no hay ninguna instancia o si hay varias.
     */
    public static JpaBackend get() {
        // Snapshot atómico de las instancias.
        JpaBackend[] instances = entities.values().toArray(new JpaBackend[0]);

        switch(instances.length) {
            case 1:
                JpaBackend instance = instances[0];
                if(instance.isOpen()) return instance;
                else throw new IllegalStateException("La instancia solicitada no está disponible");
            case 0:
                throw new IllegalStateException("No hay disponible ninguna instancia");
            default:
                throw new IllegalStateException("Invocación ambigua: hay varios candidatos");
        }
    }

    @Override
    public void close() {
        if(closed.compareAndSet(false, true)) {
            entities.remove(persistenceUnit);
            if(isOpen()) emf.close();
        }
    }

    // Transacciones.
    /**
     * Ejecuta una acción dentro de una transacción, devolviendo un resultado.
     * @param <T> El tipo de resultado de la acción.
     * @param action La acción a ejecutar.
     * @return El resultado de la acción.
     */
    public <T> T transactionR(Function<EntityManager, T> action) {
        if(!isOpen()) throw new IllegalStateException("La fábrica de gestores de entidad está cerrada");

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
    public static <T> T transactionR(String persistenceUnit, Function<EntityManager, T> action) {
        return get(persistenceUnit).transactionR(action);
    }

    /**
     * Ejecuta una acción dentro de una transacción, sin devolver resultado.
     * @param action La acción a ejecutar.
     */
    public void transaction(Consumer<EntityManager> action) {
        transactionR(em -> {
            action.accept(em);
            return null;
        });
    }

    /**
     * Versión sin índice de {@link #transaction(String, Consumer)} para cuando sólo hay una instancia.
     * @param action La acción a ejecutar.
     */
    public static void transaction(String persistenceUnit, Consumer<EntityManager> action) {
        get(persistenceUnit).transaction(action);
    }
    // Fin transacciones.
}