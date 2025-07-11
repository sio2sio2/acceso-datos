package edu.acceso.example.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementa el patrón Factory para escoger un objeto adecuado
 */
public class Factory<I> {
    private static final Logger logger = LoggerFactory.getLogger(Factory.class);
    private final static String alias = "alias";
    /*
     * Mapa cuyas claves son los nombres asociados a una clase
     * que implementa la interfaz {@link I} y cuyos valores son las propias clases
     * que implementan dicha interfaz.
     */
    private final Map<String, Class<? extends I>> classes;

    /**
     * Escanea un paquete para obtener clases.
     * @param <I> El tipo de la interfaz.
     * @param packageName El nombre del paquete.
     * @param interfaceClass La interfaz que implementan todas las clases que se quieren encontrar.
     */
    public Factory(String packageName, Class<I> interfaceClass) {
        checkPackage(packageName);
        Reflections reflections = new Reflections(packageName);

        classes = reflections.getSubTypesOf(interfaceClass)
            .stream()
            .flatMap(clazz -> getAliases(clazz).stream().map(alias -> new AbstractMap.SimpleEntry<>(alias, clazz)))
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (existing, replacement) -> existing,
                HashMap::new
            ));
    }

    /**
     * Verifica que el paquete existe.
     * @param packageName El nombre del paquete a verificar.
     */
    private void checkPackage(String packageName) {
        if(packageName != null) {
            String path = packageName.replace('.', '/');
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            if(resource == null) {
                throw new IllegalArgumentException("El paquete '" + packageName + "' no existe.");
            }
        }
        else {
            throw new IllegalArgumentException("El nombre del paquete no puede ser nulo.");
        }
    }

    /**
     * Lista los nombres por los que se puede referir una clase (el propio nombre
     * de la clase más todos los que contenga el atributo alias)
     * @param clazz La clase que se inspecciona
     * @return El listado de clases.
     */
    private static List<String> getAliases(Class<?> clazz) {
        List<String> aliases = new ArrayList<>(List.of(clazz.getSimpleName()));

        try {
            Field field = clazz.getDeclaredField(alias);
            if(!field.canAccess(null)) field.setAccessible(true);

            Object value = field.get(null);

            if(value instanceof String && !((String) value).isBlank()) {
                aliases.add((String) value);
            }
            else if(value instanceof String[]) {
                aliases.addAll(Arrays.asList((String[]) value));
            }
        }
        catch(NoSuchFieldException | IllegalAccessException err) {}

        return aliases.stream()
            .filter(alias -> alias != null)
            .map(alias -> alias.trim().toLowerCase())
            .filter(alias -> !alias.isBlank())
            .distinct()
            .toList();
    }

    /**
     * Obtiene el traductor.
     * @param formato El formato en el que se quiere traducir.
     * @return El objeto traductor.
     */
    @SuppressWarnings("null")
    public I getObject(String formato) {
        Class<? extends I> clazz = classes.get(formato.toLowerCase());

        if(clazz == null) {
            String formatos = classes.keySet().stream().collect(Collectors.joining("\n - "));
            logger.error("'{}': Formato desconocido. Disponibles:\n - {}", formato, formatos);
            System.exit(2);
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene un mapa que asocia cada clases con su lista de alias a partir del mapa que relaciona cada alias con su clase corrspondiente
     * @param <T> El tipo de la interfaz
     * @return Otro mapa en que cada clase está relacionada con todos los alias que tiene.
     */
    public Map<Class<? extends I>, String[]> getAliasesByClass() {
        return classes.entrySet().stream()
            .collect(Collectors.groupingBy(
                Map.Entry::getValue,
                Collectors.mapping(
                    Map.Entry::getKey,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            Collections.sort(list);
                            return list.toArray(String[]::new);
                        }
                    )
                )
            ));
    }

    public Map<String, Class<? extends I>> getClasses() {
        return classes;
    }
}
