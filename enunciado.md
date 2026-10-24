# Práctica: Entrenador Personal

Práctica de programación en Java (Maven + JUnit 5). Se plantea con la misma estructura y dificultad que la práctica
del recetario, pero con otra temática: un **gestor de rutinas de entrenamiento** con **planificador semanal**.

## 1. Objetivo

Desarrollar una aplicación de consola que permita a un entrenador personal:

- Crear **rutinas** de entrenamiento (un nombre, unos ejercicios y unos estiramientos).
- Guardarlas en un **catálogo** de capacidad limitada, buscarlas por nombre, editarlas y eliminarlas.
- **Planificar** qué rutina se hace cada día de la semana y ver el plan en forma de tabla.
- **Guardar y cargar** las rutinas y **guardar el plan** semanal en ficheros de texto.

Con esta práctica se trabajan: clases y objetos, **arrays** (con tamaño fijo y control manual del número de
elementos), cadenas, entrada/salida por consola con `Scanner`, ficheros de texto, excepciones (`IOException`) y
pruebas unitarias.

## 2. Restricciones

- Lenguaje: Java 22 (o superior). Proyecto Maven (`pom.xml` incluido).
- **Solo arrays**: no se pueden usar colecciones (`ArrayList`, `List`, `Map`...) ni `Arrays.asList`. Sí se permiten
  `String`, `StringBuilder`, `Scanner`, `Math`, `Arrays.copyOf` y las clases de `java.io` / `java.nio.file` para
  ficheros.
- **No se pueden modificar los tests** ni la firma (nombre, parámetros, tipo devuelto) de los métodos del esqueleto.
  Sí puedes añadir atributos, constantes y métodos auxiliares (privados).
- No se puede usar `System.exit`.
- Todos los ficheros se leen y escriben en **UTF-8**.
- Todas las lecturas por teclado se hacen **línea a línea** (`nextLine()`).
- Cuida la calidad del código: nombres claros, métodos cortos con una única responsabilidad, sin código duplicado,
  sin números mágicos, un único `return` por método, y javadoc en los métodos públicos.

## 3. Estructura del proyecto

```
src/main/java/
  Rutina.java              Una rutina: nombre + ejercicios + estiramientos
  CatalogoDeRutinas.java   Colección de rutinas de capacidad fija
  PlanificadorSemanal.java Una rutina (o descanso) por cada día de la semana
  Utilidades.java          Lectura por teclado y días de la semana
  InterfazUsuario.java     Menús de consola
  Main.java                Punto de entrada
src/test/java/             Tests (JUnit 5)
src/test/resources/rutinas_predefinidas.txt   Fichero de ejemplo con 8 rutinas
```

Completa los métodos marcados con `@todo` y los comentarios de cada método del esqueleto. Puedes ir de abajo arriba:
primero `Utilidades` y `Rutina`, luego `CatalogoDeRutinas` y `PlanificadorSemanal`, y por último
`InterfazUsuario` y `Main`.

## 4. Clase `Utilidades`

Métodos estáticos:

| Método | Descripción |
|---|---|
| `String leerCadena(Scanner teclado, String s)` | Muestra `s` **sin salto de línea** y devuelve la línea leída. |
| `int leerNumero(Scanner teclado, String mensaje, int minimo, int maximo)` | Muestra `mensaje` y lee un entero. Mientras la entrada **no sea un entero** o esté **fuera del rango** `[minimo, maximo]`, vuelve a mostrar el mensaje y a leer. |
| `int leerDiaDeLaSemana(Scanner teclado, String mensaje)` | Como el anterior pero lee la letra de un día. Repite mientras no sea válida. Devuelve la posición (0-6). |
| `int diaSemanaAPosicion(String dia)` | `L`→0, `M`→1, `X`→2, `J`→3, `V`→4, `S`→5, `D`→6. Acepta mayúsculas y minúsculas. Devuelve **-1** si no es un día válido (incluido `null`, cadena vacía o de más de un carácter). |
| `String posicionADiaSemana(int pos)` | 0→`Lunes`, 1→`Martes`, 2→`Miércoles`, 3→`Jueves`, 4→`Viernes`, 5→`Sábado`, 6→`Domingo`. Cualquier otra posición devuelve `Desconocido`. |

