// Partida.java
public class Partida {
    
    private String[] jugador;
    private int[] puntuacion;
    private Victoria condicion;

    Partida(String[] nombres, Victoria condicion) {
        jugador = nombres;
        puntuacion = new int[nombres.length];
        this.condicion = condicion;
    }

    public boolean jugar(int[] puntos) {
        if(puntos.length != puntuacion.length) return false;
        for(int i=0; i<puntos.length; i++) puntuacion[i] += puntos[i];
        return true;
    }

    public String vencedor() {
        int vencedor = condicion.comprobar(puntuacion);
        return jugador[vencedor];
    }
}
