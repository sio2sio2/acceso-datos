package edu.acceso.ejemplo_conn.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import edu.acceso.sqlutils.Entity;
import edu.acceso.sqlutils.annotations.Fk;


/**
 * Modela un estudiante.
 */
public class Estudiante implements Entity {

    /**
     * Identificador del estudiante.
     */
    private int id;
    /**
     * Nombre completo del estudiante.
     */
    private String nombre;
    /**
     * Fecha de nacimiento del estudiante.
     */
    private LocalDate nacimiento;

    /**
     * Centro al que está adscrito.
     */
    @Fk private Centro centro;

    public Estudiante() {
        super();
    }

    /**
     * Carga los datos del estudiante.
     * @param id El identificador del estudiante.
     * @param nombre El nombre del estudiante.
     * @param nacimiento La fecha de nacimiento.
     * @param centro El centro al que está adscrito.
     * @return El propio objeto.
     */
    public Estudiante cargarDatos(int id, String nombre, LocalDate nacimiento, Centro centro) {
        setId(id);
        setNombre(nombre);
        setNacimiento(nacimiento);
        setCentro(centro);

        return this;
    }

    /**
     * Constructor que carga todos los datos.
     * @param id El identificador del estudiante.
     * @param nombre El nombre del estudiante.
     * @param nacimiento La fecha de nacimiento.
     * @param centro El centro al que está adscrito.
     * @return El propio objeto.
     */
    public Estudiante(int id, String nombre, LocalDate nacimiento, Centro centro) {
        this.cargarDatos(id, nombre, nacimiento, centro);
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
    public LocalDate getNacimiento() {
        return nacimiento;
    }
    public void setNacimiento(LocalDate nacimiento) {
        this.nacimiento = nacimiento;
    }
    public Centro getCentro() {
        return centro;
    }
    public void setCentro(Centro centro) {
        this.centro = centro;
    }
    
    @Override
    public String toString() {
        LocalDate hoy = LocalDate.now();

        return String.format("%s (%d años)", getNombre(), ChronoUnit.YEARS.between(getNacimiento(), hoy));
    }
}
