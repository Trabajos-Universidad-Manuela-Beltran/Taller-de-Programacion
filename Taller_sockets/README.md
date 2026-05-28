# Taller de Programación con Sockets
**Ingeniería de Software — Semestre VII**  
**Área:** Redes y Comunicaciones | **Nivel:** Intermedio – Avanzado

---

## Estructura del proyecto

```
Taller_sockets/
├── 1.servidor_echo_python/
│   ├── servidor_echo.py
│   └── cliente_echo.py
├── 2.chat_p2p_java/
│   └── ChatP2P.java
├── 3.sensor_iot_udp/
│   ├── servidor_iot.py
│   └── sensores_iot.py
├── 4.servidor_tls_python/
│   ├── generar_certificado.py
│   ├── servidor_tls.py
│   └── cliente_tls.py
├── 5.monitor_puertos_java_nio/
│   └── MonitorPuertosNIO.java
└── README.md  ← estás aquí
```

---

## Requisitos previos

| Herramienta | Versión mínima | Para qué se usa |
|---|---|---|
| Python | 3.7+ | Ejercicios 1, 3 y 4 |
| Java JDK | 11+ | Ejercicios 2 y 5 |
| `cryptography` (pip) | cualquiera | Ejercicio 4 (generar certificado) |

Instalar la librería de Python necesaria para el Ejercicio 4:
```bash
pip install cryptography
```

Verificar que Java esté instalado:
```bash
java -version
javac -version
```
Si no lo tienes, descárgalo desde: https://adoptium.net

---

## Punto 1: Servidor Echo en Python

### ¿Qué hace?
Implementa un servidor TCP que escucha en el puerto **5000**. Cuando un cliente envía cualquier texto, el servidor lo convierte completamente a **MAYÚSCULAS** y lo devuelve. Puede atender **múltiples clientes al mismo tiempo** gracias a un modelo de un hilo por conexión (`threading`).

### Archivos
- `servidor_echo.py` — el servidor TCP con soporte concurrente
- `cliente_echo.py` — cliente interactivo para hacer pruebas

### ¿Cómo funciona internamente?
1. Se crea un `socket` TCP con `AF_INET` y `SOCK_STREAM`.
2. Se configura `SO_REUSEADDR` para poder reiniciar el servidor sin esperar el estado `TIME_WAIT`.
3. El servidor entra en un bucle `accept()` que espera conexiones entrantes.
4. Por cada cliente que llega, se lanza un **hilo daemon** que ejecuta `atender_cliente()`.
5. Dentro del hilo, se lee el mensaje con `recv()`, se transforma con `.upper()` y se responde con `sendall()`.
6. El hilo termina cuando el cliente cierra la conexión (recv devuelve `b""`).

### Cómo ejecutar
```bash
# Terminal 1 — Servidor
python servidor_echo.py

# Terminal 2, 3, 4... — Clientes (se pueden abrir varios a la vez)
python cliente_echo.py
```

### Ejemplo de salida
```
Servidor:                          Cliente:
[+] Cliente conectado: 127.0.0.1   Tú > hola mundo
[127.0.0.1] Recibido: 'hola mundo' Servidor > HOLA MUNDO
[127.0.0.1] Enviando: 'HOLA MUNDO'
```

---

## Punto 2: Chat P2P en Java

### ¿Qué hace?
Implementa una aplicación de **chat peer-to-peer** donde dos instancias del mismo programa pueden comunicarse simultáneamente. Una instancia actúa como servidor (espera la conexión) y la otra como cliente (inicia la conexión). Ambas pueden enviar y recibir mensajes **al mismo tiempo** gracias a dos hilos concurrentes.

### Archivos
- `ChatP2P.java` — contiene toda la lógica: servidor, cliente, hilo emisor y hilo receptor

### ¿Cómo funciona internamente?
El programa tiene dos modos de ejecución:
- **Modo servidor:** crea un `ServerSocket` en el puerto 6000, espera hasta que el cliente se conecte y luego pasa al chat.
- **Modo cliente:** crea un `Socket` y se conecta a `localhost:6000`.

Una vez establecida la conexión, se lanzan dos hilos:
- **`HiloReceptor`** (hilo daemon): lee continuamente del `BufferedReader` del socket e imprime cada mensaje recibido.
- **`HiloEmisor`** (hilo principal): lee del teclado con `BufferedReader(System.in)` y envía cada línea con `PrintWriter`. Termina con la palabra `salir`.

Este diseño evita que el envío bloquee la recepción y viceversa.

