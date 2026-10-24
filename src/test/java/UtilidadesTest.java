import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests para la clase Utilidades")
public class UtilidadesTest {

    @ParameterizedTest
    @DisplayName("Leer cadena de texto")
    @ValueSource(strings = {"Hola Mundo", "1234", "Texto con espacios"})
    void leerCadenaDeTexto(String entrada) {
        Scanner teclado = new Scanner(entrada + "\n");
        assertEquals(entrada, Utilidades.leerCadena(teclado, "Introduce un texto: "));
    }

    @Test
    @DisplayName("Leer cadena muestra el mensaje sin salto de línea")
    void leerCadenaMuestraMensaje() {
        PrintStream original = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturada));
            Utilidades.leerCadena(new Scanner("x\n"), "Mensaje: ");
        } finally {
            System.setOut(original);
        }
        assertEquals("Mensaje: ", capturada.toString());
    }

    @ParameterizedTest
    @DisplayName("Leer número dentro del rango")
    @ValueSource(ints = {1, 2, 3, 4, 5, 10})
    void leerNumeroDentroDelRango(int entrada) {
        Scanner teclado = new Scanner(entrada + "\n");
        assertEquals(entrada, Utilidades.leerNumero(teclado, "Número entre 1 y 10: ", 1, 10));
    }

    @ParameterizedTest
    @DisplayName("Leer número fuera del rango: se repite la petición")
    @CsvSource({"0, 5", "11, 5", "0, 10", "11, 10", "-3, 1"})
    void leerNumeroFueraDelRango(int entradaMala, int entradaBuena) {
        Scanner teclado = new Scanner(entradaMala + "\n" + entradaMala + "\n" + entradaBuena + "\n");
        assertEquals(entradaBuena, Utilidades.leerNumero(teclado, "Número entre 1 y 10: ", 1, 10));
    }

    @ParameterizedTest
    @DisplayName("Leer número con entrada que no es un número: se repite la petición")
    @ValueSource(strings = {"abc", "", "3.5", "uno", "1 2"})
    void leerNumeroNoNumerico(String entradaMala) {
        Scanner teclado = new Scanner(entradaMala + "\n" + "7\n");
        assertEquals(7, Utilidades.leerNumero(teclado, "Número entre 1 y 10: ", 1, 10));
    }

    @Test
    @DisplayName("Leer número muestra el mensaje en cada intento")
    void leerNumeroRepiteMensaje() {
        PrintStream original = System.out;
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(capturada));
            Utilidades.leerNumero(new Scanner("x\n99\n2\n"), "N: ", 1, 3);
        } finally {
            System.setOut(original);
        }
        assertEquals("N: N: N: ", capturada.toString());
    }

    @ParameterizedTest
    @DisplayName("Leer día de la semana válido (mayúscula o minúscula)")
    @CsvSource({"L, 0", "M, 1", "X, 2", "J, 3", "V, 4", "S, 5", "D, 6",
            "l, 0", "m, 1", "x, 2", "j, 3", "v, 4", "s, 5", "d, 6"})
    void leerDiaDeLaSemanaValido(String entrada, int posicion) {
        Scanner teclado = new Scanner(entrada + "\n");
        assertEquals(posicion, Utilidades.leerDiaDeLaSemana(teclado, "Día: "));
    }

    @ParameterizedTest
    @DisplayName("Leer día de la semana inválido: se repite la petición")
    @CsvSource({"A, L, 0", "Z, M, 1", "LL, X, 2", "1, J, 3", "'', V, 4", "Lunes, S, 5", "Q, D, 6"})
    void leerDiaDeLaSemanaInvalido(String entradaMala, String entradaBuena, int posicion) {
        Scanner teclado = new Scanner(entradaMala + "\n" + entradaMala + "\n" + entradaBuena + "\n");
        assertEquals(posicion, Utilidades.leerDiaDeLaSemana(teclado, "Día: "));
    }

    @Test
    @DisplayName("Convertir día de la semana a posición")
    void convertirDiaSemanaAPosicion() {
        assertEquals(0, Utilidades.diaSemanaAPosicion("L"));
        assertEquals(1, Utilidades.diaSemanaAPosicion("M"));
        assertEquals(2, Utilidades.diaSemanaAPosicion("X"));
        assertEquals(3, Utilidades.diaSemanaAPosicion("J"));
        assertEquals(4, Utilidades.diaSemanaAPosicion("V"));
        assertEquals(5, Utilidades.diaSemanaAPosicion("S"));
        assertEquals(6, Utilidades.diaSemanaAPosicion("D"));
        assertEquals(6, Utilidades.diaSemanaAPosicion("d"));
    }

    @ParameterizedTest
    @DisplayName("Día de la semana no válido devuelve -1")
    @ValueSource(strings = {"A", "", "LM", "1", " ", "Lunes"})
    void diaSemanaAPosicionInvalido(String dia) {
        assertEquals(-1, Utilidades.diaSemanaAPosicion(dia));
    }

    @Test
    @DisplayName("Día de la semana nulo devuelve -1")
    void diaSemanaAPosicionNulo() {
        assertEquals(-1, Utilidades.diaSemanaAPosicion(null));
    }

    @Test
    @DisplayName("Convertir posición a día de la semana")
    void convertirPosicionADiaSemana() {
        assertEquals("Lunes", Utilidades.posicionADiaSemana(0));
        assertEquals("Martes", Utilidades.posicionADiaSemana(1));
        assertEquals("Miércoles", Utilidades.posicionADiaSemana(2));
        assertEquals("Jueves", Utilidades.posicionADiaSemana(3));
        assertEquals("Viernes", Utilidades.posicionADiaSemana(4));
        assertEquals("Sábado", Utilidades.posicionADiaSemana(5));
        assertEquals("Domingo", Utilidades.posicionADiaSemana(6));
    }

    @ParameterizedTest
    @DisplayName("Posición no válida devuelve Desconocido")
    @ValueSource(ints = {-1, 7, 42})
    void posicionADiaSemanaInvalida(int pos) {
        assertEquals("Desconocido", Utilidades.posicionADiaSemana(pos));
    }
}
