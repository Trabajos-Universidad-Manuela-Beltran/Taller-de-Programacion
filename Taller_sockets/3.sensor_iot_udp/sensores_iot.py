"""
Ejercicio 3 - Sensores IoT simulados (UDP)
============================================
Taller de Programación con Sockets - Ingeniería de Software
Semestre VII

DESCRIPCIÓN:
    Simula 5 sensores de temperatura. Cada sensor:
      - Tiene un ID único (sensor_01 ... sensor_05)
      - Genera lecturas aleatorias entre 15°C y 45°C
      - Envía un paquete UDP al servidor cada 1 segundo
      - Muestra la confirmación recibida del servidor

CÓMO EJECUTAR:
    Con el servidor corriendo: python sensores_iot.py
"""

import socket
import json
import time
import random
import threading

SERVIDOR_HOST = "localhost"
SERVIDOR_PORT = 5001
INTERVALO     = 1.0    # segundos entre lecturas
TIMEOUT       = 2.0    # segundos para esperar respuesta del servidor


def sensor(sensor_id: str, temp_base: float) -> None:
    """
    Simula un sensor individual.
    Genera temperaturas con variación aleatoria ±3°C alrededor de temp_base.
    """
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as cli:
        cli.settimeout(TIMEOUT)

        while True:
            # Temperatura simulada con pequeña variación aleatoria
            temperatura = round(temp_base + random.uniform(-3.0, 3.0), 1)

            paquete = json.dumps({
                "id":   sensor_id,
                "temp": temperatura
            })

            try:
                cli.sendto(
                    paquete.encode("utf-8"),
                    (SERVIDOR_HOST, SERVIDOR_PORT)
                )

                respuesta, _ = cli.recvfrom(1024)
                resp_data = json.loads(respuesta.decode("utf-8"))

                if resp_data.get("status") != "OK":
                    print(f"[{sensor_id}] Respuesta inesperada: {resp_data}")

            except socket.timeout:
                print(f"[{sensor_id}] Sin respuesta del servidor (timeout)")
            except OSError as e:
                print(f"[{sensor_id}] Error de red: {e}")
                break

            time.sleep(INTERVALO)


def main() -> None:
    # Configuración de los 5 sensores: (id, temperatura_base)
    sensores_config = [
        ("sensor_01", 22.0),   # Sala principal     ~22°C
        ("sensor_02", 35.0),   # Cuarto de máquinas ~35°C
        ("sensor_03", 18.0),   # Almacén frío       ~18°C
        ("sensor_04", 28.0),   # Oficina            ~28°C
        ("sensor_05", 40.0),   # Servidor externo   ~40°C
    ]

    print("=" * 50)
    print("  SIMULADOR DE SENSORES IoT - 5 sensores")
    print(f"  Enviando lecturas a {SERVIDOR_HOST}:{SERVIDOR_PORT}")
    print("  Ctrl+C para detener")
    print("=" * 50)

    hilos = []
    for sensor_id, temp_base in sensores_config:
        hilo = threading.Thread(
            target=sensor,
            args=(sensor_id, temp_base),
            daemon=True,
            name=sensor_id
        )
        hilo.start()
        hilos.append(hilo)
        print(f"[+] {sensor_id} iniciado (base: {temp_base}°C)")
        time.sleep(0.1)   # Pequeño desfase para no saturar el servidor

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\n[!] Sensores detenidos.")


if __name__ == "__main__":
    main()
