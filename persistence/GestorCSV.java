// ============================================================
// PAQUETE: persistence (persistencia)
// Se encarga SOLO de leer y escribir el archivo CSV.
// No sabe nada del menú ni de la lógica del negocio.
// ============================================================

package persistence;

import model.Socio;

import java.io.BufferedReader;   // BufferedReader (lector con buffer): lee el archivo línea a línea
import java.io.BufferedWriter;   // BufferedWriter (escritor con buffer): escribe en el archivo
import java.io.FileReader;       // FileReader (lector de archivo): abre el archivo para leer
import java.io.FileWriter;       // FileWriter (escritor de archivo): abre el archivo para escribir
import java.io.IOException;      // IOException (excepcion de entrada/salida): error al leer/escribir
import java.util.ArrayList;

// ============================================================
// CLASE: GestorCSV
// ============================================================
// ¿Por qué existe esta clase?
//   Separa la responsabilidad de manejar archivos del resto del sistema.
//   Gimnasio no necesita saber cómo se guarda un socio en disco:
//   solo le dice a GestorCSV "guardá esta lista" y listo.
//
// Concepto nuevo: MANEJO DE EXCEPCIONES (Exception Handling)
//   Cuando trabajamos con archivos, pueden pasar errores fuera de
//   nuestro control (disco lleno, archivo bloqueado, sin permisos).
//   Java nos obliga a manejar esos casos con try-catch.
//
//   try   (intentar): "intentá ejecutar este bloque"
//   catch (atrapar) : "si algo falla, ejecutá este bloque de error"
//   finally         : "ejecutá esto siempre, haya error o no"
// ============================================================

public class GestorCSV {

    // ----------------------------------------------------------
    // CONSTANTE: nombre del archivo CSV
    // ----------------------------------------------------------
    // "static final" (estática final) = CONSTANTE:
    //   static (estático): pertenece a la clase, no a un objeto concreto
    //   final  (final/inmutable): no se puede cambiar una vez asignada
    // Por convención, las constantes se escriben en MAYÚSCULAS con guiones bajos.
    // Es equivalente a una constante en PSeInt: DEFINIR NOMBRE = valor

    private static final String ARCHIVO        = "socios.csv";
    private static final String CABECERA       = "id,nombre,edad,plan,asistencia,cuotaAlDia";
    private static final String SEPARADOR      = ",";
    private static final int    COLUMNAS_CSV   = 6; // Cuántos campos tiene cada fila

    // ==========================================================
    // MÉTODO: guardar
    // ==========================================================
    // Recibe la lista completa de socios y la escribe en el archivo.
    // Sobreescribe el archivo entero cada vez (es lo más simple y seguro).

    public static void guardar(ArrayList<Socio> socios) {
        // try-with-resources (intentar con recursos):
        //   Variante especial de try que cierra el archivo automáticamente
        //   cuando el bloque termina, haya error o no.
        //   Es más seguro porque evita que el archivo quede "abierto" por error.
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ARCHIVO))) {

            // Escribir la línea de cabecera (encabezado de las columnas)
            escritor.write(CABECERA);
            escritor.newLine(); // newLine() (nueva línea): equivale a presionar Enter

            // Recorrer todos los socios y escribir una línea por cada uno
            for (Socio socio : socios) {
                // Construimos la línea CSV uniendo los datos con el separador ","
                // Nota: si el nombre tuviera una coma, habría que escaparla.
                // Para este proyecto universitario usamos nombres simples.
                String linea = socio.getId()
                        + SEPARADOR + socio.getNombre()
                        + SEPARADOR + socio.getEdad()
                        + SEPARADOR + socio.getPlan().name() // .name() convierte Plan.PREMIUM → "PREMIUM"
                        + SEPARADOR + socio.getAsistencia()
                        + SEPARADOR + socio.isCuotaAlDia(); // true o false como texto

                escritor.write(linea);
                escritor.newLine();
            }

        } catch (IOException error) {
            // IOException: error de entrada/salida al intentar escribir el archivo
            System.out.println("  [X] No se pudo guardar el archivo: " + error.getMessage());
        }
    }

    // ==========================================================
    // MÉTODO: cargar
    // ==========================================================
    // Lee el archivo CSV y devuelve una lista de objetos Socio.
    // Si el archivo no existe todavía, devuelve una lista vacía sin error.

    public static ArrayList<Socio> cargar() {
        ArrayList<Socio> lista = new ArrayList<>();

        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO))) {

            String linea;

            // .readLine() (leer línea): devuelve la siguiente línea del archivo,
            // o null cuando llega al final.
            // La primera línea es la cabecera, la salteamos con una lectura previa.
            lector.readLine(); // Saltea la línea "id,nombre,edad,plan,asistencia,cuotaAlDia"

            // while (mientras): sigue leyendo mientras haya líneas en el archivo
            while ((linea = lector.readLine()) != null) {

                // Ignorar líneas vacías o con solo espacios
                if (linea.trim().isEmpty()) continue; // continue (continuar): va a la siguiente iteración

                // .split() (dividir): corta el String en partes usando el separador
                // "1,Juan,25,PREMIUM,12,true".split(",") → ["1","Juan","25","PREMIUM","12","true"]
                String[] partes = linea.split(SEPARADOR);

                // Validación: si la línea no tiene exactamente 6 columnas, la saltea
                if (partes.length != COLUMNAS_CSV) continue;

                // Convertir cada parte al tipo que corresponde:
                // Integer.parseInt() (parsear a entero): convierte "25" → 25
                // Boolean.parseBoolean() (parsear a booleano): convierte "true" → true
                int     id          = Integer.parseInt(partes[0].trim());
                String  nombre      = partes[1].trim();
                int     edad        = Integer.parseInt(partes[2].trim());
                String  plan        = partes[3].trim();
                int     asistencia  = Integer.parseInt(partes[4].trim());
                boolean cuotaAlDia  = Boolean.parseBoolean(partes[5].trim());

                // Creamos el objeto Socio usando el constructor
                Socio socio = new Socio(id, nombre, edad, plan);

                // El constructor inicializa asistencia en 0 y cuota en true.
                // Necesitamos restaurar los valores reales del archivo:
                for (int i = 0; i < asistencia; i++) {
                    socio.incrementarAsistencia(); // Restauramos la asistencia real
                }
                socio.setCuotaAlDia(cuotaAlDia); // Restauramos el estado de cuota real

                lista.add(socio);
            }

        } catch (java.io.FileNotFoundException e) {
            // FileNotFoundException (archivo no encontrado): es normal la primera vez
            // No mostramos error: simplemente devolvemos la lista vacía.
            System.out.println("  [i] No se encontro archivo de datos. Comenzando con lista vacia.");

        } catch (IOException error) {
            System.out.println("  [X] Error al leer el archivo: " + error.getMessage());

        } catch (NumberFormatException error) {
            // NumberFormatException: si un campo que debería ser número tiene texto inválido
            System.out.println("  [X] Datos corruptos en el archivo CSV. Algunas filas no se cargaron.");
        }

        return lista;
    }

    // ==========================================================
    // MÉTODO: obtenerMaxId
    // ==========================================================
    // Busca el ID más alto en la lista cargada para que el siguiente
    // socio nuevo tenga un ID que no repita a ninguno existente.

    public static int obtenerMaxId(ArrayList<Socio> socios) {
        int maxId = 0;
        for (Socio socio : socios) {
            if (socio.getId() > maxId) {
                maxId = socio.getId(); // Actualizamos si encontramos uno mayor
            }
        }
        return maxId;
    }
}
