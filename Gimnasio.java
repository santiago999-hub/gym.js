// ============================================================
// CLASE: Gimnasio
// ARCHIVO: Gimnasio.java
// ============================================================
// ¿Por qué existe esta clase?
//   Es el "cerebro" del sistema. Contiene la lista de socios y
//   todos los métodos que representan las operaciones del negocio:
//   registrar, buscar, modificar, eliminar, etc.
//   Se llama también "capa de servicio" o "lógica de negocio".
//
// Esta clase NO sabe nada del menú ni del teclado.
// Solo recibe datos, opera con ellos y devuelve resultados.
// Eso la hace reutilizable y fácil de testear.
// ============================================================

import java.util.ArrayList;  // ArrayList: lista dinámica (puede crecer o achicarse)
import java.util.Comparator; // Comparator (comparador): sirve para ordenar listas

public class Gimnasio {

    // ----------------------------------------------------------
    // ATRIBUTOS
    // ----------------------------------------------------------

    // ArrayList<Socio> (Lista de Socios)
    // Es como un vector en PSeInt, pero sin tamaño fijo.
    // El <Socio> entre ángulos indica que SOLO puede guardar objetos Socio.
    // Eso se llama "genérico" (generic) o "tipo parametrizado".
    private ArrayList<Socio> socios;

    // Contador que asigna IDs únicos automáticamente (como autoincrement en bases de datos)
    private int contadorId;

    // ----------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------

    public Gimnasio() {
        socios     = new ArrayList<>(); // new ArrayList<>(): crea la lista vacía en memoria
        contadorId = 1;                 // Los IDs comienzan desde 1
    }

    // ==========================================================
    // FUNCIONALIDAD 1: ALTA DE SOCIO
    // ==========================================================
    // Crea un nuevo Socio y lo agrega a la lista.

    public void agregarSocio(String nombre, int edad, String plan) {
        // Primero validamos que el plan sea uno de los permitidos
        if (!esPlanValido(plan)) {
            System.out.println("  [X] Plan invalido. Use: BASICO, INTERMEDIO o PREMIUM");
            return; // return sin valor: sale del método inmediatamente (como Retornar en PSeInt)
        }

        // "new Socio(...)" invoca el constructor de Socio y crea el objeto en memoria
        Socio nuevoSocio = new Socio(contadorId, nombre, edad, plan);

        // .add() (agregar): agrega el objeto al final de la lista
        socios.add(nuevoSocio);

        contadorId++; // Incrementa para que el próximo socio tenga un ID diferente

        System.out.println("  [OK] Socio registrado con ID: " + nuevoSocio.getId());
    }

    // ==========================================================
    // FUNCIONALIDAD 2: BUSCAR SOCIO POR ID
    // ==========================================================

    // Método PRIVADO (private) de búsqueda interna.
    // Devuelve el objeto Socio si lo encuentra, o null si no existe.
    // "null" (nulo) en Java significa "ningún objeto / vacío".
    // Otros métodos de esta clase lo usan como herramienta interna.
    private Socio buscarPorId(int id) {
        // for-each (para cada): recorre todos los elementos de la lista
        // Equivalente en PSeInt: Para Cada socio En socios Hacer
        for (Socio socio : socios) {
            if (socio.getId() == id) {
                return socio; // Encontrado: devuelve el objeto
            }
        }
        return null; // Recorrió toda la lista y no encontró ninguno
    }

    // Método PÚBLICO que muestra el resultado en consola
    public void buscarSocioPorId(int id) {
        Socio socio = buscarPorId(id);

        if (socio == null) { // Si es null, no existe
            System.out.println("  [X] No se encontro ningun socio con ID: " + id);
        } else {
            System.out.println(socio); // Llama automáticamente al toString() de Socio
        }
    }

