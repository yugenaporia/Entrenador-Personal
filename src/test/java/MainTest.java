import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la clase Main")
public class MainTest {
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

    private String ejecutarMain(String entrada, String... args) {
        System.setIn(new ByteArrayInputStream(entrada.getBytes()));
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturada));
        Main.main(args);
        return capturada.toString().replace("\r\n", "\n");
    }

    @Test
    @DisplayName("Sin argumentos muestra el uso y no arranca la aplicación")
    void sinArgumentos() {
        String salida = ejecutarMain("");
        assertTrue(salida.contains("Uso:"));
        assertFalse(salida.contains("Menú Principal"));
    }

    @Test
    @DisplayName("Con demasiados argumentos muestra el uso")
    void demasiadosArgumentos() {
        String salida = ejecutarMain("", "3", "3", "3", "archivo.txt", "extra");
        assertTrue(salida.contains("Uso:"));
        assertFalse(salida.contains("Menú Principal"));
    }

    @Test
    @DisplayName("Con pocos argumentos muestra el uso")
    void pocosArgumentos() {
        String salida = ejecutarMain("", "3", "3");
        assertTrue(salida.contains("Uso:"));
        assertFalse(salida.contains("Menú Principal"));
    }

    @ParameterizedTest
    @DisplayName("Con argumentos numéricos inválidos muestra el uso")
    @ValueSource(strings = {"abc", "-1", "2.5", ""})
    void argumentoNoNumerico(String malo) {
        assertTrue(ejecutarMain("", malo, "3", "3").contains("Uso:"));
        assertTrue(ejecutarMain("", "3", malo, "3").contains("Uso:"));
        assertTrue(ejecutarMain("", "3", "3", malo).contains("Uso:"));
    }

    @Test
    @DisplayName("Con 3 argumentos válidos arranca la aplicación")
    void tresArgumentosValidos() {
        String salida = ejecutarMain("7\n", "3", "3", "3");
        assertFalse(salida.contains("Uso:"));
        assertTrue(salida.contains("--- Menú Principal ---"));
    }

    @Test
    @DisplayName("Con 4 argumentos válidos arranca la aplicación con las rutinas del archivo cargadas")
    void cuatroArgumentosValidos() {
        String salida = ejecutarMain("2\nyoga\n1\n4\n7\n", "6", "6", "20",
                "src/test/resources/rutinas_predefinidas.txt");
        assertTrue(salida.contains("--- Menú Principal ---"));
        assertTrue(salida.contains("Rutina: Yoga Suave"));
    }
}
