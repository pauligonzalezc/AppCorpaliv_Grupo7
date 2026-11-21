package cl.duoc.appcorpaliv.model

data class Registro(
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val estado: String,
    val correo: String = "",
    val telefono: String = "",
    val detalle: String = "",
    val adjunto: Adjunto? = null,
    val fecha: Long = System.currentTimeMillis()
)
