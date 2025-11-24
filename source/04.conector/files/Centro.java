package edu.acceso.test_dao.modelo;

import java.util.Arrays;

/**
 * Modela un centro de enseñanza.
 */
public class Centro implements Entity {

    public static enum Titularidad {
        PUBLICA("público"),
        PRIVADA("privado");

        private String desc;

        Titularidad(String desc) {
            this.desc = desc;
        }

        @Override
        public String toString() {
            return desc;
        }

        /**
         * Obtiene la titularidad a partir de la descripción.
         * @param desc La descripción
         * @return El elemento Titularidad o null, si no hay ninguno con esa descripción.
         */
        public static Titularidad fromString(String desc) {
            return Arrays.stream(Titularidad.values())
                .filter(t -> t.getDescripcion().compareToIgnoreCase(desc) == 0)
                .findFirst()
                .orElse(null);
        }
    }

    /**
     * Código identificativo del centro.
     */
    private Long id;
    /**
     * Nombre del centro.
     */
    private String nombre;
    /**
     * Titularidad: pública o privada.
     */
    private Titularidad titularidad;

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
    public Centro cargarDatos(Long id, String nombre, Titularidad titularidad) {
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
    public Centro(Long id, String nombre, Titularidad titularidad) {
        cargarDatos(id, nombre, titularidad);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Titularidad getTitularidad() {
        return titularidad;
    }

    public void setTitularidad(Titularidad titularidad) {
        this.titularidad = titularidad;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", getNombre(), getId());
    }
}
