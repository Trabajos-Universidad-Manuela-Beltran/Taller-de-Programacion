import threading

# Ejercicio 1: Hilo que imprime números del 1 al 10
#
# En Python no existe la interfaz Runnable; en su lugar se pasa
# una función como argumento 'target' al constructor Thread.
# Esa función hace el papel de run().

def contador():
    # Este código se ejecutará en un hilo separado al del programa principal.
    for i in range(1, 11):
        print(f"Número: {i}")

# Creamos el hilo asociando la función 'contador' como tarea.
hilo = threading.Thread(target=contador)

# start() lanza el hilo. A partir de aquí contador() corre
# concurrentemente con el resto del programa.
hilo.start()

# join() hace que el hilo principal espere a que este hilo termine
# antes de continuar. Sin join(), main podría terminar antes que el hilo.
hilo.join()
