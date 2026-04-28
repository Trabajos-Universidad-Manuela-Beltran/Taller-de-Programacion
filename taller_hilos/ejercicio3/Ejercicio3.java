// Ejercicio 3: Simular una cuenta regresiva usando hilos
//
// Muestra cómo pasar datos a un hilo mediante el constructor
// y cómo usar Thread.sleep() para introducir pausas sin bloquear
// otros hilos que pudieran estar corriendo en paralelo.

class CuentaRegresiva implements Runnable {
    private int inicio; // Valor desde el que empieza la cuenta

    CuentaRegresiva(int inicio) {
        this.inicio = inicio;
    }

    public void run() {
        // Iteramos de 'inicio' hasta 0 de forma descendente.
        for (int i = inicio; i >= 0; i--) {
            System.out.println("Cuenta: " + i);
            try {
                // sleep() pausa SOLO este hilo (en milisegundos).
                // Otros hilos del programa siguen corriendo durante la pausa.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // InterruptedException ocurre si alguien llama hilo.interrupt()
                // mientras está durmiendo. Buena práctica: no ignorarla.
                e.printStackTrace();
            }
        }
        System.out.println("¡Despegue!");
    }
}

public class Ejercicio3 {
    public static void main(String[] args) {
        // Pasamos el valor inicial por constructor: la cuenta empieza en 10.
        Thread hilo = new Thread(new CuentaRegresiva(10));
        hilo.start();
    }
}
