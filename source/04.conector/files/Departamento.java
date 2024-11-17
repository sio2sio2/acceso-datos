/*
 * Esto en realidad se debería comportar como un Enum. Podríamos redefinir la
 * clase, para que tuviera ese comportamiento y al cargar los datos de la tabla,
 * impidiera crear más objetos.
 */
public class Departamento {

    private int id;
    private String denominacion;

    public Departamento() { super(); }

    public Departamento cargarDatos(int id, String denominacion) {
        setId(id);
        setDenominacion(denominacion);
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }
}
