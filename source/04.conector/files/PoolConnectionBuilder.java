import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import edu.acceso.sqlutils.DataAccessException;

/** Genera conexiones a la base de datos incluidas en un pool de conexiones */
public class PoolConnectionBuilder implements AutoCloseable {

    private final HikariDataSource ds;
    public static short maxConnections = 10;
    public static short minConnections = 1;

    private PoolConnectionBuilder(String url, String user, Strin password) {
        HikariConfig hconfig = new HikariConfig();
        hconfig.setJdbcUrl(url);
        hconfig.setUsername(user);
        hconfig.setPassword(password);
        // Mínimo y máximo de conexiones.
        hconfig.setMaximumPoolSize(maxConnections);
        hconfig.setMinimumIdle(minConnections);

        ds = new HikariDataSource(hconfig);
    }

    public static PoolConnectionBuilder getInstance(String url) {
        return PoolConnectionBuilder.getInstance(url, null, null);
    }

    public static PoolConnectionBuilder getInstance(String url, String user, String password) {
        return new PoolConnectionBuilder(url, user, password);
    }

    public Connection getConnection() throws SQLException {
         return ds.getConnection();
    }

    public HikariDataSource getDataSource() {
        return ds;
    }

    @Override
    public void close() {
        ds.close();
    }
}
