import threading
import time

# Ejercicio 9: Comparar ejecución secuencial vs concurrente
#
# Secuencial: start() + join() en el mismo ciclo → cada hilo espera al anterior.
# Concurrente: todos los start() primero, todos los join() después → se solapan.
# La diferencia de tiempo comprueba empíricamente el beneficio de la concurrencia.

def trabajo(nombre):
    for i in range(1, 4):
        print(f"{nombre} procesando item {i}")
        # Simula trabajo que tarda tiempo (p.ej. I/O, red).
        time.sleep(0.5)

# --- Ejecución SECUENCIAL ---
print("=== Ejecución SECUENCIAL ===")
inicio = time.time()

for nombre in ["Tarea-1", "Tarea-2"]:
    t = threading.Thread(target=trabajo, args=(nombre,))
    t.start()
    # join() aquí mismo hace que el bucle espere a que este hilo acabe
    # antes de crear el siguiente → ejecución uno tras otro.
    t.join()

print(f"Tiempo secuencial: {(time.time() - inicio):.2f} s\n")

# --- Ejecución CONCURRENTE ---
print("=== Ejecución CONCURRENTE ===")
inicio = time.time()

# Creamos y arrancamos todos los hilos sin esperar a ninguno.
hilos = [threading.Thread(target=trabajo, args=(f"Tarea-{i}",)) for i in range(1, 3)]
for h in hilos:
    h.start()

# Solo después de arrancar todos esperamos a que terminen.
for h in hilos:
    h.join()

print(f"Tiempo concurrente: {(time.time() - inicio):.2f} s")
# El tiempo concurrente debería ser aproximadamente la mitad del secuencial.
