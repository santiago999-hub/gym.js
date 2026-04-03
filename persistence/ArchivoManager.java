// ============================================================
// PAQUETE: persistence (persistencia)
// ============================================================
// ArchivoManager (Gestor de Archivos) es el PUNTO CENTRAL de
// toda la persistencia del sistema.
//
// ¿Por qué existe esta clase si ya hay GestorCSV, GestorCuotasCSV
// y GestorIngresosCSV?
//   Antes cada clase de Gimnasio llamaba directamente a su gestor.
//   ArchivoManager actúa como un "coordinador único": Gimnasio
//   solo habla con ArchivoManager, y este delega (redirige) a
//   cada gestor especializado. Ventajas:
//     - Si algún día cambiamos el formato (CSV → JSON → base de datos),
//       solo cambiamos ArchivoManager, no toda la app.
//     - El backup se puede disparar desde un solo lugar.
//     - Queda claro DÓNDE vive toda la lógica de archivos.
//
// Concepto nuevo: FACADE PATTERN (Patrón Fachada)
//   Es un patrón de diseño: una clase que simplifica el acceso
//   a un conjunto de clases más complejas.
//   La fachada es la "puerta de entrada" al subsistema de archivos.
// ============================================================

package persistence;

import model.Socio;
import model.RegistroCuota;
import model.RegistroIngreso;

