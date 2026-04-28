// Ejercicio 2: Dos hilos que imprimen mensajes diferentes
//
// Cada clase representa una tarea independiente. Al lanzar ambos hilos
// con start(), el sistema operativo los intercala según su planificador,
// por eso el orden de los mensajes puede variar en cada ejecución.

class MensajeA implements Runnable {
    public void run() {
        // Hilo A imprime su propio mensaje 5 veces.
        for (int i = 0; i < 5; i++) {
            System.out.println("Hilo A: Hola desde el primer hilo");
        }
    }
}

class MensajeB implements Runnable {
    public void run() {
        // Hilo B imprime un mensaje distinto al de A, también 5 veces.
        // Ambos hilos corren al mismo tiempo, así que sus líneas
        // pueden aparecer entremezcladas en la consola.
        for (int i = 0; i < 5; i++) {
            System.out.println("Hilo B: Hola desde el segundo hilo");
        }
    }
}

public class Ejercicio2 {
    public static void main(String[] args) {
        Thread hiloA = new Thread(new MensajeA());
        Thread hiloB = new Thread(new MensajeB());

        // Ambos start() se llaman uno tras otro en main.
        // Java lanza los dos hilos casi simultáneamente;
        // no esperamos a que A termine para arrancar B.
        hiloA.start();
        hiloB.start();
    }
}
