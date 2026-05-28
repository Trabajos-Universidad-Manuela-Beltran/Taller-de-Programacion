/**
 * Ejercicio 2 - Chat P2P (Java)
 * =================================
 * Taller de Programación con Sockets - Ingeniería de Software
 * Semestre VII
 *
 * DESCRIPCIÓN:
 *   Aplicación de chat peer-to-peer donde dos instancias pueden enviarse
 *   y recibir mensajes simultáneamente.
 *
 *   Cada instancia usa DOS hilos:
 *     - Hilo Receptor : espera mensajes del otro extremo e imprime en pantalla.
 *     - Hilo Emisor   : lee del teclado y envía al otro extremo.
 *
 * CÓMO COMPILAR:
 *   javac ChatP2P.java
 *
 * CÓMO EJECUTAR (dos terminales en la misma máquina):
 *   Terminal A (actúa como servidor, escucha en puerto 6000):
 *     java ChatP2P servidor
 *
 *   Terminal B (actúa como cliente, se conecta a localhost:6000):
 *     java ChatP2P cliente
 *
 *   Para cerrar: escribe 'salir' y presiona Enter en cualquiera de las dos.
 */

import java.io.*;
import java.net.*;

public class ChatP2P {

    private static final int    PUERTO  = 6000;
    private static final String HOST    = "localhost";

    // ── Hilo Receptor ─────────────────────────────────────────────────────────
    /**
     * Escucha continuamente el socket y muestra cada mensaje recibido.
     * Termina cuando el extremo remoto cierra la conexión.
     */
    static class HiloReceptor extends Thread {
        private final BufferedReader entrada;

        HiloReceptor(InputStream is) {
            this.entrada = new BufferedReader(new InputStreamReader(is));
            setDaemon(true);
            setName("Hilo-Receptor");
        }

        @Override
        public void run() {
            try {
                String linea;
                while ((linea = entrada.readLine()) != null) {
                    System.out.println("\n[Otro] " + linea);
                    System.out.print("Tú > ");   // Reimprime el prompt
                }
            } catch (IOException e) {
                // La conexión fue cerrada
            }
            System.out.println("\n[!] El otro usuario cerró la conexión.");
            System.exit(0);
        }
    }

    // ── Hilo Emisor ───────────────────────────────────────────────────────────
    /**
     * Lee del teclado y envía cada línea al extremo remoto.
     * Termina cuando el usuario escribe 'salir'.
     */
    static class HiloEmisor extends Thread {
        private final PrintWriter    salida;
        private final BufferedReader teclado;

        HiloEmisor(OutputStream os) throws IOException {
            this.salida   = new PrintWriter(new OutputStreamWriter(os), true);
            this.teclado  = new BufferedReader(new InputStreamReader(System.in));
            setName("Hilo-Emisor");
        }

        @Override
        public void run() {
            try {
                String linea;
                System.out.print("Tú > ");
                while ((linea = teclado.readLine()) != null) {
                    if (linea.equalsIgnoreCase("salir")) {
                        System.out.println("[+] Cerrando chat...");
                        salida.println("[El otro usuario salió del chat]");
                        break;
                    }
                    salida.println(linea);
                    System.out.print("Tú > ");
                }
            } catch (IOException e) {
                System.err.println("[!] Error al leer entrada: " + e.getMessage());
            }
            System.exit(0);
        }
    }

    // ── Main ──────────────────────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.out.println("Uso: java ChatP2P [servidor|cliente]");
            System.out.println("  servidor → espera conexión en puerto " + PUERTO);
            System.out.println("  cliente  → se conecta a " + HOST + ":" + PUERTO);
            return;
        }

        Socket socket;

        if (args[0].equalsIgnoreCase("servidor")) {
            // ── Modo Servidor ──────────────────────────────────────────────────
            System.out.println("==========================================");
            System.out.println("  CHAT P2P - Modo SERVIDOR");
            System.out.println("  Esperando conexión en puerto " + PUERTO + "...");
            System.out.println("==========================================");

            try (ServerSocket srv = new ServerSocket(PUERTO)) {
                srv.setReuseAddress(true);
                socket = srv.accept();   // Espera al cliente
            }
            System.out.println("[+] Cliente conectado: "
                    + socket.getInetAddress().getHostAddress());

        } else {
            // ── Modo Cliente ──────────────────────────────────────────────────
            System.out.println("==========================================");
            System.out.println("  CHAT P2P - Modo CLIENTE");
            System.out.println("  Conectando a " + HOST + ":" + PUERTO + "...");
            System.out.println("==========================================");

            socket = new Socket();
            socket.connect(new InetSocketAddress(HOST, PUERTO), 5000);
            System.out.println("[+] Conexión establecida con el servidor");
        }

        System.out.println("[!] Chat listo. Escribe 'salir' para cerrar.\n");

        // Lanzar ambos hilos
        HiloReceptor receptor = new HiloReceptor(socket.getInputStream());
        HiloEmisor   emisor   = new HiloEmisor(socket.getOutputStream());

        receptor.start();
        emisor.start();

        // Esperar a que el emisor termine (el receptor es daemon)
        emisor.join();
        socket.close();
    }
}
