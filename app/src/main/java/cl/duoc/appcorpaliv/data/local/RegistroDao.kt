package cl.duoc.appcorpaliv.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Query("SELECT * FROM registros ORDER BY id DESC")
    fun obtenerRegistros(): Flow<List<RegistroEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarRegistro(registro: RegistroEntity)

    @Update
    suspend fun actualizarRegistro(registro: RegistroEntity)

    @Delete
    suspend fun eliminarRegistro(registro: RegistroEntity)
}
