import threading
import time

# Ejercicio 3: Simular una cuenta regresiva usando hilos
#
# time.sleep() dentro de un hilo pausa únicamente ese hilo.
# El hilo principal u otros hilos continúan ejecutándose.

def cuenta_regresiva(inicio):
    # range con paso -1 genera la secuencia descendente: inicio, inicio-1, ..., 0
    for i in range(inicio, -1, -1):
        print(f"Cuenta: {i}")
        # Pausa de 0.5 segundos entre cada número para simular el conteo real.
        time.sleep(0.5)
    print("¡Despegue!")

# args es una tupla con los argumentos que recibe la función objetivo.
hilo = threading.Thread(target=cuenta_regresiva, args=(10,))
hilo.start()

# join() bloquea al hilo principal hasta que la cuenta termine.
hilo.join()
