/**
 * Ejercicio 5 - Monitor de Puertos con Java NIO
 * ================================================
 * Taller de Programación con Sockets - Ingeniería de Software
 * Semestre VII
 *
 * DESCRIPCIÓN:
 *   Servidor no bloqueante implementado con Java NIO que puede manejar
 *   100 conexiones simultáneas usando un ÚNICO hilo de ejecución.
 *
 *   Componentes clave de Java NIO usados:
 *     - ServerSocketChannel : versión no bloqueante del ServerSocket
 *     - SocketChannel       : versión no bloqueante del Socket
 *     - Selector            : multiplexor que monitorea múltiples canales
 *     - ByteBuffer          : buffer de datos (NIO no usa streams)
 *
 *   Patrón de funcionamiento (Reactor pattern):
 *     El Selector.select() bloquea el hilo SOLO cuando no hay eventos.
 *     Cuando llega un evento (nueva conexión o datos), lo despacha
 *     al manejador correspondiente sin necesidad de hilos adicionales.
 *
 * CÓMO COMPILAR:
 *   javac MonitorPuertosNIO.java
 *
 * CÓMO EJECUTAR:
 *   java MonitorPuertosNIO
 *
 *   Conectar clientes de prueba con:
 *     telnet localhost 7000
 *   O con el cliente Python del Ejercicio 1 (cambiar PORT a 7000)
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class MonitorPuertosNIO {

    private static final int    PUERTO          = 7000;
    private static final int    BUFFER_SIZE     = 4096;
    private static final int    MAX_CONEXIONES  = 100;

    // Contador de conexiones activas
    private static int conexionesActivas = 0;

    public static void main(String[] args) throws IOException {

        // ── 1. Crear el Selector (epoll en Linux, kqueue en macOS) ─────────────
        Selector selector = Selector.open();

        // ── 2. Crear el ServerSocketChannel no bloqueante ──────────────────────
        ServerSocketChannel servidorChannel = ServerSocketChannel.open();
        servidorChannel.configureBlocking(false);   // MODO NO BLOQUEANTE
        servidorChannel.bind(new InetSocketAddress(PUERTO));

        // ── 3. Registrar el canal con el selector para aceptar conexiones ──────
        servidorChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("=======================================================");
        System.out.println("  MONITOR DE PUERTOS - Java NIO (No Bloqueante)");
        System.out.println("  Puerto       : " + PUERTO);
        System.out.println("  Máx conexiones simultáneas: " + MAX_CONEXIONES);
        System.out.println("  Hilos usados : 1 (este mismo)");
        System.out.println("  Ctrl+C para detener");
        System.out.println("=======================================================");

        // ── 4. Bucle de eventos (Event Loop) ───────────────────────────────────
        while (true) {

            // select() bloquea hasta que al menos un canal esté listo
            int canalesListos = selector.select();
            if (canalesListos == 0) continue;

            Set<SelectionKey> claves = selector.selectedKeys();
            Iterator<SelectionKey> iterador = claves.iterator();

            while (iterador.hasNext()) {
                SelectionKey clave = iterador.next();
                iterador.remove();   // Eliminar para no procesar dos veces

                if (!clave.isValid()) continue;

                if (clave.isAcceptable()) {
                    // ── Nueva conexión entrante ────────────────────────────────
                    manejarAceptacion(clave, selector);

                } else if (clave.isReadable()) {
                    // ── Datos disponibles para leer ────────────────────────────
                    manejarLectura(clave);
                }
            }
        }
    }

    /**
     * Acepta una nueva conexión TCP y la registra en el selector.
     * Si se superan MAX_CONEXIONES, rechaza la nueva conexión.
     */
    private static void manejarAceptacion(SelectionKey clave, Selector selector)
            throws IOException {

        ServerSocketChannel servidorChannel = (ServerSocketChannel) clave.channel();
        SocketChannel clienteChannel = servidorChannel.accept();

        if (clienteChannel == null) return;

        if (conexionesActivas >= MAX_CONEXIONES) {
            // Límite alcanzado: rechazar y notificar al cliente
            ByteBuffer rechazo = ByteBuffer.wrap(
                "SERVIDOR LLENO: máximo de conexiones alcanzado\n"
                    .getBytes(StandardCharsets.UTF_8)
            );
            clienteChannel.write(rechazo);
            clienteChannel.close();
            System.out.println("[!] Conexión rechazada (límite " + MAX_CONEXIONES + " alcanzado)");
            return;
        }

        // Configurar el canal del cliente como no bloqueante
        clienteChannel.configureBlocking(false);

        // Registrar el canal del cliente para leer datos
        clienteChannel.register(selector, SelectionKey.OP_READ);

        conexionesActivas++;
        String clienteDir = clienteChannel.getRemoteAddress().toString();

        System.out.println("[+] Nueva conexión NIO:  " + clienteDir
                + "  |  Activas: " + conexionesActivas);

        // Enviar mensaje de bienvenida
        String bienvenida = "=== Monitor NIO conectado | Puerto " + PUERTO
                + " | Envía texto para eco en MAYÚSCULAS ===\n";
        clienteChannel.write(ByteBuffer.wrap(
            bienvenida.getBytes(StandardCharsets.UTF_8)
        ));
    }

    /**
     * Lee datos de un canal cliente y responde con el texto en mayúsculas.
     * Si el cliente cierra la conexión, libera el recurso.
     */
    private static void manejarLectura(SelectionKey clave) throws IOException {

        SocketChannel clienteChannel = (SocketChannel) clave.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        int bytesLeidos;
        try {
            bytesLeidos = clienteChannel.read(buffer);
        } catch (IOException e) {
            cerrarCliente(clave, clienteChannel);
            return;
        }

        if (bytesLeidos == -1) {
            // El cliente cerró la conexión limpiamente
            cerrarCliente(clave, clienteChannel);
            return;
        }

        if (bytesLeidos > 0) {
            // Preparar buffer para leer lo que acaba de llegar
            buffer.flip();
            byte[] datos = new byte[buffer.limit()];
            buffer.get(datos);

            String mensaje    = new String(datos, StandardCharsets.UTF_8).trim();
            String respuesta  = mensaje.toUpperCase() + "\n";

            System.out.println("[" + clienteChannel.getRemoteAddress() + "]"
                    + " Recibido: '" + mensaje + "'"
                    + " → Enviando: '" + respuesta.trim() + "'");

            // Enviar respuesta eco en mayúsculas
            clienteChannel.write(ByteBuffer.wrap(
                respuesta.getBytes(StandardCharsets.UTF_8)
            ));
        }
    }

    /**
     * Cierra el canal de un cliente y libera su clave del selector.
     */
    private static void cerrarCliente(SelectionKey clave, SocketChannel canal) {
        try {
            String dir = canal.getRemoteAddress().toString();
            clave.cancel();
            canal.close();
            conexionesActivas = Math.max(0, conexionesActivas - 1);
            System.out.println("[-] Desconectado: " + dir
                    + "  |  Activas: " + conexionesActivas);
        } catch (IOException ignored) {}
    }
}
