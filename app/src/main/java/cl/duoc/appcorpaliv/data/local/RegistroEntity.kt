package cl.duoc.appcorpaliv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val estado: String,
    val correo: String = "",
    val telefono: String = "",
    val detalle: String = "",
    val adjuntoUri: String? = null,
    val adjuntoTipo: String? = null,
    val adjuntoNombre: String? = null,
    val fecha: Long = System.currentTimeMillis()
)
