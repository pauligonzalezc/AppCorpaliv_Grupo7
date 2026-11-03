package cl.duoc.appcorpaliv.model

data class AccesibilidadUiState(
    val tamanoTexto: Float = 1f,
    val altoContraste: Boolean = false,
    val leerEnVozAlta: Boolean = false,
    val reducirAnimaciones: Boolean = false,
    val mostrarMensaje: Boolean = false
)
