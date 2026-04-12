package edu.acceso.tarea_4_1.infraestructure.persistence;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;

import edu.acceso.sqlutils.DataSourceFactory;

/**
 * Fábrica de DataSource para H2 Database.
 */
public class H2CPFactory implements DataSourceFactory {

    private final static int DEFAULT_POOL_SIZE = 5;
    private final int poolSize;

    public H2CPFactory() {
        this(DEFAULT_POOL_SIZE);
    }

    public H2CPFactory(int poolSize) {
        this.poolSize = poolSize;
    }

    @Override
    public DataSource create(String url, String user, String password) {
        JdbcConnectionPool ds = JdbcConnectionPool.create(url, user, password);
        ds.setMaxConnections(poolSize);
        return ds;
    }
}
