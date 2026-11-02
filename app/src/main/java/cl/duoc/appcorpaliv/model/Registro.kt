package cl.duoc.appcorpaliv.model

import cl.duoc.appcorpaliv.data.local.RegistroEntity

data class Registro(
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val estado: String
)

fun RegistroEntity.toDomain(): Registro {
    return Registro(
        id = this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        estado = this.estado
    )
}

fun Registro.toEntity(): RegistroEntity {
    return RegistroEntity(
        id = this.id,
        nombre = this.nombre,
        tipo = this.tipo,
        estado = this.estado
    )
}
