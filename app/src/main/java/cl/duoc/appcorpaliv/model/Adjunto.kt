package cl.duoc.appcorpaliv.model

data class Adjunto(
    val uri: String,
    val tipo: String,
    val nombre: String
) {
    val esImagen: Boolean get() = tipo.startsWith("image/")
    val esPdf: Boolean get() = tipo == "application/pdf"
}
