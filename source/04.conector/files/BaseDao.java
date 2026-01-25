package edu.acceso.tarea_4_1.infraestructure.persistence.dao;

import java.sql.Connection;

import edu.acceso.sqlutils.crud.Entity;
import edu.acceso.sqlutils.errors.DataAccessException;
import edu.acceso.tarea_4_1.infraestructure.persistence.Conexion;

public abstract class BaseDao<T extends Entity> implements Crud<T> {

    protected final String key;

    BaseDao(String key) {
        this.key = key;
    }

    protected Connection getConnection() throws DataAccessException{
        return TransactionManager.get(key).getConnection();
    }
}
