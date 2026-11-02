package cl.duoc.appcorpaliv.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros")
data class RegistroEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val estado: String
)
