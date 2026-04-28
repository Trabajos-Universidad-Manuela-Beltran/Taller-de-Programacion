// Ejercicio 8: Hilos con pausas usando sleep
//
// Ilustra cómo distintas velocidades de sleep producen ritmos
// de ejecución diferentes en hilos concurrentes.
// El hilo rápido terminará antes aunque hayan arrancado al mismo tiempo.

class TareaConPausa implements Runnable {
    private String nombre;
    private int pausaMs; // Tiempo de pausa en milisegundos entre pasos

    TareaConPausa(String nombre, int pausaMs) {
        this.nombre = nombre;
        this.pausaMs = pausaMs;
    }

    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(nombre + " - paso " + i);
            try {
                // Cada hilo usa su propio tiempo de pausa.
                // sleep() no bloquea a los otros hilos, solo a este.
                Thread.sleep(pausaMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(nombre + " finalizado.");
    }
}

public class Ejercicio8 {
    public static void main(String[] args) {
        // Hilo lento: 1 segundo entre pasos → tarda ~5 s en total.
        Thread lento  = new Thread(new TareaConPausa("Hilo-Lento",  1000));
        // Hilo rápido: 300 ms entre pasos → tarda ~1.5 s en total.
        Thread rapido = new Thread(new TareaConPausa("Hilo-Rapido",  300));

        // Ambos arrancan a la vez; el rápido terminará primero.
        lento.start();
        rapido.start();
    }
}
