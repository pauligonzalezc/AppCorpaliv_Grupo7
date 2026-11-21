package cl.duoc.appcorpaliv.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// interfaz define los endpoints del ms registros-service
interface RegistroApiService {

    @GET("api/registros")
    suspend fun obtenerRegistros(): List<RegistroDto>

    @GET("api/registros/{id}")
    suspend fun obtenerRegistro(@Path("id") id: Int): RegistroDto

    @POST("api/registros")
    suspend fun crearRegistro(@Body registro: RegistroDto): RegistroDto

    @PUT("api/registros/{id}")
    suspend fun actualizarRegistro(@Path("id") id: Int, @Body registro: RegistroDto): RegistroDto

    @DELETE("api/registros/{id}")
    suspend fun eliminarRegistro(@Path("id") id: Int): Response<Unit>
}
