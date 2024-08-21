// App.java
public class App {
    public static void main(String[] args) throws Exception {
        Victoria ganaElMayor = new Victoria() {
            public int comprobar(int[] num) {
                int max = -1;
                int idx = 0, i;

                for(i=0; i<num.length; i++) {
                    if(num[i] > max) {
                        max = num[i];
                        idx = i;
                    }
                }
                return idx;
            }
        };

        String[] jugadores = {"Juan", "Alberto", "Pablo"};
        Partida partida = new Partida(jugadores, ganaElMayor);
        partida.jugar(new int[] {100, 20, 35});
        partida.jugar(new int[] {0, 90, 45});
        System.out.printf("El vencedor es %s.\n", partida.vencedor());
    }

}
