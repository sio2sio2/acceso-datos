package edu.acceso.tarea_5_1.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

/**
 * Clase de utilidad para gestionar múltiples instancias de {@link EntityManagerFactory}.
 * Permite crear instancias a partir del nombre de la unidad de persistencia y un mapa de propiedades,
 * y recuperarlas posteriormente mediante un índice.
 * También proporciona métodos para ejecutar transacciones de forma sencilla.
 */
public class JpaBackend {

    /**
     * Lista de claves hash que representan las instancias creadas.
     * Se utiliza una lista para mantener el orden de creación y permitir la recuperación por índice.
     */
    private static ArrayList<Integer> keys = new ArrayList<>();
    /**
     * Mapa que asocia las claves hash con las instancias de EntityManagerFactory.
     */
    private static Map<Integer, EntityManagerFactory> instances = new HashMap<>();

    /**
     * Constructor privado para evitar instanciación.
     */
    private JpaBackend() { super(); }

    /**
     * Genera un {@link EntityManagerFactory} a partir del nombre de la unidad de persistencia
     * y un mapa que modifica sus propiedades.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @param props El mapa que define las propiedades definidas en tiempo de ejecución.
     * @return Un índice que representa la instancia creada.
     * @throws IllegalArgumentException Si el nombre de la unidad de persistencia es nulo.
     * @throws IllegalStateException Si ya existe una instancia con esos parámetros.
     */
    public static int createEntityManagerFactory(String persistenceUnit, Map<String, String> props) {
        if(persistenceUnit == null) throw new IllegalArgumentException("El nombre de la unidad de persistencia no puede ser nulo"); 

        int hashCode = Objects.hash(persistenceUnit, props);

        EntityManagerFactory instance = instances.get(hashCode);
        if(instance != null && instance.isOpen()) {
            throw new IllegalStateException("Ya existe una EntityManagerFactory con esos parámetros");
        }
        instance = Persistence.createEntityManagerFactory(persistenceUnit, props);
        instances.put(hashCode, instance);
        keys.add(hashCode);
        return keys.size();
    }

    /**
     * Genera un {@link EntityManagerFactory} a partir del nombre de la unidad de persistencia
     * Se sobreentiende que no se modifica o añade ninguna propiedad.
     * @param persistenceUnit El nombre de la unidad de persistencia.
     * @return Un índice que representa la instancia creada.
     * @throws IllegalArgumentException Si el nombre de la unidad de persistencia es nulo.
     * @throws IllegalStateException Si ya existe una instancia con esos parámetros.
     */
    public static int createEntityManagerFactory(String persistenceUnit) {
        return createEntityManagerFactory(persistenceUnit, null);
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente.
     * @param index El índice de la instancia a recuperar.
     * @return La instancia de EntityManagerFactory correspondiente al índice.
     * @throws IllegalArgumentException Si el índice está fuera de rango.
     */
    public static EntityManagerFactory getEntityManagerFactory(int index) {
        if(index < 1 || index > keys.size()) throw new IllegalArgumentException("Índice fuera de rango");

        int hashCode = keys.get(index - 1);
        EntityManagerFactory instance = instances.get(hashCode);
        if(instance != null && instance.isOpen()) return instance;
        else {
            instances.remove(hashCode);
            keys.remove(index - 1);
            throw new IllegalStateException("La instancia solicitada no está disponible");
        }
    }

    /**
     * Devuelve un objeto EntityManagerFactory generado anteriormente. Sólo funciona si se generó uno.
     * @return El objeto resultante.
     * @throws IllegalStateException Si no hay ninguna instancia o si hay varias.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory instance = null;

        switch(instances.size()) {
            case 1:
                instance = instances.values().iterator().next();
                if(instance.isOpen()) return instance;
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
     * Elimina todos los objetos previamente creados.
     */
    public static void reset() {
        instances.clear();
        keys.clear();
    }

    // Transacciones.
    /**
     * Ejecuta una acción dentro de una transacción, devolviendo un resultado.
     * @param <T> El tipo de resultado de la acción.
     * @param index El índice de la instancia a utilizar.
     * @param action La acción a ejecutar.
     * @return El resultado de la acción.
     */
    public static <T> T transactionR(Integer index, Function<EntityManager, T> action) {
        EntityManagerFactory emf = index != null?getEntityManagerFactory(index):getEntityManagerFactory();
        try(EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                T result = action.apply(em);
                tx.commit();
                return result;
            }
            catch(Exception e) {
                if(tx != null && tx.isActive()) tx.rollback();
                throw new RuntimeException("Fallo en la transacción", e);
            }
        }
    }

    /**
     * Versión sin índice de {@link #transactionR(Integer, Function)} para cuando sólo hay una instancia.
     * @param <T> El tipo de resultado de la acción.
     * @param action La acción a ejecutar.
     * @return El resultado de la acción.
     */
    public static <T> T transactionR(Function<EntityManager, T> action) {
        return transactionR(null, action);
    }

    /**
     * Ejecuta una acción dentro de una transacción, sin devolver resultado.
     * @param index El índice de la instancia a utilizar.
     * @param action La acción a ejecutar.
     */
    public static void transaction(Integer index, Consumer<EntityManager> action) {
        transactionR(index, em -> {
            action.accept(em);
            return null;
        });
    }

    /**
     * Versión sin índice de {@link #transaction(Integer, Consumer)} para cuando sólo hay una instancia.
     * @param action La acción a ejecutar.
     */
    public static void transaction(Consumer<EntityManager> action) {
        transaction(null, action);
    }
    // Fin transacciones.
}