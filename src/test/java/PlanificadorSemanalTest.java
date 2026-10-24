import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase PlanificadorSemanal")
public class PlanificadorSemanalTest {

    private static Rutina rutina(String nombre) {
        return new Rutina(nombre, 3, 3);
    }

    @ParameterizedTest
    @DisplayName("Agregar entrenamiento a un día específico")
    @ValueSource(strings = {"Full Body", "Yoga", "Pierna y Glúteo"})
    void agregarEntrenamientoADiaEspecifico(String nombre) {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(0, rutina(nombre));
        assertTrue(planificador.toString().contains(nombre));
    }

    @ParameterizedTest
    @DisplayName("Agregar entrenamiento a varios días")
    @CsvSource({
            "Full Body, Yoga, HIIT",
            "Pecho y Tríceps, Espalda y Bíceps, Hombro y Core",
            "Movilidad, Pierna, Cardio"
    })
    void agregarEntrenamientoAVariosDias(String n1, String n2, String n3) {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(0, rutina(n1));
        planificador.agregarEntrenamiento(1, rutina(n2));
        planificador.agregarEntrenamiento(2, rutina(n3));
        String plan = planificador.toString();
        assertTrue(plan.contains(n1));
        assertTrue(plan.contains(n2));
        assertTrue(plan.contains(n3));
    }

    @ParameterizedTest
    @DisplayName("Agregar entrenamiento a un día ya ocupado lo sustituye")
    @CsvSource({
            "Full Body, Yoga",
            "Pecho, Espalda",
            "Movilidad, Cardio"
    })
    void agregarEntrenamientoADiaOcupado(String anterior, String nueva) {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(4, rutina(anterior));
        planificador.agregarEntrenamiento(4, rutina(nueva));
        String plan = planificador.toString();
        assertFalse(plan.contains(anterior));
        assertTrue(plan.contains(nueva));
    }

    @Test
    @DisplayName("Agregar null libera el día (descanso)")
    void agregarNullLiberaDia() {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(2, rutina("Yoga"));
        planificador.agregarEntrenamiento(2, null);
        assertFalse(planificador.toString().contains("Yoga"));
    }

    @ParameterizedTest
    @DisplayName("Un día fuera de rango se ignora sin lanzar excepciones")
    @ValueSource(ints = {-1, 7, 100})
    void diaFueraDeRango(int dia) {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        assertDoesNotThrow(() -> planificador.agregarEntrenamiento(dia, rutina("Yoga")));
        assertFalse(planificador.toString().contains("Yoga"));
    }

    @Test
    @DisplayName("Representación textual del planificador semanal sin rutinas")
    void planificadorSemanalSinRutinas() {
        String separador = "-".repeat(77);
        String esperado = separador + "\n"
                + " Lunes      Martes     Miércoles  Jueves     Viernes    Sábado     Domingo" + " ".repeat(3) + "\n"
                + separador + "\n"
                + " ".repeat(77) + "\n"
                + separador + "\n"
                + "\n";
        assertEquals(esperado, new PlanificadorSemanal().toString());
    }

    @Test
    @DisplayName("Representación textual del planificador semanal con algunas rutinas")
    void planificadorSemanalConRutinas() {
        // La rutina más larga tiene 16 caracteres ("Espalda y Bíceps") -> columnas de 17 + 1 espacio inicial
        String separador = "-".repeat(7 * 18);
        String esperado = separador + "\n"
                + " Lunes             Martes            Miércoles         Jueves            "
                + "Viernes           Sábado            Domingo" + " ".repeat(10) + "\n"
                + separador + "\n"
                + " Pierna y Glúteo                                       Espalda y Bíceps  "
                + "                                    Yoga" + " ".repeat(13) + "\n"
                + separador + "\n"
                + "\n";
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(0, rutina("Pierna y Glúteo"));
        planificador.agregarEntrenamiento(3, rutina("Espalda y Bíceps"));
        planificador.agregarEntrenamiento(6, rutina("Yoga"));
        assertEquals(esperado, planificador.toString());
    }

    @Test
    @DisplayName("El ancho de las columnas se adapta al nombre más largo")
    void anchoSeAdaptaAlNombreMasLargo() {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(5, rutina("Una rutina con un nombre muy pero que muy largo"));
        String[] lineas = planificador.toString().split("\n");
        int ancho = "Una rutina con un nombre muy pero que muy largo".length() + 1;
        assertEquals(7 * (ancho + 1), lineas[0].length(), "La longitud del separador depende del nombre más largo");
        assertEquals(lineas[0].length(), lineas[1].length(), "Todas las líneas de la tabla miden lo mismo");
        assertEquals(lineas[0].length(), lineas[3].length(), "Todas las líneas de la tabla miden lo mismo");
    }

    @Test
    @DisplayName("Guardar plan en archivo")
    void guardarPlanEnArchivo() throws IOException {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        planificador.agregarEntrenamiento(0, rutina("Pierna y Glúteo"));
        planificador.agregarEntrenamiento(3, rutina("Espalda y Bíceps"));
        planificador.agregarEntrenamiento(6, rutina("Yoga"));

        Path fichero = Files.createTempFile("semana", ".txt");
        try {
            planificador.guardarPlanEnArchivo(fichero.toString());
            String esperado = """
                    Lunes: Pierna y Glúteo
                    Martes: Descanso
                    Miércoles: Descanso
                    Jueves: Espalda y Bíceps
                    Viernes: Descanso
                    Sábado: Descanso
                    Domingo: Yoga
                    """;
            assertEquals(esperado, Files.readString(fichero));
        } finally {
            Files.deleteIfExists(fichero);
        }
    }

    @Test
    @DisplayName("Guardar plan vacío y sobreescribir un archivo existente")
    void guardarPlanVacioSobreescribe() throws IOException {
        Path fichero = Files.createTempFile("semana", ".txt");
        try {
            Files.writeString(fichero, "basura\n".repeat(50));
            new PlanificadorSemanal().guardarPlanEnArchivo(fichero.toString());
            String esperado = """
                    Lunes: Descanso
                    Martes: Descanso
                    Miércoles: Descanso
                    Jueves: Descanso
                    Viernes: Descanso
                    Sábado: Descanso
                    Domingo: Descanso
                    """;
            assertEquals(esperado, Files.readString(fichero));
        } finally {
            Files.deleteIfExists(fichero);
        }
    }

    @Test
    @DisplayName("Guardar en una ruta inválida lanza IOException")
    void guardarRutaInvalida() {
        PlanificadorSemanal planificador = new PlanificadorSemanal();
        assertThrows(IOException.class, () -> planificador.guardarPlanEnArchivo("no/existe/carpeta/plan.txt"));
    }
}
