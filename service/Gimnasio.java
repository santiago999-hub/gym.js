// ============================================================
// PAQUETE: service (servicio)
// Contiene la lógica del negocio: operaciones sobre los datos.
// Gimnasio sabe QUÉ hacer con los socios, pero no cómo mostrarlos.
// ============================================================

package service;

// "import" (importar): trae una clase de otro paquete para poder usarla aquí.
// Como hacer "usar" en PSeInt para incluir una librería.
// Sin este import, Java no sabría qué es "Socio".
import model.Socio;
import model.Plan;           // Plan: el enum que reemplaza al String "BASICO"/"INTERMEDIO"/"PREMIUM"
import persistence.GestorCSV;

import java.util.ArrayList;  // ArrayList: lista dinámica (puede crecer o achicarse)
import java.util.Comparator; // Comparator (comparador): sirve para ordenar listas

// ============================================================
// CLASE: Gimnasio
// ============================================================
// ¿Por qué existe esta clase?
//   Es el "cerebro" del sistema. Contiene la lista de socios y
//   todos los métodos que representan las operaciones del negocio.
//   NO sabe nada del menú ni del teclado.
//   Solo recibe datos, opera con ellos y devuelve resultados.
// ============================================================

public class Gimnasio {

    // ----------------------------------------------------------
    // ATRIBUTOS
    // ----------------------------------------------------------

    // ArrayList<Socio> (Lista de Socios): lista dinámica sin tamaño fijo.
    // El <Socio> entre ángulos indica que SOLO puede guardar objetos Socio.
    // Eso se llama "genérico" (generic) o "tipo parametrizado".
    private ArrayList<Socio> socios;

    // Contador que asigna IDs únicos automáticamente
    private int contadorId;

    // ----------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------

    public Gimnasio() {
        // Al iniciar, intentamos cargar los datos del archivo CSV.
        // Si el archivo no existe (primera vez), devuelve una lista vacía.
        socios = GestorCSV.cargar();

        // El ID del próximo socio nuevo = ID más alto que ya existe + 1
        // Así nunca repetimos IDs aunque abramos y cerremos el programa.
        contadorId = GestorCSV.obtenerMaxId(socios) + 1;

        System.out.println("  [i] Sistema listo. Socios cargados: " + socios.size());
    }

    // ==========================================================
    // FUNCIONALIDAD 1: ALTA DE SOCIO
    // ==========================================================

    public void agregarSocio(String nombre, int edad, String planTexto) {
        // Plan.desdeCadena() convierte el texto del usuario al enum.
        // Si el texto no es válido, devuelve null.
        Plan plan = Plan.desdeCadena(planTexto);
        if (plan == null) {
            System.out.println("  [X] Plan invalido. Opciones: " + Plan.listarOpciones());
            return;
        }
        // Ya no pasamos el texto: pasamos plan.name() que es el nombre garantizado del enum
        Socio nuevoSocio = new Socio(contadorId, nombre, edad, plan.name());
        socios.add(nuevoSocio);
        contadorId++;
        GestorCSV.guardar(socios);
        System.out.println("  [OK] Socio registrado con ID: " + nuevoSocio.getId());
    }

    // ==========================================================
    // FUNCIONALIDAD 2: BUSCAR SOCIO POR ID
    // ==========================================================

    // Método PRIVADO de búsqueda interna.
    // Devuelve el objeto Socio si lo encuentra, o null si no existe.
    // "null" (nulo): significa "ningún objeto / vacío".
    private Socio buscarPorId(int id) {
        // for-each (para cada): recorre todos los elementos de la lista
        // Equivalente en PSeInt: Para Cada socio En socios Hacer
        for (Socio socio : socios) {
            if (socio.getId() == id) {
                return socio;
            }
        }
        return null;
    }

    // Método PÚBLICO que muestra el resultado en consola
    public void buscarSocioPorId(int id) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro ningun socio con ID: " + id);
        } else {
            System.out.println(socio); // Llama automáticamente al toString() de Socio
        }
    }

    // ==========================================================
    // FUNCIONALIDAD 3: MODIFICAR SOCIO
    // ==========================================================

    public void modificarNombre(int id, String nuevoNombre) {
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        String anterior = socio.getNombre();
        socio.setNombre(nuevoNombre);
        GestorCSV.guardar(socios);
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
        GestorCSV.guardar(socios);
        System.out.println("  [OK] Edad actualizada a: " + nuevaEdad + " anos.");
    }

    public void modificarPlan(int id, String planTexto) {
        Plan nuevoPlan = Plan.desdeCadena(planTexto);
        if (nuevoPlan == null) {
            System.out.println("  [X] Plan invalido. Opciones: " + Plan.listarOpciones());
            return;
        }
        Socio socio = buscarPorId(id);
        if (socio == null) {
            System.out.println("  [X] No se encontro el socio con ID: " + id);
            return;
        }
        String anterior = socio.getPlan().name();
        socio.setPlan(nuevoPlan.name());
        GestorCSV.guardar(socios);
        System.out.println("  [OK] Plan actualizado: " + anterior + " -> " + nuevoPlan.name());
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
        // .remove(objeto): elimina ese objeto de la lista. La lista se reorganiza sola.
        socios.remove(socio);
        GestorCSV.guardar(socios);
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
        GestorCSV.guardar(socios);
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
        GestorCSV.guardar(socios);
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
        // %n    -> salto de línea multiplataforma
        System.out.printf("  %-5s %-20s %-5s %-12s %-8s %-12s%n",
                          "ID", "NOMBRE", "EDAD", "PLAN", "ASIST.", "CUOTA");
        System.out.println("  " + "-".repeat(66));

        for (Socio socio : socios) {
            System.out.printf("  %-5d %-20s %-5d %-12s %-8d %-12s%n",
                    socio.getId(),
                    socio.getNombre(),
                    socio.getEdad(),
                    socio.getPlan().name(), // .name() convierte el enum a "BASICO", "PREMIUM", etc.
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
        System.out.println("  Plan   : " + socio.getPlan().getDescripcion()); // ej: "Plan Premium"
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
        ArrayList<Socio> ranking = new ArrayList<>(socios);

        // .sort() (ordenar): ordena la lista según un criterio que le indicamos
        // Comparator.comparingInt(): compara por un campo numérico
        // Socio::getAsistencia → "referencia a método" (method reference)
        // .reversed() (invertido): ordena de MAYOR a MENOR
        ranking.sort(Comparator.comparingInt(Socio::getAsistencia).reversed());

        System.out.println("\n  ======== RANKING POR ASISTENCIA ========");
        System.out.printf("  %-5s %-20s %-12s %-10s%n", "POS.", "NOMBRE", "PLAN", "VISITAS");
        System.out.println("  " + "-".repeat(52));

        // for clásico con índice: usamos "i" para mostrar la posición en el ranking
        for (int i = 0; i < ranking.size(); i++) {
            Socio socio = ranking.get(i); // .get(i) (obtener): accede al elemento por posición
            System.out.printf("  %-5d %-20s %-12s %-10d%n",
                    (i + 1),
                    socio.getNombre(),
                    socio.getPlan().name(), // mostramos el nombre del enum como texto
                    socio.getAsistencia());
        }
        System.out.println("  " + "-".repeat(52) + "\n");
    }

    // ----------------------------------------------------------
    // NOTA: esPlanValido() fue ELIMINADO.
    // Con el enum Plan, la validación la hace Plan.desdeCadena().
    // Si devuelve null, el texto no era válido. Simple y seguro.
    // ----------------------------------------------------------
}
