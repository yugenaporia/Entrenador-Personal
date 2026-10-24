import java.io.IOException;

public class PlanificadorSemanal {

    public PlanificadorSemanal() {
        // Inicialización del planificador semanal
    }

    public void agregarEntrenamiento(int dia, Rutina rutina) {
        // Planifica una rutina en un día de la semana (0 = lunes ... 6 = domingo)
    }

    @Override
    public String toString() {
        // Devuelve una representación en forma de tabla del planificador semanal
        return null; // @todo MODIFICAR PARA DEVOLVER LA CADENA CORRECTA
    }

    public void guardarPlanEnArchivo(String nombreArchivo) throws IOException {
        // Guarda el planificador semanal en un archivo de texto
    }
}
