// ============================================================
// PAQUETE: persistence (persistencia)
// ============================================================
// GestorCuotasCSV se encarga ÚNICAMENTE de leer y escribir el
// archivo "cuotas.csv". No sabe nada del menú ni de la lógica.
//
// Mismo patrón que GestorCSV pero para cuotas de pago.
// ============================================================

package persistence;

import model.RegistroCuota;

import java.io.BufferedReader;   // BufferedReader: lee archivo línea por línea eficientemente
import java.io.BufferedWriter;   // BufferedWriter: escribe en archivo de forma eficiente
import java.io.FileReader;       // FileReader: abre el archivo de texto para leer
import java.io.FileWriter;       // FileWriter: abre el archivo de texto para escribir
import java.io.IOException;      // IOException: error de entrada/salida (disco, permisos, etc.)
import java.time.LocalDate;      // LocalDate: tipo de dato para fechas sin hora
import java.util.ArrayList;

public class GestorCuotasCSV {

    // Nombre del archivo y encabezado (cabecera) de las columnas
    private static final String ARCHIVO  = "cuotas.csv";
    private static final String CABECERA = "dni,nombreSocio,fechaPago,monto,metodoPago,estado,proximoVencimiento,observaciones";

    // ==========================================================
    // MÉTODO: guardar
    // ==========================================================
    // Recibe la lista completa de cuotas y sobreescribe el archivo.
    // Igual que GestorCSV: guardamos TODO cada vez (simple y seguro).

    public static void guardar(ArrayList<RegistroCuota> cuotas) {
        // try-with-resources: cierra el archivo automáticamente al terminar
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO))) {
            escritor.write(CABECERA);
            escritor.newLine();
            for (RegistroCuota cuota : cuotas) {
                // cuota.toCsv() devuelve la línea con los datos separados por comas
                escritor.write(cuota.toCsv());
                escritor.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [X] Error al guardar cuotas.csv: " + e.getMessage());
        }
    }

    // ==========================================================
    // MÉTODO: cargar
    // ==========================================================
    // Lee el archivo y recrea los objetos RegistroCuota en memoria.
    // Si el archivo no existe todavía, devuelve lista vacía sin error.

    public static ArrayList<RegistroCuota> cargar() {
        ArrayList<RegistroCuota> lista = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
            lector.readLine(); // Saltear la línea de cabecera (encabezado)

            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                // split con LÍMITE 8: divide en máximo 8 partes.
                // Si "observaciones" tiene espacios o caracteres especiales,
                // al ser la última parte, los captura todos juntos.
                // NOTA: observaciones NO puede contener comas (limitación del formato CSV).
                String[] p = linea.split(",", 8);
                if (p.length < 7) continue; // línea incompleta: saltear

                String    dni         = p[0].trim();
                String    nombre      = p[1].trim();
                // LocalDate.parse(): convierte el texto "2026-04-03" al tipo LocalDate
                LocalDate fechaPago   = LocalDate.parse(p[2].trim());
                double    monto       = Double.parseDouble(p[3].trim());
                String    metodo      = p[4].trim();
                String    estado      = p[5].trim();
                LocalDate vencimiento = LocalDate.parse(p[6].trim());
                // Si existe la columna 8 (índice 7), usarla. Si no, texto por defecto.
                String    obs         = (p.length == 8) ? p[7].trim() : "Sin observaciones";

                // Usamos el constructor de "restaurar desde CSV" (con todos los datos ya conocidos)
                lista.add(new RegistroCuota(dni, nombre, fechaPago, monto, metodo, estado, vencimiento, obs));
            }

        } catch (java.io.FileNotFoundException e) {
            // Completamente normal la primera vez: el archivo todavía no existe
        } catch (IOException | NumberFormatException e) {
            System.out.println("  [X] Error al cargar cuotas.csv: " + e.getMessage());
        }

        return lista;
    }
}
