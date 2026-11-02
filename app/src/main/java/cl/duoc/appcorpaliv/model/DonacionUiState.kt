package cl.duoc.appcorpaliv.model

data class DonacionUiState(
    val esSocio: Boolean = false,
    val monto: String = "10000",
    val destino: String = "Taller Protegido",
    val nombre: String = "",
    val correo: String = "",
    val errores: DonacionErrores = DonacionErrores()
)

data class DonacionErrores(
    val monto: String? = null,
    val nombre: String? = null,
    val correo: String? = null
)