    // ==========================================================
    // FUNCIONALIDAD 3: MODIFICAR SOCIO
    // ==========================================================
    // Tres métodos separados, uno por campo. Esto respeta el principio
    // de que cada método tiene una sola responsabilidad clara.

    public void modificarNombre(int id, String nuevoNombre) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        String anterior = socio.getNombre();
        socio.setNombre(nuevoNombre);
        System.out.println("  [OK] Nombre actualizado: " + anterior + " -> " + nuevoNombre);
    }

    public void modificarEdad(int id, int nuevaEdad) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        if (nuevaEdad <= 0 || nuevaEdad >= 120) {
            System.out.println("  [X] Edad invalida. Ingrese un valor entre 1 y 119.");
            return;
        }
        socio.setEdad(nuevaEdad);
        System.out.println("  [OK] Edad actualizada a: " + nuevaEdad + " anos.");
    }

    public void modificarPlan(int id, String nuevoPlan) {
        if (!esPlanValido(nuevoPlan)) {
            System.out.println("  [X] Plan invalido. Use: BASICO, INTERMEDIO o PREMIUM");
            return;
        }
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        String anterior = socio.getPlan();
        socio.setPlan(nuevoPlan); // setPlan también actualiza la rutina automáticamente
        System.out.println("  [OK] Plan actualizado: " + anterior + " -> " + nuevoPlan.toUpperCase());
        System.out.println("  [OK] Rutina actualizada automaticamente.");
    }

    // ==========================================================
    // FUNCIONALIDAD 4: ELIMINAR SOCIO
    // ==========================================================

    public void eliminarSocio(int id) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        // .remove(objeto): elimina ese objeto específico de la lista
        // La lista se reorganiza sola (no quedan huecos)
        socios.remove(socio);
        System.out.println("  [OK] Socio '" + socio.getNombre() + "' eliminado correctamente.");
    }

    // ==========================================================
    // FUNCIONALIDAD 5: REGISTRAR ASISTENCIA
    // ==========================================================

    public void registrarAsistencia(int id) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        socio.incrementarAsistencia();
        System.out.println("  [OK] Asistencia registrada para '" + socio.getNombre()
                         + "'. Total de visitas: " + socio.getAsistencia());
    }

    // ==========================================================
    // FUNCIONALIDAD 6: CONTROLAR CUOTA
    // ==========================================================

    public void verificarCuota(int id) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        if (socio.isCuotaAlDia()) {
            System.out.println("  [OK] " + socio.getNombre() + " tiene la cuota AL DIA.");
        } else {
            System.out.println("  [!!] " + socio.getNombre() + " tiene la cuota PENDIENTE.");
        }
    }

    public void actualizarCuota(int id, boolean estado) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        socio.setCuotaAlDia(estado);
        // Operador ternario: condicion ? "valorSiTrue" : "valorSiFalse"
        String texto = estado ? "AL DIA" : "PENDIENTE";
        System.out.println("  [OK] Cuota de '" + socio.getNombre() + "' marcada como: " + texto);
    }

    // ==========================================================
    // FUNCIONALIDAD 7: MOSTRAR TODOS LOS SOCIOS
    // ==========================================================

    public void mostrarTodosLosSocios() {
        // .isEmpty() (esta vacia?): devuelve true si la lista no tiene elementos
        if (socios.isEmpty()) {
            System.out.println("  No hay socios registrados en el sistema.");
            return;
        }

        System.out.println("\n  ======== LISTADO DE SOCIOS ========");

        // printf (imprimir con formato): permite alinear texto en columnas
        // %-5s  -> texto alineado a la IZQUIERDA en 5 caracteres
        // %-20s -> texto alineado a la IZQUIERDA en 20 caracteres
        // %n    -> salto de línea (equivalente a \n pero multiplataforma)
        System.out.printf("  %-5s %-20s %-5s %-12s %-8s %-12s%n",
                          "ID", "NOMBRE", "EDAD", "PLAN", "ASIST.", "CUOTA");
        System.out.println("  " + "-".repeat(66));

        // for-each: recorre cada socio de la lista
        for (Socio socio : socios) {
            System.out.printf("  %-5d %-20s %-5d %-12s %-8d %-12s%n",
                    socio.getId(),
                    socio.getNombre(),
                    socio.getEdad(),
                    socio.getPlan(),
                    socio.getAsistencia(),
                    socio.isCuotaAlDia() ? "Al dia [OK]" : "PENDIENTE");
        }

        System.out.println("  " + "-".repeat(66));
        // .size() (tamanio/cantidad): devuelve cuántos elementos tiene la lista
        System.out.println("  Total: " + socios.size() + " socio(s)\n");
    }

    // ==========================================================
    // FUNCIONALIDAD 8: MOSTRAR RUTINA ASIGNADA
    // ==========================================================

    public void mostrarRutina(int id) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        System.out.println("\n  ======== RUTINA DE " + socio.getNombre().toUpperCase() + " ========");
        System.out.println("  Plan   : " + socio.getPlan());
        System.out.println("  Rutina : " + socio.getRutina());
        System.out.println();
    }

    // ==========================================================
    // FUNCIONALIDAD 9: CANTIDAD TOTAL DE SOCIOS
    // ==========================================================

    public int cantidadTotalSocios() {
        return socios.size();
    }

    // ==========================================================
    // FUNCIONALIDAD 10: RANKING POR ASISTENCIA
    // ==========================================================

    public void mostrarRankingPorAsistencia() {
        if (socios.isEmpty()) {
            System.out.println("  No hay socios para mostrar en el ranking.");
            return;
        }

        // Creamos una COPIA de la lista para no alterar el orden original
        // new ArrayList<>(socios) → copia todos los elementos en una nueva lista
        ArrayList<Socio> ranking = new ArrayList<>(socios);

        // .sort() (ordenar): ordena la lista según un criterio que le indicamos
        // Comparator.comparingInt() (comparador por entero): compara por un campo numérico
        // Socio::getAsistencia → "referencia a método" (method reference), equivale a s -> s.getAsistencia()
        // .reversed() (invertido): ordena de MAYOR a MENOR
        ranking.sort(Comparator.comparingInt(Socio::getAsistencia).reversed());

        System.out.println("\n  ======== RANKING POR ASISTENCIA ========");
        System.out.printf("  %-5s %-20s %-12s %-10s%n", "POS.", "NOMBRE", "PLAN", "VISITAS");
        System.out.println("  " + "-".repeat(52));

        // for clásico con índice: usamos "i" para mostrar la posición en el ranking
        // int i = 0 → variable inicial
        // i < ranking.size() → condición de continuación
        // i++ → incremento al final de cada repetición
        for (int i = 0; i < ranking.size(); i++) {
            // .get(i) (obtener en posición i): accede al elemento por índice (0, 1, 2...)
            Socio socio = ranking.get(i);
            System.out.printf("  %-5d %-20s %-12s %-10d%n",
                    (i + 1),              // Posición: empieza en 1 (no en 0)
                    socio.getNombre(),
                    socio.getPlan(),
                    socio.getAsistencia());
        }
        System.out.println("  " + "-".repeat(52) + "\n");
    }

    // ----------------------------------------------------------
    // MÉTODO PRIVADO AUXILIAR: esPlanValido
    // ----------------------------------------------------------
    // Verifica si el plan ingresado es uno de los tres permitidos.
    // IMPORTANTE: En Java, los String (cadenas de texto) NUNCA se comparan
    // con == (eso compara la dirección en memoria, no el contenido).
    // Siempre se usa .equals() (igual a) para comparar texto.

    private boolean esPlanValido(String plan) {
        String p = plan.toUpperCase();
        return p.equals("BASICO") || p.equals("INTERMEDIO") || p.equals("PREMIUM");
    }
}