## 5. Clase `Rutina`

Una rutina tiene un **nombre**, una lista de **ejercicios** y una lista de **estiramientos**. Cada lista tiene un
máximo de elementos fijado en el constructor: `Rutina(String nombre, int maxEjercicios, int maxEstiramientos)`.

- `getNombre()`, `getMaxEjercicios()`, `getMaxEstiramientos()`, `numEjercicios()`, `numEstiramientos()`.
- `getEjercicios()` y `getEstiramientos()` devuelven un **array nuevo (copia)** cuya longitud es igual al número
  de elementos actuales (ni más, ni con `null` al final). Modificar ese array no debe alterar la rutina.
- `agregarEjercicio(String)` / `agregarEstiramiento(String)` añaden al final y devuelven `true`. Si la lista ya está
  completa **no se añade**, se devuelve `false` y se imprime (con salto de línea):
  - `No se pueden añadir más ejercicios.`
  - `No se pueden añadir más estiramientos.`
- `ejerciciosCompletos()` / `estiramientosCompletos()`: `true` si se ha alcanzado el máximo (una rutina con
  máximo 0 está completa desde el principio).
- `toString()` (para pantalla). Los ejercicios con guion y los estiramientos numerados desde 1. Cada línea
  termina en `\n`, incluida la última:

```
Rutina: Full Body
Ejercicios:
- Sentadillas 3x12
- Flexiones 3x8
Estiramientos:
1. Postura del niño
2. Mariposa
```

- `toRawString()` (para fichero). Nombre, un ejercicio por línea, la línea `ESTIRAMIENTOS`, un estiramiento por
  línea y la línea de cierre `=====`. Cada línea termina en `\n`:

```
Full Body
Sentadillas 3x12
Flexiones 3x8
ESTIRAMIENTOS
Postura del niño
Mariposa
=====
```

## 6. Clase `CatalogoDeRutinas`

Almacena hasta `maxRutinasEnCatalogo` rutinas (capacidad fija), en orden de inserción.

- `agregarRutina(Rutina)`: la añade al final y devuelve `true`. Si el catálogo está completo devuelve `false` y
  muestra `No se pueden añadir más rutinas.`. Si la rutina es `null` devuelve `false` sin mostrar nada.
- `buscarRutinaPorNombre(String texto)`: devuelve un array con las rutinas cuyo nombre **contiene** el texto, **sin
  distinguir mayúsculas de minúsculas**, en orden de inserción. El array tiene **exactamente** tantas posiciones como
  rutinas encontradas (si no hay ninguna, longitud 0, nunca `null`). Con texto vacío devuelve todas.
- `eliminarRutina(Rutina)`: elimina la rutina que sea **el mismo objeto** (no basta con que tenga el mismo nombre),
  manteniendo el orden del resto. Si no está, no hace nada.
- `numRutinas()` y `catalogoCompleto()`.
- `guardarRutinasEnArchivo(String nombreArchivo)`: escribe todas las rutinas, una tras otra, con su `toRawString()`.
  Si el fichero existe, se sobreescribe. Lanza `IOException` si no puede escribir.
- `cargarRutinasDeArchivo(String nombreArchivo, int maxEjercicios, int maxEstiramientos)`: lee un fichero con el
  formato anterior y **añade** las rutinas al catálogo (creándolas con esos máximos). Detalles:
  - Las líneas en blanco entre rutinas se ignoran.
  - Una rutina puede no tener ejercicios o no tener estiramientos.
  - **Deja de leer en cuanto el catálogo se llena**, sin mostrar mensajes de error por las rutinas sobrantes.
  - Si una rutina del fichero tiene más ejercicios/estiramientos que el máximo, se usan `agregarEjercicio` /
    `agregarEstiramiento`, por lo que se mostrarán sus mensajes de error y se descartarán los sobrantes.
  - Lanza `IOException` si el fichero no existe o no se puede leer.

