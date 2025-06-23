import java.util.Map;

public class Singleton {
    private static Singleton instance;

    // Posiblemente haya atributos de instancia (y sus correspondintes getters)

    private Singleton(Map<String, String> args) {
        // Utilizamos los argumentos para
        // definir los valores de los atributos
    }

    public static Singleton initialize(Map <String, String> args) {
        if(instance == null) {
            instance = new Singleton(args);
            return instance;
        }
        throw new IllegalStateException("La instancia ya fue inicializada");
    }

    public static Singleton getInstance() {
        if(instance == null) throw new IllegalStateException("La instancia no está inicializada: use .initialize");
        return instance;
    }
}
