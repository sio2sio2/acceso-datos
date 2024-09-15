public class Profesor {

    @JacksonXmlProperty(isAttribute = true)
    private String id;

    private String apelativo;
    private String nombre;
    private String apellidos;
    private String departamento;

    public Profesor() {}

    public Profesor(String id, String apelativo, String nombre, String apellidos, String departamento) {
        setId(id);
        setApelativo(apelativo);
        setNombre(nombre);
        setApellidos(apellidos);
        setDepartamento(departamento);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getApelativo() { return apelativo; }
    public void setApelativo(String apelativo) { this.apelativo = apelativo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

}
