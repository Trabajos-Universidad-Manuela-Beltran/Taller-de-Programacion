// Ejercicio 5: Ejecutar tres hilos simultáneamente
//
// La misma clase Tarea sirve para los tres hilos; cada instancia
// recibe un nombre diferente para identificarse en la salida.
// Gracias al sleep(), los hilos se intercalan visiblemente.

class Tarea implements Runnable {
    private String nombre; // Identificador del hilo para la consola

    Tarea(String nombre) {
        this.nombre = nombre;
    }

    public void run() {
        for (int i = 1; i <= 4; i++) {
            System.out.println(nombre + " - iteración " + i);
            try {
                // La pausa hace que el planificador tenga oportunidad
                // de dar tiempo de CPU a los otros dos hilos.
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(nombre + " terminó.");
    }
}

public class Ejercicio5 {
    public static void main(String[] args) {
        // Creamos tres hilos con la misma lógica pero distinto nombre.
        Thread t1 = new Thread(new Tarea("Hilo-1"));
        Thread t2 = new Thread(new Tarea("Hilo-2"));
        Thread t3 = new Thread(new Tarea("Hilo-3"));

        // Los tres arrancan casi al mismo tiempo; la JVM y el SO
        // los ejecutan de forma concurrente.
        t1.start();
        t2.start();
        t3.start();
    }
}
