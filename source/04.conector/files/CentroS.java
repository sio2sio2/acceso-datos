package edu.acceso.borrarlo.modelo;

import edu.acceso.sqlutils.Entity;

/**
 * Modela un centro de enseñanza.
 */
public class Centro implements Entity {

    /**
     * Código identificativo del centro.
     */
    private int id;
    /**
     * Nombre del centro.
     */
    private String nombre;
    /**
     * Titularidad: pública o privada.
     */
    private String titularidad;

    public Centro() {
        super();
    }

    /**
     * Carga todos los datos en el objeto.
     * @param id Código del centro.
     * @param nombre Nombre del centro.
     * @param titularidad Titularidad del centro.
     * @return El propio objeto.
     */
    public Centro cargarDatos(int id, String nombre, String titularidad) {
        setId(id);
        setNombre(nombre);
        setTitularidad(titularidad);
        return this;
    }

    /**
     * Constructor que admite todos los datos de definición del centro.
     * @param id Código del centro.
     * @param nombre Nombre del centro.
     * @param titularidad Titularidad del centro (pública o privada)
     */
    public Centro(int id, String nombre, String titularidad) {
        cargarDatos(id, nombre, titularidad);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTitularidad() {
        return titularidad;
    }

    public void setTitularidad(String titularidad) {
        this.titularidad = titularidad;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getNombre(), getId());
    }
}
