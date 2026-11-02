package cl.duoc.appcorpaliv.data.repository

import cl.duoc.appcorpaliv.data.local.RegistroDao
import cl.duoc.appcorpaliv.model.Registro
import cl.duoc.appcorpaliv.model.toDomain
import cl.duoc.appcorpaliv.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RegistroRepository(
    private val registroDao: RegistroDao
) {
    val registros: Flow<List<Registro>> = registroDao.obtenerRegistros().map { listaEntidades ->
        listaEntidades.map { entidad -> entidad.toDomain() }
    }

    suspend fun insertarRegistro(registro: Registro) = registroDao.insertarRegistro(registro.toEntity())
}
