package cl.duoc.appcorpaliv.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// singleton contiene la config de retrofit
object RetrofitInstance {

    // URL del microservicio. 10.0.2.2 es el computador donde corre el microservicio, visto desde el emulador.
    // Para un teléfono real, cambiar por la IP del computador en el wifi (ej: http://192.168.1.10:8080/)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // se instancia la api
    val api: RegistroApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Conversor JSON
            .build()
            .create(RegistroApiService::class.java)
    }
}
