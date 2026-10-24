public class Rutina {

    public Rutina(String nombre, int maxEjercicios, int maxEstiramientos) {
        // Inicialización de la rutina
    }

    public String getNombre() {
        // Devuelve el nombre de la rutina
        return null; // @todo MODIFICAR PARA DEVOLVER EL NOMBRE DE LA RUTINA
    }

    public String[] getEjercicios() {
        // Devuelve los ejercicios de la rutina (array de longitud igual al número de ejercicios actuales)
        return null; // @todo MODIFICAR PARA DEVOLVER LOS EJERCICIOS DE LA RUTINA
    }

    public String[] getEstiramientos() {
        // Devuelve los estiramientos de la rutina (array de longitud igual al número de estiramientos actuales)
        return null; // @todo MODIFICAR PARA DEVOLVER LOS ESTIRAMIENTOS DE LA RUTINA
    }

    public boolean agregarEjercicio(String ejercicio) {
        // Añade un ejercicio a la rutina (si está completa, muestra un mensaje de error)
        return false; // @todo MODIFICAR PARA DEVOLVER SI SE HA AÑADIDO EL EJERCICIO
    }

    public boolean agregarEstiramiento(String estiramiento) {
        // Añade un estiramiento a la rutina (si está completa, muestra un mensaje de error)
        return false; // @todo MODIFICAR PARA DEVOLVER SI SE HA AÑADIDO EL ESTIRAMIENTO
    }

    public boolean ejerciciosCompletos() {
        // Comprueba si la rutina tiene el máximo de ejercicios
        return false; // @todo MODIFICAR PARA DEVOLVER SI ESTÁN COMPLETOS LOS EJERCICIOS
    }

    public boolean estiramientosCompletos() {
        // Comprueba si la rutina tiene el máximo de estiramientos
        return false; // @todo MODIFICAR PARA DEVOLVER SI ESTÁN COMPLETOS LOS ESTIRAMIENTOS
    }

    public int numEjercicios() {
        // Devuelve el número de ejercicios de la rutina
        return 0; // @todo MODIFICAR PARA DEVOLVER EL NÚMERO DE EJERCICIOS
    }

    public int numEstiramientos() {
        // Devuelve el número de estiramientos de la rutina
        return 0; // @todo MODIFICAR PARA DEVOLVER EL NÚMERO DE ESTIRAMIENTOS
    }

    @Override
    public String toString() {
        // Devuelve una representación en forma de cadena de la rutina, para mostrar por pantalla
        return null; // @todo MODIFICAR PARA DEVOLVER LA CADENA CORRECTA
    }

    public String toRawString() {
        // Devuelve una representación en forma de cadena de la rutina sin formato, para guardar en archivo
        return null; // @todo MODIFICAR PARA DEVOLVER LA CADENA CORRECTA
    }

    public int getMaxEjercicios() {
        // Devuelve el máximo de ejercicios permitidos en la rutina
        return 0; // @todo MODIFICAR PARA DEVOLVER EL MÁXIMO DE EJERCICIOS
    }

    public int getMaxEstiramientos() {
        // Devuelve el máximo de estiramientos permitidos en la rutina
        return 0; // @todo MODIFICAR PARA DEVOLVER EL MÁXIMO DE ESTIRAMIENTOS
    }
}
