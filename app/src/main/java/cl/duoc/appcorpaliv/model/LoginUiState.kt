package cl.duoc.appcorpaliv.model

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val sesionIniciada: Boolean = false,
    val errores: LoginErrores = LoginErrores()
)
