// ============================================================
// CLASE: Main
// ARCHIVO: Main.java
// ============================================================
// ¿Por qué existe esta clase?
//   Es el PUNTO DE ENTRADA del programa. Java busca exactamente
//   un método llamado "main" para saber por dónde empezar.
//   Su única responsabilidad es: mostrar el menú, leer lo que
//   escribe el usuario y llamar a los métodos de Gimnasio.
//   NO contiene lógica de negocio (eso está en Gimnasio.java).
// ============================================================

import java.util.Scanner; // Scanner (escaner): permite leer datos escritos por el usuario

public class Main {

    // ==========================================================
    // MÉTODO PRINCIPAL (Entry Point)
    // ==========================================================
    // public  → cualquiera puede llamarlo
    // static  → pertenece a la CLASE, no a un objeto (por eso Java lo puede
    //           llamar sin crear un objeto Main primero)
    // void    → no devuelve ningún valor
    // main    → nombre especial que Java busca para iniciar el programa
    // String[] args → permite pasar argumentos desde la terminal (no lo usamos aquí)

    public static void main(String[] args) {

        // System.in (entrada del sistema): representa el teclado
        Scanner scanner = new Scanner(System.in);

        // Creamos el objeto gimnasio. A partir de aquí todo funciona a través de él.
        Gimnasio gimnasio = new Gimnasio();

        int opcion; // Variable que guarda la opción del menú que elige el usuario

        // Encabezado de bienvenida
        System.out.println("\n  +----------------------------------+");
        System.out.println("  |  SISTEMA DE GESTION DE GIMNASIO  |");
        System.out.println("  |          Version 1.0             |");
        System.out.println("  +----------------------------------+\n");

        // do-while (hacer-mientras):
        //   Ejecuta el bloque de código AL MENOS UNA VEZ y luego repite
        //   mientras la condición sea verdadera.
        //   Equivalente en PSeInt: Repetir ... Hasta Que opcion = 0
        do {
            mostrarMenu();

            System.out.print("\n  Ingrese una opcion: ");
            opcion = leerEntero(scanner); // Leemos con validación (evita crash si escriben letras)

            // switch (interruptor/selector): evalúa "opcion" y ejecuta el caso correspondiente
            switch (opcion) {

                // --------------------------------------------------
                case 1: // ALTA DE SOCIO
                // --------------------------------------------------
                    System.out.println("\n  --- ALTA DE SOCIO ---");
                    System.out.print("  Nombre: ");
                    String nombre = scanner.nextLine(); // nextLine(): lee toda la línea (acepta espacios)
                    System.out.print("  Edad: ");
                    int edad = leerEntero(scanner);
                    System.out.println("  Planes disponibles: BASICO | INTERMEDIO | PREMIUM");
                    System.out.print("  Plan: ");
                    String plan = scanner.nextLine().toUpperCase();
                    gimnasio.agregarSocio(nombre, edad, plan);
                    break; // break (salir): evita que Java siga ejecutando los siguientes casos

                // --------------------------------------------------
                case 2: // BUSCAR SOCIO POR ID
                // --------------------------------------------------
                    System.out.println("\n  --- BUSCAR SOCIO ---");
                    System.out.print("  ID del socio: ");
                    int idBuscar = leerEntero(scanner);
                    gimnasio.buscarSocioPorId(idBuscar);
                    break;

                // --------------------------------------------------
                case 3: // MODIFICAR SOCIO
                // --------------------------------------------------
                    menuModificar(scanner, gimnasio);
                    break;

                // --------------------------------------------------
                case 4: // ELIMINAR SOCIO
                // --------------------------------------------------
                    System.out.println("\n  --- ELIMINAR SOCIO ---");
                    System.out.print("  ID del socio a eliminar: ");
                    int idEliminar = leerEntero(scanner);
                    System.out.print("  Confirma la eliminacion? (s/n): ");
                    String confirmacion = scanner.nextLine();
                    // equalsIgnoreCase (igual ignorando mayusculas): "S", "s" y "S" son iguales
                    if (confirmacion.equalsIgnoreCase("s")) {
                        gimnasio.eliminarSocio(idEliminar);
                    } else {
                        System.out.println("  Operacion cancelada.");
                    }
                    break;

                // --------------------------------------------------
                case 5: // REGISTRAR ASISTENCIA
                // --------------------------------------------------
                    System.out.println("\n  --- REGISTRAR ASISTENCIA ---");
                    System.out.print("  ID del socio: ");
                    int idAsistencia = leerEntero(scanner);
                    gimnasio.registrarAsistencia(idAsistencia);
                    break;

                // --------------------------------------------------
                case 6: // CONTROLAR CUOTA
                // --------------------------------------------------
                    menuCuota(scanner, gimnasio);
                    break;

                // --------------------------------------------------
                case 7: // VER TODOS LOS SOCIOS
                // --------------------------------------------------
                    gimnasio.mostrarTodosLosSocios();
                    break;

                // --------------------------------------------------
                case 8: // VER RUTINA DE UN SOCIO
                // --------------------------------------------------
                    System.out.println("\n  --- RUTINA DE SOCIO ---");
                    System.out.print("  ID del socio: ");
                    int idRutina = leerEntero(scanner);
                    gimnasio.mostrarRutina(idRutina);
                    break;

                // --------------------------------------------------
                case 9: // CANTIDAD TOTAL DE SOCIOS
                // --------------------------------------------------
                    System.out.println("\n  Total de socios registrados: "
                                      + gimnasio.cantidadTotalSocios());
                    break;

                // --------------------------------------------------
                case 10: // RANKING POR ASISTENCIA
                // --------------------------------------------------
                    gimnasio.mostrarRankingPorAsistencia();
                    break;

                // --------------------------------------------------
                case 0: // SALIR
                // --------------------------------------------------
                    System.out.println("\n  +----------------------------------+");
                    System.out.println("  |  Hasta luego! Buena jornada!    |");
                    System.out.println("  +----------------------------------+\n");
                    break;

                // --------------------------------------------------
                default: // Si el usuario escribió un número que no está en el menú
                // --------------------------------------------------
                    System.out.println("  [X] Opcion invalida. Intente de nuevo.");
            }

            // Pausa entre operaciones para que el usuario pueda leer el resultado
            if (opcion != 0) {
                System.out.println("\n  [Presione ENTER para continuar...]");
                scanner.nextLine();
            }

        } while (opcion != 0); // El menú se repite hasta que el usuario elige 0

        // .close() (cerrar): libera el recurso Scanner cuando ya no se necesita.
        // Buena práctica: siempre cerrar los recursos que se abren.
        scanner.close();
    }

