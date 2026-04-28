import threading
import string

# Ejercicio 6: Hilo que imprime letras de la A a la Z
#
# string.ascii_uppercase es la cadena 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'.
# Iterar sobre ella es más legible que usar códigos ASCII numéricos.

def imprimir_letras():
    # Cada iteración entrega una letra del alfabeto en mayúsculas.
    for letra in string.ascii_uppercase:
        print(f"Letra: {letra}")

hilo = threading.Thread(target=imprimir_letras)
hilo.start()
hilo.join()  # Esperamos a que el hilo termine de imprimir todas las letras.
