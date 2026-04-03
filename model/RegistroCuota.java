// ============================================================
// PAQUETE: model (modelo)
// ============================================================
// RegistroCuota representa el comprobante detallado de un pago
// de cuota. Cada vez que un socio paga, se crea un objeto de
// esta clase con toda la información del pago.
//
// ¿Por qué una clase aparte?
//   Un simple "true/false" (cuotaAlDia en Socio) no basta para
//   un sistema real. Necesitamos saber: ¿cuándo pagó? ¿cuánto?
//   ¿cómo? ¿cuándo vence el próximo pago? Esta clase guarda todo.
// ============================================================

package model;

import java.time.LocalDate;                        // LocalDate (fecha local): solo día/mes/año, sin hora
import java.time.format.DateTimeFormatter;         // DateTimeFormatter: formatea fechas a texto legible

public class RegistroCuota {

    // ----------------------------------------------------------
    // ATRIBUTOS (Attributes) — datos de cada registro de cuota
    // ----------------------------------------------------------

    private String    dni;                 // DNI (documento) del socio que pagó
    private String    nombreSocio;         // Nombre del socio (para mostrar sin buscar en la lista)
    private LocalDate fechaPago;           // Fecha en que se realizó el pago
    private double    monto;              // Importe cobrado en la cuota (ej: 5000.00)
    private String    metodoPago;         // "EFECTIVO", "TRANSFERENCIA" o "TARJETA"
    private String    estado;             // "PAGADO" o "PENDIENTE"
    private LocalDate proximoVencimiento; // Fecha en que vence el próximo pago
    private String    observaciones;      // Notas adicionales opcionales del operador

    // DateTimeFormatter: convierte fecha a texto con formato "15/04/2026"
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ----------------------------------------------------------
    // CONSTRUCTOR PRINCIPAL (para nuevos pagos registrados ahora)
    // ----------------------------------------------------------
    // Se ejecuta cuando el operador registra un pago NUEVO en el momento.
    // El sistema calcula automáticamente:
    //   → fechaPago         = hoy
    //   → proximoVencimiento = hoy + 1 mes

    public RegistroCuota(String dni, String nombreSocio, double monto,
                         String metodoPago, String observaciones) {
        this.dni                = dni;
        this.nombreSocio        = nombreSocio;
        this.fechaPago          = LocalDate.now();               // LocalDate.now(): captura la fecha de hoy
        this.monto              = monto;
        this.metodoPago         = metodoPago.toUpperCase();      // siempre en MAYÚSCULAS para consistencia
        this.estado             = "PAGADO";                      // al registrar un pago, queda pagado
        this.proximoVencimiento = LocalDate.now().plusMonths(1); // plusMonths(1): suma exactamente 1 mes
        // Si no hay observaciones, usamos un texto por defecto legible
        this.observaciones = (observaciones == null || observaciones.trim().isEmpty())
                             ? "Sin observaciones" : observaciones.trim();
    }

    // ----------------------------------------------------------
    // CONSTRUCTOR PARA RESTAURAR DESDE ARCHIVO CSV
    // ----------------------------------------------------------
    // Se usa cuando GestorCuotasCSV.cargar() lee el archivo y recrea
    // los objetos en memoria. Aquí ya tenemos TODOS los datos guardados.

    public RegistroCuota(String dni, String nombreSocio, LocalDate fechaPago,
                         double monto, String metodoPago, String estado,
                         LocalDate proximoVencimiento, String observaciones) {
        this.dni                = dni;
        this.nombreSocio        = nombreSocio;
        this.fechaPago          = fechaPago;
        this.monto              = monto;
        this.metodoPago         = metodoPago;
        this.estado             = estado;
        this.proximoVencimiento = proximoVencimiento;
        this.observaciones      = observaciones;
    }

    // ----------------------------------------------------------
    // GETTERS (Obtenedores) — la única forma de leer los datos
    // desde fuera de esta clase (porque son private)
    // ----------------------------------------------------------

    public String    getDni()                { return dni; }
    public String    getNombreSocio()        { return nombreSocio; }
    public LocalDate getFechaPago()          { return fechaPago; }
    public double    getMonto()              { return monto; }
    public String    getMetodoPago()         { return metodoPago; }
    public String    getEstado()             { return estado; }
    public LocalDate getProximoVencimiento() { return proximoVencimiento; }
    public String    getObservaciones()      { return observaciones; }

    // ----------------------------------------------------------
    // MÉTODO: estaVencida()
    // ----------------------------------------------------------
    // Pregunta: ¿Ya pasó la fecha de vencimiento?
    //   LocalDate.now()        = hoy
    //   .isAfter(fecha)        = ¿hoy es DESPUÉS de esa fecha?
    //   Si es true → cuota vencida (el socio debe pagar)

    public boolean estaVencida() {
        return LocalDate.now().isAfter(proximoVencimiento);
    }

    // ----------------------------------------------------------
    // MÉTODO: toCsv() (To CSV = convertir a línea de archivo CSV)
    // ----------------------------------------------------------
    // Genera la línea de texto que se guarda en cuotas.csv.
    // LocalDate se guarda en formato ISO automáticamente: "2026-04-15"
    // NOTA: el campo "observaciones" no puede contener comas (,)
    //        porque se usa como separador en el archivo.

    public String toCsv() {
        return dni + "," + nombreSocio + "," + fechaPago
             + "," + monto + "," + metodoPago + "," + estado
             + "," + proximoVencimiento + "," + observaciones;
    }

    // ----------------------------------------------------------
    // MÉTODO: toString() — texto legible para mostrar en consola
    // ----------------------------------------------------------

    @Override
    public String toString() {
        // Operador ternario: si estaVencida() es true → "[VENCIDA]", si no → "[VIGENTE]"
        String vigencia = estaVencida() ? "[VENCIDA!!]" : "[VIGENTE]";
        return "  Socio    : " + nombreSocio + " (DNI: " + dni + ")\n"
             + "  Pago     : " + fechaPago.format(FORMATO_FECHA) + "\n"
             + "  Monto    : $" + String.format("%.2f", monto) + "\n"
             + "  Metodo   : " + metodoPago + "\n"
             + "  Estado   : " + estado + "\n"
             + "  Vence    : " + proximoVencimiento.format(FORMATO_FECHA) + "  " + vigencia + "\n"
             + "  Obs.     : " + observaciones;
    }
}
