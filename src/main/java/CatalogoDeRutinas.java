import java.io.IOException;

public class CatalogoDeRutinas {

    public CatalogoDeRutinas(int maxRutinasEnCatalogo) {
        // Inicialización del catálogo de rutinas
    }

    public boolean agregarRutina(Rutina rutina) {
        // Añade una rutina al catálogo (si está completo, muestra un mensaje de error)
        return false; // @todo MODIFICAR PARA DEVOLVER SI SE HA AÑADIDO LA RUTINA
    }

    public Rutina[] buscarRutinaPorNombre(String texto) {
        // Busca rutinas cuyo nombre contenga el texto (sin distinguir mayúsculas) y devuelve todas las encontradas
        return null; // @todo MODIFICAR PARA DEVOLVER LAS RUTINAS ENCONTRADAS
    }

    public void guardarRutinasEnArchivo(String nombreArchivo) throws IOException {
        // Guarda las rutinas en un archivo de texto
    }

    public void cargarRutinasDeArchivo(String nombreArchivo, int maxEjercicios, int maxEstiramientos) throws IOException {
        // Carga las rutinas desde un archivo de texto y las añade al catálogo
    }

    public boolean catalogoCompleto() {
        // Comprueba si el catálogo de rutinas está completo
        return false; // @todo MODIFICAR PARA DEVOLVER SI ESTÁ COMPLETO
    }

    public int numRutinas() {
        // Devuelve el número de rutinas en el catálogo
        return 0; // @todo MODIFICAR PARA DEVOLVER EL NÚMERO DE RUTINAS
    }

    public void eliminarRutina(Rutina seleccionada) {
        // Elimina una rutina del catálogo
    }
}
