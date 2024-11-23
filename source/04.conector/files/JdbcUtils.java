package edu.acceso.probando_conector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Clase que implementa algunos métodos estáticos adicionales a JDBC.
 */
public class JdbcUtils {

    /**
     * Implementa un iterador a partir de un ResultSet.
     */
    private static class ResultSetIterator implements Iterator<ResultSet> {

        private final ResultSet rs;
        private boolean avanzar;
        private boolean hasNextElement;

        public ResultSetIterator(ResultSet rs) {
            this.rs = rs;
            avanzar = true;
        }

        @Override
        public boolean hasNext() {
            if(avanzar) {
                try {
                    hasNextElement = rs.next();
                }
                catch(SQLException err) {
                    throw new RuntimeException(err);
                }
                finally {
                    avanzar = false;
                }
            }
            return hasNextElement;
        }


        @Override
        public ResultSet next() {
            avanzar = true;
            return rs;
        }
    }
    
    /**
     * Genera un flujo con las filas generadas en un ResultSet.
     * @param rs Los resutados de una consulta.
     * @return Un flujo en el que cada elemento es el siguiente estado del ResultSet proporcionado.
     * @throws SQLException Cuando se produce un error al realizar la consulta.
     */
    public static Stream<ResultSet> resultSetToStream(ResultSet rs) throws SQLException {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(rs), Spliterator.ORDERED), false);
    }

    /**
     * Genera un flujo de objetos derivados del resultado de una consulta.
     * @param <T> La clase del objeto.
     * @param rs  El objeto que representa los resultado de la consulta.
     * @param mapper La función que permite transformar la fila en un objeto.
     * @return El flujo de objetos.
     * @throws SQLExceptionCuando Cuando se produce un error al realizar la consulta.
     */
    public static <T> Stream<T> resultSetToStream(ResultSet rs, Function<ResultSet, T> mapper) throws SQLException {
        return resultSetToStream(rs).map(mapper);
    }
}
