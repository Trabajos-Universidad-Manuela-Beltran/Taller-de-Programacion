import threading

# Ejercicio 4: Hilo que calcula la suma de números del 1 al N
#
# sum(range(...)) es equivalente al bucle acumulador de Java pero
# más idiomático en Python. El hilo realiza el cómputo y muestra
# el resultado cuando termina.

def sumador(n):
    # range(1, n+1) genera los enteros del 1 al n (inclusive).
    # sum() los acumula todos en una sola operación.
    suma = sum(range(1, n + 1))
    print(f"Suma del 1 al {n} = {suma}")

# Pasamos n=100 como argumento a la función del hilo.
hilo = threading.Thread(target=sumador, args=(100,))
hilo.start()
hilo.join()  # Esperamos a que imprima el resultado antes de salir.
