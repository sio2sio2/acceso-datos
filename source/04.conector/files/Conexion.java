/**
 * Clase encargada de establecer la conexión a la base de datos.
 * Para una única base de datos, puede usarse el patrón Singleton,
 * en vez del Multiton que se encuentra en TestDao.
 * Se basa en el uso de ConnectionPool y TransactionManager.
 */
public class Conexion implements AutoCloseable {
   /** Clave que identifica esta clase */
   public static final String KEY = new Object().toString();

   private static Conexion instance;
   private final ConnectionPool cp;

   private Conexion(String dbUrl, String user, String password) {
      // Pool de conexiones con HikariCP
      cp = ConnectionPool.create(Conexion.KEY, dbUrl, user, password);
      // Usamos el gestor de transacciones
      // con un listener para gestionar registros diferidos
      cp.initTransactionManager(Map.of(
         LoggingManager.KEY, new LoggingManager()
      ));
   }

   /**
    * Crea el objeto Singleton a partir de los datos suministrados.
    * @param dbUrl URL de conexión.
    * @param user Usuario de conexión a la base de datos.
    * @param password Contraseña.
    * @throws IllegalStateException Si el objeto ya fue creado.
    * @return El objeto creado
    */
   public static Conexion create(String dbUrl, String user, String password) {
      if(instance != null) throw new IllegalStateException("Ya se creó la conexión");

      instance = new Conexion(dbUrl, user, password);
      return instance;
   }

   /**
    * Devuelve el objeto de Conexion.
    * @throws IllegalStateException Si el objeto no se había creado antes.
    * @return El objeto solicitado
    */
   public static Conexion get() {
      if(instance == null) throw new IllegalStateException("No existe ningún objeto de Conexion");

      return instance;
   }

   /**
    * Inicializa la base de datos con el esquema proporcionado
    * @param schema El guion SQL con el esquema y los datos iniciales.
    * @return La propia conexión para encadenar llamadas.
    */
   public Conexion initialize(InputStream schema) throws DataAccessException {
        Objects.requireNonNull(schema, "El esquema no puede ser nulo");

        transaction(ctxt -> {
            Connection conn = ctxt.connection();

            // Si la base de datos ya está inicializada, no hacemos nada.
            if(SqlUtils.isDatabaseInitialized(conn)) return;

            try {
                SqlUtils.executeSQL(conn, schema);
            } catch(SQLException e) {
                throw new DataAccessException("Error al crear el esquema en la base de datos", e);
            } catch(IOException e) {
                throw new RuntimeException("Error al intentar leer el esquema", e);
            }
        });         
        return this;
   }

   /**
    * Informa de si el objeto (o sea, el {@link ConnectionPool} que maneja) sigue abierto
    * @return {@code true} si continúa abierto.
    */
   public boolean isOpen() {
      return cp.isOpen();
   }

   /**
    * Cierra el objeto cerrando el {@link ConnectionPool} que maneja)
    */
   @Override
   public void close() {
      cp.close();
   }

   /**
    * Abre, usando el {@link TransactionManager} asociado, una transacción que devuelve resultado
    * @param <T> El tipo que dato que devuelve
    * @param operations Las operaciones que constituyen la transacción.
      @return Los datos devueltos por la transacción.
    * @throws IllegalStateSxception Si la conexión ya está cerrada.
    */
   public <T> T transactionR(TransactionableR<T> operations) throws DataAccessException {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      return cp.getTransactionManager().transaction(operations);
   }

   /**
    * Abre, usando el {@link TransactionManager} asociado, una transacción.
    * @param operations Las operaciones que constituyen la transacción.
    * @throws IllegalStateException Si la conexión ya está cerrada.
    */
   public void transaction(Transactionable operations) throws DataAccessException {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      cp.getTransactionManager().transaction(operations);
   }

   /**
    * Devuelve la conexión asociada a la transacción activa.
    * @return La conexión solicitada
    * @throws IllegalStateException Si la conexión ya está cerrada o no hay conexión activa.
    */
   public Connection getConnection() {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      return cp.getTransactionManager().getConnection();
   }

   /**
    * Permite acceder cómodamente al gestor de registros diferidos.
    * Es útil cuando se desea registrar mensajes sólo cuando se conoce
    * la suerte de la transacción.
    * @return El gestor de registros solicitado.
    */
   public LoggingManager getLoggingManager() {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      return cp.getTransactionManager().getListener(LoggingManager.KEY, LoggingManager.class);
   }
}
