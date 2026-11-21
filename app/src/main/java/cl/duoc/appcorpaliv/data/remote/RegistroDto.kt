package cl.duoc.appcorpaliv.data.remote

import cl.duoc.appcorpaliv.model.Adjunto
import cl.duoc.appcorpaliv.model.Registro

// registro tal como viaja entre la app y el ms */
data class RegistroDto(
    val id: Int? = null,
    val nombre: String,
    val tipo: String,
    val estado: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val detalle: String? = null,
    val adjuntoUri: String? = null,
    val adjuntoTipo: String? = null,
    val adjuntoNombre: String? = null,
    val fecha: Long? = null
)

fun RegistroDto.toDomain(): Registro = Registro(
    id = id ?: 0,
    nombre = nombre,
    tipo = tipo,
    estado = estado ?: "Nuevo",
    correo = correo.orEmpty(),
    telefono = telefono.orEmpty(),
    detalle = detalle.orEmpty(),
    adjunto = if (adjuntoUri != null && adjuntoTipo != null) {
        Adjunto(uri = adjuntoUri, tipo = adjuntoTipo, nombre = adjuntoNombre ?: "Archivo adjunto")
    } else null,
    fecha = fecha ?: 0L
)

// estos id y la fecha los asigna el servidor, por eso no se envian
fun Registro.toDto(): RegistroDto = RegistroDto(
    nombre = nombre,
    tipo = tipo,
    estado = estado,
    correo = correo,
    telefono = telefono,
    detalle = detalle,
    adjuntoUri = adjunto?.uri,
    adjuntoTipo = adjunto?.tipo,
    adjuntoNombre = adjunto?.nombre
)
