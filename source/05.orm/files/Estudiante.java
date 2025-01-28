package edu.acceso.test_hibernate.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private LocalDate nacimiento;

    @ManyToOne
    @JoinColumn(name = "id_centro", nullable = false) // El campo se llama id_centro en la BD.
    private Centro centro;

    public Estudiante() {
        super();
    }

    public Estudiante cargarDatos(String nombre, LocalDate nacimiento, Centro centro) {
        setNombre(nombre);
        setNacimiento(nacimiento);
        setCentro(centro);
        return this;
    }

    public Estudiante(String nombre, LocalDate nacimiento, Centro centro) {
        cargarDatos(nombre, nacimiento, centro);
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
        return String.format("%s (%s, % d años)", getNombre(), getCentro().getNombre(), ChronoUnit.YEARS.between(getNacimiento(), LocalDate.now()));
    }
    
}
