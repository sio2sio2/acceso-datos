// App.java
public class App {
    public static void main(String[] args) throws Exception {
        String[] jugadores = {"Juan", "Alberto", "Pablo"};
        Partida partida = new Partida(jugadores, puntos -> puntos.length - 1);
        partida.jugar(new int[] {100, 20, 35});
        partida.jugar(new int[] {0, 90, 45});
        System.out.printf("El vencedor es %s.\n", partida.vencedor());
    }
}