    // ==========================================================
    // MÉTODO: mostrarMenu
    // ==========================================================
    // "private" → solo Main lo usa
    // "static"  → está dentro de main() que también es static,
    //             por eso este método también debe ser static

    private static void mostrarMenu() {
        System.out.println("\n  +-------------------------------------+");
        System.out.println("  |           MENU PRINCIPAL            |");
        System.out.println("  +-------------------------------------+");
        System.out.println("  |  1.  Alta de socio                  |");
        System.out.println("  |  2.  Buscar socio por ID            |");
        System.out.println("  |  3.  Modificar socio                |");
        System.out.println("  |  4.  Eliminar socio                 |");
        System.out.println("  |  5.  Registrar asistencia           |");
        System.out.println("  |  6.  Controlar cuota                |");
        System.out.println("  |  7.  Ver todos los socios           |");
        System.out.println("  |  8.  Ver rutina de socio            |");
        System.out.println("  |  9.  Cantidad total de socios       |");
        System.out.println("  |  10. Ranking por asistencia         |");
        System.out.println("  |  0.  Salir                          |");
        System.out.println("  +-------------------------------------+");
    }

    // ==========================================================
    // MÉTODO: menuModificar (submenú)
    // ==========================================================
    // Parámetros recibidos:
    //   Scanner scanner  → necesitamos seguir leyendo del teclado
    //   Gimnasio gimnasio → necesitamos llamar a sus métodos

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
                System.out.println("  Planes: BASICO | INTERMEDIO | PREMIUM");
                System.out.print("  Nuevo plan: ");
                String nuevoPlan = scanner.nextLine();
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
    // MÉTODO: leerEntero (con validación)
    // ==========================================================
    // Problema clásico: si el usuario escribe "abc" cuando esperamos
    // un número, el programa lanza una excepción y se "rompe".
    // Este método evita eso repitiendo la lectura hasta obtener un número.

    private static int leerEntero(Scanner scanner) {
        // while (mientras): repite mientras la condición sea verdadera
        // !scanner.hasNextInt() → "mientras lo siguiente NO sea un entero"
        // hasNextInt() (tiene siguiente entero?): verifica sin consumir el valor
        while (!scanner.hasNextInt()) {
            System.out.print("  [X] Ingrese solo numeros enteros: ");
            scanner.next(); // Descarta el texto inválido que está en el buffer
        }
        int valor = scanner.nextInt(); // nextInt() (siguiente entero): lee el número
        scanner.nextLine();            // IMPORTANTE: limpia el '\n' que queda en el buffer
                                       // (si no hacemos esto, el próximo nextLine() lee vacío)
        return valor;
    }
}
