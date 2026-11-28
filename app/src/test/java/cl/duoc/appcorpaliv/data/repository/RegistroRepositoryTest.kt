package cl.duoc.appcorpaliv.data.repository

import cl.duoc.appcorpaliv.data.remote.RegistroApiService
import cl.duoc.appcorpaliv.data.remote.RegistroDto
import cl.duoc.appcorpaliv.model.Registro
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response


class RegistroRepositoryTest : StringSpec({

    "obtenerRegistros() debe retornar la lista que entrega la API convertida a Registro" {
        // 1 simulamos el resultado de la API
        val fakeDtos = listOf(
            RegistroDto(id = 1, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Nuevo", fecha = 100L),
            RegistroDto(id = 2, nombre = "Luis Soto", tipo = "Donación única", estado = "Cerrado", fecha = 200L)
        )
        // 2 creamos un mock de RegistroApiService
        val mockApi = mockk<RegistroApiService>()
        coEvery { mockApi.obtenerRegistros() } returns fakeDtos
        // 3 usamos el repositorio inyectando el mock
        val repo = RegistroRepository(api = mockApi)
        // 4 ejecutamos el test
        runTest {
            val result = repo.obtenerRegistros()
            result.map { it.nombre } shouldContainExactly listOf("Ana Pérez", "Luis Soto")
            result.map { it.estado } shouldContainExactly listOf("Nuevo", "Cerrado")
        }
    }

    "crearRegistro() debe enviar el registro y retornar el que guardó el servidor" {
        val mockApi = mockk<RegistroApiService>()
        val enviado = slot<RegistroDto>()
        coEvery { mockApi.crearRegistro(capture(enviado)) } returns
            RegistroDto(id = 7, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Nuevo", fecha = 123L)
        val repo = RegistroRepository(api = mockApi)

        runTest {
            val creado = repo.crearRegistro(Registro(nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Nuevo"))

            creado.id shouldBe 7
            creado.fecha shouldBe 123L
            // el id y la fecha los asigna el servidor, por eso no se envían
            enviado.captured.id shouldBe null
            enviado.captured.fecha shouldBe null
        }
    }

    "actualizarRegistro() debe llamar al PUT con el id y el nuevo estado" {
        val mockApi = mockk<RegistroApiService>()
        coEvery { mockApi.actualizarRegistro(5, any()) } returns
            RegistroDto(id = 5, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Contactado")
        val repo = RegistroRepository(api = mockApi)

        runTest {
            val actualizado = repo.actualizarRegistro(
                Registro(id = 5, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Contactado")
            )

            actualizado.estado shouldBe "Contactado"
            coVerify { mockApi.actualizarRegistro(5, match { it.estado == "Contactado" }) }
        }
    }

    "eliminarRegistro() debe terminar sin error cuando el servidor responde 204" {
        val mockApi = mockk<RegistroApiService>()
        coEvery { mockApi.eliminarRegistro(3) } returns Response.success(204, Unit)
        val repo = RegistroRepository(api = mockApi)

        runTest {
            repo.eliminarRegistro(Registro(id = 3, nombre = "Ana", tipo = "Voluntariado", estado = "Nuevo"))
            coVerify(exactly = 1) { mockApi.eliminarRegistro(3) }
        }
    }

    "eliminarRegistro() debe ignorar un 404 porque el registro ya no existe" {
        val mockApi = mockk<RegistroApiService>()
        coEvery { mockApi.eliminarRegistro(3) } returns Response.error(404, "".toResponseBody())
        val repo = RegistroRepository(api = mockApi)

        runTest {
            // si lanzara una excepción el test fallaría
            repo.eliminarRegistro(Registro(id = 3, nombre = "Ana", tipo = "Voluntariado", estado = "Nuevo"))
        }
    }

    "eliminarRegistro() debe lanzar HttpException cuando el servidor responde 500" {
        val mockApi = mockk<RegistroApiService>()
        coEvery { mockApi.eliminarRegistro(3) } returns Response.error(500, "".toResponseBody())
        val repo = RegistroRepository(api = mockApi)

        runTest {
            val error = shouldThrow<HttpException> {
                repo.eliminarRegistro(Registro(id = 3, nombre = "Ana", tipo = "Voluntariado", estado = "Nuevo"))
            }
            error.code() shouldBe 500
        }
    }
})
