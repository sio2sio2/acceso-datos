package edu.acceso.ej4_1.bd;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager implements AutoCloseable {

    private Connection conn;
    private boolean committed;

    public TransactionManager(Connection conn) throws SQLException {
        setConn(conn);
        conn.setAutoCommit(false);
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void commit() throws SQLException {
        conn.commit();
        committed = true;
    }

    @Override
    public void close() throws SQLException {
        if(!committed) conn.rollback();
        conn.setAutoCommit(true);
    }
}
