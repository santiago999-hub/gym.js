// ============================================================
// PAQUETE: model (modelo)
// ============================================================

package model;

// ============================================================
// ENUM: Plan
// ============================================================
// ¿Qué es un enum (enumeration = enumeración)?
//   Es un tipo especial que define un conjunto FIJO y CERRADO de valores.
//   Como una lista de opciones que nunca puede tener otras diferentes.
//
// ¿Por qué usarlo en lugar de String?
//   Con String:  cualquier texto es válido → "BASICO", "basico", "BACISO", ""
//   Con enum:    SOLO Plan.BASICO, Plan.INTERMEDIO o Plan.PREMIUM son válidos
//   Si escribís Plan.BACISO → el compilador lo detecta ANTES de ejecutar.
//
// Concepto clave: TYPE SAFETY (seguridad de tipos)
//   El enum garantiza que NUNCA pueda existir un plan inválido en el sistema.
//
// Un enum también puede tener atributos y métodos propios,
// como si fuera una clase. Eso es exactamente lo que hacemos aquí:
// cada valor del enum "sabe" su rutina y su descripción.
// ============================================================

public enum Plan {

    // ----------------------------------------------------------
    // VALORES DEL ENUM
    // ----------------------------------------------------------
    // Cada valor es una CONSTANTE del tipo Plan.
    // Entre paréntesis pasamos los datos que le corresponden a cada uno.
    // La sintaxis es: NOMBRE_CONSTANTE("descripcion", "rutina")
    // El punto y coma al final es OBLIGATORIO cuando el enum tiene métodos.

    BASICO(
        "Plan Basico",
        "Cardio + Elongacion (30 min) | 3 veces por semana"
    ),
    INTERMEDIO(
        "Plan Intermedio",
        "Pesas + Cardio (60 min) | 4 veces por semana"
    ),
    PREMIUM(
        "Plan Premium",
        "Pesas + Cardio + Funcional (90 min) | 5 veces por semana"
    );  // <-- Punto y coma: termina la lista de constantes

    // ----------------------------------------------------------
    // ATRIBUTOS del enum
    // ----------------------------------------------------------
    // Cada constante del enum tiene estos datos propios.
    // "final" (final/inmutable): no se pueden cambiar después de crear.

    private final String descripcion;
    private final String rutina;

    // ----------------------------------------------------------
    // CONSTRUCTOR del enum
    // ----------------------------------------------------------
    // A diferencia de las clases normales, el constructor de un enum
    // es SIEMPRE "private" (no puede ser public).
    // Java lo llama internamente cuando se crean BASICO, INTERMEDIO, PREMIUM.

    Plan(String descripcion, String rutina) {
        this.descripcion = descripcion;
        this.rutina      = rutina;
    }

    // ----------------------------------------------------------
    // GETTERS (Obtenedores)
    // ----------------------------------------------------------

    public String getDescripcion() {
        return descripcion;
    }

    public String getRutina() {
        return rutina;
    }

    // ----------------------------------------------------------
    // MÉTODO ESTÁTICO: desdeCadena
    // ----------------------------------------------------------
    // Convierte un String (texto del teclado o del CSV) al enum Plan.
    // Si el texto no coincide con ningún valor válido, devuelve null.
    //
    // ¿Por qué lo necesitamos?
    //   El usuario escribe "BASICO" desde el teclado (es un String).
    //   El CSV también guarda "BASICO" (es un String).
    //   Este método convierte ese texto → Plan.BASICO (el enum).
    //
    // Plan.values() (valores): devuelve un array con todos los valores del enum
    //   → [Plan.BASICO, Plan.INTERMEDIO, Plan.PREMIUM]
    // .name() (nombre): devuelve el nombre de la constante como String
    //   → Plan.BASICO.name() = "BASICO"

    public static Plan desdeCadena(String texto) {
        if (texto == null) return null;
        for (Plan plan : Plan.values()) {
            if (plan.name().equalsIgnoreCase(texto.trim())) {
                return plan;
            }
        }
        return null; // null: texto no coincide con ningún plan válido
    }

    // ----------------------------------------------------------
    // MÉTODO ESTÁTICO: listarOpciones
    // ----------------------------------------------------------
    // Devuelve un String con las opciones disponibles para mostrar en el menú.
    // Ejemplo: "BASICO | INTERMEDIO | PREMIUM"

    public static String listarOpciones() {
        StringBuilder sb = new StringBuilder(); // StringBuilder: construye Strings eficientemente
        Plan[] valores = Plan.values();
        for (int i = 0; i < valores.length; i++) {
            sb.append(valores[i].name());
            if (i < valores.length - 1) sb.append(" | "); // Separador entre opciones
        }
        return sb.toString();
    }
}
