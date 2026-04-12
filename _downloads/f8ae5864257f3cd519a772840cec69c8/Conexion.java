/**
 * Clase encargada de establecer la conexión a la base de datos.
 * Para una única base de datos, puede usarse el patrón Singleton,
 * en vez del Multiton que se encuentra en TestDao.
 * Se basa en el uso de JdbcConnection y TransactionManager.
 */
public class Conexion implements AutoCloseable {
   /** Clave que identifica esta clase */
   public static final String KEY = new Object().toString();

   private static Conexion instance;
   private final JdbcConnection jc;

   private Conexion(String dbUrl, String user, String password) {
      jc = JdbcConnection.create(Conexion.KEY, dbUrl, user, password)
         .withTransactionManager(Map.of(LoggingManager.KEY, new LoggingManager()));
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
    * @param key Clave que identifica la conexión. En realidad, no es necesaria
    * al ser esto un patrón Singleton, pero se obliga a usar este parámetro por
    * compatibilidad con el código de TestDAO.
    * @throws IllegalStateException Si el objeto no se había creado antes.
    * @return El objeto solicitado
    */
   public static Conexion get(String key) {
      if(instance == null) throw new IllegalStateException("No existe ningún objeto de Conexion");
      if(!KEY.equals(key)) throw new IllegalArgumentException("La clave es inválida. Use la definida en la propia clase");

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
            if(SqlUtils.isDatabaseEmpty(conn)) return;

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
    * Informa de si el objeto (o sea, el {@link JdbcConnection} que maneja) sigue abierto
    * @return {@code true} si continúa abierto.
    */
   public boolean isOpen() {
      return jc.isOpen();
   }

   /**
    * Cierra el objeto cerrando el {@link JdbcConnection} que maneja)
    */
   @Override
   public void close() {
      jc.close();
   }

   /**
    * Abre, usando el {@link TransactionManager} asociado, una transacción que devuelve resultado
    * @param <T> El tipo que dato que devuelve
    * @param operations Las operaciones que constituyen la transacción.
      @return Los datos devueltos por la transacción.
    * @throws IllegalStateSxception Si la conexión ya está cerrada.
    */
   public <T> T transactionR(TransactionableR<Connection, T> operations) throws DataAccessException {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      return jc.getTransactionManager().transaction(operations);
   }

   /**
    * Abre, usando el {@link TransactionManager} asociado, una transacción.
    * @param operations Las operaciones que constituyen la transacción.
    * @throws IllegalStateException Si la conexión ya está cerrada.
    */
   public void transaction(Transactionable<Connection> operations) throws DataAccessException {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      jc.getTransactionManager().transaction(operations);
   }

   /**
    * Devuelve la conexión asociada a la transacción activa.
    * @return La conexión solicitada
    * @throws IllegalStateException Si la conexión ya está cerrada o no hay conexión activa.
    */
   public Connection getConnection() {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      return jc.getTransactionManager().getConnection();
   }

   /**
    * Acceso cómodo al gestor de registros diferidos.
    * Permite registrar mensajes al término de la transacción
    * para conocer si se confirma o se deshace.
    * @return El gestor de registros solicitado.
    */
   public LoggingManager getLoggingManager() {
      if(!isOpen()) throw new IllegalStateException("La conexión está cerrada");
      return jc.getTransactionManager().getListener(LoggingManager.KEY, LoggingManager.class);
   }
}
