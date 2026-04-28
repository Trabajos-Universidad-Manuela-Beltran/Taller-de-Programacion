// Ejercicio 4: Hilo que calcula la suma de números del 1 al N
//
// Demuestra que los hilos pueden realizar cómputo real, no solo imprimir.
// El resultado se calcula en el hilo secundario y se muestra al terminar.

class Sumador implements Runnable {
    private int n; // Límite superior de la suma

    Sumador(int n) {
        this.n = n;
    }

    public void run() {
        int suma = 0;
        // Acumulamos la suma de 1 hasta n dentro del hilo.
        // Este trabajo ocurre en paralelo con lo que haga el hilo principal.
        for (int i = 1; i <= n; i++) {
            suma += i;
        }
        // Una vez terminado el cómputo, mostramos el resultado.
        // Para compartir el resultado con otros hilos de forma segura
        // se necesitaría sincronización (ver ejercicio 10).
        System.out.println("Suma del 1 al " + n + " = " + suma);
    }
}

public class Ejercicio4 {
    public static void main(String[] args) {
        // Creamos el hilo indicando que calcule la suma hasta 100.
        Thread hilo = new Thread(new Sumador(100));
        hilo.start(); // La suma se realiza en un hilo aparte.
    }
}
