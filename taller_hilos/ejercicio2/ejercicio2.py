import threading

# Ejercicio 2: Dos hilos que imprimen mensajes diferentes
#
# Al lanzar dos hilos sin sincronización, el GIL de Python los alterna
# automáticamente. El orden exacto de impresión no está garantizado.

def mensaje_a():
    for _ in range(5):
        print("Hilo A: Hola desde el primer hilo")

def mensaje_b():
    for _ in range(5):
        print("Hilo B: Hola desde el segundo hilo")

# Creamos un hilo por cada función-tarea.
hilo_a = threading.Thread(target=mensaje_a)
hilo_b = threading.Thread(target=mensaje_b)

# Arrancamos ambos hilos. A partir de aquí corren concurrentemente.
hilo_a.start()
hilo_b.start()

# Esperamos a que los dos terminen antes de que el programa finalice.
hilo_a.join()
hilo_b.join()
