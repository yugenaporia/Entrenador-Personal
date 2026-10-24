import java.util.Scanner;

public class InterfazUsuario {
    private CatalogoDeRutinas catalogo;
    private PlanificadorSemanal planificador;
    private int maxEjercicios;
    private int maxEstiramientos;

    public InterfazUsuario(int maxEjercicios, int maxEstiramientos, int maxRutinasEnCatalogo) {
        // Inicialización de la herramienta de entrenamiento
    }

    public InterfazUsuario(int maxEjercicios, int maxEstiramientos, int maxRutinasEnCatalogo, String archivoRutinas) {
        this(maxEjercicios, maxEstiramientos, maxRutinasEnCatalogo);

        // Cargar las rutinas predefinidas al iniciar la aplicación
    }

    public void iniciar() {
        Scanner scanner = new Scanner(System.in);
        menuPrincipal(scanner);
        scanner.close();
    }

    private void menuPrincipal(Scanner scanner) {
        // Muestra el menú principal y gestiona la entrada del usuario para dirigirlo a la opción seleccionada
    }

    private void agregarRutina(Scanner scanner) {
        // Solicita al usuario los datos de la rutina y la añade al catálogo
    }

    private void consultarRutina(Scanner scanner) {
        // Busca una rutina por su nombre y activa el menú de edición
    }

    private Rutina buscarRutinaPorNombre(Scanner scanner) {
        // Solicita al usuario un texto para buscar y seleccionar una rutina por su nombre
        return null; // @todo MODIFICAR PARA DEVOLVER LA RUTINA SELECCIONADA (null si el usuario cancela)
    }

    private void editarRutina(Scanner scanner, Rutina seleccionada) {
        // Pantalla de edición de rutina
    }

    private Rutina seleccionarRutina(Scanner scanner, Rutina[] rutinas) {
        // Muestra las rutinas encontradas y solicita al usuario que elija una
        return null; // @todo MODIFICAR PARA DEVOLVER LA RUTINA SELECCIONADA
    }

    private void planificarEntrenamientos(Scanner scanner) {
        // Inicia el proceso de planificación de entrenamientos
    }

    private void guardarRutinas(Scanner scanner) {
        // Solicita al usuario un nombre de archivo y guarda las rutinas en ese archivo
    }

    private void cargarRutinas(Scanner scanner) {
        // Solicita al usuario un nombre de archivo y carga las rutinas desde ese archivo
    }

    private void guardarPlanSemanal(Scanner scanner) {
        // Solicita al usuario un nombre de archivo y guarda el plan semanal en ese archivo
    }
}
