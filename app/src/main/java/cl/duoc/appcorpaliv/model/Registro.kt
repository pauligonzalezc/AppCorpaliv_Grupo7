package cl.duoc.appcorpaliv.model

import cl.duoc.appcorpaliv.data.local.RegistroEntity

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

fun RegistroEntity.toDomain(): Registro {
    return Registro(
        id = this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        estado = this.estado,
        correo = this.correo,
        telefono = this.telefono,
        detalle = this.detalle,
        adjunto = if (this.adjuntoUri != null && this.adjuntoTipo != null) {
            Adjunto(uri = this.adjuntoUri, tipo = this.adjuntoTipo, nombre = this.adjuntoNombre ?: "Archivo adjunto")
        } else null,
        fecha = this.fecha
    )
}

fun Registro.toEntity(): RegistroEntity {
    return RegistroEntity(
        id = this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        estado = this.estado,
        correo = this.correo,
        telefono = this.telefono,
        detalle = this.detalle,
        adjuntoUri = this.adjunto?.uri,
        adjuntoTipo = this.adjunto?.tipo,
        adjuntoNombre = this.adjunto?.nombre,
        fecha = this.fecha
    )
}
