import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase CatalogoDeRutinas")
public class CatalogoDeRutinasTest {

    private static Rutina rutina(String nombre) {
        return new Rutina(nombre, 3, 3);
    }

    @Test
    @DisplayName("Un catálogo nuevo está vacío")
    void catalogoNuevo() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        assertEquals(0, catalogo.numRutinas());
        assertFalse(catalogo.catalogoCompleto());
    }

    @Test
    @DisplayName("Agregar rutina a catálogo vacío")
    void agregarRutinaACatalogoVacio() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        assertTrue(catalogo.agregarRutina(rutina("Full Body")));
        assertEquals(1, catalogo.numRutinas());
    }

    @Test
    @DisplayName("Agregar rutina a catálogo con rutinas")
    void agregarRutinaACatalogoConRutinas() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        catalogo.agregarRutina(rutina("Full Body"));
        assertTrue(catalogo.agregarRutina(rutina("Yoga")));
        assertEquals(2, catalogo.numRutinas());
    }

    @Test
    @DisplayName("Agregar rutina a catálogo completo muestra error y no la añade")
    void agregarRutinaACatalogoCompleto() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(1);
        catalogo.agregarRutina(rutina("Full Body"));
        assertTrue(catalogo.catalogoCompleto());

        PrintStream original = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturada));
        boolean agregada;
        try {
            agregada = catalogo.agregarRutina(rutina("Yoga"));
        } finally {
            System.setOut(original);
        }
        assertFalse(agregada);
        assertEquals(1, catalogo.numRutinas());
        assertEquals("No se pueden añadir más rutinas.", capturada.toString().trim());
    }

    @Test
    @DisplayName("Agregar una rutina nula no hace nada")
    void agregarRutinaNula() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        assertFalse(catalogo.agregarRutina(null));
        assertEquals(0, catalogo.numRutinas());
    }

    @ParameterizedTest
    @DisplayName("Buscar rutina por nombre (existente, solo una; sin distinguir mayúsculas)")
    @ValueSource(strings = {"Yoga", "yoga", "YOGA", "og", "OG"})
    void buscarRutinaExistenteUna(String texto) {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        catalogo.agregarRutina(rutina("Yoga Suave"));
        catalogo.agregarRutina(rutina("HIIT Cardio"));
        Rutina[] resultados = catalogo.buscarRutinaPorNombre(texto);
        assertEquals(1, resultados.length);
        assertEquals("Yoga Suave", resultados[0].getNombre());
    }

    @ParameterizedTest
    @DisplayName("Buscar rutina por nombre (existente, varias, en orden de inserción)")
    @ValueSource(strings = {"Pierna", "pierna", "PIERNA", "ierna", "rna"})
    void buscarRutinaExistenteVarias(String texto) {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        catalogo.agregarRutina(rutina("Pierna y Glúteo"));
        catalogo.agregarRutina(rutina("Yoga"));
        catalogo.agregarRutina(rutina("Pierna Explosiva"));
        Rutina[] resultados = catalogo.buscarRutinaPorNombre(texto);
        assertEquals(2, resultados.length);
        assertEquals("Pierna y Glúteo", resultados[0].getNombre());
        assertEquals("Pierna Explosiva", resultados[1].getNombre());
    }

    @ParameterizedTest
    @DisplayName("Buscar rutina por nombre (no existente devuelve array vacío)")
    @ValueSource(strings = {"Pilates", "pilates", "PILATES", "crossfit", "cross"})
    void buscarRutinaNoExistente(String texto) {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        catalogo.agregarRutina(rutina("Yoga"));
        catalogo.agregarRutina(rutina("HIIT"));
        Rutina[] resultados = catalogo.buscarRutinaPorNombre(texto);
        assertNotNull(resultados);
        assertEquals(0, resultados.length);
    }

    @Test
    @DisplayName("Buscar en catálogo vacío devuelve array vacío")
    void buscarEnCatalogoVacio() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        Rutina[] resultados = catalogo.buscarRutinaPorNombre("yoga");
        assertNotNull(resultados);
        assertEquals(0, resultados.length);
    }

    @Test
    @DisplayName("Buscar con texto vacío devuelve todas las rutinas")
    void buscarTextoVacio() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(5);
        catalogo.agregarRutina(rutina("Yoga"));
        catalogo.agregarRutina(rutina("HIIT"));
        catalogo.agregarRutina(rutina("Full Body"));
        Rutina[] resultados = catalogo.buscarRutinaPorNombre("");
        assertEquals(3, resultados.length, "El array devuelto no debe tener huecos (nulls) al final");
        assertEquals("Yoga", resultados[0].getNombre());
        assertEquals("HIIT", resultados[1].getNombre());
        assertEquals("Full Body", resultados[2].getNombre());
    }

    @Test
    @DisplayName("Eliminar rutina existente")
    void eliminarRutinaExistente() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        Rutina rutina = rutina("Yoga");
        catalogo.agregarRutina(rutina);
        catalogo.eliminarRutina(rutina);
        assertEquals(0, catalogo.numRutinas());
        assertEquals(0, catalogo.buscarRutinaPorNombre("").length);
    }

    @Test
    @DisplayName("Eliminar una rutina intermedia conserva el orden del resto")
    void eliminarRutinaIntermedia() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        Rutina a = rutina("A");
        Rutina b = rutina("B");
        Rutina c = rutina("C");
        catalogo.agregarRutina(a);
        catalogo.agregarRutina(b);
        catalogo.agregarRutina(c);
        catalogo.eliminarRutina(b);
        Rutina[] resultados = catalogo.buscarRutinaPorNombre("");
        assertEquals(2, resultados.length);
        assertSame(a, resultados[0]);
        assertSame(c, resultados[1]);
    }

    @Test
    @DisplayName("Tras eliminar se puede volver a agregar")
    void eliminarLiberaHueco() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(1);
        Rutina a = rutina("A");
        catalogo.agregarRutina(a);
        catalogo.eliminarRutina(a);
        assertTrue(catalogo.agregarRutina(rutina("B")));
    }

    @Test
    @DisplayName("Eliminar rutina no existente (mismo nombre, distinto objeto) no hace nada")
    void eliminarRutinaNoExistente() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        catalogo.agregarRutina(rutina("Yoga"));
        catalogo.eliminarRutina(rutina("Yoga"));
        assertEquals(1, catalogo.numRutinas());
    }

    @Test
    @DisplayName("Cargar rutinas de archivo")
    void cargarRutinasDeArchivo() throws IOException {
        String contenido = """
                Full Body
                Sentadillas
                Flexiones
                ESTIRAMIENTOS
                Postura del niño
                Mariposa
                =====
                Yoga
                Saludo al sol
                ESTIRAMIENTOS
                Savasana
                =====
                Solo ejercicios
                Plancha
                ESTIRAMIENTOS
                =====
                """;
        Path archivo = Files.createTempFile("rutinas", ".txt");
        try {
            Files.writeString(archivo, contenido);
            CatalogoDeRutinas catalogo = new CatalogoDeRutinas(5);
            catalogo.cargarRutinasDeArchivo(archivo.toString(), 5, 5);

            assertEquals(3, catalogo.numRutinas());
            Rutina full = catalogo.buscarRutinaPorNombre("Full Body")[0];
            assertArrayEquals(new String[]{"Sentadillas", "Flexiones"}, full.getEjercicios());
            assertArrayEquals(new String[]{"Postura del niño", "Mariposa"}, full.getEstiramientos());
            Rutina yoga = catalogo.buscarRutinaPorNombre("Yoga")[0];
            assertArrayEquals(new String[]{"Saludo al sol"}, yoga.getEjercicios());
            assertArrayEquals(new String[]{"Savasana"}, yoga.getEstiramientos());
            Rutina solo = catalogo.buscarRutinaPorNombre("Solo")[0];
            assertEquals(1, solo.numEjercicios());
            assertEquals(0, solo.numEstiramientos());
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    @DisplayName("Cargar más rutinas de las que caben: solo se cargan las primeras y no se muestran errores")
    void cargarMasRutinasQueCapacidad() throws IOException {
        PrintStream original = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(2);
        try {
            System.setOut(new PrintStream(capturada));
            catalogo.cargarRutinasDeArchivo("src/test/resources/rutinas_predefinidas.txt", 6, 6);
        } finally {
            System.setOut(original);
        }

        assertEquals(2, catalogo.numRutinas());
        assertTrue(catalogo.catalogoCompleto());
        Rutina[] cargadas = catalogo.buscarRutinaPorNombre("");
        assertEquals("Full Body Principiante", cargadas[0].getNombre());
        assertEquals("Pierna y Glúteo", cargadas[1].getNombre());
        assertEquals("", capturada.toString(), "Al llenarse el catálogo se debe dejar de leer sin mostrar errores");
    }

    @Test
    @DisplayName("Cargar rutinas añade a las que ya había")
    void cargarAgregaALasExistentes() throws IOException {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(20);
        catalogo.agregarRutina(rutina("Previa"));
        catalogo.cargarRutinasDeArchivo("src/test/resources/rutinas_predefinidas.txt", 6, 6);
        assertEquals(9, catalogo.numRutinas());
        assertEquals("Previa", catalogo.buscarRutinaPorNombre("")[0].getNombre());
    }

    @Test
    @DisplayName("Cargar archivo inexistente lanza IOException")
    void cargarArchivoInexistente() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(3);
        assertThrows(IOException.class, () -> catalogo.cargarRutinasDeArchivo("no/existe/rutinas.txt", 3, 3));
    }

    @Test
    @DisplayName("Guardar rutinas en archivo")
    void guardarRutinasEnArchivo() throws IOException {
        Path archivo = Files.createTempFile("rutinas", ".txt");
        try {
            CatalogoDeRutinas catalogo = new CatalogoDeRutinas(5);
            Rutina r1 = rutina("Full Body");
            r1.agregarEjercicio("Sentadillas");
            r1.agregarEjercicio("Flexiones");
            r1.agregarEstiramiento("Postura del niño");
            Rutina r2 = rutina("Yoga");
            r2.agregarEjercicio("Saludo al sol");
            r2.agregarEstiramiento("Savasana");
            r2.agregarEstiramiento("Mariposa");
            catalogo.agregarRutina(r1);
            catalogo.agregarRutina(r2);

            catalogo.guardarRutinasEnArchivo(archivo.toString());

            String esperado = """
                    Full Body
                    Sentadillas
                    Flexiones
                    ESTIRAMIENTOS
                    Postura del niño
                    =====
                    Yoga
                    Saludo al sol
                    ESTIRAMIENTOS
                    Savasana
                    Mariposa
                    =====
                    """;
            assertEquals(esperado, Files.readString(archivo));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    @DisplayName("Guardar sobreescribe el contenido previo del archivo")
    void guardarSobreescribe() throws IOException {
        Path archivo = Files.createTempFile("rutinas", ".txt");
        try {
            Files.writeString(archivo, "contenido antiguo que debe desaparecer\n".repeat(20));
            CatalogoDeRutinas catalogo = new CatalogoDeRutinas(1);
            catalogo.agregarRutina(rutina("Yoga"));
            catalogo.guardarRutinasEnArchivo(archivo.toString());
            assertEquals("Yoga\nESTIRAMIENTOS\n=====\n", Files.readString(archivo));
        } finally {
            Files.deleteIfExists(archivo);
        }
    }

    @Test
    @DisplayName("Guardar en una ruta inválida lanza IOException")
    void guardarRutaInvalida() {
        CatalogoDeRutinas catalogo = new CatalogoDeRutinas(1);
        assertThrows(IOException.class, () -> catalogo.guardarRutinasEnArchivo("no/existe/carpeta/rutinas.txt"));
    }

    @Test
    @DisplayName("Lo que se guarda se puede volver a cargar (ida y vuelta)")
    void guardarYCargar() throws IOException {
        Path archivo = Files.createTempFile("rutinas", ".txt");
        try {
            CatalogoDeRutinas origen = new CatalogoDeRutinas(20);
            origen.cargarRutinasDeArchivo("src/test/resources/rutinas_predefinidas.txt", 6, 6);
            origen.guardarRutinasEnArchivo(archivo.toString());

            CatalogoDeRutinas destino = new CatalogoDeRutinas(20);
            destino.cargarRutinasDeArchivo(archivo.toString(), 6, 6);

            assertEquals(origen.numRutinas(), destino.numRutinas());
            Rutina[] a = origen.buscarRutinaPorNombre("");
            Rutina[] b = destino.buscarRutinaPorNombre("");
            for (int i = 0; i < a.length; i++) {
                assertEquals(a[i].toRawString(), b[i].toRawString());
            }
        } finally {
            Files.deleteIfExists(archivo);
        }
    }
}