## 7. Clase `PlanificadorSemanal`

Guarda, como mucho, **una rutina por día** (0 = lunes ... 6 = domingo). Un día sin rutina es un día de descanso.

- `agregarEntrenamiento(int dia, Rutina rutina)`: planifica la rutina en ese día, **sustituyendo** la anterior si la
  hubiera. Si `rutina` es `null` el día queda libre. Si `dia` no está entre 0 y 6 no hace nada ni lanza excepción.
- `toString()`: tabla con **una columna por día**.
  - **Ancho de columna** = longitud del texto más largo entre las cabeceras (`Lunes`...`Domingo`) y los nombres de
    las rutinas planificadas, **más 1**. (Sin rutinas, el texto más largo es `Miércoles`, así que el ancho es 10.)
  - Cada celda es **un espacio + el texto alineado a la izquierda y rellenado con espacios hasta el ancho**.
  - Las líneas separadoras son guiones y miden `7 * (ancho + 1)` caracteres.
  - Estructura: separador, cabecera, separador, fila de rutinas (vacía si no hay), separador y una **línea en
    blanco** final (la cadena termina en `\n\n`). Todas las líneas terminan en `\n`.

Ejemplo con `Pierna` el lunes, `Yoga Suave` el jueves y `HIIT` el domingo (el nombre más largo mide 10 → ancho 11).
Ojo: las líneas de texto acaban con espacios de relleno:

```
------------------------------------------------------------------------------------
 Lunes       Martes      Miércoles   Jueves      Viernes     Sábado      Domingo   
------------------------------------------------------------------------------------
 Pierna                              Yoga Suave                          HIIT      
------------------------------------------------------------------------------------

```

- `guardarPlanEnArchivo(String nombreArchivo)`: una línea por día con el formato `Día: nombre de la rutina`, o
  `Día: Descanso` si no hay rutina. Si el fichero existe, se sobreescribe. Lanza `IOException` si no puede escribir.

```
Lunes: Pierna
Martes: Descanso
Miércoles: Descanso
Jueves: Yoga Suave
Viernes: Descanso
Sábado: Descanso
Domingo: HIIT
```

## 8. Clase `InterfazUsuario`

`InterfazUsuario(int maxEjercicios, int maxEstiramientos, int maxRutinasEnCatalogo)` crea el catálogo y el planificador.
`InterfazUsuario(..., String archivoRutinas)` además **carga las rutinas de ese fichero** al arrancar (mostrando el
mismo mensaje de éxito o de error que la opción 5 del menú; si falla, la aplicación arranca con el catálogo vacío).

`iniciar()` muestra el menú principal en bucle hasta que el usuario elige **7. Salir**. Los textos deben ser
**exactamente** los siguientes (los `▮` indican que el mensaje **no** lleva salto de línea: el cursor se queda a
continuación).

### Menú principal

```
--- Menú Principal ---
1. Agregar Rutina
2. Consultar/Editar Rutina
3. Planificar Entrenamientos
4. Guardar Rutinas
5. Cargar Rutinas
6. Guardar Plan Semanal
7. Salir

Elige una opción: ▮
```

Si la opción no es un número entre 1 y 7, solo se repite `Elige una opción: ` (el menú no se vuelve a dibujar hasta
que se complete una acción).

### 1. Agregar rutina

```
Nombre de la rutina: ▮
Introduce los ejercicios (una línea por ejercicio, escribe 'fin' para terminar):
Introduce los estiramientos (una línea por estiramiento, escribe 'fin' para terminar):
¡Rutina agregada exitosamente!            (o bien:)  No se pudo añadir la rutina.
```

- `fin` se reconoce sin distinguir mayúsculas de minúsculas (`fin`, `FIN`, `Fin`...).
- **Se siguen leyendo líneas hasta `fin` aunque la lista esté completa**; cada línea sobrante provoca el mensaje de
  error de `Rutina` y se descarta. La rutina se crea con lo que cupo.
- Si el catálogo está completo, tras leer todo se muestra `No se pudo añadir la rutina.` (además del mensaje de error
  del catálogo).

