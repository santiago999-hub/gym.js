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
import model.RegistroCuota;  // RegistroCuota: registro detallado de cada pago de cuota
import model.RegistroIngreso; // RegistroIngreso: registro de cada visita al gimnasio por DNI
import persistence.GestorCSV;
import persistence.GestorCuotasCSV;   // GestorCuotasCSV: maneja el archivo cuotas.csv
import persistence.GestorIngresosCSV; // GestorIngresosCSV: maneja el archivo ingresos.csv

import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDate;           // LocalDate: fecha sin hora (para vencimientos)
import java.time.LocalDateTime;       // LocalDateTime: fecha + hora (para ingresos)
import java.time.format.DateTimeFormatter; // DateTimeFormatter: formatea fechas a texto
import java.time.temporal.ChronoUnit; // ChronoUnit (unidad cronológica): calcula días entre fechas

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

    // ArrayList<Socio>: lista dinámica sin tamaño fijo.
    private ArrayList<Socio> socios;

    // Lista de todos los registros de cuotas (historial completo de pagos)
    private ArrayList<RegistroCuota> cuotas;

    // Lista de todos los ingresos al gimnasio registrados por DNI
    private ArrayList<RegistroIngreso> ingresos;

    // Contador que asigna IDs únicos automáticamente
    private int contadorId;

    // ----------------------------------------------------------
    // CONSTRUCTOR
    // ----------------------------------------------------------

    public Gimnasio() {
        // Cargamos socios desde socios.csv
        socios = GestorCSV.cargar();
        contadorId = GestorCSV.obtenerMaxId(socios) + 1;

        // Cargamos historial de cuotas desde cuotas.csv
        cuotas = GestorCuotasCSV.cargar();

        // Cargamos historial de ingresos desde ingresos.csv
        ingresos = GestorIngresosCSV.cargar();

        System.out.println("  [i] Sistema listo. Socios: " + socios.size()
            + " | Cuotas: " + cuotas.size()
            + " | Ingresos: " + ingresos.size());
    }

    // ==========================================================
    // FUNCIONALIDAD 1: ALTA DE SOCIO
    // ==========================================================

    public void agregarSocio(String dni, String nombre, int edad, String planTexto) {
        // Verificar que el DNI no esté ya registrado (unicidad)
        if (buscarPorDni(dni) != null) {
            System.out.println("  [X] Ya existe un socio registrado con el DNI: " + dni);
            return;
        }
        Plan plan = Plan.desdeCadena(planTexto);
        if (plan == null) {
            System.out.println("  [X] Plan invalido. Opciones: " + Plan.listarOpciones());
            return;
        }
        // Constructor actualizado: ahora recibe DNI como tercer argumento
        Socio nuevoSocio = new Socio(contadorId, dni, nombre, edad, plan.name());
        socios.add(nuevoSocio);
        contadorId++;
        GestorCSV.guardar(socios);
        System.out.println("  [OK] Socio registrado con ID: " + nuevoSocio.getId() + " | DNI: " + dni);
    }

    // ==========================================================
    // FUNCIONALIDAD 2: BUSCAR SOCIO POR ID
    // ==========================================================

    // Método PRIVADO de búsqueda interna por ID.
    // Devuelve el objeto Socio si lo encuentra, o null si no existe.
    private Socio buscarPorId(int id) {
        for (Socio socio : socios) {
            if (socio.getId() == id) {
                return socio;
            }
        }
        return null;
    }

    // Método PRIVADO de búsqueda interna por DNI.
    // Se usa en los módulos de cuotas e ingresos.
    // .equals() (igual): compara el contenido del String, no la referencia en memoria.
    private Socio buscarPorDni(String dni) {
        for (Socio socio : socios) {
            if (socio.getDni().equals(dni)) {
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

        System.out.printf("  %-5s %-12s %-20s %-5s %-12s %-8s %-12s%n",
                          "ID", "DNI", "NOMBRE", "EDAD", "PLAN", "ASIST.", "CUOTA");
        System.out.println("  " + "-".repeat(78));

        for (Socio socio : socios) {
            System.out.printf("  %-5d %-12s %-20s %-5d %-12s %-8d %-12s%n",
                    socio.getId(),
                    socio.getDni(),
                    socio.getNombre(),
                    socio.getEdad(),
                    socio.getPlan().name(),
                    socio.getAsistencia(),
                    socio.isCuotaAlDia() ? "Al dia [OK]" : "PENDIENTE");
        }

        System.out.println("  " + "-".repeat(78));
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

    // ==========================================================
    // MÓDULO 1: CUOTAS — registro y control de pagos mensuales
    // ==========================================================

    // ----------------------------------------------------------
    // MÉTODO: registrarPagoCuota
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   El operador ingresa el DNI del socio y los datos del pago.
    //   El sistema crea un RegistroCuota, lo guarda en el historial,
    //   y marca al socio como "cuota al día" automáticamente.
    //
    // Parámetros:
    //   dni         → para encontrar al socio
    //   monto       → importe cobrado (ej: 5000.0)
    //   metodoPago  → "EFECTIVO", "TRANSFERENCIA" o "TARJETA"
    //   observaciones → nota libre del operador

    public void registrarPagoCuota(String dni, double monto,
                                   String metodoPago, String observaciones) {
        Socio socio = buscarPorDni(dni);
        if (socio == null) {
            System.out.println("  [X] No se encontro ningun socio con DNI: " + dni);
            return;
        }

        // Creamos el registro de cuota con los datos del pago
        RegistroCuota nuevaCuota = new RegistroCuota(dni, socio.getNombre(),
                                                      monto, metodoPago, observaciones);
        cuotas.add(nuevaCuota); // .add() agrega el registro al final de la lista

        // Al registrar un pago, la cuota queda automáticamente "al día"
        socio.setCuotaAlDia(true);

        // Persistimos ambos archivos: el estado del socio y el nuevo registro de cuota
        GestorCSV.guardar(socios);
        GestorCuotasCSV.guardar(cuotas);

        System.out.println("\n  [OK] Pago registrado correctamente.");
        System.out.println("  " + "-".repeat(44));
        System.out.println(nuevaCuota);
        System.out.println("  " + "-".repeat(44));
    }

    // ----------------------------------------------------------
    // MÉTODO: verificarCuotaVencida
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Busca el pago más reciente del socio y compara la fecha de
    //   vencimiento con hoy. Si ya pasó, marca la cuota como PENDIENTE
    //   automáticamente y avisa cuántos días lleva vencida.
    //   Si no venció, muestra cuántos días faltan.

    public void verificarCuotaVencida(String dni) {
        Socio socio = buscarPorDni(dni);
        if (socio == null) {
            System.out.println("  [X] No se encontro ningun socio con DNI: " + dni);
            return;
        }

        // Buscar el RegistroCuota más reciente de este socio
        // "La más reciente" = la que tiene el vencimiento MÁS LEJANO en el tiempo
        RegistroCuota ultimaCuota = null;
        for (RegistroCuota c : cuotas) {
            if (c.getDni().equals(dni)) {
                // Si aún no tenemos ninguna, o esta vence después que la que teníamos
                if (ultimaCuota == null
                    || c.getProximoVencimiento().isAfter(ultimaCuota.getProximoVencimiento())) {
                    ultimaCuota = c;
                }
            }
        }

        if (ultimaCuota == null) {
            System.out.println("  [!] " + socio.getNombre() + " no tiene cuotas registradas en el sistema.");
            System.out.println("  Use la opcion 'Registrar pago de cuota' para registrar el primer pago.");
            return;
        }

        // DateTimeFormatter para mostrar fechas con formato "dd/MM/yyyy"
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        if (ultimaCuota.estaVencida()) {
            // ChronoUnit.DAYS.between(): calcula la diferencia en DÍAS entre dos fechas
            long diasVencida = ChronoUnit.DAYS.between(ultimaCuota.getProximoVencimiento(), LocalDate.now());
            socio.setCuotaAlDia(false); // auto-marcar como pendiente
            GestorCSV.guardar(socios);
            System.out.println("  [!!] CUOTA VENCIDA para: " + socio.getNombre());
            System.out.println("  Vencio el  : " + ultimaCuota.getProximoVencimiento().format(fmt));
            System.out.println("  Dias vencida: " + diasVencida + " dia(s)");
            System.out.println("  Estado auto-actualizado a PENDIENTE.");
        } else {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), ultimaCuota.getProximoVencimiento());
            System.out.println("  [OK] Cuota VIGENTE para: " + socio.getNombre());
            System.out.println("  Vence el      : " + ultimaCuota.getProximoVencimiento().format(fmt));
            System.out.println("  Dias restantes: " + diasRestantes + " dia(s)");
        }
    }

    // ----------------------------------------------------------
    // MÉTODO: mostrarHistorialPagos
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Muestra todos los pagos que hizo un socio (ordenados del más
    //   reciente al más antiguo). Útil para resolver disputas o auditorías.

    public void mostrarHistorialPagos(String dni) {
        Socio socio = buscarPorDni(dni);
        if (socio == null) {
            System.out.println("  [X] No se encontro ningun socio con DNI: " + dni);
            return;
        }

        // Filtrar solo los registros de cuota que corresponden a este DNI
        ArrayList<RegistroCuota> historial = new ArrayList<>();
        for (RegistroCuota c : cuotas) {
            if (c.getDni().equals(dni)) {
                historial.add(c);
            }
        }

        if (historial.isEmpty()) {
            System.out.println("  [i] " + socio.getNombre() + " no tiene pagos registrados aun.");
            return;
        }

        // Ordenar por fecha de pago: más reciente primero
        // Comparator.comparing() + .reversed() = orden descendente
        historial.sort(Comparator.comparing(RegistroCuota::getFechaPago).reversed());

        System.out.println("\n  ======== HISTORIAL DE PAGOS: "
                           + socio.getNombre().toUpperCase() + " ========");
        for (int i = 0; i < historial.size(); i++) {
            System.out.println("\n  --- Cuota " + (i + 1) + " ---");
            System.out.println(historial.get(i));
        }
        System.out.println("\n  Total de pagos registrados: " + historial.size() + "\n");
    }

    // ----------------------------------------------------------
    // MÉTODO: listarSociosMorosos
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Lista todos los socios con cuota PENDIENTE (morosos).
    //   Un socio se marca moroso cuando:
    //     a) El operador lo marcó manualmente como pendiente, o
    //     b) verificarCuotaVencida() detectó que su cuota expiró.

    public void listarSociosMorosos() {
        if (socios.isEmpty()) {
            System.out.println("  No hay socios registrados en el sistema.");
            return;
        }

        ArrayList<Socio> morosos = new ArrayList<>();
        for (Socio s : socios) {
            if (!s.isCuotaAlDia()) { // !isCuotaAlDia() = cuota NO está al día
                morosos.add(s);
            }
        }

        if (morosos.isEmpty()) {
            System.out.println("  [OK] No hay socios morosos. Todos tienen la cuota al dia.");
            return;
        }

        System.out.println("\n  ======== SOCIOS CON CUOTA PENDIENTE (MOROSOS) ========");
        System.out.printf("  %-12s %-22s %-12s%n", "DNI", "NOMBRE", "PLAN");
        System.out.println("  " + "-".repeat(50));
        for (Socio s : morosos) {
            System.out.printf("  %-12s %-22s %-12s%n",
                s.getDni(), s.getNombre(), s.getPlan().name());
        }
        System.out.println("  " + "-".repeat(50));
        System.out.println("  Total morosos: " + morosos.size() + " socio(s)\n");
    }

    // ==========================================================
    // MÓDULO 2: INGRESOS — control de visitas por DNI
    // ==========================================================

    // ----------------------------------------------------------
    // MÉTODO: registrarIngresoPorDni
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Simula el lector de DNI en la puerta del gimnasio.
    //   El socio ingresa su número y el sistema:
    //     1. Verifica que el socio existe
    //     2. Crea un RegistroIngreso con la fecha y hora exacta
    //     3. Incrementa el contador de asistencias del socio
    //     4. Guarda todo en disco

    public void registrarIngresoPorDni(String dni) {
        Socio socio = buscarPorDni(dni);
        if (socio == null) {
            System.out.println("  [X] DNI no reconocido: " + dni);
            System.out.println("  El socio no esta registrado en el sistema.");
            return;
        }

        // Creamos el ingreso: LocalDateTime.now() se llama dentro del constructor
        RegistroIngreso ingreso = new RegistroIngreso(dni, socio.getNombre());
        ingresos.add(ingreso);

        // Incrementamos el contador de visitas del socio
        socio.incrementarAsistencia();

        // Guardamos ambos archivos
        GestorCSV.guardar(socios);
        GestorIngresosCSV.guardar(ingresos);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("  [OK] Bienvenido/a, " + socio.getNombre() + "!");
        System.out.println("  Fecha y hora    : " + ingreso.getFechaHora().format(fmt));
        System.out.println("  Total de visitas: " + socio.getAsistencia());
    }

    // ----------------------------------------------------------
    // MÉTODO: mostrarHistorialIngresos
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Muestra todas las visitas de un socio con fecha y hora,
    //   ordenadas del ingreso más reciente al más antiguo.

    public void mostrarHistorialIngresos(String dni) {
        Socio socio = buscarPorDni(dni);
        if (socio == null) {
            System.out.println("  [X] No se encontro ningun socio con DNI: " + dni);
            return;
        }

        // Filtrar los ingresos que tienen el DNI solicitado
        ArrayList<RegistroIngreso> historial = new ArrayList<>();
        for (RegistroIngreso ing : ingresos) {
            if (ing.getDni().equals(dni)) {
                historial.add(ing);
            }
        }

        if (historial.isEmpty()) {
            System.out.println("  [i] " + socio.getNombre() + " no tiene ingresos registrados aun.");
            return;
        }

        // Ordenar por fechaHora: más reciente primero
        historial.sort(Comparator.comparing(RegistroIngreso::getFechaHora).reversed());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        System.out.println("\n  ======== HISTORIAL DE INGRESOS: "
                           + socio.getNombre().toUpperCase() + " ========");
        System.out.printf("  %-5s  %-20s%n", "Nro.", "FECHA Y HORA");
        System.out.println("  " + "-".repeat(30));
        for (int i = 0; i < historial.size(); i++) {
            System.out.printf("  %-5d  %s%n",
                (i + 1), historial.get(i).getFechaHora().format(fmt));
        }
        System.out.println("  " + "-".repeat(30));
        System.out.println("  Total de visitas registradas: " + historial.size() + "\n");
    }

    // ----------------------------------------------------------
    // MÉTODO: verificarAusencia
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Busca la visita MÁS RECIENTE del socio y calcula cuántos
    //   días llevan sin asistir. Si son más de 7 días, avisa.
    //   Útil para que el gimnasio contacte a socios inactivos.

    public void verificarAusencia(String dni) {
        Socio socio = buscarPorDni(dni);
        if (socio == null) {
            System.out.println("  [X] No se encontro ningun socio con DNI: " + dni);
            return;
        }

        // Encontrar el ingreso más reciente de este DNI
        RegistroIngreso ultimoIngreso = null;
        for (RegistroIngreso ing : ingresos) {
            if (ing.getDni().equals(dni)) {
                if (ultimoIngreso == null
                    || ing.getFechaHora().isAfter(ultimoIngreso.getFechaHora())) {
                    ultimoIngreso = ing;
                }
            }
        }

        if (ultimoIngreso == null) {
            System.out.println("  [!] " + socio.getNombre() + " NUNCA ha ingresado al gimnasio.");
            System.out.println("  No hay registros de visitas en el sistema.");
            return;
        }

        // .toLocalDate(): extrae solo la FECHA de un LocalDateTime (descarta la hora)
        LocalDate ultimaVisita = ultimoIngreso.getFechaHora().toLocalDate();
        // ChronoUnit.DAYS.between(desde, hasta): cuenta los días entre dos fechas
        long diasAusente = ChronoUnit.DAYS.between(ultimaVisita, LocalDate.now());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("  Socio         : " + socio.getNombre() + " (DNI: " + dni + ")");
        System.out.println("  Ultimo ingreso: " + ultimaVisita.format(fmt));
        System.out.println("  Dias ausente  : " + diasAusente);

        if (diasAusente > 7) {
            System.out.println("  [!!] AUSENCIA PROLONGADA: mas de 7 dias sin asistir.");
            System.out.println("  Se recomienda contactar al socio.");
        } else {
            System.out.println("  [OK] Asistencia reciente. No hay ausencia prolongada.");
        }
    }
}
