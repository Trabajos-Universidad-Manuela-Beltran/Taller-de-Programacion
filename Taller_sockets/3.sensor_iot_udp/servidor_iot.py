"""
Ejercicio 3 - Servidor Central IoT (UDP)
==========================================
Taller de Programación con Sockets - Ingeniería de Software
Semestre VII

DESCRIPCIÓN:
    Servidor UDP que recibe lecturas de temperatura de hasta 5 sensores.
    Calcula y muestra en tiempo real:
      - Última lectura de cada sensor
      - Promedio global de todos los sensores activos
      - Temperatura mínima y máxima recibidas

CÓMO EJECUTAR:
    Terminal 1: python servidor_iot.py
    Terminal 2: python sensores_iot.py
"""

import socket
import json
from datetime import datetime

HOST   = "0.0.0.0"
PORT   = 5001
BUFFER = 1024

# Almacena la última lectura de cada sensor  { sensor_id: temperatura }
lecturas: dict[str, float] = {}


def mostrar_estadisticas() -> None:
    """Imprime en pantalla el estado actual de todos los sensores."""
    print("\n" + "=" * 55)
    print(f"  REPORTE  [{datetime.now().strftime('%H:%M:%S')}]")
    print("=" * 55)

    if not lecturas:
        print("  (sin datos todavía)")
        return

    for sensor_id, temp in sorted(lecturas.items()):
        barra = "█" * int(temp / 2)   # barra visual proporcional
        print(f"  {sensor_id:<10}  {temp:5.1f} °C  {barra}")

    valores = list(lecturas.values())
    promedio = sum(valores) / len(valores)
    print("-" * 55)
    print(f"  Sensores activos : {len(lecturas)}")
    print(f"  Promedio         : {promedio:.2f} °C")
    print(f"  Mínima           : {min(valores):.1f} °C")
    print(f"  Máxima           : {max(valores):.1f} °C")
    print("=" * 55)


def main() -> None:
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as srv:
        srv.bind((HOST, PORT))
        print("=" * 55)
        print(f"  SERVIDOR IoT UDP - Puerto {PORT}")
        print("  Esperando lecturas de sensores...")
        print("  Ctrl+C para detener")
        print("=" * 55)

        while True:
            try:
                datos, addr = srv.recvfrom(BUFFER)

                # Parsear el paquete JSON enviado por el sensor
                paquete = json.loads(datos.decode("utf-8"))
                sensor_id   = paquete["id"]
                temperatura = float(paquete["temp"])

                lecturas[sensor_id] = temperatura

                # Confirmar recepción al sensor
                respuesta = json.dumps({"status": "OK", "recibido": temperatura})
                srv.sendto(respuesta.encode("utf-8"), addr)

                mostrar_estadisticas()

            except json.JSONDecodeError:
                print(f"[!] Paquete malformado de {addr}")
            except KeyboardInterrupt:
                print("\n[!] Servidor detenido.")
                break


if __name__ == "__main__":
    main()
