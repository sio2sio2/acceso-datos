package edu.acceso.ejemplo_conn.backend;

import edu.acceso.ejemplo_conn.modelo.Centro;
import edu.acceso.ejemplo_conn.modelo.Estudiante;
import edu.acceso.sqlutils.Crud;

public interface Conexion {

    public Crud<Centro> getCentroDao();
    public Crud<Estudiante> getEstudianteDao();
}
