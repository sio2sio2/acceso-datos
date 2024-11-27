public class FunctionalResultSet {

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
     * Como Function<T, R> pero permite propagar una SQLException.
     */
    @FunctionalInterface
    public static interface CheckedFunction<T, R> {
        R apply(T t) throws SQLException;
    }

    /**
     * Transforma el SQLEception que propaga una CheckedFunction en un RuntimeException, que es una excepción
     * que no necesita ser declarada.
     * @param <T> El tipo que devuelve CheckedFunction.
     * @param checked Un "función" CheckedFunction.
     * @return Una "función" que ha sustituido SQLException por RuntimeException.
     */
    public static <T> Function<ResultSet, T> checkedToUnchecked(CheckedFunction<ResultSet, T> checked) {
        return t -> {
            try {
                return checked.apply(t);
            }
            catch(SQLException err) {
                throw new RuntimeException(err);
            }
        };
    }

    /**
     * Genera un flujo con las filas generadas en un ResultSet.
     * @param rs Los resutados de una consulta.
     * @return Un flujo en el que cada elemento es el siguiente estado del ResultSet proporcionado.
     * @throws SQLException Cuando se produce un error al realizar la consulta.
     */
    public static Stream<ResultSet> resultSetToStream(ResultSet rs) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ResultSetIterator(rs), Spliterator.ORDERED), false);
    }

    /**
     * Genera un flujo de objetos derivados del resultado de una consulta.
     * @param <T> La clase del objeto.
     * @param rs  El objeto que representa los resultado de la consulta.
     * @param mapper La función que permite transformar la fila en un objeto (puede generar un SQLException).
     * @return El flujo de objetos.
     * @throws SQLException Cuando Cuando se produce un error al realizar la consulta.
     */
    public static <T> Stream<T> resultSetToStream(ResultSet rs, CheckedFunction<ResultSet, T> mapper) {
        return resultSetToStream(rs).map(checkedToUnchecked(mapper));
    }
}
