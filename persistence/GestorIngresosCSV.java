// ============================================================
// PAQUETE: persistence (persistencia)
// ============================================================
// GestorIngresosCSV guarda y carga el historial de ingresos
// al gimnasio desde el archivo "ingresos.csv".
//
// Cada línea del archivo representa una visita al gimnasio:
//   12345678,Juan Perez,2026-04-03T15:30:00
// ============================================================

package persistence;

import model.RegistroIngreso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;   // LocalDateTime: fecha + hora juntas
import java.util.ArrayList;

public class GestorIngresosCSV {

    private static final String ARCHIVO  = "ingresos.csv";
    private static final String CABECERA = "dni,nombreSocio,fechaHora";

    // ==========================================================
    // MÉTODO: guardar
    // ==========================================================

    public static void guardar(ArrayList<RegistroIngreso> ingresos) {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO))) {
            escritor.write(CABECERA);
            escritor.newLine();
            for (RegistroIngreso ing : ingresos) {
                escritor.write(ing.toCsv());
                escritor.newLine();
            }
        } catch (IOException e) {
            System.out.println("  [X] Error al guardar ingresos.csv: " + e.getMessage());
        }
    }

    // ==========================================================
    // MÉTODO: cargar
    // ==========================================================

    public static ArrayList<RegistroIngreso> cargar() {
        ArrayList<RegistroIngreso> lista = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {
            lector.readLine(); // saltear cabecera

            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                // split con límite 3: divide en máximo 3 partes.
                // El campo fechaHora tiene formato ISO "2026-04-03T15:30:00"
                // que NO contiene comas, así que es seguro.
                String[] p = linea.split(",", 3);
                if (p.length < 3) continue;

                String        dni       = p[0].trim();
                String        nombre    = p[1].trim();
                // LocalDateTime.parse(): convierte "2026-04-03T15:30:00" a LocalDateTime
                // El formato ISO es el que genera LocalDateTime.toString() al guardar
                LocalDateTime fechaHora = LocalDateTime.parse(p[2].trim());

                lista.add(new RegistroIngreso(dni, nombre, fechaHora));
            }

        } catch (java.io.FileNotFoundException e) {
            // Normal la primera vez: el archivo todavía no existe
        } catch (IOException e) {
            System.out.println("  [X] Error al cargar ingresos.csv: " + e.getMessage());
        }

        return lista;
    }
}
