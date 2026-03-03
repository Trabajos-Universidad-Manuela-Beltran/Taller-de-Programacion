# Ejercicio 2: Sistema de Ventas ABC con Streams en Java

## Descripción
Este ejercicio demuestra el uso de la API Stream de Java para procesar datos de un sistema de ventas empresarial.

## Archivos
- `Ventas_ABC.java` - Programa principal con implementación de Streams
- `README.md` - Este archivo de documentación

## Cómo ejecutar
```bash
# Compilar
javac Ejercicio2/Ventas_ABC.java

# Ejecutar (desde la carpeta Trabajo-03-03)
java Ejercicio2.Ventas_ABC
```

## Operaciones Stream implementadas
1. **Filter + ForEach**: Filtrar vendedores con ventas > $5,000,000
2. **Max + Comparator**: Encontrar vendedor con mayor venta
3. **MapToDouble + Sum**: Calcular total de ventas
4. **MapToDouble + Average**: Calcular promedio de ventas
5. **Sorted**: Ordenar vendedores por ventas (descendente)

---

## ANÁLISIS TÉCNICO DEL USO DE STREAMS

1. ¿QUÉ HACE CADA OPERACIÓN STREAM UTILIZADA?

• stream(): Convierte una colección (List) en un flujo de datos secuencial que 
  permite operaciones funcionales. Es el punto de entrada para usar la API Stream.

• filter(): Operación intermedia que recibe un predicado (expresión booleana) y 
  retorna un nuevo stream con solo los elementos que cumplen esa condición.
  Ejemplo: v -> v.getVentasMensuales() > 5000000

• forEach(): Operación terminal que recibe un consumidor y ejecuta una acción 
  para cada elemento del stream. Es útil para imprimir resultados.

• max(): Operación terminal que encuentra el elemento máximo según un Comparator.
  Retorna un Optional para manejar el caso de stream vacío.

• mapToDouble(): Operación intermedia que transforma cada elemento del stream 
  a un valor primitivo double. Optimiza el rendimiento evitando autoboxing.

• sum(): Operación terminal que calcula la suma de todos los elementos 
  en un stream de valores numéricos.

• average(): Operación terminal que calcula el promedio de los elementos 
  numéricos. Retorna OptionalDouble para manejar streams vacíos.

• sorted(): Operación intermedia que ordena los elementos según un Comparator.
  .reversed() invierte el orden de descendente a ascendente.

2. ¿QUÉ ES UNA EXPRESIÓN LAMBDA?

Una expresión lambda es una función anónima que permite implementar interfaces 
funcionales de manera concisa. Sintaxis: (parámetros) -> { cuerpo }

Ejemplos en este código:
• v -> v.getVentasMensuales() > 5000000  (Predicate)
• v -> System.out.println("  - " + v)     (Consumer)
• Comparator.comparingDouble(Vendedor::getVentasMensuales)  (Comparator)

Ventajas:
- Código más conciso y legible
- Reduce necesidad de clases anónimas
- Facilita programación funcional
- Mejora rendimiento con compilación optimizada

3. ¿QUÉ ES UN OPTIONAL?

Optional es un contenedor que puede contener o no un valor no nulo. 
Fue introducido en Java 8 para evitar NullPointerException.

Características:
- Optional<Vendedor>: puede contener un Vendedor o estar vacío
- OptionalDouble: versión especializada para valores double primitivos
- Métodos principales: ifPresent(), orElse(), isPresent()

En este código se usa para:
- Manejar resultado de max() cuando no hay vendedores
- Manejar resultado de average() cuando la lista está vacía

4. ¿QUÉ VENTAJAS TIENE USAR STREAMS FRENTE A CICLOS TRADICIONALES?

Ventajas de Streams:
• Legibilidad: Código más declarativo y expresivo
• Componibilidad: Encadenamiento de operaciones fluido
• Paralelización: Fácil conversión a procesamiento paralelo con .parallel()
• Inmutabilidad: No modifica la colección original
• Optimización: JVM puede optimizar operaciones internamente
• Reducción de código: Menos líneas que ciclos for/while tradicionales

Ejemplo comparativo:
// Tradicional (10+ líneas)
double total = 0;
for (Vendedor v : vendedores) {
    if (v.getVentasMensuales() > 5000000) {
        total += v.getVentasMensuales();
    }
}

// Con Streams (1 línea)
double total = vendedores.stream()
    .filter(v -> v.getVentasMensuales() > 5000000)
    .mapToDouble(Vendedor::getVentasMensuales)
    .sum();

5. ¿CÓMO ESTE EJEMPLO SE RELACIONA CON SISTEMAS EMPRESARIALES REALES?

Este ejemplo modela escenarios reales de sistemas empresariales:

• Sistemas de Gestión de Ventas (CRM):
  - Análisis de rendimiento de vendedores
  - Cálculo de comisiones y bonificaciones
  - Segmentación por zonas geográficas

• Business Intelligence (BI):
  - Reportes de ventas en tiempo real
  - Identificación de mejores vendedores
  - Análisis de tendencias y métricas KPI

• Sistemas de Recursos Humanos:
  - Evaluación de desempeño
  - Cálculo de incentivos
  - Ranking de empleados

• Aplicaciones Financieras:
  - Procesamiento de transacciones
  - Cálculo de comisiones
  - Generación de reportes contables

Ventajas en producción:
- Escalabilidad: Procesamiento eficiente de grandes volúmenes de datos
- Mantenimiento: Código más fácil de entender y modificar
- Testing: Operaciones puras facilitan pruebas unitarias
- Performance: Optimización automática por JVM

La API Stream es fundamental en el desarrollo empresarial moderno con Java,
permitiendo procesar datos de manera eficiente y legible en aplicaciones
críticas de negocio.

---

## Resultados esperados
Al ejecutar el programa, verás:
- **3 vendedores** con ventas mayores a $5,000,000
- **Ana López** como la mejor vendedora ($9,200,000)
- **Total de ventas**: $32,100,000
- **Promedio de ventas**: $6,420,000
- **Ranking completo** ordenado de mayor a menor

## Tecnologías utilizadas
- Java 8+
- API Stream (java.util.stream)
- Expresiones Lambda
- Optional y OptionalDouble
- Programación Funcional
