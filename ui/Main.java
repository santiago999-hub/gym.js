// ============================================================
// PAQUETE: ui (user interface = interfaz de usuario)
// Contiene todo lo que el usuario ve e interactúa:
// el menú, los mensajes y la lectura del teclado.
// NO contiene lógica de negocio: solo coordina y delega.
// ============================================================

package ui;

// Importamos Gimnasio desde el paquete "service"
// Sin este import, Java no sabría qué es "Gimnasio".
import service.Gimnasio;
import model.Plan; // Plan: el enum que define los planes válidos del gimnasio

import java.util.Scanner; // Scanner (escaner): permite leer datos del teclado

// ============================================================
// CLASE: Main
// ============================================================
// Punto de entrada del programa. Java busca exactamente el
// método "main" para saber por dónde empezar la ejecución.
// ============================================================

public class Main {

    // ==========================================================
    // MÉTODO PRINCIPAL (Entry Point — Punto de Entrada)
    // ==========================================================
    // public  → cualquiera puede llamarlo
    // static  → pertenece a la CLASE, no a un objeto
    // void    → no devuelve ningún valor
    // main    → nombre especial que Java busca para iniciar
    // String[] args → permite pasar argumentos desde la terminal

    public static void main(String[] args) {

        // System.in (entrada del sistema): representa el teclado
        Scanner scanner = new Scanner(System.in);

        // Creamos el objeto gimnasio, que maneja toda la lógica
        Gimnasio gimnasio = new Gimnasio();

        int opcion;

        System.out.println("\n  +----------------------------------+");
        System.out.println("  |  SISTEMA DE GESTION DE GIMNASIO  |");
        System.out.println("  |          Version 1.0             |");
        System.out.println("  +----------------------------------+\n");

        // do-while (hacer-mientras):
        //   Ejecuta el bloque AL MENOS UNA VEZ y luego repite
        //   mientras la condición sea verdadera.
        //   Equivalente en PSeInt: Repetir ... Hasta Que opcion = 0
        do {
            mostrarMenu();

            System.out.print("\n  Ingrese una opcion: ");
            opcion = leerEntero(scanner);

            switch (opcion) {

                case 1: // ALTA DE SOCIO
                    System.out.println("\n  --- ALTA DE SOCIO ---");
                    System.out.print("  DNI (Documento Nacional de Identidad): ");
                    String dniAlta = scanner.nextLine();
                    System.out.print("  Nombre: ");
                    String nombre = scanner.nextLine();
                    System.out.print("  Edad: ");
                    int edad = leerEntero(scanner);
                    // elegirPlan() muestra el submenu numerico y devuelve el nombre del plan
                    String plan = elegirPlan(scanner);
                    gimnasio.agregarSocio(dniAlta, nombre, edad, plan);
                    break; // break (salir): evita que Java siga ejecutando los siguientes casos

                case 2: // BUSCAR SOCIO POR ID
                    System.out.println("\n  --- BUSCAR SOCIO ---");
                    System.out.print("  ID del socio: ");
                    int idBuscar = leerEntero(scanner);
                    gimnasio.buscarSocioPorId(idBuscar);
                    break;

                case 3: // MODIFICAR SOCIO
                    menuModificar(scanner, gimnasio);
                    break;

                case 4: // ELIMINAR SOCIO
                    System.out.println("\n  --- ELIMINAR SOCIO ---");
                    System.out.print("  ID del socio a eliminar: ");
                    int idEliminar = leerEntero(scanner);
                    System.out.print("  Confirma la eliminacion? (s/n): ");
                    String confirmacion = scanner.nextLine();
                    // equalsIgnoreCase (igual ignorando mayusculas): "S" y "s" son iguales
                    if (confirmacion.equalsIgnoreCase("s")) {
                        gimnasio.eliminarSocio(idEliminar);
                    } else {
                        System.out.println("  Operacion cancelada.");
                    }
                    break;

                case 5: // REGISTRAR ASISTENCIA
                    System.out.println("\n  --- REGISTRAR ASISTENCIA ---");
                    System.out.print("  ID del socio: ");
                    int idAsistencia = leerEntero(scanner);
                    gimnasio.registrarAsistencia(idAsistencia);
                    break;

                case 6: // CONTROLAR CUOTA
                    menuCuota(scanner, gimnasio);
                    break;

                case 7: // VER TODOS LOS SOCIOS
                    gimnasio.mostrarTodosLosSocios();
                    break;

                case 8: // VER RUTINA DE UN SOCIO
                    System.out.println("\n  --- RUTINA DE SOCIO ---");
                    System.out.print("  ID del socio: ");
                    int idRutina = leerEntero(scanner);
                    gimnasio.mostrarRutina(idRutina);
                    break;

                case 9: // CANTIDAD TOTAL DE SOCIOS
                    System.out.println("\n  Total de socios registrados: "
                                      + gimnasio.cantidadTotalSocios());
                    break;

                case 10: // RANKING POR ASISTENCIA
                    gimnasio.mostrarRankingPorAsistencia();
                    break;

                case 11: // MÓDULO COMPLETO DE CUOTAS
                    menuCuotasCompleto(scanner, gimnasio);
                    break;

                case 12: // MÓDULO DE INGRESOS POR DNI
                    menuIngresoDni(scanner, gimnasio);
                    break;

                case 0: // SALIR
                    System.out.println("\n  +----------------------------------+");
                    System.out.println("  |  Hasta luego! Buena jornada!    |");
                    System.out.println("  +----------------------------------+\n");
                    break;

                default: // Número que no está en el menú
                    System.out.println("  [X] Opcion invalida. Intente de nuevo.");
            }

            if (opcion != 0) {
                System.out.println("\n  [Presione ENTER para continuar...]");
                scanner.nextLine();
            }

        } while (opcion != 0);

        scanner.close(); // .close() (cerrar): libera el recurso Scanner
    }

