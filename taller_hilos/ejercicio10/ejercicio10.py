import threading
import time
from collections import deque

# Ejercicio 10: Múltiples hilos interactuando — Productor/Consumidor
#
# threading.Condition combina un Lock con la capacidad de hacer wait()/notify().
# Es el equivalente directo al synchronized + wait/notifyAll de Java.
#
# Flujo:
#   Productor agrega items al deque mientras haya espacio; si está lleno → wait().
#   Consumidor saca items del deque mientras haya stock; si está vacío → wait().
#   Cada vez que uno modifica el deque, llama notify_all() para despertar al otro.

almacen = deque()   # Buffer compartido entre productor y consumidor
capacidad = 5       # Máximo de items que puede haber al mismo tiempo
lock = threading.Condition()  # Controla el acceso exclusivo al buffer

def productor():
    for item in range(1, 9):  # Produce items del 1 al 8
        with lock:             # Adquiere el lock (equivale a synchronized)
            # Espera mientras el almacén esté lleno.
            # wait() libera el lock temporalmente para que el consumidor pueda entrar.
            while len(almacen) == capacidad:
                print("Almacén lleno, productor esperando...")
                lock.wait()

            almacen.append(item)
            print(f"Producido: {item} | Stock: {len(almacen)}")

            # Notifica a todos los hilos en wait() sobre este Condition.
            # El consumidor que esperaba por items se despertará.
            lock.notify_all()

        time.sleep(0.3)  # Produce un item cada 300 ms (fuera del lock)

def consumidor():
    for _ in range(8):  # Consume los mismos 8 items
        with lock:
            # Espera mientras no haya nada que consumir.
            while not almacen:
                print("Almacén vacío, consumidor esperando...")
                lock.wait()

            item = almacen.popleft()  # Extrae el item más antiguo (FIFO)
            print(f"Consumido: {item} | Stock: {len(almacen)}")

            # Notifica al productor que hay espacio libre en el almacén.
            lock.notify_all()

        time.sleep(0.6)  # Consume más lento → el almacén se irá llenando

t_prod = threading.Thread(target=productor, name="Productor")
t_cons = threading.Thread(target=consumidor, name="Consumidor")

t_prod.start()
t_cons.start()

# Esperamos a que ambos hilos terminen su trabajo.
t_prod.join()
t_cons.join()
