public interface Conexion {

    @FunctionalInterface
    public interface Transaccionable {
        void run(Crud<Centro> centroDao, Crud<Estudiante> estudianteDao) throws DataAccessException;
    }

    public Crud<Centro> getCentroDao();
    public Crud<Estudiante> getEstudianteDao();

    public void transaccion(Transaccionable operaciones) throws DataAccessException;
}
