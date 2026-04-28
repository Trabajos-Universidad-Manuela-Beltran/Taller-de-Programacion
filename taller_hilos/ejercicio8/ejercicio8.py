import threading
import time

# Ejercicio 8: Hilos con pausas usando sleep
#
# Dos hilos realizan la misma tarea pero con tiempos de pausa distintos.
# Observar la salida muestra claramente que corren en paralelo y que
# el hilo rápido adelanta pasos mientras el lento aún está en los primeros.

def tarea_con_pausa(nombre, pausa):
    for i in range(1, 6):
        print(f"{nombre} - paso {i}")
        # time.sleep() suspende solo este hilo durante 'pausa' segundos.
        time.sleep(pausa)
    print(f"{nombre} finalizado.")

# Hilo lento: 1 s de pausa por paso → ~5 s en total.
lento  = threading.Thread(target=tarea_con_pausa, args=("Hilo-Lento",  1.0))
# Hilo rápido: 0.3 s de pausa por paso → ~1.5 s en total.
rapido = threading.Thread(target=tarea_con_pausa, args=("Hilo-Rapido", 0.3))

lento.start()
rapido.start()

# Esperamos a ambos. El rápido terminará y join() regresará enseguida;
# luego esperamos al lento el tiempo que le quede.
lento.join()
rapido.join()