### Cómo compilar y ejecutar
```bash
# Compilar
javac ChatP2P.java

# Terminal A — modo servidor (espera la conexión)
java ChatP2P servidor

# Terminal B — modo cliente (se conecta)
java ChatP2P cliente
```

### Ejemplo de sesión
```
Terminal A (servidor):          Terminal B (cliente):
[+] Cliente conectado           [+] Conexión establecida
Tú > Hola!                      [Otro] Hola!
[Otro] Cómo estás?              Tú > Cómo estás?
Tú > Muy bien, gracias          [Otro] Muy bien, gracias
```

---

## Punto 3: Sensor IoT simulado (UDP)

### ¿Qué hace?
Simula un sistema IoT real con **5 sensores de temperatura** que envían lecturas de forma independiente al mismo servidor central. El servidor recibe los datagramas UDP, actualiza su tabla interna y muestra en tiempo real el **promedio, mínimo y máximo** de todos los sensores activos.

### Archivos
- `servidor_iot.py` — servidor UDP central que recibe y procesa lecturas
- `sensores_iot.py` — simulador de 5 sensores concurrentes

### ¿Cómo funciona internamente?
**Protocolo de comunicación:** cada sensor envía un JSON por UDP:
```json
{"id": "sensor_01", "temp": 23.4}
```
El servidor responde con:
```json
{"status": "OK", "recibido": 23.4}
```

**Servidor (`servidor_iot.py`):**
- Usa `SOCK_DGRAM` (UDP, sin conexión).
- `recvfrom()` devuelve tanto los datos como la dirección del remitente.
- Mantiene un diccionario `lecturas[sensor_id] = temperatura`.
- Después de cada recepción, recalcula y muestra las estadísticas.

**Sensores (`sensores_iot.py`):**
- Cada sensor es un hilo independiente (`threading.Thread`).
- Genera temperaturas con variación aleatoria `±3°C` sobre una temperatura base.
- Los 5 sensores envían lecturas cada 1 segundo de forma simultánea.

### Cómo ejecutar
```bash
# Terminal 1 — Servidor central
python servidor_iot.py

# Terminal 2 — Sensores simulados
python sensores_iot.py
```

### Ejemplo de reporte del servidor
```
===================================================
  REPORTE  [14:32:01]
===================================================
  sensor_01    22.3 °C  ███████████
  sensor_02    36.1 °C  ██████████████████
  sensor_03    17.8 °C  ████████
  sensor_04    29.5 °C  ██████████████
  sensor_05    41.2 °C  ████████████████████
---------------------------------------------------
  Sensores activos : 5
  Promedio         : 29.38 °C
  Mínima           : 17.8 °C
  Máxima           : 41.2 °C
===================================================
```

---

## Punto 4: Servidor con TLS en Python

### ¿Qué hace?
Extiende el **Ejercicio 1** añadiendo una capa de seguridad **TLS (Transport Layer Security)**. Todo el tráfico entre cliente y servidor viaja **cifrado**, por lo que no puede ser interceptado por terceros. Usa un certificado X.509 autofirmado generado con la librería `cryptography`.

### Archivos
- `generar_certificado.py` — genera `server.key` (clave privada) y `server.crt` (certificado)
- `servidor_tls.py` — servidor Echo cifrado con TLS, puerto 5443
- `cliente_tls.py` — cliente que verifica el certificado del servidor y cifra la comunicación

### ¿Cómo funciona internamente?
**Generación del certificado:**
El script crea una clave RSA de 2048 bits y un certificado X.509 autofirmado con validez de 1 año. El certificado indica que es válido para el host `localhost`.

**Servidor TLS:**
1. Se crea un `ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)`.
2. Se carga el certificado y la clave privada con `load_cert_chain()`.
3. Se configura TLS 1.2 como versión mínima (se descarta TLS 1.0 y 1.1).
4. El socket TCP normal se "envuelve" con `context.wrap_socket()`.
5. A partir de ahí, la comunicación es idéntica al Ejercicio 1 pero todo viaja cifrado.

**Cliente TLS:**
1. Crea un `ssl.SSLContext(ssl.PROTOCOL_TLS_CLIENT)`.
2. Carga el certificado del servidor como CA de confianza.
3. Envuelve su socket TCP con `wrap_socket(server_hostname="localhost")`.
4. Si el certificado no coincide, la conexión se rechaza automáticamente.

