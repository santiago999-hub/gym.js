// ============================================================
// PAQUETE: model (modelo)
// Contiene las clases que representan entidades del mundo real.
// Socio es un "modelo de datos": define qué ES un socio.
// ============================================================

// "package" (paquete): le dice a Java en qué carpeta vive este archivo.
// DEBE coincidir exactamente con el nombre de la carpeta.
package model;

// import no necesario: Plan está en el mismo paquete "model"

// ============================================================
// CLASE: Socio
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
    private Plan plan;           // Plan contratado: ahora es de tipo Plan (enum), no String
    private int asistencia;      // Cantidad de veces que asistió al gimnasio
    private boolean cuotaAlDia;  // boolean (booleano): true = pagó / false = debe

    // ----------------------------------------------------------
    // CONSTRUCTOR (Constructor)
    // ----------------------------------------------------------
    // Es el método especial que se ejecuta cuando escribimos: new Socio(...)
    // Es como el "formulario de inscripción": recibe los datos iniciales
    // y los guarda dentro del objeto que se está creando.
    // "public" (público): cualquier clase puede crear un Socio.

    public Socio(int id, String nombre, int edad, String planTexto) {
        // "this" (este/este objeto): distingue el atributo del parámetro
        // cuando tienen el mismo nombre. this.nombre = el atributo,
        // nombre (sin this) = el parámetro recibido.
        this.id         = id;
        this.nombre     = nombre;
        this.edad       = edad;
        // Plan.desdeCadena(): convierte el String "BASICO" → Plan.BASICO
        // Si el texto es inválido, desdeCadena() devuelve null.
        // En ese caso usamos Plan.BASICO como valor por defecto.
        Plan planConvertido = Plan.desdeCadena(planTexto);
        this.plan       = (planConvertido != null) ? planConvertido : Plan.BASICO;
        this.asistencia = 0;    // Al inscribirse comienza en 0 visitas
        this.cuotaAlDia = true; // Al inscribirse se asume que pagó
    }

    // NOTA: el método privado asignarRutina() ya no existe.
    // Ahora la rutina vive DENTRO del enum Plan (en el atributo "rutina" de cada constante).
    // Esto es mejor porque la info de cada plan está en un solo lugar.

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
        if (edad > 0 && edad < 120) {
            this.edad = edad;
        }
    }

    // --- PLAN ---
    // El getter ahora devuelve el objeto Plan (enum), no un String.
    public Plan getPlan() {
        return plan;
    }

    public void setPlan(String planTexto) {
        // Convertimos el texto del usuario al enum correspondiente
        Plan planConvertido = Plan.desdeCadena(planTexto);
        if (planConvertido != null) {
            this.plan = planConvertido;
        }
        // La rutina no necesita actualizarse: plan.getRutina() siempre devuelve la correcta
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
    // Ya no guardamos rutina como atributo separado.
    // La obtenemos directamente del enum: plan.getRutina()
    // Así siempre está sincronizada con el plan, sin riesgo de inconsistencia.
    public String getRutina() {
        return plan.getRutina();
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
             + "  ID        : " + id                    + "\n"
             + "  Nombre    : " + nombre                + "\n"
             + "  Edad      : " + edad                  + " anos\n"
             + "  Plan      : " + plan.getDescripcion() + "\n" // plan.getDescripcion() → "Plan Premium"
             + "  Asistencia: " + asistencia            + " visitas\n"
             + "  Cuota     : " + estadoCuota           + "\n"
             + "  Rutina    : " + plan.getRutina()      + "\n" // plan.getRutina() viene del enum
             + "╚══════════════════════════════════════════╝";
    }
}
