// Ejercicio 1: Hilo que imprime números del 1 al 10
//
// Implementamos Runnable en lugar de extender Thread para mantener
// la clase libre de heredar comportamiento innecesario de Thread.
// El método run() es el punto de entrada del hilo: todo lo que esté
// aquí se ejecuta cuando el hilo arranca.

class Contador implements Runnable {
    public void run() {
        // Este bucle se ejecuta dentro del hilo secundario,
        // no en el hilo principal (main).
        for (int i = 1; i <= 10; i++) {
            System.out.println("Número: " + i);
        }
    }
}

public class Ejercicio1 {
    public static void main(String[] args) {
        // Envolvemos la tarea (Runnable) dentro de un Thread.
        // El objeto Thread sabe cómo lanzar un hilo del sistema operativo.
        Thread hilo = new Thread(new Contador());

        // start() crea el hilo y llama a run() en ese nuevo hilo.
        // Si llamáramos a hilo.run() directamente, se ejecutaría en main
        // sin ninguna concurrencia.
        hilo.start();
    }
}
