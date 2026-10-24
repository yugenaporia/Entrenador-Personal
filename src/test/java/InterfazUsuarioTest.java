import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de integración de la interfaz de usuario")
public class InterfazUsuarioTest {
    private static final String RUTINAS_PREDEFINIDAS = "src/test/resources/rutinas_predefinidas.txt";

    private InputStream entradaOriginal;
    private PrintStream salidaOriginal;

    @BeforeEach
    void guardarEntradaYSalida() {
        entradaOriginal = System.in;
        salidaOriginal = System.out;
    }

    @AfterEach
    void restaurarEntradaYSalida() {
        System.setIn(entradaOriginal);
        System.setOut(salidaOriginal);
    }

    /** Simula la entrada del usuario, ejecuta la interfaz y devuelve todo lo que ha escrito en pantalla. */
    private String ejecutar(InterfazUsuario interfaz, String entrada) {
        System.setIn(new ByteArrayInputStream(entrada.getBytes()));
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salidaCapturada));
        interfaz.iniciar();
        return salidaCapturada.toString().replace("\r\n", "\n");
    }

    private String ejecutar(String entrada, int maxEjercicios, int maxEstiramientos, int maxRutinas) {
        return ejecutar(new InterfazUsuario(maxEjercicios, maxEstiramientos, maxRutinas), entrada);
    }

    private String ejecutar(String entrada) {
        return ejecutar(entrada, 3, 3, 3);
    }

    /** Entrada que da de alta la rutina "Full Body" (2 ejercicios y 1 estiramiento). */
    private static final String ALTA_FULL_BODY = """
            1
            Full Body
            Sentadillas
            Flexiones
            fin
            Mariposa
            fin
            """;

    @Test
    @DisplayName("Menú principal (formato exacto)")
    void menuPrincipal() {
        String esperado = """
                --- Menú Principal ---
                1. Agregar Rutina
                2. Consultar/Editar Rutina
                3. Planificar Entrenamientos
                4. Guardar Rutinas
                5. Cargar Rutinas
                6. Guardar Plan Semanal
                7. Salir

                Elige una opción:\s""";
        assertEquals(esperado, ejecutar("7\n"));
    }

    @Test
    @DisplayName("Menú principal: opciones inválidas se ignoran y se vuelve a pedir")
    void menuPrincipalOpcionInvalida() {
        String salida = ejecutar("9\nabc\n0\n7\n");
        assertEquals(1, salida.split("--- Menú Principal ---", -1).length - 1,
                "El menú no debe redibujarse en cada error de lectura");
        assertEquals(4, salida.split("Elige una opción: ", -1).length - 1, "3 intentos inválidos + 1 válido");
    }

    @Test
    @DisplayName("Menú principal: tras cada acción se vuelve a mostrar el menú")
    void menuPrincipalSeRepite() {
        String salida = ejecutar("4\nprueba_no_se_usa/ruta/invalida.txt\n7\n");
        assertEquals(2, salida.split("--- Menú Principal ---", -1).length - 1);
    }

    @Test
    @DisplayName("Menu: Agregar rutina satisfactoriamente")
    void agregarRutinaSatisfactoriamente() {
        String salida = ejecutar(ALTA_FULL_BODY + "7\n");
        assertTrue(salida.contains("Nombre de la rutina: "));
        assertTrue(salida.contains("Introduce los ejercicios (una línea por ejercicio, escribe 'fin' para terminar):"));
        assertTrue(salida.contains("Introduce los estiramientos (una línea por estiramiento, escribe 'fin' para terminar):"));
        assertTrue(salida.contains("¡Rutina agregada exitosamente!"));
        assertFalse(salida.contains("No se pudo añadir la rutina."));
    }

    @Test
    @DisplayName("Menu: Agregar rutina fallida (catálogo lleno)")
    void agregarRutinaFallida() {
        String salida = ejecutar(ALTA_FULL_BODY + "7\n", 3, 3, 0);
        assertTrue(salida.contains("No se pudo añadir la rutina."));
        assertFalse(salida.contains("¡Rutina agregada exitosamente!"));
    }

    @Test
    @DisplayName("Menu: Agregar rutina con más elementos que el máximo (se siguen leyendo líneas hasta 'fin')")
    void agregarRutinaExcediendoMaximos() {
        String entrada = """
                1
                Full Body
                Sentadillas
                Flexiones
                Dominadas
                FIN
                Mariposa
                Postura del niño
                Fin
                7
                """;
        String salida = ejecutar(entrada, 1, 1, 3);
        assertTrue(salida.contains("No se pueden añadir más ejercicios."));
        assertTrue(salida.contains("No se pueden añadir más estiramientos."));
        assertTrue(salida.contains("¡Rutina agregada exitosamente!"), "La rutina se crea con lo que cupo");
    }

    @Test
    @DisplayName("Menu: Consultar, seleccionar y editar rutina")
    void consultarRutina() {
        String entrada = ALTA_FULL_BODY + """
                2
                Full
                1
                1
                Burpees
                2
                full
                1
                2
                Postura del niño
                2
                FULL
                1
                3
                2
                full
                -FIN-
                7
                """;
        String salida = ejecutar(entrada);
        assertTrue(salida.contains("Introduce el texto de la rutina a buscar (-FIN- para volver): "));
        assertTrue(salida.contains("Rutinas encontradas:\n1. Full Body\n"));
        assertTrue(salida.contains("Elige una rutina: "));
        assertTrue(salida.contains("1. Añadir ejercicio\n2. Añadir estiramiento\n3. Eliminar rutina\n4. Volver\n"));
        assertTrue(salida.contains("Introduce el ejercicio a añadir: "));
        assertTrue(salida.contains("Introduce el estiramiento a añadir: "));
        assertTrue(salida.contains("Rutina: Full Body\nEjercicios:\n- Sentadillas\n- Flexiones\n- Burpees\n"),
                "Al volver a consultar la rutina debe aparecer el ejercicio añadido");
        assertTrue(salida.contains("Estiramientos:\n1. Mariposa\n2. Postura del niño\n"),
                "Al volver a consultar la rutina debe aparecer el estiramiento añadido");
        assertTrue(salida.contains("Rutina eliminada."));
        assertTrue(salida.contains("No se encontraron rutinas."), "Tras eliminarla ya no debe encontrarse");
    }

    @Test
    @DisplayName("Menu: Consultar rutina y cancelar la búsqueda")
    void consultarRutinaCancelando() {
        String salida = ejecutar("2\n-FIN-\n7\n");
        assertTrue(salida.contains("Introduce el texto de la rutina a buscar (-FIN- para volver): "));
        assertFalse(salida.contains("Rutinas encontradas:"));
    }

    @Test
    @DisplayName("Menu: Consultar con varias coincidencias y elegir la segunda")
    void consultarVariasCoincidencias() {
        String entrada = """
                1
                Pierna A
                fin
                fin
                1
                Pierna B
                fin
                fin
                2
                pierna
                2
                4
                7
                """;
        String salida = ejecutar(entrada);
        assertTrue(salida.contains("Rutinas encontradas:\n1. Pierna A\n2. Pierna B\n"));
        assertTrue(salida.contains("Rutina: Pierna B\n"));
        assertFalse(salida.contains("Rutina: Pierna A\n"));
    }

    @Test
    @DisplayName("Menu: Elegir una rutina fuera de rango vuelve a preguntar")
    void seleccionFueraDeRango() {
        String entrada = ALTA_FULL_BODY + "2\nfull\n5\n0\nabc\n1\n4\n7\n";
        String salida = ejecutar(entrada);
        assertEquals(4, salida.split("Elige una rutina: ", -1).length - 1, "3 intentos inválidos + 1 válido");
        assertTrue(salida.contains("Rutina: Full Body\n"));
    }

    @Test
    @DisplayName("Menu: Planificar entrenamientos")
    void planificarEntrenamientos() {
        String entrada = ALTA_FULL_BODY + """
                3
                L
                Full
                1
                7
                """;
        String salida = ejecutar(entrada);
        assertTrue(salida.contains("Planificación de entrenamientos para la semana:"));
        assertTrue(salida.contains("Introduce el día de la semana (L, M, X, J, V, S, D): "));
        assertTrue(salida.contains("Rutinas encontradas:"));
        assertTrue(salida.contains("Rutina planificada para Lunes"));
    }

    @Test
    @DisplayName("Menu: Planificar muestra la tabla con lo ya planificado y repite si el día es inválido")
    void planificarMuestraTablaYValidaDia() {
        String entrada = ALTA_FULL_BODY + """
                3
                x
                Full
                1
                3
                Z
                m
                Full
                1
                7
                """;
        String salida = ejecutar(entrada);
        assertTrue(salida.contains("Rutina planificada para Miércoles"));
        assertTrue(salida.contains("Rutina planificada para Martes"));

        String cabecera = "Planificación de entrenamientos para la semana:";
        assertEquals(2, salida.split(cabecera, -1).length - 1, "Se ha entrado dos veces en la planificación");
        int inicioSegunda = salida.lastIndexOf(cabecera);
        int finTabla = salida.indexOf("Introduce el día de la semana", inicioSegunda);
        assertTrue(salida.substring(inicioSegunda, finTabla).contains("Full Body"),
                "La segunda vez la tabla ya debe mostrar la rutina planificada");

        String pregunta = "Introduce el día de la semana (L, M, X, J, V, S, D): ";
        assertEquals(3, salida.split(java.util.regex.Pattern.quote(pregunta), -1).length - 1,
                "1 vez la primera planificación + 2 la segunda (Z es inválido)");
    }

    @Test
    @DisplayName("Menu: Planificar y cancelar la búsqueda no planifica nada")
    void planificarCancelando() {
        String salida = ejecutar("3\nL\n-FIN-\n7\n");
        assertFalse(salida.contains("Rutina planificada para"));
    }

    @Test
    @DisplayName("Menu: Guardar rutinas")
    void guardarRutinas() throws IOException {
        Path archivo = Files.createTempFile("rutinas_ui", ".txt");
        try {
            String salida = ejecutar(ALTA_FULL_BODY + "4\n" + archivo + "\n7\n");
            assertTrue(salida.contains("Introduce el nombre del archivo donde guardar las rutinas: "));
            assertTrue(salida.contains("Rutinas guardadas en " + archivo));
            assertEquals("Full Body\nSentadillas\nFlexiones\nESTIRAMIENTOS\nMariposa\n=====\n",
                    Files.readString(archivo));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    @DisplayName("Menu: Guardar rutinas en una ruta inválida muestra el error")
    void guardarRutinasError() {
        String salida = ejecutar("4\nno/existe/carpeta/rutinas.txt\n7\n");
        assertTrue(salida.contains("Error al guardar el archivo."));
        assertFalse(salida.contains("Rutinas guardadas en"));
    }

    @Test
    @DisplayName("Menu: Cargar rutinas")
    void cargarRutinas() {
        String salida = ejecutar("5\n" + RUTINAS_PREDEFINIDAS + "\n2\nyoga\n1\n4\n7\n", 6, 6, 20);
        assertTrue(salida.contains("Introduce la ruta del archivo de donde cargar las rutinas: "));
        assertTrue(salida.contains("Rutinas cargadas desde " + RUTINAS_PREDEFINIDAS));
        assertTrue(salida.contains("Rutina: Yoga Suave"), "Las rutinas cargadas deben poder consultarse");
    }

    @Test
    @DisplayName("Menu: Cargar rutinas de un archivo inexistente muestra el error")
    void cargarRutinasError() {
        String salida = ejecutar("5\nno/existe.txt\n7\n");
        assertTrue(salida.contains("Error al cargar el archivo."));
        assertFalse(salida.contains("Rutinas cargadas desde"));
    }

    @Test
    @DisplayName("Menu: Guardar plan semanal")
    void guardarPlanSemanal() throws IOException {
        Path archivo = Files.createTempFile("plan_ui", ".txt");
        try {
            String entrada = ALTA_FULL_BODY + "3\nL\nFull\n1\n6\n" + archivo + "\n7\n";
            String salida = ejecutar(entrada);
            assertTrue(salida.contains("Introduce el nombre del archivo donde guardar el plan semanal: "));
            assertTrue(salida.contains("Plan semanal guardado en " + archivo));
            String esperado = """
                    Lunes: Full Body
                    Martes: Descanso
                    Miércoles: Descanso
                    Jueves: Descanso
                    Viernes: Descanso
                    Sábado: Descanso
                    Domingo: Descanso
                    """;
            assertEquals(esperado, Files.readString(archivo));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    @DisplayName("Menu: Guardar plan semanal en ruta inválida muestra el error")
    void guardarPlanSemanalError() {
        String salida = ejecutar("6\nno/existe/carpeta/plan.txt\n7\n");
        assertTrue(salida.contains("Error al guardar el archivo."));
    }

    @Test
    @DisplayName("Constructor con archivo: carga las rutinas al arrancar")
    void constructorConArchivo() {
        InterfazUsuario interfaz = new InterfazUsuario(6, 6, 20, RUTINAS_PREDEFINIDAS);
        String salida = ejecutar(interfaz, "2\nyoga\n1\n4\n7\n");
        assertTrue(salida.contains("Rutina: Yoga Suave"));
    }

    @Test
    @DisplayName("Constructor con archivo inexistente: avisa y arranca con el catálogo vacío")
    void constructorConArchivoInexistente() {
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturada));
        InterfazUsuario interfaz = new InterfazUsuario(3, 3, 3, "no/existe.txt");
        System.setOut(salidaOriginal);
        assertTrue(capturada.toString().contains("Error al cargar el archivo."));

        String salida = ejecutar(interfaz, "2\n\n-FIN-\n7\n");
        assertTrue(salida.contains("No se encontraron rutinas."));
    }
}