### Cómo ejecutar
```bash
# Paso 1: Generar certificados (solo la primera vez)
cd 4.servidor_tls_python
python generar_certificado.py
# Crea: server.key y server.crt

# Paso 2: Iniciar el servidor TLS
python servidor_tls.py

# Paso 3: Conectar el cliente (en otra terminal)
python cliente_tls.py
```

### Diferencias con Ejercicio 1
| Aspecto | Ejercicio 1 | Ejercicio 4 |
|---|---|---|
| Puerto | 5000 | 5443 |
| Tráfico | Texto plano | Cifrado TLS |
| Certificado | No aplica | X.509 autofirmado |
| Versión TLS | No aplica | TLS 1.2 mínimo |
| Interceptable | Sí | No |

---

## Punto 5: Monitor de puertos con Java NIO

### ¿Qué hace?
Implementa un servidor de alta concurrencia usando **Java NIO (New I/O)** que puede manejar **100 conexiones simultáneas usando un único hilo**. A diferencia del Ejercicio 1 (un hilo por cliente), este servidor no crea ningún hilo adicional: usa el patrón **Reactor** con un `Selector` que multiplexea todos los canales.

### Archivos
- `MonitorPuertosNIO.java` — servidor NIO completo con Selector y SocketChannel

### ¿Cómo funciona internamente?
Java NIO introduce tres abstracciones clave:

**`ServerSocketChannel`** — versión no bloqueante del `ServerSocket`. Al configurarse con `configureBlocking(false)`, el método `accept()` retorna `null` inmediatamente si no hay conexiones en lugar de bloquearse.

**`SocketChannel`** — versión no bloqueante del `Socket`. El método `read()` retorna 0 si no hay datos disponibles.

**`Selector`** — el corazón del sistema. Permite registrar múltiples canales y llamar `select()` una vez. Este método bloquea el hilo hasta que **al menos uno** de los canales registrados esté listo para su operación. Al retornar, entrega el conjunto de claves (`SelectionKey`) listas.

**Flujo de operación:**
```
Selector.select() — espera eventos
    ↓
Para cada SelectionKey lista:
    ├── OP_ACCEPT → manejarAceptacion() → registrar nuevo canal con OP_READ
    └── OP_READ   → manejarLectura() → leer ByteBuffer, transformar a mayúsculas, enviar
```

**`ByteBuffer`** — NIO no usa streams sino buffers. El buffer debe "voltear" con `flip()` antes de leer lo que acaba de recibir.

### Comparación con el Ejercicio 1 (threading vs NIO)
| Aspecto | Ejercicio 1 (threading) | Ejercicio 5 (NIO) |
|---|---|---|
| Hilos usados | 1 por cliente | 1 total |
| Escalabilidad | Limitada por RAM/SO | Miles de conexiones |
| Complejidad del código | Baja | Media-Alta |
| Modelo | Bloqueante | No bloqueante (Reactor) |
| Casos de uso | Pocas conexiones | Alta concurrencia (tipo Nginx) |

### Cómo compilar y ejecutar
```bash
# Compilar
javac MonitorPuertosNIO.java

# Ejecutar el servidor (puerto 7000)
java MonitorPuertosNIO

# Conectar clientes de prueba con telnet
telnet localhost 7000

# O con el cliente del Ejercicio 1 (cambiar PORT = 7000 en cliente_echo.py)
```

---

## Resumen de puertos usados

| Ejercicio | Puerto | Protocolo | Descripción |
|---|---|---|---|
| 1. Servidor Echo | 5000 | TCP | Echo en mayúsculas |
| 2. Chat P2P | 6000 | TCP | Chat bidireccional |
| 3. Sensor IoT | 5001 | UDP | Lecturas de temperatura |
| 4. Servidor TLS | 5443 | TCP+TLS | Echo cifrado |
| 5. Monitor NIO | 7000 | TCP | NIO no bloqueante |

---

## Conceptos clave del taller

- **Socket TCP** — orientado a conexión, garantiza entrega y orden (Three-Way Handshake)
- **Socket UDP** — sin conexión, sin garantía, mayor velocidad (ideal para IoT y streaming)
- **Threading** — modelo simple: un hilo por cliente, limitado por recursos del sistema
- **NIO / Selector** — modelo Reactor: un hilo maneja N conexiones, base de servidores modernos
- **TLS** — capa de seguridad sobre TCP; handshake + cifrado simétrico para los datos
- **SO_REUSEADDR** — evita el error "Address already in use" al reiniciar el servidor

---

*Taller desarrollado para el curso de Ingeniería de Software — 2025*
