package Ejercicio2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class Ventas_ABC {

    public static void main(String[] args) {
        
        // Crear una lista de vendedores con diferentes datos
        List<Vendedor> vendedores = Arrays.asList(
            new Vendedor("Carlos Rodríguez", "Norte", 8500000, 5.0),
            new Vendedor("María González", "Sur", 3200000, 3.5),
            new Vendedor("Luis Martínez", "Este", 6700000, 4.5),
            new Vendedor("Ana López", "Oeste", 9200000, 6.0),
            new Vendedor("Pedro Sánchez", "Centro", 4500000, 4.0)
        );

        System.out.println("=== SISTEMA DE VENTAS ABC - ANÁLISIS CON STREAMS ===\n");

        // a) Mostrar vendedores con ventas mayores a 5,000,000
        System.out.println("a) Vendedores con ventas mayores a $5,000,000:");
        vendedores.stream()                    // Crear un stream a partir de la lista
                .filter(v -> v.getVentasMensuales() > 5000000)  // Filtrar solo vendedores con ventas > 5M
                .forEach(v -> System.out.println("  - " + v));  // Imprimir cada vendedor filtrado

        System.out.println();

        // b) Encontrar el vendedor con mayor venta
        System.out.println("b) Vendedor con mayor venta:");
        Optional<Vendedor> vendedorMaxVenta = vendedores.stream()  // Crear stream
                .max(Comparator.comparingDouble(Vendedor::getVentasMensuales));  // Encontrar máximo por ventas
        
        vendedorMaxVenta.ifPresent(v -> System.out.println("  - " + v));  // Imprimir si existe

        System.out.println();

        // c) Calcular total de ventas del departamento
        System.out.println("c) Total de ventas del departamento:");
        double totalVentas = vendedores.stream()                    // Crear stream
                .mapToDouble(Vendedor::getVentasMensuales)        // Convertir a stream de double (ventas)
                .sum();                                            // Sumar todas las ventas
        
        System.out.printf("  - Total ventas: $%,.2f\n", totalVentas);

        System.out.println();

        // d) Calcular promedio de ventas
        System.out.println("d) Promedio de ventas:");
        OptionalDouble promedioVentas = vendedores.stream()        // Crear stream
                .mapToDouble(Vendedor::getVentasMensuales)        // Convertir a stream de double
                .average();                                        // Calcular promedio
        
        promedioVentas.ifPresent(prom -> System.out.printf("  - Promedio ventas: $%,.2f\n", prom));

        System.out.println();

        // e) Ordenar vendedores de mayor a menor venta
        System.out.println("e) Vendedores ordenados de mayor a menor venta:");
        vendedores.stream()                    // Crear stream
                .sorted(Comparator.comparingDouble(Vendedor::getVentasMensuales).reversed())  // Ordenar descendente
                .forEach(v -> System.out.println("  - " + v));  // Imprimir cada vendedor ordenado

        System.out.println("\n=== FIN DEL ANÁLISIS ===");
    }
}

// Clase Vendedor con atributos privados y métodos requeridos
class Vendedor {
    private String nombre;
    private String zona;
    private double ventasMensuales;
    private double comision;

    // Constructor
    public Vendedor(String nombre, String zona, double ventasMensuales, double comision) {
        this.nombre = nombre;
        this.zona = zona;
        this.ventasMensuales = ventasMensuales;
        this.comision = comision;
    }

    // Métodos getters
    public String getNombre() {
        return nombre;
    }

    public String getZona() {
        return zona;
    }

    public double getVentasMensuales() {
        return ventasMensuales;
    }

    public double getComision() {
        return comision;
    }

    // Método toString() para representación en cadena
    @Override
    public String toString() {
        return String.format("Vendedor: %s | Zona: %s | Ventas: $%,.2f | Comisión: %.1f%%", 
                nombre, zona, ventasMensuales, comision);
    }
}

