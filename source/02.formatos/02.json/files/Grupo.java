import java.util.Arrays;

public class Grupo {
    private short nivel;
    private String etapa;
    private char grupo;
    private Tutor tutor;
    private Alumno[] miembros;

    public Grupo() {}

    public Grupo(short nivel, String etapa, char grupo, Tutor tutor, Alumno[] miembros) {
        setNivel(nivel);
        setEtapa(etapa);
        setGrupo(grupo);
        setTutor(tutor);
        setMiembros(miembros);
    }

    public short getNivel() {
        return nivel;
    }

    public void setNivel(short nivel) {
        this.nivel = nivel;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa =  etapa;
    }

    public char getGrupo() {
        return grupo;
    }

    public void setGrupo(char grupo) {
        this.grupo = grupo;
    }

    public Tutor getTutor() {
        return tutor;
    }

    public void setTutor(Tutor tutor) {
        this.tutor = tutor;
    }

    public Alumno[] getMiembros() {
        return miembros;
    }

    public void setMiembros(Alumno[] miembros) {
        this.miembros = miembros;
    }

    public String nombre() {
        return String.format("%dº%s-%c", nivel, etapa, grupo);
    }

    @Override
    public String toString() {
        return String.format("%s (%s): %s", nombre(), tutor, Arrays.toString(miembros));
    }
}
