package Ejercicio1;
import java.util.Arrays;
import java.util.List;

public class streams_umb {

    public static void main(String[] args) {

        List<Estudiante> estudiante = Arrays.asList(
                // Lista de estudiantes de la UMB - Facultad - Edad - $ Diario de la U
                new Estudiante("Diana", "Software", 17, 20000),
                new Estudiante("Felipe", "Software", 16, 25000),
                new Estudiante("Andres", "Mecatronica", 17, 30000),
                new Estudiante("Camilo", "Biomedica", 18, 15000),
                new Estudiante("Esteban", "Biomedica", 19, 10000)
        );

        System.out.println("Manejo de Streams en Java - Universidad Manuela Beltran");

        // 1. Filtrar estudiantes de la UMB con $ diario de la U > 20.000
        System.out.println("Estudiante con diario > 20000:");
        estudiante.stream()
                .filter(e -> e.getDiario() > 20000)
                .forEach(e -> System.out.println(e.getNombre() + " - " + e.getDiario() + " - " + e.getFacultad()));

        // 2. Encontrar el estudiante de la UMB mas joven de la clase
        estudiante.stream()
                .min((e1, e2) -> Integer.compare(e1.getEdad(), e2.getEdad()))
                .ifPresent(e -> System.out.println("Estudiante mas joven: " + e.getNombre()
                        + " (" + e.getEdad() + " años) " + e.getFacultad()));

        // 3. Sumar todos los diarios de los estudiantes de la clase
        double totalDiario = estudiante.stream()
                .mapToDouble(Estudiante::getDiario)
                .sum();

        System.out.println("Total de dinero de estudiantes en la clase: " + totalDiario);
    }

    // Clase interna para representar un Estudiante de la UMB
    static class Estudiante {
        private String nombre;
        private String facultad;
        private int edad;
        private double diario;

        public Estudiante(String nombre, String facultad, int edad, double diario) {
            this.nombre = nombre;
            this.edad = edad;
            this.facultad = facultad;
            this.diario = diario;
        }

        // Retornar parametros en cada método
        public String getNombre() {
            return nombre;
        }

        public int getEdad() {
            return edad;
        }

        public String getFacultad() {
            return facultad;
        }

        public double getDiario() {
            return diario;
        }
    }
}