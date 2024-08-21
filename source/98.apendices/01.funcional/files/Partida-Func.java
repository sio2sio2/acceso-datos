//Partida.java
import java.util.function.Function;

public class Partida {
    
    private String[] jugador;
    private int[] puntuacion;
    private Function<int[], Integer> condicion;

    Partida(String[] nombres, Function<int[], Integer> condicion) {
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
        int vencedor = condicion.apply(puntuacion);
        return jugador[vencedor];
    }
}
