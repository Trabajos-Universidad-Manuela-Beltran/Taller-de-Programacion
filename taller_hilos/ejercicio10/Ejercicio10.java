// Ejercicio 10: Clase con múltiples hilos interactuando — Productor/Consumidor
//
// Patrón clásico de concurrencia: un hilo produce datos y otro los consume.
// El Almacen actúa como buffer compartido de capacidad limitada.
//
// Problema sin sincronización: el productor podría sobreescribir datos
// que el consumidor no ha leído, o el consumidor podría leer de un buffer vacío.
// Solución: synchronized + wait()/notifyAll() garantizan acceso seguro.

import java.util.LinkedList;
import java.util.Queue;

class Almacen {
    private Queue<Integer> cola = new LinkedList<>();
    private final int capacidad = 5; // Número máximo de items simultáneos

    // synchronized garantiza que solo un hilo ejecuta este método a la vez.
    synchronized void producir(int item) throws InterruptedException {
        // Si el almacén está lleno, el productor espera (libera el lock)
        // hasta que el consumidor notifique que hay espacio.
        while (cola.size() == capacidad) {
            System.out.println("Almacén lleno, productor esperando...");
            wait(); // Suspende este hilo y libera el monitor del objeto
        }
        cola.add(item);
        System.out.println("Producido: " + item + " | Stock: " + cola.size());
        // Despierta a todos los hilos que estén en wait() sobre este objeto.
        // El consumidor que espera por items sabrá que ya hay algo en la cola.
        notifyAll();
    }

    synchronized int consumir() throws InterruptedException {
        // Si no hay nada que consumir, el consumidor espera.
        while (cola.isEmpty()) {
            System.out.println("Almacén vacío, consumidor esperando...");
            wait();
        }
        int item = cola.poll(); // Extrae el primer elemento de la cola
        System.out.println("Consumido: " + item + " | Stock: " + cola.size());
        // Notifica al productor que hay espacio libre en el almacén.
        notifyAll();
        return item;
    }
}

class Productor implements Runnable {
    private Almacen almacen;

    Productor(Almacen almacen) {
        this.almacen = almacen; // Referencia compartida con el consumidor
    }

    public void run() {
        // Produce 8 items numerados del 1 al 8.
        for (int i = 1; i <= 8; i++) {
            try {
                almacen.producir(i);
                Thread.sleep(300); // Produce un item cada 300 ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Consumidor implements Runnable {
    private Almacen almacen;

    Consumidor(Almacen almacen) {
        this.almacen = almacen; // Mismo objeto Almacen que usa el Productor
    }

    public void run() {
        // Consume los mismos 8 items que producirá el Productor.
        for (int i = 0; i < 8; i++) {
            try {
                almacen.consumir();
                Thread.sleep(600); // Consume más lento que el productor → almacén se llena
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class Ejercicio10 {
    public static void main(String[] args) {
        // Un único Almacen compartido entre Productor y Consumidor.
        // Es el recurso compartido que requiere sincronización.
        Almacen almacen = new Almacen();

        Thread productor  = new Thread(new Productor(almacen),  "Productor");
        Thread consumidor = new Thread(new Consumidor(almacen), "Consumidor");

        // Ambos corren en paralelo e interactúan a través del Almacen.
        productor.start();
        consumidor.start();
    }
}
