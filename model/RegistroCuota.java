// ============================================================
// PAQUETE: model (modelo)
// ============================================================
// RegistroCuota representa el comprobante detallado de un pago
// de cuota. Versión 2: incluye recargos por mora, descuentos
// promocionales y soporte de pagos parciales.
//
// CAMPOS NUEVOS vs versión anterior:
//   recargo      → importe adicional por mora (ej: 10% de la cuota)
//   descuento    → importe reducido por promo (ej: 15% de descuento)
//   montoPagado  → lo que realmente abonó el socio (puede ser parcial)
//   tipo         → "MENSUAL", "ANUAL" o "PARCIAL"
// ============================================================

package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RegistroCuota {

    // ----------------------------------------------------------
    // ATRIBUTOS
    // ----------------------------------------------------------

    private String    dni;
    private String    nombreSocio;
    private LocalDate fechaPago;
    private double    monto;          // Monto BASE de la cuota (sin recargo ni descuento)
    private double    recargo;        // Importe extra por mora: ej. 500.00 (no porcentaje, sino el valor ya calculado)
    private double    descuento;      // Importe descontado: ej. 750.00
    private double    montoPagado;    // Lo que realmente abonó: puede ser menor que monto+recargo-descuento
    private String    metodoPago;     // "EFECTIVO", "TRANSFERENCIA" o "TARJETA"
    private String    estado;         // "PAGADO", "PARCIAL" o "PENDIENTE"
    private LocalDate proximoVencimiento;
    private String    tipo;           // "MENSUAL", "ANUAL" o "PARCIAL"
    private String    observaciones;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ----------------------------------------------------------
    // CONSTRUCTOR PRINCIPAL — para pagos completos nuevos
    // ----------------------------------------------------------
    // recargo y descuento = 0 por defecto (sin mora ni promo).
    // montoPagado = monto (pago completo).

    public RegistroCuota(String dni, String nombreSocio, double monto,
                         String metodoPago, String observaciones) {
        this.dni                = dni;
        this.nombreSocio        = nombreSocio;
        this.fechaPago          = LocalDate.now();
        this.monto              = monto;
        this.recargo            = 0.0;
        this.descuento          = 0.0;
        this.montoPagado        = monto;          // pago completo: abonó todo el monto base
        this.metodoPago         = metodoPago.toUpperCase();
        this.estado             = "PAGADO";
        this.proximoVencimiento = LocalDate.now().plusMonths(1);
        this.tipo               = "MENSUAL";
        this.observaciones = (observaciones == null || observaciones.trim().isEmpty())
                             ? "Sin observaciones" : observaciones.trim();
    }

    // ----------------------------------------------------------
    // CONSTRUCTOR PARA RESTAURAR DESDE ARCHIVO CSV
    // ----------------------------------------------------------
    // Recibe TODOS los campos ya parseados (leídos del archivo).

    public RegistroCuota(String dni, String nombreSocio, LocalDate fechaPago,
                         double monto, double recargo, double descuento,
                         double montoPagado, String metodoPago, String estado,
                         LocalDate proximoVencimiento, String tipo, String observaciones) {
        this.dni                = dni;
        this.nombreSocio        = nombreSocio;
        this.fechaPago          = fechaPago;
        this.monto              = monto;
        this.recargo            = recargo;
        this.descuento          = descuento;
        this.montoPagado        = montoPagado;
        this.metodoPago         = metodoPago;
        this.estado             = estado;
        this.proximoVencimiento = proximoVencimiento;
        this.tipo               = tipo;
        this.observaciones      = observaciones;
    }

    // ----------------------------------------------------------
    // GETTERS
    // ----------------------------------------------------------

    public String    getDni()                { return dni; }
    public String    getNombreSocio()        { return nombreSocio; }
    public LocalDate getFechaPago()          { return fechaPago; }
    public double    getMonto()              { return monto; }
    public double    getRecargo()            { return recargo; }
    public double    getDescuento()          { return descuento; }
    public double    getMontoPagado()        { return montoPagado; }
    public String    getMetodoPago()         { return metodoPago; }
    public String    getEstado()             { return estado; }
    public LocalDate getProximoVencimiento() { return proximoVencimiento; }
    public String    getTipo()               { return tipo; }
    public String    getObservaciones()      { return observaciones; }

    // ----------------------------------------------------------
    // SETTERS SELECTIVOS
    // ----------------------------------------------------------
    // Solo los campos que tienen sentido modificar después de creado.

    public void setRecargo(double recargo)       { this.recargo = recargo; }
    public void setDescuento(double descuento)   { this.descuento = descuento; }
    public void setMontoPagado(double mp)        { this.montoPagado = mp; }
    public void setEstado(String estado)         { this.estado = estado; }
    public void setObservaciones(String obs)     { this.observaciones = obs; }

    // ----------------------------------------------------------
    // MÉTODO: montoTotal()
    // ----------------------------------------------------------
    // Calcula el importe final que el socio debería pagar:
    //   monto base + recargo por mora - descuento promocional
    // Es el TOTAL REAL de la deuda.

    public double montoTotal() {
        return monto + recargo - descuento;
    }

    // ----------------------------------------------------------
    // MÉTODO: saldoPendiente()
    // ----------------------------------------------------------
    // Si hizo un pago parcial, ¿cuánto le falta pagar?
    //   total - lo que ya entregó

    public double saldoPendiente() {
        return montoTotal() - montoPagado;
    }

    // ----------------------------------------------------------
    // MÉTODO: estaVencida()
    // ----------------------------------------------------------

    public boolean estaVencida() {
        return LocalDate.now().isAfter(proximoVencimiento);
    }

    // ----------------------------------------------------------
    // MÉTODO: toCsv()
    // ----------------------------------------------------------
    // Formato nuevo (12 columnas):
    //   dni,nombreSocio,fechaPago,monto,recargo,descuento,montoPagado,
    //   metodoPago,estado,proximoVencimiento,tipo,observaciones

    public String toCsv() {
        return dni + "," + nombreSocio + "," + fechaPago
             + "," + monto + "," + recargo + "," + descuento
             + "," + montoPagado + "," + metodoPago + "," + estado
             + "," + proximoVencimiento + "," + tipo + "," + observaciones;
    }

    // ----------------------------------------------------------
    // MÉTODO: toString()
    // ----------------------------------------------------------

    @Override
    public String toString() {
        String vigencia = estaVencida() ? "[VENCIDA!!]" : "[VIGENTE]";
        StringBuilder sb = new StringBuilder();
        sb.append("  Socio    : ").append(nombreSocio).append(" (DNI: ").append(dni).append(")\n");
        sb.append("  Tipo     : ").append(tipo).append("\n");
        sb.append("  Pago     : ").append(fechaPago.format(FORMATO_FECHA)).append("\n");
        sb.append("  Monto    : $").append(String.format("%.2f", monto)).append("\n");
        if (recargo > 0)   sb.append("  Recargo  : +$").append(String.format("%.2f", recargo)).append(" (mora)\n");
        if (descuento > 0) sb.append("  Descuento: -$").append(String.format("%.2f", descuento)).append(" (promo)\n");
        sb.append("  Total    : $").append(String.format("%.2f", montoTotal())).append("\n");
        sb.append("  Pagado   : $").append(String.format("%.2f", montoPagado));
        if (saldoPendiente() > 0.01) sb.append("  [Saldo: $").append(String.format("%.2f", saldoPendiente())).append("]");
        sb.append("\n");
        sb.append("  Metodo   : ").append(metodoPago).append("\n");
        sb.append("  Estado   : ").append(estado).append("\n");
        sb.append("  Vence    : ").append(proximoVencimiento.format(FORMATO_FECHA)).append("  ").append(vigencia).append("\n");
        sb.append("  Obs.     : ").append(observaciones);
        return sb.toString();
    }
}