import java.io.BufferedReader;   // BufferedReader (lector con buffer): lee archivos línea a línea
import java.io.BufferedWriter;   // BufferedWriter (escritor con buffer): escribe en archivos
import java.io.File;             // File (archivo): representa un archivo o carpeta en el disco
import java.io.FileReader;       // FileReader (lector de archivo): abre texto para leer
import java.io.FileWriter;       // FileWriter (escritor de archivo): abre texto para escribir
import java.io.IOException;      // IOException (excepción de E/S): error al leer o escribir
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ArchivoManager {

    // ----------------------------------------------------------
    // CONSTANTES — nombres de los archivos del sistema
    // ----------------------------------------------------------
    // "static final" (estática final) = CONSTANTE:
    //   No puede cambiar su valor; pertenece a la clase, no a objetos.
    //   Por convención se escriben en MAYÚSCULAS.

    private static final String ARCHIVO_SOCIOS      = "socios.csv";
    private static final String ARCHIVO_CUOTAS      = "cuotas.csv";
    private static final String ARCHIVO_INGRESOS    = "ingresos.csv";
    private static final String ARCHIVO_ESTADISTICAS = "estadisticas.txt";
    private static final String CARPETA_BACKUP      = "backup";  // carpeta donde guardar las copias

    // ==========================================================
    // SECCIÓN 1: SOCIOS
    // ==========================================================
    // Los métodos de esta sección DELEGAN (redirigen) al GestorCSV
    // ya existente. No repiten lógica: reutilizan lo que funciona.
    // ==========================================================

    // ¿Para qué sirve "guardarSocios"?
    //   Centraliza el guardado. Gimnasio llama a ArchivoManager,
    //   que llama a GestorCSV. Si mañana cambiamos el formato,
    //   solo tocamos este método.

    public static void guardarSocios(ArrayList<Socio> socios) {
        GestorCSV.guardar(socios); // delega (delegate) al gestor especializado
    }

    public static ArrayList<Socio> cargarSocios() {
        return GestorCSV.cargar();
    }

    // ==========================================================
    // SECCIÓN 2: CUOTAS
    // ==========================================================

    public static void guardarCuotas(ArrayList<RegistroCuota> cuotas) {
        GestorCuotasCSV.guardar(cuotas);
    }

    public static ArrayList<RegistroCuota> cargarCuotas() {
        return GestorCuotasCSV.cargar();
    }

    // ==========================================================
    // SECCIÓN 3: INGRESOS
    // ==========================================================

    public static void guardarIngresos(ArrayList<RegistroIngreso> ingresos) {
        GestorIngresosCSV.guardar(ingresos);
    }

    public static ArrayList<RegistroIngreso> cargarIngresos() {
        return GestorIngresosCSV.cargar();
    }

    // ==========================================================
    // SECCIÓN 4: VERIFICACIÓN DE ARCHIVOS
    // ==========================================================

    // ----------------------------------------------------------
    // MÉTODO: archivoExiste (file exists = archivo existe)
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Antes de intentar leer un archivo, verificamos si existe.
    //   File.exists() (existe?): devuelve true si el archivo está en el disco.
    //   Evita errores del tipo "FileNotFoundException" al arrancar.

    public static boolean archivoExiste(String nombreArchivo) {
        return new File(nombreArchivo).exists();
    }

    // ----------------------------------------------------------
    // MÉTODO: iniciarSistema
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Muestra en pantalla qué archivos se encontraron al arrancar.
    //   Así el operador sabe desde el primer momento si hay datos
    //   previos o si arranca con un sistema vacío.

    public static void iniciarSistema() {
        System.out.println("  [i] Verificando archivos del sistema...");
        System.out.println("  Socios   : " + (archivoExiste(ARCHIVO_SOCIOS)   ? "[OK] " + ARCHIVO_SOCIOS   : "[NUEVO]"));
        System.out.println("  Cuotas   : " + (archivoExiste(ARCHIVO_CUOTAS)   ? "[OK] " + ARCHIVO_CUOTAS   : "[NUEVO]"));
        System.out.println("  Ingresos : " + (archivoExiste(ARCHIVO_INGRESOS) ? "[OK] " + ARCHIVO_INGRESOS : "[NUEVO]"));
    }

    // ==========================================================
    // SECCIÓN 5: ESTADÍSTICAS EN ARCHIVO TXT
    // ==========================================================

    // ----------------------------------------------------------
    // MÉTODO: guardarEstadisticas
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Escribe un resumen ejecutivo en "estadisticas.txt" cada vez
    //   que el sistema se cierra. Sirve para auditoría o para ver
    //   el estado del gimnasio sin abrir el programa.
    //
    // Parámetros:
    //   totalSocios  → cuántos socios hay registrados
    //   morosos      → cuántos tienen cuota pendiente
    //   ingresosMes  → total cobrado en el mes actual
    //   totalIngresos → cantidad de visitas registradas

    public static void guardarEstadisticas(int totalSocios, int morosos,
                                            double ingresosMes, int totalIngresos) {
        // DateTimeFormatter: da formato legible a la fecha y hora
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String ahora = LocalDateTime.now().format(fmt);

        // try-with-resources: cierra el archivo automáticamente al terminar
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO_ESTADISTICAS))) {
            escritor.write("============================================");
            escritor.newLine();
            escritor.write("  ESTADISTICAS DEL SISTEMA - GYM");
            escritor.newLine();
            escritor.write("  Ultima actualizacion: " + ahora);
            escritor.newLine();
            escritor.write("============================================");
            escritor.newLine();
            escritor.write("  Total socios registrados : " + totalSocios);
            escritor.newLine();
            escritor.write("  Socios morosos           : " + morosos);
            escritor.newLine();
            escritor.write("  Socios al dia            : " + (totalSocios - morosos));
            escritor.newLine();
            escritor.write(String.format("  Ingresos del mes         : $%.2f", ingresosMes));
            escritor.newLine();
            escritor.write("  Total visitas registradas: " + totalIngresos);
            escritor.newLine();
            escritor.write("============================================");
            escritor.newLine();
        } catch (IOException e) {
            System.out.println("  [X] No se pudo guardar estadisticas.txt: " + e.getMessage());
        }
    }

    // ----------------------------------------------------------
    // MÉTODO: mostrarEstadisticasGuardadas
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Lee el archivo estadisticas.txt y lo muestra en consola.
    //   Es útil para ver el último resumen sin cargar todo el sistema.

    public static void mostrarEstadisticasGuardadas() {
        if (!archivoExiste(ARCHIVO_ESTADISTICAS)) {
            System.out.println("  [i] No hay estadisticas guardadas aun.");
            return;
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_ESTADISTICAS))) {
            String linea;
            System.out.println();
            // while (mientras): lee líneas hasta llegar al final del archivo (null)
            while ((linea = lector.readLine()) != null) {
                System.out.println("  " + linea);
            }
            System.out.println();
        } catch (IOException e) {
            System.out.println("  [X] No se pudo leer estadisticas.txt: " + e.getMessage());
        }
    }

    // ==========================================================
    // SECCIÓN 6: BACKUP AUTOMÁTICO
    // ==========================================================

    // ----------------------------------------------------------
    // MÉTODO: backupAutomatico (automatic backup = copia de seguridad automática)
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Antes de cerrar el programa, copia los 3 archivos principales
    //   a una carpeta llamada "backup/" con la fecha y hora en el nombre.
    //   Así, si algo se corrompe, siempre hay una copia anterior.
    //
    // Ejemplo de archivo creado:
    //   backup/socios_2026-04-03_15-30.csv
    //   backup/cuotas_2026-04-03_15-30.csv
    //   backup/ingresos_2026-04-03_15-30.csv
    //
    // Concepto: BACKUP (copia de seguridad)
    //   Es una copia de los datos en un lugar separado para prevenir pérdidas.

    public static void backupAutomatico() {
        // Crear la carpeta "backup" si todavía no existe
        // File.mkdirs() (make directories = crear directorios): crea la carpeta y sus padres
        File carpeta = new File(CARPETA_BACKUP);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // Formato de fecha para el nombre del archivo: "2026-04-03_15-30"
        // Usamos guiones en lugar de "/" o ":" porque esos caracteres
        // no son válidos en nombres de archivos en Windows.
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String timestamp = LocalDateTime.now().format(fmt); // timestamp = marca de tiempo

        // Copiar cada archivo al backup
        copiarArchivo(ARCHIVO_SOCIOS,   CARPETA_BACKUP + "/socios_"   + timestamp + ".csv");
        copiarArchivo(ARCHIVO_CUOTAS,   CARPETA_BACKUP + "/cuotas_"   + timestamp + ".csv");
        copiarArchivo(ARCHIVO_INGRESOS, CARPETA_BACKUP + "/ingresos_" + timestamp + ".csv");

        System.out.println("  [OK] Backup guardado: backup/*_" + timestamp + ".csv");
    }

    // ----------------------------------------------------------
    // MÉTODO PRIVADO: copiarArchivo
    // ----------------------------------------------------------
    // ¿Por qué "private" (privado)?
    //   Solo ArchivoManager lo necesita. Es un detalle interno.
    //   Afuera solo se ve backupAutomatico().
    //
    // ¿Qué hace?
    //   Lee el archivo de origen línea por línea con BufferedReader
    //   y escribe cada línea en el archivo de destino con BufferedWriter.
    //   Si el archivo de origen no existe, avisa pero no detiene el programa.

    private static void copiarArchivo(String origen, String destino) {
        // Verificar que el archivo original existe antes de intentar copiarlo
        if (!archivoExiste(origen)) {
            // No es un error grave: puede que todavía no se haya creado ningún dato
            return;
        }

        // try-with-resources con DOS recursos: abre lector Y escritor juntos
        // Java los cierra automáticamente en orden inverso al terminar.
        try (BufferedReader lector  = new BufferedReader(new FileReader(origen));
             BufferedWriter escritor = new BufferedWriter(new FileWriter(destino))) {

            String linea;
            // Copiamos línea por línea: read (leer) → write (escribir)
            while ((linea = lector.readLine()) != null) {
                escritor.write(linea);
                escritor.newLine(); // newLine() (nueva línea): equivale a presionar Enter
            }

        } catch (IOException e) {
            System.out.println("  [X] No se pudo copiar " + origen + ": " + e.getMessage());
        }
    }

    // ----------------------------------------------------------
    // MÉTODO: limpiarBackupsAntiguos
    // ----------------------------------------------------------
    // ¿Para qué sirve?
    //   Si el programa se usa mucho, la carpeta backup puede crecer.
    //   Este método elimina los backups que tienen más de 'diasMaximos' días.
    //   Solo eliminamos archivos del sistema, nunca datos principales.
    //
    // File.listFiles() (listar archivos): devuelve un array con todos los archivos de la carpeta
    // File.lastModified() (ultima modificación): fecha Unix del archivo en milisegundos
    // System.currentTimeMillis() (milisegundos actuales): tiempo del sistema ahora mismo

    public static void limpiarBackupsAntiguos(int diasMaximos) {
        File carpeta = new File(CARPETA_BACKUP);
        if (!carpeta.exists()) return;

        // Convertimos los días a milisegundos para comparar con lastModified()
        // 1 día = 24 horas × 60 min × 60 seg × 1000 milisegundos
        long limiteMilis = (long) diasMaximos * 24 * 60 * 60 * 1000;
        long ahora = System.currentTimeMillis();

        File[] archivos = carpeta.listFiles(); // listFiles(): lista todos los archivos
        if (archivos == null) return;

        int eliminados = 0;
        for (File archivo : archivos) {
            // Si el archivo es más viejo que el límite, lo borramos
            if (ahora - archivo.lastModified() > limiteMilis) {
                archivo.delete(); // .delete() (eliminar): borra el archivo del disco
                eliminados++;
            }
        }

        if (eliminados > 0) {
            System.out.println("  [i] Se eliminaron " + eliminados + " backup(s) antiguos.");
        }
    }
}
