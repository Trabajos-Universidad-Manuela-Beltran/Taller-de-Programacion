// Ejercicio 7: Simular un proceso de descarga con hilos
//
// Caso de uso real: descargar varios archivos a la vez en lugar de
// esperar a que uno termine para empezar el otro.
// Cada hilo simula una barra de progreso propia con sleep().

class Descarga implements Runnable {
    private String archivo; // Nombre del archivo que "descarga" este hilo

    Descarga(String archivo) {
        this.archivo = archivo;
    }

    public void run() {
        System.out.println("Iniciando descarga: " + archivo);

        // Simulamos incrementos de progreso del 0% al 100% en pasos de 20%.
        for (int progreso = 0; progreso <= 100; progreso += 20) {
            System.out.println(archivo + " - " + progreso + "% completado");
            try {
                // Cada "bloque" tarda 400 ms en descargarse.
                // Mientras este hilo duerme, el otro hilo de descarga avanza.
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(archivo + " - Descarga completada.");
    }
}

public class Ejercicio7 {
    public static void main(String[] args) {
        // Lanzamos dos descargas en paralelo: ambas progresan al mismo tiempo.
        Thread d1 = new Thread(new Descarga("archivo1.zip"));
        Thread d2 = new Thread(new Descarga("archivo2.mp4"));
        d1.start();
        d2.start();
    }
}
