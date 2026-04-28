// Ejercicio 9: Comparar ejecución secuencial vs concurrente
//
// La clave está en el uso de join():
//   - Secuencial: arrancamos T1, esperamos (join), arrancamos T2, esperamos.
//     El tiempo total es la suma de los tiempos individuales.
//   - Concurrente: arrancamos T1 y T2, luego esperamos a los dos.
//     El tiempo total es el del hilo más lento, no la suma.

class Trabajo implements Runnable {
    private String nombre;

    Trabajo(String nombre) {
        this.nombre = nombre;
    }

    public void run() {
        // Cada "item" simula una operación que tarda 500 ms (e.g., llamada a red).
        for (int i = 1; i <= 3; i++) {
            System.out.println(nombre + " procesando item " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Ejercicio9 {
    public static void main(String[] args) throws InterruptedException {

        // --- Ejecución SECUENCIAL ---
        System.out.println("=== Ejecución SECUENCIAL ===");
        long inicio = System.currentTimeMillis();

        Thread t1 = new Thread(new Trabajo("Tarea-1"));
        Thread t2 = new Thread(new Trabajo("Tarea-2"));

        // join() después de cada start() obliga a que T1 termine
        // antes de lanzar T2 → comportamiento secuencial.
        t1.start(); t1.join();
        t2.start(); t2.join();

        long tiempoSecuencial = System.currentTimeMillis() - inicio;
        System.out.println("Tiempo secuencial: " + tiempoSecuencial + " ms\n");

        // --- Ejecución CONCURRENTE ---
        System.out.println("=== Ejecución CONCURRENTE ===");
        inicio = System.currentTimeMillis();

        Thread c1 = new Thread(new Trabajo("Tarea-1"));
        Thread c2 = new Thread(new Trabajo("Tarea-2"));

        // Arrancamos ambos antes de hacer join() a cualquiera.
        // Los dos hilos trabajan al mismo tiempo.
        c1.start();
        c2.start();
        c1.join(); // Esperamos a que c1 termine...
        c2.join(); // ...luego a que c2 termine (probablemente ya acabó).

        long tiempoConcurrente = System.currentTimeMillis() - inicio;
        System.out.println("Tiempo concurrente: " + tiempoConcurrente + " ms");
        // El tiempo concurrente debería ser ~la mitad del secuencial.
    }
}
