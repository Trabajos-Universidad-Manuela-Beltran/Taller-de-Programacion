"""
Ejercicio 1 - Cliente Echo (Python)
=====================================
Taller de Programación con Sockets - Ingeniería de Software

DESCRIPCIÓN:
    Cliente TCP de prueba para el Servidor Echo.
    Envía mensajes y muestra la respuesta en mayúsculas recibida del servidor.

CÓMO EJECUTAR:
    Con el servidor corriendo:  python cliente_echo.py
    Escribe un mensaje y presiona Enter. Escribe 'salir' para cerrar.
"""

import socket

HOST   = "localhost"
PORT   = 5000
BUFFER = 4096


def main() -> None:
    print("=" * 45)
    print("   CLIENTE ECHO - Conectando a", HOST, PORT)
    print("   Escribe 'salir' para cerrar")
    print("=" * 45)

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as cli:
        try:
            cli.connect((HOST, PORT))
            print("[+] Conexión establecida con el servidor\n")
        except ConnectionRefusedError:
            print("[!] No se pudo conectar. ¿El servidor está corriendo?")
            return

        while True:
            mensaje = input("Tú > ")
            if mensaje.lower() == "salir":
                print("[+] Cerrando conexión.")
                break
            if not mensaje:
                continue

            cli.sendall((mensaje + "\n").encode("utf-8"))

            respuesta = cli.recv(BUFFER).decode("utf-8").strip()
            print(f"Servidor > {respuesta}\n")


if __name__ == "__main__":
    main()
