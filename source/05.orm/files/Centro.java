package edu.acceso.test_hibernate.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Centro") // Sólo útil si la table se llama de modo diferente.
public class Centro {

    public static enum Titularidad {
        PUBLICA, PRIVADA
    }

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)  // GENERATED ALWAYS BY IDENTITY
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Titularidad titularidad;

    public Centro() {
        super();
    }

    public Centro cargarDatos(long id, String nombre, Titularidad titularidad) {
        setId(id);
        setNombre(nombre);
        setTitularidad(titularidad);
        return this;
    }

    public Centro(long id, String nombre, Titularidad titularidad) {
        cargarDatos(id, nombre, titularidad);
    }

    public Long getId() {
        return id;
    }

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
        return String.format("%s (%d)", getNombre(), getId());
    }
}