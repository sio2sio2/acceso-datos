public class Alumno {
    private String nombre;
    private LocalDate fechaNacimiento;

    public Alumno() {}

    public Alumno(String nombre, int edad) {
        setNombre(nombre);
        setEdad(edad);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
       this.nombre = nombre;
    }

   public LocalDate getFechaNacimiento() {
       return fechaNacimiento
   }

    public int getEdad() {
       return Perido.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    public void setFechaNacimiento(LocalDate nacimiento) {
        this.fechaNacimiento = nacimiento;
    }

    @Override
    public String toString() {
        return String.format("%s: %d", nombre, edad);
    }
}
