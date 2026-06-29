package edu.acceso.test_dao.persistence.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.acceso.test_dao.modelo.Centro;
import edu.acceso.test_dao.modelo.Centro.Titularidad;

/**
 * Implementación de {@link Crud} para la entidad {@link Centro} usando SQL.
 * Esta clase proporciona métodos para realizar operaciones CRUD sobre centros
 * en una base de datos relacional.
 */
@Repository
public class CentroSqlDao implements Crud<Centro> {
    private static final Logger logger = LoggerFactory.getLogger(CentroSqlDao.class);

    private final JdbcTemplate jt;

    /**
     * Constructor que inicializa el proveedor de conexiones con una conexión existente.
     * @param jt El {@link JdbcTemplate} a usar para las operaciones de base de datos.
     */
    public CentroSqlDao(JdbcTemplate jt) {
        this.jt = jt;
    }

    /**
     * Convierte un {@link ResultSet} en un objeto {@link Centro}.
     *
     * @param rs El {@link ResultSet} que contiene los datos del centro.
     * @return Un objeto {@link Centro} con los datos del {@link ResultSet}.
     * @throws SQLException Si ocurre un error al acceder a los datos del {@link ResultSet}.
     */
    static Centro resultSetToCentro(ResultSet rs, String prefix) throws SQLException {
        Long id = rs.getLong(prefix + "id");
        String nombre = rs.getString(prefix + "nombre");
        Titularidad titularidad = Titularidad.fromString(rs.getString(prefix + "titularidad"));
        return new Centro(id, nombre, titularidad);
    }

    @Override
    public Optional<Centro> get(Long id) {
        String sqlString = "SELECT * FROM Centro WHERE id = ?";
        Centro centro = null;

        try {
            centro = jt.queryForObject(sqlString, (rs, rowNum) -> resultSetToCentro(rs, ""), id);
            logger.trace("Centro con ID={} encontrado: {}", id, centro);
        } catch(EmptyResultDataAccessException e) {
            logger.trace("Centro con ID={} no encontrado", id);
        }

        return Optional.ofNullable(centro);
    }

    @Override
    public List<Centro> get() {
        String sqlString = "SELECT * FROM Centro";

        List<Centro> centros = jt.query(sqlString, (rs, rowNum) -> resultSetToCentro(rs, ""));
        logger.trace("{} centros encontrados", centros.size());
        return centros;
    }

    @Override
    public void delete(Long id) {
        String sqlString = "DELETE FROM Centro WHERE id = ?";

        boolean deleted = jt.update(sqlString, id) > 0;

        // TODO: Por ahora no se tiene en cuenta que abortar una transacción podría generar un registro fantasma.
        if(deleted) logger.trace("Centro con ID={} borrado", id);
        else logger.trace("Centro con ID={} no encontrado", id);
    }

    @Override
    public void insert(Centro centro) {
        String sqlString = "INSERT INTO Centro (nombre, titularidad, id) VALUES (?, ?, ?)";

        jt.update(sqlString, centro.getNombre(), centro.getTitularidad().toString(), centro.getId());
        logger.trace("Centro con ID={} insertado", centro.getId());
    }

    @Override
    public void update(Centro centro) {
        String sqlString = "UPDATE Centro SET nombre = ?, titularidad = ? WHERE id = ?";

        boolean updated = jt.update(sqlString, centro.getNombre(), centro.getTitularidad().toString(), centro.getId()) > 0;
        if(updated) logger.trace("Centro con ID={} actualizado", centro.getId());
        else logger.trace("Centro con ID={} no encontrado", centro.getId());
    }

    @Override
    public void update(Long oldId, Long newId) {
        String sqlString = "UPDATE Centro SET id = ? WHERE id = ?";

        boolean updated = jt.update(sqlString, newId, oldId) > 0;
        if(updated) logger.trace("Centro con ID={} actualizado a ID={}", oldId, newId);
        else logger.trace("Centro con ID={} no encontrado", oldId);
    }
}