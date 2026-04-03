// ============================================================
// PAQUETE: model (modelo)
// ============================================================
// RegistroIngreso representa cada vez que un socio entra al gimnasio.
// Al ingresar el DNI en la puerta, el sistema crea un RegistroIngreso
// con la fecha y hora exacta del momento.
//
// ¿Por qué una clase aparte?
//   El campo "asistencia" en Socio solo guarda un contador (número total).
//   RegistroIngreso guarda CUÁNDO fue cada visita: fecha y hora precisa.
//   Eso permite detectar ausencias, mostrar historial y hacer estadísticas.
// ============================================================

package model;

import java.time.LocalDateTime;                    // LocalDateTime: fecha + hora juntas ("2026-04-03T15:30")
import java.time.format.DateTimeFormatter;         // DateTimeFormatter: formatea fechas a texto legible

public class RegistroIngreso {

    // ----------------------------------------------------------
    // ATRIBUTOS
    // ----------------------------------------------------------

    private String        dni;         // DNI del socio que ingresó
    private String        nombreSocio; // Nombre del socio (guardado para mostrar sin buscar en lista)
    private LocalDateTime fechaHora;   // Fecha Y hora exacta del ingreso

    // Formato para mostrar: "03/04/2026 15:30"
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ----------------------------------------------------------
    // CONSTRUCTOR PRINCIPAL (para ingresos registrados en tiempo real)
    // ----------------------------------------------------------
    // Se usa cuando el socio pasa su DNI por el lector.
    // LocalDateTime.now() captura el instante exacto del ingreso.

    public RegistroIngreso(String dni, String nombreSocio) {
        this.dni         = dni;
        this.nombreSocio = nombreSocio;
        this.fechaHora   = LocalDateTime.now(); // Captura el momento EXACTO: fecha y hora actuales
    }

    // ----------------------------------------------------------
    // CONSTRUCTOR PARA RESTAURAR DESDE ARCHIVO CSV
    // ----------------------------------------------------------
    // Se usa cuando GestorIngresosCSV.cargar() recrea los objetos
    // desde el archivo en disco.

    public RegistroIngreso(String dni, String nombreSocio, LocalDateTime fechaHora) {
        this.dni         = dni;
        this.nombreSocio = nombreSocio;
        this.fechaHora   = fechaHora;
    }

    // ----------------------------------------------------------
    // GETTERS
    // ----------------------------------------------------------

    public String        getDni()         { return dni; }
    public String        getNombreSocio() { return nombreSocio; }
    public LocalDateTime getFechaHora()   { return fechaHora; }

    // ----------------------------------------------------------
    // MÉTODO: toCsv()
    // ----------------------------------------------------------
    // LocalDateTime.toString() genera formato ISO "2026-04-03T15:30:00".
    // Este formato no contiene comas, por eso es seguro en CSV.

    public String toCsv() {
        return dni + "," + nombreSocio + "," + fechaHora;
    }

    // ----------------------------------------------------------
    // MÉTODO: toString() — texto legible para mostrar en consola
    // ----------------------------------------------------------

    @Override
    public String toString() {
        return "  DNI    : " + dni
             + "  |  Nombre : " + nombreSocio
             + "  |  Ingreso: " + fechaHora.format(FORMATO);
    }
}
