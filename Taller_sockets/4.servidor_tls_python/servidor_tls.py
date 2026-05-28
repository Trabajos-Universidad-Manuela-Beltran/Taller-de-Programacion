"""
Ejercicio 4 - Servidor Echo con TLS (Python)
==============================================
Taller de Programación con Sockets - Ingeniería de Software
Semestre VII

DESCRIPCIÓN:
    Extiende el Ejercicio 1 añadiendo cifrado TLS mediante ssl.SSLContext.
    Todo el tráfico entre cliente y servidor viaja cifrado.
    Usa certificado autofirmado generado con generar_certificado.py

CÓMO EJECUTAR:
    1. Generar certificados (solo la primera vez):
           python generar_certificado.py

    2. Iniciar el servidor TLS:
           python servidor_tls.py

    3. Conectar con el cliente seguro:
           python cliente_tls.py

DIFERENCIA CON EJERCICIO 1:
    - Se crea un SSLContext con protocolo TLS_SERVER
    - El socket TCP se "envuelve" con context.wrap_socket()
    - El cliente debe confiar en el certificado del servidor
"""

import socket
import ssl
import threading
import os

# ── Configuración ─────────────────────────────────────────────────────────────
HOST      = "0.0.0.0"
PORT      = 5443        # Puerto convencional para TLS (443 en producción)
BUFFER    = 4096
CERT_FILE = "server.crt"
KEY_FILE  = "server.key"
# ──────────────────────────────────────────────────────────────────────────────


def verificar_certificados() -> None:
    """Verifica que existan los archivos de certificado y clave."""
    if not os.path.exists(CERT_FILE) or not os.path.exists(KEY_FILE):
        print(f"[!] No se encontraron {CERT_FILE} o {KEY_FILE}")
        print("    Ejecuta primero: python generar_certificado.py")
        raise SystemExit(1)


def atender_cliente(conn: ssl.SSLSocket, addr: tuple) -> None:
    """
    Igual que en Ejercicio 1 pero la conexión ya está cifrada con TLS.
    Recibe texto, lo convierte a mayúsculas y lo devuelve.
    """
    print(f"[+] Cliente TLS conectado:  {addr[0]}:{addr[1]}")
    print(f"    Cipher suite usado: {conn.cipher()}")

    with conn:
        while True:
            try:
                datos = conn.recv(BUFFER)
                if not datos:
                    break

                mensaje_original = datos.decode("utf-8").strip()
                mensaje_eco      = mensaje_original.upper()

                print(f"[{addr[0]}] Recibido  : '{mensaje_original}'")
                print(f"[{addr[0]}] Enviando  : '{mensaje_eco}'")

                conn.sendall((mensaje_eco + "\n").encode("utf-8"))

            except ssl.SSLError as e:
                print(f"[!] Error TLS con {addr}: {e}")
                break
            except ConnectionResetError:
                break

    print(f"[-] Cliente desconectado: {addr[0]}:{addr[1]}")


def main() -> None:
    verificar_certificados()

    # ── Crear contexto TLS del servidor ───────────────────────────────────────
    contexto = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)

    # Cargar el certificado y la clave privada
    contexto.load_cert_chain(certfile=CERT_FILE, keyfile=KEY_FILE)

    # Solo aceptar TLS 1.2 y 1.3 (deshabilitar versiones inseguras)
    contexto.minimum_version = ssl.TLSVersion.TLSv1_2

    # ── Crear socket TCP base y envolverlo con TLS ────────────────────────────
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as tcp_socket:
        tcp_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        tcp_socket.bind((HOST, PORT))
        tcp_socket.listen(50)

        # Envolver el ServerSocket con TLS
        with contexto.wrap_socket(tcp_socket, server_side=True) as srv_tls:
            print("=" * 55)
            print(f"  SERVIDOR ECHO TLS - Puerto {PORT}")
            print(f"  Certificado: {CERT_FILE}")
            print("  Todo el tráfico está CIFRADO")
            print("  Ctrl+C para detener")
            print("=" * 55)

            while True:
                try:
                    conn_tls, addr = srv_tls.accept()
                    hilo = threading.Thread(
                        target=atender_cliente,
                        args=(conn_tls, addr),
                        daemon=True
                    )
                    hilo.start()
                except KeyboardInterrupt:
                    print("\n[!] Servidor TLS detenido.")
                    break
                except ssl.SSLError as e:
                    print(f"[!] Error en handshake TLS: {e}")


if __name__ == "__main__":
    main()
