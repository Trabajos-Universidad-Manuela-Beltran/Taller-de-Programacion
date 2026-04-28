// Ejercicio 6: Hilo que imprime letras de la A a la Z
//
// En Java los char son enteros, por lo que se puede iterar
// incrementando la variable como si fuera un número.
// El hilo secundario se encarga de recorrer todo el alfabeto.

class ImpresionLetras implements Runnable {
    public void run() {
        // 'A' vale 65 y 'Z' vale 90 en Unicode/ASCII.
        // El bucle avanza de carácter en carácter hasta Z.
        for (char c = 'A'; c <= 'Z'; c++) {
            System.out.println("Letra: " + c);
        }
    }
}

public class Ejercicio6 {
    public static void main(String[] args) {
        Thread hilo = new Thread(new ImpresionLetras());
        // start() delega la impresión de letras al hilo secundario.
        hilo.start();
    }
}
