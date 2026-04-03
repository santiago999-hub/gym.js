// ============================================================
// PAQUETE: persistence (persistencia)
// ============================================================
// GestorCuotasCSV se encarga ÚNICAMENTE de leer y escribir el
// archivo "cuotas.csv". No sabe nada del menú ni de la lógica.
//
// Formato CSV v2 (12 columnas):
//   dni,nombreSocio,fechaPago,monto,recargo,descuento,montoPagado,
//   metodoPago,estado,proximoVencimiento,tipo,observaciones
//
// Compatibilidad hacia atrás: si el archivo tiene el formato v1
// (8 columnas, sin recargo/descuento/montoPagado/tipo), los nuevos
// campos se inicializan con valores por defecto seguros.
// ============================================================

package persistence;

import model.RegistroCuota;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class GestorCuotasCSV {

    private static final String ARCHIVO   = "cuotas.csv";
    private static final String CABECERA  =
        "dni,nombreSocio,fechaPago,monto,recargo,descuento,montoPagado," +
        "metodoPago,estado,proximoVencimiento,tipo,observaciones";
    private static final int COLS_NUEVO = 12; // formato v2
    private static final int COLS_VIEJO = 8;  // formato v1 (sin recargo/descuento/montoPagado/tipo)

    // ==========================================================
    // MÉTODO: guardar
    // ==========================================================

    public static void guardar(ArrayList<RegistroCuota> cuotas) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO))) {
            escritor.write(CABECERA);
            escritor.newLine();
            for (RegistroCuota cuota : cuotas) {
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

    public static ArrayList<RegistroCuota> cargar() {
        ArrayList<RegistroCuota> lista = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
            lector.readLine(); // saltear cabecera

            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                // Dividimos con límite 12 para proteger el campo "observaciones"
                // si contiene espacios. Las comas dentro de él romperían el parseo.
                String[] p = linea.split(",", COLS_NUEVO);

                if (p.length == COLS_VIEJO) {
                    // === FORMATO VIEJO (v1): sin recargo, descuento, montoPagado, tipo ===
                    // Columnas: dni,nombreSocio,fechaPago,monto,metodoPago,estado,proximoVencimiento,observaciones
                    String    dni         = p[0].trim();
                    String    nombre      = p[1].trim();
                    LocalDate fechaPago   = LocalDate.parse(p[2].trim());
                    double    monto       = Double.parseDouble(p[3].trim());
                    String    metodo      = p[4].trim();
                    String    estado      = p[5].trim();
                    LocalDate vencimiento = LocalDate.parse(p[6].trim());
                    String    obs         = p[7].trim();
                    // Valores por defecto para los campos nuevos:
                    lista.add(new RegistroCuota(dni, nombre, fechaPago, monto,
                                                0.0, 0.0, monto,
                                                metodo, estado, vencimiento,
                                                "MENSUAL", obs));

                } else if (p.length >= COLS_NUEVO) {
                    // === FORMATO NUEVO (v2): todos los campos presentes ===
                    String    dni         = p[0].trim();
                    String    nombre      = p[1].trim();
                    LocalDate fechaPago   = LocalDate.parse(p[2].trim());
                    double    monto       = Double.parseDouble(p[3].trim());
                    double    recargo     = Double.parseDouble(p[4].trim());
                    double    descuento   = Double.parseDouble(p[5].trim());
                    double    montoPagado = Double.parseDouble(p[6].trim());
                    String    metodo      = p[7].trim();
                    String    estado      = p[8].trim();
                    LocalDate vencimiento = LocalDate.parse(p[9].trim());
                    String    tipo        = p[10].trim();
                    String    obs         = p[11].trim();
                    lista.add(new RegistroCuota(dni, nombre, fechaPago, monto,
                                                recargo, descuento, montoPagado,
                                                metodo, estado, vencimiento,
                                                tipo, obs));
                }
                // Si la línea tiene un número de columnas diferente, la saltamos silenciosamente
            }

        } catch (java.io.FileNotFoundException e) {
            // Normal la primera vez: el archivo todavía no existe
        } catch (IOException | NumberFormatException e) {
            System.out.println("  [X] Error al cargar cuotas.csv: " + e.getMessage());
        }

        return lista;
    }
}
