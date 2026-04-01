// ============================================================
// CLASE: Socio
// ARCHIVO: Socio.java
// ============================================================
// ¿Por qué existe esta clase?
//   En POO (Programación Orientada a Objetos), cada "cosa del mundo real"
//   se convierte en una clase. Un Socio es una ENTIDAD del negocio:
//   tiene sus propios datos (atributos) y comportamientos (métodos).
//   Esta clase es el "molde" a partir del cual se crean todos los socios.
//
// Concepto PSeInt → Java:
//   Registro/estructura  →  Clase con atributos
//   Variable            →  Atributo (attribute)
//   Procedimiento       →  Método (method)
// ============================================================

public class Socio {

    // ----------------------------------------------------------
    // ATRIBUTOS (Attributes) — las características de cada socio
    // ----------------------------------------------------------
    // "private" (privado): solo esta clase puede acceder directamente
    // a estos datos. Esto se llama ENCAPSULAMIENTO (Encapsulation).
    // La idea es proteger los datos de modificaciones accidentales.

    private int id;              // Número único que identifica al socio
    private String nombre;       // Nombre completo del socio
    private int edad;            // Edad del socio en años
    private String plan;         // Plan contratado: "BASICO", "INTERMEDIO" o "PREMIUM"
    private int asistencia;      // Cantidad de veces que asistió al gimnasio
    private boolean cuotaAlDia;  // boolean (booleano): true = pagó / false = debe
    private String rutina;       // Descripción de la rutina asignada según el plan

    // ----------------------------------------------------------
    // CONSTRUCTOR (Constructor)
    // ----------------------------------------------------------
    // Es el método especial que se ejecuta cuando escribimos: new Socio(...)
    // Es como el "formulario de inscripción": recibe los datos iniciales
    // y los guarda dentro del objeto que se está creando.
    // "public" (público): cualquier clase puede crear un Socio.

    public Socio(int id, String nombre, int edad, String plan) {
        // "this" (este/este objeto): distingue el atributo del parámetro
        // cuando tienen el mismo nombre. this.nombre = el atributo,
        // nombre (sin this) = el parámetro recibido.
        this.id        = id;
        this.nombre    = nombre;
        this.edad      = edad;
        this.plan      = plan.toUpperCase(); // toUpperCase() (a mayúsculas): normaliza el texto
        this.asistencia = 0;                 // Al inscribirse comienza en 0 visitas
        this.cuotaAlDia = true;              // Al inscribirse se asume que pagó
        this.rutina    = asignarRutina(this.plan); // La rutina se asigna según el plan
    }

    // ----------------------------------------------------------
    // MÉTODO PRIVADO: asignarRutina
    // ----------------------------------------------------------
    // Es "private" porque es lógica interna de la clase.
    // Nadie de afuera necesita llamar a este método directamente.
    // Se llama solo desde el constructor y desde setPlan().

    private String asignarRutina(String plan) {
        // switch (interruptor/selector): evalúa el valor de "plan"
        // y ejecuta el bloque del "case" que coincida.
        // Es equivalente al Segun/Caso en PSeInt.
        switch (plan) {
            case "BASICO":
                return "Cardio + Elongacion (30 min) | 3 veces por semana";
            case "INTERMEDIO":
                return "Pesas + Cardio (60 min) | 4 veces por semana";
            case "PREMIUM":
                return "Pesas + Cardio + Funcional (90 min) | 5 veces por semana";
            default: // default (por defecto): si ningún case coincide
                return "Rutina personalizada - consultar con el entrenador";
        }
    }

    // ----------------------------------------------------------
    // GETTERS (Obtenedores) y SETTERS (Establecedores)
    // ----------------------------------------------------------
    // Como los atributos son "private", los métodos "get" y "set"
    // son la única forma de leer o cambiar esos datos desde afuera.
    // Getter: devuelve el valor del atributo → "¿cuál es el nombre?"
    // Setter: modifica el valor del atributo → "cambia el nombre a..."
    // Esta separación nos permite agregar validaciones en cualquier momento.

    // --- ID ---
    public int getId() {
        return id; // return (retornar/devolver): devuelve el valor al que llamó el método
    }

    // --- NOMBRE ---
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) { // void (vacío): este método no devuelve ningún valor
        this.nombre = nombre;
    }

    // --- EDAD ---
    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        // Validación: no permitir edades imposibles
        if (edad > 0 && edad < 120) {
            this.edad = edad;
        }
    }

    // --- PLAN ---
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan  = plan.toUpperCase();
        this.rutina = asignarRutina(this.plan); // Al cambiar plan, la rutina se actualiza sola
    }

    // --- ASISTENCIA ---
    public int getAsistencia() {
        return asistencia;
    }

    // No hay setter de asistencia: solo se puede incrementar, nunca editar a mano.
    // Esto garantiza integridad del dato (no podemos poner -5 asistencias).
    public void incrementarAsistencia() {
        this.asistencia++; // "++" (incremento): equivale a asistencia = asistencia + 1
    }

    // --- CUOTA AL DÍA ---
    // Para atributos boolean, el getter usa "is" (es) en lugar de "get"
    public boolean isCuotaAlDia() {
        return cuotaAlDia;
    }

    public void setCuotaAlDia(boolean cuotaAlDia) {
        this.cuotaAlDia = cuotaAlDia;
    }

    // --- RUTINA ---
    public String getRutina() {
        return rutina;
    }

    // ----------------------------------------------------------
    // MÉTODO: toString (a cadena de texto)
    // ----------------------------------------------------------
    // Java llama a este método automáticamente cuando hacemos
    // System.out.println(unSocio). Nos permite personalizar cómo
    // se ve un objeto cuando se imprime.
    // @Override (sobreescribir): indica que reemplazamos el toString
    // que Java trae por defecto (que mostraría algo como "Socio@3a1b2c")

    @Override
    public String toString() {
        // Operador ternario: condición ? "si es true" : "si es false"
        // Es un if-else compacto de una sola línea.
        String estadoCuota = cuotaAlDia ? "Al dia [OK]" : "PENDIENTE [DEUDA]";

        return "╔══════════════════════════════════════════╗\n"
             + "  ID        : " + id              + "\n"
             + "  Nombre    : " + nombre          + "\n"
             + "  Edad      : " + edad            + " anos\n"
             + "  Plan      : " + plan            + "\n"
             + "  Asistencia: " + asistencia      + " visitas\n"
             + "  Cuota     : " + estadoCuota     + "\n"
             + "  Rutina    : " + rutina          + "\n"
             + "╚══════════════════════════════════════════╝";
    }
}
