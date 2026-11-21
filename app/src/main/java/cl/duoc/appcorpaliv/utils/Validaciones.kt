package cl.duoc.appcorpaliv.utils


object Validaciones {

    const val LARGO_MAXIMO_NOMBRE = 40

    private val separadores = setOf(' ', '\'', '-')

    // palabras de letras unidas por un solo espacio, apostrofe o guion
    private val regexNombre = Regex("^\\p{L}+([ '\\-]\\p{L}+)*$")

    fun filtrarNombre(valor: String): String {
        val resultado = StringBuilder()
        for (letra in valor) {
            val caracter = if (letra == '’' || letra == '´' || letra == '`') '\'' else letra
            val esValido = caracter.isLetter() || caracter in separadores
            if (!esValido) continue
            if (caracter in separadores && (resultado.isEmpty() || resultado.last() in separadores)) continue
            resultado.append(caracter)
        }
        return resultado.toString().take(LARGO_MAXIMO_NOMBRE)
    }

    // mensaje de error del campo, o null si es valido
    fun validarNombre(valor: String, mensajeVacio: String = "El nombre es obligatorio"): String? {
        val limpio = valor.trim()
        return when {
            limpio.isBlank() -> mensajeVacio
            limpio.length < 2 -> "Debe tener al menos 2 letras"
            !limpio.matches(regexNombre) -> "Solo se permiten letras, espacios, ' y -"
            else -> null
        }
    }
}
