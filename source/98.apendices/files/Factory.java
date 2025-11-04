package edu.acceso.tarea_2_4.infrastructure.factory;

import java.lang.reflect.Field;
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

/**
 * Implementa el patrón Factory para escoger una clase entre varias que implementan una misma interfaz
 * 
 * <p>Para ello necesitamos una interfaz (p.ej. {@code MiInterfaz}) implementada por varias clases. Cada una de ellas
 * habrá sido definida del siguiente modo:
 * 
 * <pre>
 *    public class MiClase implements MiInterfaz {
 *       private static final String alias = "nombreCorto";
 *       //private static final String[] alias = { "nombreCorto", "otroNombreCorto" };
 * 
 *       // Implementación de la clase.
 *    }
 * </pre>
 * 
 * <p>Esto permite que la clase pueda ser referida bien por el propio nombre de la clase o bien por cualquiera de los alias
 * definidos.  Si no se define ningún alias, la clase solo podrá ser referida por su nombre. No se distingue entre
 * mayúsculas y minúsculas. De este modo podremos escoger la clase del siguiente modo:
 * 
 * <pre>
 *    Factory&lt;MiInterfaz&gt; factory = new Factory&lt;&gt;("paquete.donde.están.las.clases", MiInterfaz.class);
 *    try {
 *        MiInterfaz instance = factory.getInstance("nombreCorto");
 * 
 *        // Usar la instancia...
 *    } catch(IllegalArgumentException e) {
 *        // El nombre o alias no existe
 *    }
 * </pre>
 * <p>En caso de que la interfaz se encuentre en el mismo paquete que las clases que la implementan, se puede simplificar
 * la creación de la fábrica:
 * <pre>   Factory&lt;MiInterfaz&gt; factory = new Factory&lt;&gt;(MiInterfaz.class);</pre>
 * 
 * <p> {@code .getInstance(String)} sólo funciona si las clases que implementan la interfaz tienen un constructor sin parámetros.
 * En caso contrario puede usarse {@code .get(String)} para obtener la clase y luego crear la instancia manualmente.
 * 
 * @param <I> La interfaz que implementan las clases seleccionables.
 */
public class Factory<I> {
    /**
     * Nombre del atributo que contendrá los nombres alternativos para la clase.
     */
    private final static String alias = "alias";
    /**
     * Mapa cuyas claves son los nombres asociados a una clase
     * que implementa la interfaz {@link I} y cuyos valores son las propias clases
     * que implementan dicha interfaz.
     */
    private final Map<String, Class<? extends I>> classes;

    /**
     * Constructor que escanea el paquete indicado para obtener las clases que implementan la interfaz dada.
     * @param interfaceClass La interfaz que implementan todas las clases que se quieren encontrar.
     * @param packageName El nombre del paquete donde se encuentran las clases que implementan la interfaz.
     */
    public Factory(Class<I> interfaceClass, String packageName) {
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
     * Constructor que escanea el paquete donde se encuentra una interfaz para obtener las clases que la implementan.
     * Se supone que las clases que implementan la interfaz están en el mismo paquete que la propia interfaz.
     * @param interfaceClass La interfaz que implementan todas las clases que se quieren encontrar.
     */
    public Factory(Class<I> interfaceClass) {
        this(interfaceClass, interfaceClass.getPackageName());
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
     * Obtiene la clase apropiada.
     * @param value El nombre de la clase o uno de sus alias
     * @return La clase correspondiente
     * @throws IllegalArgumentException Cuando no existe ninguna clase con ese nombre o alias.
     */
    public Class<? extends I> get(String value) throws IllegalArgumentException {
        Class<? extends I> clazz = classes.get(value.toLowerCase());

        if(clazz == null) {
            String formatos = classes.keySet().stream().collect(Collectors.joining("\n - "));
            throw new IllegalArgumentException(String.format("'%s': Formato desconocido. Disponibles:\n - %s", value, formatos));
        }

        return clazz;
    }

    /**
     * Crea directamente una instancia de la clase correspondiente al nombre o alias dado.
     * 
     * <p>La clase debe tener un constructor sin parámetros y, además, la construcción no debe
     * tener dependencias que puedan cruzarse. En ese caso, debe usarse {@code .get(String)},
     * que no llega a crear la instancia.
     * @param value El nombre de la clase o uno de sus alias
     * @return La instancia de la clase correspondiente
     * @throws IllegalStateException Cuando no se puede crear la instancia. Habitualmente porque no tiene un constructor sin parámetros.
     */
    public I getInstance(String value) throws IllegalStateException {
        Class<? extends I> clazz = get(value);
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(String.format("No se pudo crear una instancia de '%s'.", value), e);
        }
    }

    /**
     * Obtiene un mapa que asocia cada clase con su lista de alias a partir {@link #classes}.
     * Es útil para generar ayuda.
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

    /**
     * Obtiene un mapa que asocia cada alias con su clase correspondiente.
     * @return Un mapa que relaciona cada alias con su clase.
     */
    public Map<String, Class<? extends I>> getClasses() {
        return classes;
    }
}
