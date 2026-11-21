package cl.duoc.appcorpaliv.data.repository

import cl.duoc.appcorpaliv.data.remote.RegistroApiService
import cl.duoc.appcorpaliv.data.remote.RetrofitInstance
import cl.duoc.appcorpaliv.data.remote.toDomain
import cl.duoc.appcorpaliv.data.remote.toDto
import cl.duoc.appcorpaliv.model.Registro
import retrofit2.HttpException

//  accede a los registros del ms por retrofit
// el vm muestra el mensaje
class RegistroRepository(
    private val api: RegistroApiService = RetrofitInstance.api
) {

    suspend fun obtenerRegistros(): List<Registro> =
        api.obtenerRegistros().map { it.toDomain() }

    suspend fun crearRegistro(registro: Registro): Registro =
        api.crearRegistro(registro.toDto()).toDomain()

    suspend fun actualizarRegistro(registro: Registro): Registro =
        api.actualizarRegistro(registro.id, registro.toDto()).toDomain()

    suspend fun eliminarRegistro(registro: Registro) {
        val respuesta = api.eliminarRegistro(registro.id)
        if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
    }
}
