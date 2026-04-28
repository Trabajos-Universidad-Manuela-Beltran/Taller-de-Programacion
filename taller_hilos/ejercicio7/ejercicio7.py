import threading
import time

# Ejercicio 7: Simular un proceso de descarga con hilos
#
# Dos archivos se "descargan" en paralelo. La salida muestra cómo
# los dos progresos avanzan intercalados, lo que sería imposible
# en ejecución secuencial.

def descarga(archivo):
    print(f"Iniciando descarga: {archivo}")

    # range(0, 101, 20) produce: 0, 20, 40, 60, 80, 100
    for progreso in range(0, 101, 20):
        print(f"{archivo} - {progreso}% completado")
        # Pausa que simula el tiempo que tarda en llegar cada bloque de datos.
        time.sleep(0.4)

    print(f"{archivo} - Descarga completada.")

# Cada hilo recibe el nombre de su archivo por medio de args.
d1 = threading.Thread(target=descarga, args=("archivo1.zip",))
d2 = threading.Thread(target=descarga, args=("archivo2.mp4",))

# Arrancamos las dos descargas simultáneamente.
d1.start()
d2.start()

# Esperamos a que ambas terminen antes de cerrar el programa.
d1.join()
d2.join()
