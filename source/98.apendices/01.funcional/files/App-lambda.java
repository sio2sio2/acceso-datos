// App.java
public class App {
    public static void main(String[] args) throws Exception {
        Victoria ganaElMenor = puntos -> {
            int min = puntos[0];
            int idx = 0, i;

            for(i=1; i<puntos.lpuntos; i++) {
                if(puntos[i] < min) {
                    min = puntos[i];
                    idx = i;
                }
            }
            return idx;
        };

        String[] jugadores = {"Juan", "Alberto", "Pablo"};
        Partida partida = new Partida(jugadores, ganaElMenor);
        partida.jugar(new int[] {100, 20, 35});
        partida.jugar(new int[] {0, 90, 45});
        System.out.printf("El vencedor es %s.\n", partida.vencedor());
    }
}
