"""
Ejercicio 4 - Cliente TLS (Python)
=====================================
Taller de Programación con Sockets - Ingeniería de Software

DESCRIPCIÓN:
    Cliente que se conecta al Servidor TLS (Ejercicio 4).
    Acepta el certificado autofirmado del servidor explícitamente.
    Todo el tráfico viaja cifrado.

CÓMO EJECUTAR:
    Con el servidor TLS corriendo: python cliente_tls.py
    Escribe mensajes y presiona Enter. Escribe 'salir' para cerrar.
"""

import socket
import ssl

HOST      = "localhost"
PORT      = 5443
BUFFER    = 4096
CERT_FILE = "server.crt"   # Certificado del servidor para verificación


def main() -> None:
    # ── Crear contexto TLS del cliente ────────────────────────────────────────
    contexto = ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)

    # Cargar el certificado del servidor para verificarlo (CA autofirmada)
    contexto.load_verify_locations(CERT_FILE)

    # Verificar el nombre del host en el certificado
    contexto.check_hostname = True
    contexto.verify_mode    = ssl.CERT_REQUIRED

    print("=" * 50)
    print(f"  CLIENTE TLS - Conectando a {HOST}:{PORT}")
    print("  Escribe 'salir' para cerrar")
    print("=" * 50)

    with socket.create_connection((HOST, PORT)) as tcp_socket:
        with contexto.wrap_socket(tcp_socket, server_hostname=HOST) as tls_socket:
            print(f"[+] Conexión TLS establecida")
            print(f"    Cipher suite : {tls_socket.cipher()}")
            print(f"    Versión TLS  : {tls_socket.version()}\n")

            while True:
                mensaje = input("Tú > ")
                if mensaje.lower() == "salir":
                    print("[+] Cerrando conexión.")
                    break
                if not mensaje:
                    continue

                tls_socket.sendall((mensaje + "\n").encode("utf-8"))
                respuesta = tls_socket.recv(BUFFER).decode("utf-8").strip()
                print(f"Servidor (cifrado) > {respuesta}\n")


if __name__ == "__main__":
    main()
