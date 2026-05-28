"""
Ejercicio 1 - Servidor Echo Básico (Python)
============================================
Taller de Programación con Sockets - Ingeniería de Software
Semestre VII

DESCRIPCIÓN:
    Servidor TCP que recibe texto de los clientes y lo devuelve en MAYÚSCULAS.
    Soporta múltiples clientes simultáneos usando threading (un hilo por cliente).

CÓMO EJECUTAR:
    1. Abrir una terminal y ejecutar:   python servidor_echo.py
    2. En otra(s) terminal(es) ejecutar: python cliente_echo.py

REQUISITOS:
    - Python 3.7+
    - No requiere librerías externas
"""

import socket
import threading

# ── Configuración ─────────────────────────────────────────────────────────────
HOST   = "0.0.0.0"   # Escucha en todas las interfaces de red
PORT   = 5000        # Puerto del servidor
BUFFER = 4096        # Tamaño máximo del buffer de recepción (bytes)
# ──────────────────────────────────────────────────────────────────────────────


def atender_cliente(conn: socket.socket, addr: tuple) -> None:
    """
    Hilo dedicado a un cliente.
    Recibe datos, los convierte a mayúsculas y los reenvía.
    El bucle termina cuando el cliente cierra la conexión.
    """
    print(f"[+] Cliente conectado:   {addr[0]}:{addr[1]}")
    print(f"    Hilos activos ahora: {threading.active_count() - 1}")

    with conn:
        while True:
            try:
                datos = conn.recv(BUFFER)
                if not datos:
                    # El cliente cerró la conexión limpiamente
                    break

                mensaje_original = datos.decode("utf-8").strip()
                mensaje_eco      = mensaje_original.upper()

                print(f"[{addr[0]}:{addr[1]}] Recibido : '{mensaje_original}'")
                print(f"[{addr[0]}:{addr[1]}] Enviando : '{mensaje_eco}'")

                conn.sendall((mensaje_eco + "\n").encode("utf-8"))

            except ConnectionResetError:
                # El cliente cerró la conexión abruptamente
                break
            except UnicodeDecodeError:
                conn.sendall(b"ERROR: Solo se acepta texto UTF-8\n")

    print(f"[-] Cliente desconectado: {addr[0]}:{addr[1]}")


def main() -> None:
    """Crea el servidor y acepta conexiones indefinidamente."""
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as srv:

        # Permite reutilizar el puerto inmediatamente al reiniciar el servidor
        srv.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        srv.bind((HOST, PORT))
        srv.listen(50)   # Cola de hasta 50 conexiones pendientes

        print("=" * 50)
        print("   SERVIDOR ECHO TCP - Puerto", PORT)
        print("   Convierte mensajes a MAYÚSCULAS")
        print("   Ctrl+C para detener")
        print("=" * 50)

        while True:
            try:
                conn, addr = srv.accept()
                # Crear un hilo daemon por cliente (se cierra al terminar el main)
                hilo = threading.Thread(
                    target=atender_cliente,
                    args=(conn, addr),
                    daemon=True,
                    name=f"Cliente-{addr[0]}:{addr[1]}"
                )
                hilo.start()
            except KeyboardInterrupt:
                print("\n[!] Servidor detenido por el usuario.")
                break


if __name__ == "__main__":
    main()
