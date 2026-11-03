package cl.duoc.appcorpaliv.model

data class ContactoUiState(
    val tipo: String = "Alianza inclusión laboral",
    val empresa: String = "",
    val nombre: String = "",
    val correo: String = "",
    val mensaje: String = "",
    val aceptaContacto: Boolean = false,
    val errores: ContactoErrores = ContactoErrores()
)