    // ==========================================================
    // MÉTODO: mostrarMenu
    // ==========================================================
    // "private" → solo Main lo usa
    // "static"  → está dentro de main() que también es static

    private static void mostrarMenu() {
        System.out.println("\n  +-------------------------------------+");
        System.out.println("  |           MENU PRINCIPAL            |");
        System.out.println("  +-------------------------------------+");
        System.out.println("  |  1.  Alta de socio                  |");
        System.out.println("  |  2.  Buscar socio por ID            |");
        System.out.println("  |  3.  Modificar socio                |");
        System.out.println("  |  4.  Eliminar socio                 |");
        System.out.println("  |  5.  Registrar asistencia (por ID)  |");
        System.out.println("  |  6.  Controlar cuota (basico)       |");
        System.out.println("  |  7.  Ver todos los socios           |");
        System.out.println("  |  8.  Ver rutina de socio            |");
        System.out.println("  |  9.  Cantidad total de socios       |");
        System.out.println("  |  10. Ranking por asistencia         |");
        System.out.println("  |  ---  MODULOS AVANZADOS  ---        |");
        System.out.println("  |  11. Modulo de Cuotas               |");
        System.out.println("  |  12. Modulo de Ingresos por DNI     |");
        System.out.println("  |  0.  Salir                          |");
        System.out.println("  +-------------------------------------+");
    }

    // ==========================================================
    // MÉTODO: menuModificar (submenú)
    // ==========================================================

    private static void menuModificar(Scanner scanner, Gimnasio gimnasio) {
        System.out.println("\n  --- MODIFICAR SOCIO ---");
        System.out.print("  ID del socio a modificar: ");
        int id = leerEntero(scanner);

        System.out.println("  Que desea modificar?");
        System.out.println("  1. Nombre");
        System.out.println("  2. Edad");
        System.out.println("  3. Plan");
        System.out.print("  Opcion: ");
        int sub = leerEntero(scanner);

        switch (sub) {
            case 1:
                System.out.print("  Nuevo nombre: ");
                String nuevoNombre = scanner.nextLine();
                gimnasio.modificarNombre(id, nuevoNombre);
                break;
            case 2:
                System.out.print("  Nueva edad: ");
                int nuevaEdad = leerEntero(scanner);
                gimnasio.modificarEdad(id, nuevaEdad);
                break;
            case 3:
                // elegirPlan() reutilizamos el mismo submenu que en el alta
                String nuevoPlan = elegirPlan(scanner);
                gimnasio.modificarPlan(id, nuevoPlan);
                break;
            default:
                System.out.println("  [X] Opcion invalida.");
        }
    }

    // ==========================================================
    // MÉTODO: menuCuota (submenú)
    // ==========================================================

