// Alumno.java

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.JsonAdapter;

public class Alumno implements Serializable {
    private String nombre;
    
    @JsonAdapter(YyyyMMddDateTypeAdapter.class)
    private Date nacimiento;

    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public Alumno() {}

    public Alumno(String nombre, String nacimiento) throws ParseException {
        setNombre(nombre);
        setNacimiento(nacimiento);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }

    public void setNacimiento(String nacimiento) throws ParseException {
        this.nacimiento = df.parse(nacimiento);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", nombre, df.format(nacimiento));
    }
}
