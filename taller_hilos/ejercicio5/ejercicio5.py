import threading
import time

# Ejercicio 5: Ejecutar tres hilos simultáneamente
#
# Usamos una comprensión de lista para crear los tres hilos de forma
# compacta. Cada hilo recibe su nombre vía args.

def tarea(nombre):
    for i in range(1, 5):
        print(f"{nombre} - iteración {i}")
        # La pausa permite que los otros hilos se intercalen en la salida,
        # haciendo visible la concurrencia.
        time.sleep(0.2)
    print(f"{nombre} terminó.")

# Creamos una lista con tres hilos, uno por cada nombre.
hilos = [threading.Thread(target=tarea, args=(f"Hilo-{i}",)) for i in range(1, 4)]

# Primer bucle: arrancamos todos antes de esperar a ninguno.
# Si hiciéramos start()+join() en el mismo bucle, sería secuencial.
for h in hilos:
    h.start()

# Segundo bucle: esperamos a que cada hilo termine.
for h in hilos:
    h.join()