    private static void menuCuota(Scanner scanner, Gimnasio gimnasio) {
        System.out.println("\n  --- GESTION DE CUOTA ---");
        System.out.print("  ID del socio: ");
        int id = leerEntero(scanner);

        System.out.println("  1. Verificar estado de cuota");
        System.out.println("  2. Marcar cuota como AL DIA");
        System.out.println("  3. Marcar cuota como PENDIENTE");
        System.out.print("  Opcion: ");
        int sub = leerEntero(scanner);

        switch (sub) {
            case 1:
                gimnasio.verificarCuota(id);
                break;
            case 2:
                gimnasio.actualizarCuota(id, true);
                break;
            case 3:
                gimnasio.actualizarCuota(id, false);
                break;
            default:
                System.out.println("  [X] Opcion invalida.");
        }
    }

    // ==========================================================
    // MÉTODO: menuCuotasCompleto (submenú del Módulo de Cuotas)
    // ==========================================================
    // ¿Por qué existe este método?
    //   Agrupa todas las operaciones de cuotas en un submenú propio.
    //   El menú principal llama a este método y ESTE se encarga de
    //   mostrar opciones, leer la elección y llamar al método correcto
    //   en Gimnasio. El menú principal queda limpio y sin crecer de más.

    private static void menuCuotasCompleto(Scanner scanner, Gimnasio gimnasio) {
        System.out.println("\n  +-------------------------------------+");
        System.out.println("  |       MODULO DE CUOTAS              |");
        System.out.println("  +-------------------------------------+");
        System.out.println("  |  1. Registrar pago de cuota         |");
        System.out.println("  |  2. Verificar si cuota vencio       |");
        System.out.println("  |  3. Historial de pagos de un socio  |");
        System.out.println("  |  4. Listar socios morosos           |");
        System.out.println("  +-------------------------------------+");
        System.out.print("  Opcion: ");
        int sub = leerEntero(scanner);

        switch (sub) {
            case 1: // REGISTRAR PAGO
                System.out.println("\n  --- REGISTRAR PAGO DE CUOTA ---");
                System.out.print("  DNI del socio: ");
                String dniPago = scanner.nextLine();
                System.out.print("  Monto de la cuota ($): ");
                // leerDoble(): variante que lee un número decimal
                double monto = leerDoble(scanner);
                System.out.println("  Metodo de pago:");
                System.out.println("    1. EFECTIVO");
                System.out.println("    2. TRANSFERENCIA");
                System.out.println("    3. TARJETA");
                System.out.print("  Opcion: ");
                int opMetodo = leerEntero(scanner);
                String metodo;
                switch (opMetodo) {
                    case 1:  metodo = "EFECTIVO";       break;
                    case 2:  metodo = "TRANSFERENCIA";  break;
                    case 3:  metodo = "TARJETA";        break;
                    default: metodo = "EFECTIVO";
                             System.out.println("  Opcion invalida. Se usara EFECTIVO.");
                }
                System.out.print("  Observaciones (Enter para omitir): ");
                String obs = scanner.nextLine();
                gimnasio.registrarPagoCuota(dniPago, monto, metodo, obs);
                break;

            case 2: // VERIFICAR VENCIMIENTO
                System.out.println("\n  --- VERIFICAR CUOTA ---");
                System.out.print("  DNI del socio: ");
                String dniVerif = scanner.nextLine();
                gimnasio.verificarCuotaVencida(dniVerif);
                break;

            case 3: // HISTORIAL DE PAGOS
                System.out.println("\n  --- HISTORIAL DE PAGOS ---");
                System.out.print("  DNI del socio: ");
                String dniHist = scanner.nextLine();
                gimnasio.mostrarHistorialPagos(dniHist);
                break;

            case 4: // LISTAR MOROSOS
                gimnasio.listarSociosMorosos();
                break;

            default:
                System.out.println("  [X] Opcion invalida.");
        }
    }

    // ==========================================================
    // MÉTODO: menuIngresoDni (submenú del Módulo de Ingresos)
    // ==========================================================
    // Controla el acceso al gimnasio por DNI y el historial de visitas.