### 2. Consultar/Editar rutina

1. Se pide `Introduce el texto de la rutina a buscar (-FIN- para volver): ▮`.
   - `-FIN-` (sin distinguir mayúsculas) vuelve al menú principal sin hacer nada más.
   - Si no hay coincidencias se muestra `No se encontraron rutinas.` y se vuelve a pedir el texto.
   - Un texto vacío las encuentra todas.
2. Si hay coincidencias se muestra la lista y se elige una (se repite hasta que sea válida):

```
Rutinas encontradas:
1. Pierna A
2. Pierna B
Elige una rutina: ▮
```

3. Se muestra la rutina seleccionada (`println` de su `toString()`, así queda una línea en blanco tras ella) y el menú
   de edición:

```
1. Añadir ejercicio
2. Añadir estiramiento
3. Eliminar rutina
4. Volver
Elige una opción: ▮
```

   - `1` → `Introduce el ejercicio a añadir: ▮` y se añade a la rutina.
   - `2` → `Introduce el estiramiento a añadir: ▮` y se añade a la rutina.
   - `3` → se elimina del catálogo y se muestra `Rutina eliminada.`
   - `4` → no hace nada.
   - Tras **una** acción se vuelve al menú principal.

### 3. Planificar entrenamientos

```
Planificación de entrenamientos para la semana:
<tabla del planificador: PlanificadorSemanal.toString()>
Introduce el día de la semana (L, M, X, J, V, S, D): ▮
```

Después se busca y se elige la rutina igual que en la opción 2 (mismos mensajes). Si el usuario cancela con
`-FIN-` no se planifica nada. Si elige una, se planifica y se muestra `Rutina planificada para Lunes` (con el
nombre completo del día).

### 4. Guardar rutinas

`Introduce el nombre del archivo donde guardar las rutinas: ▮` → `Rutinas guardadas en <archivo>`, o
`Error al guardar el archivo.` si hay una `IOException`.

### 5. Cargar rutinas

`Introduce la ruta del archivo de donde cargar las rutinas: ▮` → `Rutinas cargadas desde <archivo>`, o
`Error al cargar el archivo.` si hay una `IOException`.

### 6. Guardar plan semanal

`Introduce el nombre del archivo donde guardar el plan semanal: ▮` → `Plan semanal guardado en <archivo>`, o
`Error al guardar el archivo.` si hay una `IOException`.

## 9. Clase `Main`

Recibe **3 o 4 argumentos**:

```
java Main <maxEjerciciosPorRutina> <maxEstiramientosPorRutina> <maxRutinasEnCatalogo> [archivoRutinas]
```

- Los tres primeros deben ser **enteros no negativos**. Si el número de argumentos no es 3 ó 4 o alguno de los tres
  primeros no es válido, se muestra una línea que empieza por `Uso:` con el formato anterior y el programa termina
  (sin `System.exit`, simplemente acaba `main`).
- Con 3 argumentos se lanza `new InterfazUsuario(a, b, c).iniciar()`. Con 4, se usa el constructor que carga el fichero.
- Cualquier excepción inesperada se captura y se muestra `Error inesperado: <mensaje>`.

## 10. Cómo comprobar tu trabajo

```
mvn test
```

o ejecuta las clases de `src/test/java` desde IntelliJ. Hay tests de cada clase (`RutinaTest`, `CatalogoDeRutinasTest`,
`PlanificadorSemanalTest`, `UtilidadesTest`) y de integración (`InterfazUsuarioTest`, `MainTest`) que simulan la
entrada del usuario y comprueban la salida. Para probar a mano:

```
mvn -q compile
java -cp target/classes Main 5 5 10 src/test/resources/rutinas_predefinidas.txt
```

Antes de empezar, todos los tests fallan (salvo unos pocos que comprueban negativos). El objetivo es que pasen todos
y que además el código sea limpio.

## 11. Solución de referencia

En la carpeta `solucion/` hay una implementación completa que pasa los 157 tests. No la mires hasta haber hecho
tu propio intento.
