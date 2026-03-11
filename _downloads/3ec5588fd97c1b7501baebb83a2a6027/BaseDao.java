/**
 * Base para la construcción de todas las clases DAO.
 * <p>Proporciona el constructor y métodos para simplificar
 * el acceso a la conexión y al gestor de registros diferidos.
 */
public abstract class BaseDao<T extends Entity> implements Crud<T> {

    /** Clave que identifica la conexión */
    private final Conexion cx;

    /**
     * Constructor
     * @param key Clave que identifica la conexión.
     */
    protected BaseDao(String key) {
        // La clave no se necesita en realidad porque Conexion es Singleton.
        cx = Conexion.get();
    }

    /**
     * Obtiene el {@link LoggingManager} asociado a la conexión actual.
     * @return El gestor de logging solicitado.
     */
    protected LoggingManager getLoggingManager() {
        return cx.getLoggingManager();
    }

    /**
     * Obtiene la conexión asociada a la transacción actual.
     * @return La conexión solicitada.
     */
    protected Connection getConnection() {
        return cx.getConnection();
    }
}