    private static void menuIngresoDni(Scanner scanner, Gimnasio gimnasio) {
        System.out.println("\n  +-------------------------------------+");
        System.out.println("  |    MODULO DE INGRESOS POR DNI       |");
        System.out.println("  +-------------------------------------+");
        System.out.println("  |  1. Registrar ingreso por DNI       |");
        System.out.println("  |  2. Historial de ingresos           |");
        System.out.println("  |  3. Verificar ausencia prolongada   |");
        System.out.println("  |  4. Ranking de asistencia           |");
        System.out.println("  +-------------------------------------+");
        System.out.print("  Opcion: ");
        int sub = leerEntero(scanner);

        switch (sub) {
            case 1: // REGISTRAR INGRESO
                System.out.println("\n  --- INGRESO AL GIMNASIO ---");
                System.out.print("  Ingrese su DNI: ");
                String dniIngreso = scanner.nextLine();
                gimnasio.registrarIngresoPorDni(dniIngreso);
                break;

            case 2: // HISTORIAL DE INGRESOS
                System.out.println("\n  --- HISTORIAL DE INGRESOS ---");
                System.out.print("  DNI del socio: ");
                String dniHistIng = scanner.nextLine();
                gimnasio.mostrarHistorialIngresos(dniHistIng);
                break;

            case 3: // VERIFICAR AUSENCIA
                System.out.println("\n  --- VERIFICAR AUSENCIA ---");
                System.out.print("  DNI del socio: ");
                String dniAusencia = scanner.nextLine();
                gimnasio.verificarAusencia(dniAusencia);
                break;

            case 4: // RANKING DE ASISTENCIA
                gimnasio.mostrarRankingPorAsistencia();
                break;

            default:
                System.out.println("  [X] Opcion invalida.");
        }
    }

    // ==========================================================
    // MÉTODO: leerDoble (con validación)
    // ==========================================================
    // Similar a leerEntero() pero lee un número DECIMAL (double).
    // Evita que el programa se rompa si el usuario escribe letras.
    // "double" (doble precisión): tipo de dato para números con coma decimal.

    private static double leerDoble(Scanner scanner) {
        // hasNextDouble() (tiene un numero decimal?): devuelve true si lo siguiente es un decimal
        while (!scanner.hasNextDouble()) {
            System.out.print("  [X] Ingrese un numero valido (ej: 5000.00): ");
            scanner.next(); // descarta el texto inválido
        }
        double valor = scanner.nextDouble();
        scanner.nextLine(); // limpia el buffer
        return valor;
    }

    // ==========================================================
    // MÉTODO: elegirPlan (submenú de selección de plan)
    // ==========================================================
    // Muestra las opciones numeradas y repite el menú si el usuario
    // elige un número que no existe. Los datos ya ingresados (nombre,
    // edad) NO se pierden porque este método solo pregunta por el plan.
    //
    // Plan.values() devuelve el array [BASICO, INTERMEDIO, PREMIUM]
    // Recorremos ese array para mostrar y para convertir el número elegido
    // al nombre del plan correspondiente ("BASICO", "INTERMEDIO", etc.)

    private static String elegirPlan(Scanner scanner) {
        Plan[] planes = Plan.values(); // Plan.values(): array con todas las constantes del enum

        // do-while: repite hasta que el usuario elija un número válido
        while (true) {
            System.out.println("  Seleccione el plan:");

            // Mostrar cada plan numerado usando el índice del array
            // planes[0] = BASICO, planes[1] = INTERMEDIO, planes[2] = PREMIUM
            for (int i = 0; i < planes.length; i++) {
                System.out.println("    " + (i + 1) + ". " + planes[i].getDescripcion());
                // getDescripcion(): devuelve "Plan Basico", "Plan Intermedio", "Plan Premium"
            }

            System.out.print("  Opcion (1-" + planes.length + "): ");
            int opcion = leerEntero(scanner);

            // Validar que esté dentro del rango (1 a cantidad de planes)
            if (opcion >= 1 && opcion <= planes.length) {
                // opcion - 1: convertimos del número humano (1,2,3) al índice del array (0,1,2)
                // .name(): convierte el enum al texto "BASICO", "INTERMEDIO" o "PREMIUM"
                return planes[opcion - 1].name();
            }

            // Si llega acá, la opción era inválida → vuelve a mostrar el menú
            System.out.println("  [X] Opcion invalida. Elija entre 1 y " + planes.length + ".");
        }
    }

    // ==========================================================
    // MÉTODO: leerEntero (con validación)
    // ==========================================================
    // Evita que el programa se "rompa" si el usuario escribe letras
    // cuando se espera un número.

    private static int leerEntero(Scanner scanner) {
        // while (mientras): repite mientras la condición sea verdadera
        // !scanner.hasNextInt() → "mientras lo siguiente NO sea un entero"
        while (!scanner.hasNextInt()) {
            System.out.print("  [X] Ingrese solo numeros enteros: ");
            scanner.next(); // Descarta el texto inválido que está en el buffer
        }
        int valor = scanner.nextInt(); // nextInt() (siguiente entero): lee el número
        scanner.nextLine();            // Limpia el '\n' que queda en el buffer
        return valor;
    }
}
