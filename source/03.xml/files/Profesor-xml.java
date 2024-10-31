public class Profesor {

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    @JacksonXmlProperty(isAttribute = true)
    private String sustituye;

    @JacksonXmlProperty(isAttribute = true)
    private String casillero;

    private String apelativo;
    private String nombre;
    private String apellidos;
    private String departamento;

    public Profesor() {}

    // Deberíamos tratar "casillero" y "sustituye"...
    public Profesor(String id, String apelativo, String nombre, String apellidos, String departamento, String nacimiento) {
        setId(id);
        setApelativo(apelativo);
        setNombre(nombre);
        setApellidos(apellidos);
        setDepartamento(departamento);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSustituye() { return sustituye; }
    public void setSustituye(String sustituye) { this.sustituye = sustituye; }
    public String getCasillero() { return casillero; }
    public void setCasillero(String casillero) { this.casillero = casillero; }
    public String getApelativo() { return apelativo; }
    public void setApelativo(String apelativo) { this.apelativo = apelativo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String toString() {
        return String.format("%s, %s [%s] (%s)", apellidos, nombre, id, departamento);
    }
}
