package cl.duoc.appcorpaliv.model

data class VoluntariadoUiState(
    val nombre: String = "",
    val apellido: String = "",
    val telefono: String = "",
    val comuna: String = "",
    val talleres: Boolean = false,
    val eventos: Boolean = false,
    val comunicaciones: Boolean = false,
    val mentoria: Boolean = false,
    val mananas: Boolean = false,
    val tardes: Boolean = false,
    val finesDeSemana: Boolean = false,
    val adjunto: Adjunto? = null,
    val errores: VoluntariadoErrores = VoluntariadoErrores()
)
