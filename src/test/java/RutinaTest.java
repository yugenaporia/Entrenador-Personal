import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Rutina")
public class RutinaTest {

    /** Ejecuta una acción capturando lo que escribe en la salida estándar. */
    private static String capturarSalida(Runnable accion) {
        PrintStream original = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturada));
        try {
            accion.run();
        } finally {
            System.setOut(original);
        }
        return capturada.toString().replace("\r\n", "\n");
    }

    @Test
    @DisplayName("Constructor de Rutina")
    void constructorRutina() {
        Rutina rutina = new Rutina("Full Body", 4, 2);
        assertEquals("Full Body", rutina.getNombre());
        assertEquals(4, rutina.getMaxEjercicios());
        assertEquals(2, rutina.getMaxEstiramientos());
        assertEquals(0, rutina.numEjercicios());
        assertEquals(0, rutina.numEstiramientos());
        assertEquals(0, rutina.getEjercicios().length, "Una rutina nueva no tiene ejercicios");
        assertEquals(0, rutina.getEstiramientos().length, "Una rutina nueva no tiene estiramientos");
    }

    @Test
    @DisplayName("Añadir ejercicio a rutina vacía")
    void agregarEjercicioARutinaVacia() {
        Rutina rutina = new Rutina("Full Body", 3, 3);
        assertTrue(rutina.agregarEjercicio("Sentadillas"));
        assertEquals(1, rutina.numEjercicios());
    }

    @Test
    @DisplayName("Añadir ejercicio a rutina con ejercicios")
    void agregarEjercicioARutinaConEjercicios() {
        Rutina rutina = new Rutina("Full Body", 3, 3);
        rutina.agregarEjercicio("Sentadillas");
        assertTrue(rutina.agregarEjercicio("Flexiones"));
        assertEquals(2, rutina.numEjercicios());
        assertArrayEquals(new String[]{"Sentadillas", "Flexiones"}, rutina.getEjercicios(),
                "Los ejercicios deben conservar el orden de inserción");
    }

    @Test
    @DisplayName("Añadir ejercicio a rutina completa muestra error y no lo añade")
    void agregarEjercicioARutinaCompleta() {
        Rutina rutina = new Rutina("Full Body", 1, 3);
        rutina.agregarEjercicio("Sentadillas");
        boolean[] resultado = new boolean[1];
        String salida = capturarSalida(() -> resultado[0] = rutina.agregarEjercicio("Flexiones"));
        assertFalse(resultado[0]);
        assertEquals(1, rutina.numEjercicios());
        assertEquals("No se pueden añadir más ejercicios.\n", salida);
    }

    @Test
    @DisplayName("Añadir estiramiento a rutina vacía")
    void agregarEstiramientoARutinaVacia() {
        Rutina rutina = new Rutina("Full Body", 3, 3);
        assertTrue(rutina.agregarEstiramiento("Postura del niño"));
        assertEquals(1, rutina.numEstiramientos());
    }

    @Test
    @DisplayName("Añadir estiramiento a rutina con estiramientos")
    void agregarEstiramientoARutinaConEstiramientos() {
        Rutina rutina = new Rutina("Full Body", 3, 3);
        rutina.agregarEstiramiento("Postura del niño");
        assertTrue(rutina.agregarEstiramiento("Mariposa"));
        assertEquals(2, rutina.numEstiramientos());
        assertArrayEquals(new String[]{"Postura del niño", "Mariposa"}, rutina.getEstiramientos());
    }

    @Test
    @DisplayName("Añadir estiramiento a rutina completa muestra error y no lo añade")
    void agregarEstiramientoARutinaCompleta() {
        Rutina rutina = new Rutina("Full Body", 3, 1);
        rutina.agregarEstiramiento("Postura del niño");
        boolean[] resultado = new boolean[1];
        String salida = capturarSalida(() -> resultado[0] = rutina.agregarEstiramiento("Mariposa"));
        assertFalse(resultado[0]);
        assertEquals(1, rutina.numEstiramientos());
        assertEquals("No se pueden añadir más estiramientos.\n", salida);
    }

    @Test
    @DisplayName("Los ejercicios y estiramientos no se mezclan entre sí")
    void listasIndependientes() {
        Rutina rutina = new Rutina("Full Body", 1, 2);
        rutina.agregarEjercicio("Sentadillas");
        assertFalse(rutina.estiramientosCompletos(), "Añadir ejercicios no debe llenar los estiramientos");
        rutina.agregarEstiramiento("Mariposa");
        assertTrue(rutina.ejerciciosCompletos());
        assertFalse(rutina.estiramientosCompletos());
    }

    @Test
    @DisplayName("Los getters devuelven copias: modificarlas no altera la rutina")
    void gettersDevuelvenCopias() {
        Rutina rutina = new Rutina("Full Body", 3, 3);
        rutina.agregarEjercicio("Sentadillas");
        rutina.getEjercicios()[0] = "Otra cosa";
        assertEquals("Sentadillas", rutina.getEjercicios()[0]);
    }

    @Test
    @DisplayName("Ejercicios completos / no completos")
    void ejerciciosCompletos() {
        Rutina rutina = new Rutina("Full Body", 2, 3);
        rutina.agregarEjercicio("Sentadillas");
        assertFalse(rutina.ejerciciosCompletos());
        rutina.agregarEjercicio("Flexiones");
        assertTrue(rutina.ejerciciosCompletos());
    }

    @Test
    @DisplayName("Estiramientos completos / no completos")
    void estiramientosCompletos() {
        Rutina rutina = new Rutina("Full Body", 3, 2);
        rutina.agregarEstiramiento("Mariposa");
        assertFalse(rutina.estiramientosCompletos());
        rutina.agregarEstiramiento("Postura del niño");
        assertTrue(rutina.estiramientosCompletos());
    }

    @Test
    @DisplayName("Una rutina con máximo 0 está completa desde el principio")
    void maximoCero() {
        Rutina rutina = new Rutina("Vacía", 0, 0);
        assertTrue(rutina.ejerciciosCompletos());
        assertTrue(rutina.estiramientosCompletos());
        assertEquals(0, rutina.getEjercicios().length);
    }

    @ParameterizedTest
    @DisplayName("Formato textual de la rutina para mostrar en pantalla")
    @CsvSource({
            "Full Body, Sentadillas, Flexiones, Postura del niño, Mariposa",
            "Pierna y Glúteo, Hip thrust, Zancadas, Estiramiento de gemelos, Estiramiento de isquiotibiales",
            "HIIT, Burpees 40s, Sprint 30s, Respiración profunda, Estiramiento de cuello"
    })
    void toStringFormatoCorrecto(String nombre, String ejercicio1, String ejercicio2,
                                 String estiramiento1, String estiramiento2) {
        Rutina rutina = new Rutina(nombre, 3, 3);
        rutina.agregarEjercicio(ejercicio1);
        rutina.agregarEjercicio(ejercicio2);
        rutina.agregarEstiramiento(estiramiento1);
        rutina.agregarEstiramiento(estiramiento2);
        String esperado = String.format(
                "Rutina: %s\nEjercicios:\n- %s\n- %s\nEstiramientos:\n1. %s\n2. %s\n",
                nombre, ejercicio1, ejercicio2, estiramiento1, estiramiento2);
        assertEquals(esperado, rutina.toString());
    }

    @Test
    @DisplayName("Formato textual de una rutina sin ejercicios ni estiramientos")
    void toStringRutinaVacia() {
        Rutina rutina = new Rutina("Vacía", 3, 3);
        assertEquals("Rutina: Vacía\nEjercicios:\nEstiramientos:\n", rutina.toString());
    }

    @ParameterizedTest
    @DisplayName("Formato textual de la rutina para guardar en archivo (raw)")
    @CsvSource({
            "Full Body, Sentadillas, Flexiones, Postura del niño, Mariposa",
            "Pierna y Glúteo, Hip thrust, Zancadas, Estiramiento de gemelos, Estiramiento de isquiotibiales",
            "HIIT, Burpees 40s, Sprint 30s, Respiración profunda, Estiramiento de cuello"
    })
    void toRawStringFormatoCorrecto(String nombre, String ejercicio1, String ejercicio2,
                                    String estiramiento1, String estiramiento2) {
        Rutina rutina = new Rutina(nombre, 3, 3);
        rutina.agregarEjercicio(ejercicio1);
        rutina.agregarEjercicio(ejercicio2);
        rutina.agregarEstiramiento(estiramiento1);
        rutina.agregarEstiramiento(estiramiento2);
        String esperado = String.format("%s\n%s\n%s\nESTIRAMIENTOS\n%s\n%s\n=====\n",
                nombre, ejercicio1, ejercicio2, estiramiento1, estiramiento2);
        assertEquals(esperado, rutina.toRawString());
    }

    @Test
    @DisplayName("Formato raw de una rutina sin ejercicios ni estiramientos")
    void toRawStringRutinaVacia() {
        Rutina rutina = new Rutina("Vacía", 3, 3);
        assertEquals("Vacía\nESTIRAMIENTOS\n=====\n", rutina.toRawString());
    }
}
