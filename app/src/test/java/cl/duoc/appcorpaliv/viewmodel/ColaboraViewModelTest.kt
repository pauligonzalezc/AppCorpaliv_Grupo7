package cl.duoc.appcorpaliv.viewmodel

import cl.duoc.appcorpaliv.data.repository.RegistroRepository
import cl.duoc.appcorpaliv.model.Adjunto
import cl.duoc.appcorpaliv.model.ContactoUiState
import cl.duoc.appcorpaliv.model.DonacionUiState
import cl.duoc.appcorpaliv.model.EnvioEstado
import cl.duoc.appcorpaliv.model.Registro
import cl.duoc.appcorpaliv.model.VoluntariadoUiState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


@OptIn(ExperimentalCoroutinesApi::class)
class ColaboraViewModelTest : StringSpec({


    val testDispatcher = UnconfinedTestDispatcher()
    beforeTest { Dispatchers.setMain(testDispatcher) }
    afterTest { Dispatchers.resetMain() }

    fun registro(id: Int, nombre: String, estado: String = "Nuevo") =
        Registro(id = id, nombre = nombre, tipo = "Voluntariado", estado = estado, fecha = id.toLong())

    val ana = registro(1, "Ana Pérez", "Nuevo")
    val luis = registro(2, "Luis Soto", "Contactado")
    val sofia = registro(3, "Sofía Rojas", "Cerrado")


    fun repoQueGuarda(): RegistroRepository {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.crearRegistro(any()) } answers { firstArg<Registro>().copy(id = 99) }
        return repo
    }

    fun errorHttp(codigo: Int) = HttpException(Response.error<Any>(codigo, "".toResponseBody()))

    // ---cargar la lista ----------

    "cargarRegistros() debe llenar la lista con lo que entrega el repositorio" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana, luis)
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()

            viewModel.registros.value shouldContainExactly listOf(ana, luis)
            viewModel.cargandoRegistros.value shouldBe false
            viewModel.errorRegistros.value shouldBe null
        }
    }

    "cargarRegistros() sin conexión debe mostrar el mensaje de error de conexión" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } throws IOException("sin red")
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()

            viewModel.errorRegistros.value shouldBe
                "No se pudo conectar con el servidor. Revisa tu conexión e intenta de nuevo."
            viewModel.cargandoRegistros.value shouldBe false
            viewModel.registros.value shouldBe emptyList()
        }
    }

    "cargarRegistros() con error del servidor debe mostrar el código HTTP" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } throws errorHttp(500)
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.errorRegistros.value shouldBe "El servidor respondió con un error (500). Intenta de nuevo."
        }
    }

    "cargarRegistros() con un error inesperado debe mostrar un mensaje genérico" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } throws IllegalStateException("json raro")
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.errorRegistros.value shouldBe "Ocurrió un error inesperado. Intenta de nuevo."
        }
    }

    "un nuevo intento de carga debe limpiar el error anterior" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } throws IOException("sin red") andThen listOf(ana)
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.errorRegistros.value.shouldNotBeNull()

            viewModel.cargarRegistros()
            viewModel.errorRegistros.value shouldBe null
            viewModel.registros.value shouldContainExactly listOf(ana)
        }
    }

    // ---pestañas y detalle ----------

    "el filtro por estado debe mostrar solo los registros de esa pestaña" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana, luis, sofia)
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()

            viewModel.onFiltroEstadoChange("Contactado")
            viewModel.filtroEstado.value shouldBe "Contactado"
            viewModel.registrosFiltrados.value shouldContainExactly listOf(luis)

            viewModel.onFiltroEstadoChange(null) // pestaña Todos
            viewModel.registrosFiltrados.value shouldContainExactly listOf(ana, luis, sofia)
        }
    }

    "seleccionarRegistro() debe dejar ese registro como el del detalle" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana, luis)
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.seleccionarRegistro(luis)

            viewModel.registroSeleccionado.value shouldBe luis
        }
    }

    // ----formulario de donación ----------

    "validarDonacion() con datos inválidos debe marcar errores y no enviar nada" {
        val repo = repoQueGuarda()
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onMontoChange("1500")
            viewModel.onDonanteCorreoChange("correo-malo")

            viewModel.validarDonacion() shouldBe false

            val errores = viewModel.donacion.value.errores
            errores.monto shouldBe "Monto mínimo $2.000"
            errores.nombre shouldBe "El nombre es obligatorio"
            errores.apellido shouldBe "El apellido es obligatorio"
            errores.correo shouldBe "Correo inválido, por ejemplo: nombre@correo.cl"
            coVerify(exactly = 0) { repo.crearRegistro(any()) }
        }
    }

    "los campos de donación deben filtrar lo que se escribe y limpiar su error" {
        val viewModel = ColaboraViewModel(repoQueGuarda())

        viewModel.validarDonacion() // deja errores en nombre, apellido y correo
        viewModel.onMontoChange("12.5a00")
        viewModel.onDonanteNombreChange("Ana3")
        viewModel.onDonanteApellidoChange("Pérez#")
        viewModel.onDonanteCorreoChange("ana@correo.cl")
        viewModel.onDestinoChange("Alba Lab")

        val estado = viewModel.donacion.value
        estado.monto shouldBe "12500"
        estado.nombre shouldBe "Ana"
        estado.apellido shouldBe "Pérez"
        estado.destino shouldBe "Alba Lab"
        estado.errores.nombre shouldBe null
        estado.errores.apellido shouldBe null
        estado.errores.correo shouldBe null
    }

    "validarDonacion() válida debe enviar una Donación única y limpiar el formulario" {
        val repo = repoQueGuarda()
        val enviado = slot<Registro>()
        coEvery { repo.crearRegistro(capture(enviado)) } answers { enviado.captured.copy(id = 99) }
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onDonanteNombreChange("Ana")
            viewModel.onDonanteApellidoChange("Pérez")
            viewModel.onDonanteCorreoChange(" ana@correo.cl ")

            viewModel.validarDonacion() shouldBe true

            enviado.captured.nombre shouldBe "Ana Pérez"
            enviado.captured.tipo shouldBe "Donación única"
            enviado.captured.estado shouldBe "Nuevo"
            enviado.captured.correo shouldBe "ana@correo.cl"
            enviado.captured.detalle shouldBe "Monto: $10000\nDestino: Taller Protegido"
            viewModel.envio.value shouldBe EnvioEstado.Enviado
            viewModel.donacion.value shouldBe DonacionUiState()
            viewModel.registros.value.first().id shouldBe 99
            viewModel.registro.value.nombre shouldBe "Ana"
            viewModel.registro.value.tipo shouldBe "Donación única"
        }
    }

    "una donación de socio debe enviarse como Socio mensual" {
        val repo = repoQueGuarda()
        val enviado = slot<Registro>()
        coEvery { repo.crearRegistro(capture(enviado)) } answers { enviado.captured }
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onEsSocioChange(true)
            viewModel.onDonanteNombreChange("Ana")
            viewModel.onDonanteApellidoChange("Pérez")
            viewModel.onDonanteCorreoChange("ana@correo.cl")

            viewModel.validarDonacion() shouldBe true

            enviado.captured.tipo shouldBe "Socio mensual"
            enviado.captured.detalle shouldContain "al mes"
        }
    }

    // ----formulario de voluntariado ----------

    "validarVoluntariado() vacío debe exigir datos y al menos una opción" {
        val viewModel = ColaboraViewModel(repoQueGuarda())

        viewModel.validarVoluntariado() shouldBe false

        val errores = viewModel.voluntariado.value.errores
        errores.nombre shouldBe "El nombre es obligatorio"
        errores.apellido shouldBe "El apellido es obligatorio"
        errores.telefono shouldBe "Debe tener 9 dígitos después de +56"
        errores.comuna shouldBe "La comuna es obligatoria"
        errores.intereses shouldBe "Selecciona al menos una opción"
        errores.disponibilidad shouldBe "Selecciona al menos una opción"
    }

    "el teléfono debe aceptar solo 9 dígitos" {
        val viewModel = ColaboraViewModel(repoQueGuarda())

        viewModel.onTelefonoChange("9 1234-5678 99")

        viewModel.voluntariado.value.telefono shouldBe "912345678"
    }

    "marcar una opción debe limpiar el error de intereses y de disponibilidad" {
        val viewModel = ColaboraViewModel(repoQueGuarda())
        viewModel.validarVoluntariado()

        viewModel.onEventosChange(true)
        viewModel.onTardesChange(true)

        viewModel.voluntariado.value.errores.intereses shouldBe null
        viewModel.voluntariado.value.errores.disponibilidad shouldBe null
    }

    "enviarVoluntariado() debe enviar intereses, disponibilidad y adjunto" {
        val repo = repoQueGuarda()
        val enviado = slot<Registro>()
        coEvery { repo.crearRegistro(capture(enviado)) } answers { enviado.captured.copy(id = 99) }
        val viewModel = ColaboraViewModel(repo)
        val foto = Adjunto(uri = "file:///foto.jpg", tipo = "image/jpeg", nombre = "foto.jpg")

        runTest(testDispatcher) {
            viewModel.onVoluntarioNombreChange("Ana")
            viewModel.onVoluntarioApellidoChange("Pérez")
            viewModel.onTelefonoChange("912345678")
            viewModel.onComunaChange("Ñuñoa")
            viewModel.onTalleresChange(true)
            viewModel.onEventosChange(false)
            viewModel.onComunicacionesChange(true)
            viewModel.onMentoriaChange(true)
            viewModel.onMananasChange(true)
            viewModel.onTardesChange(false)
            viewModel.onFinesDeSemanaChange(true)
            viewModel.onAdjuntoChange(foto)

            viewModel.validarVoluntariado() shouldBe true
            viewModel.enviarVoluntariado()

            enviado.captured.tipo shouldBe "Voluntariado"
            enviado.captured.telefono shouldBe "+56 912345678"
            enviado.captured.detalle shouldBe
                "Comuna: Ñuñoa\n" +
                "Intereses: Talleres, Comunicaciones, Mentoría laboral\n" +
                "Disponibilidad: Mañanas, Fines de semana"
            enviado.captured.adjunto shouldBe foto
            viewModel.envio.value shouldBe EnvioEstado.Enviado
            viewModel.voluntariado.value shouldBe VoluntariadoUiState()
        }
    }

    // ----formulario de contacto ----------

    "validarContacto() inválido debe pedir mensaje largo y aceptar ser contactado" {
        val repo = repoQueGuarda()
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onContactoNombreChange("Ana")
            viewModel.onContactoApellidoChange("Pérez")
            viewModel.onContactoCorreoChange("ana@correo.cl")
            viewModel.onMensajeChange("Hola")

            viewModel.validarContacto() shouldBe false

            viewModel.contacto.value.errores.mensaje shouldBe "Escribe al menos 20 caracteres (llevas 4)"
            viewModel.contacto.value.errores.aceptaContacto shouldBe "Debes aceptar para continuar"
            viewModel.contacto.value.errores.correo shouldBe null
            coVerify(exactly = 0) { repo.crearRegistro(any()) }
        }
    }

    "validarContacto() válido debe enviar la empresa y el mensaje" {
        val repo = repoQueGuarda()
        val enviado = slot<Registro>()
        coEvery { repo.crearRegistro(capture(enviado)) } answers { enviado.captured.copy(id = 99) }
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onTipoChange("Compra corporativa")
            viewModel.onEmpresaChange(" Duoc UC ")
            viewModel.onContactoNombreChange("Ana")
            viewModel.onContactoApellidoChange("Pérez")
            viewModel.onContactoCorreoChange("ana@correo.cl")
            viewModel.onMensajeChange("Queremos comprar productos para fin de año")
            viewModel.onAceptaContactoChange(true)

            viewModel.validarContacto() shouldBe true

            enviado.captured.tipo shouldBe "Compra corporativa"
            enviado.captured.detalle shouldBe
                "Empresa: Duoc UC\nMensaje: Queremos comprar productos para fin de año"
            viewModel.contacto.value shouldBe ContactoUiState()
        }
    }

    "un contacto sin empresa no debe enviar la línea Empresa" {
        val repo = repoQueGuarda()
        val enviado = slot<Registro>()
        coEvery { repo.crearRegistro(capture(enviado)) } answers { enviado.captured }
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onContactoNombreChange("Ana")
            viewModel.onContactoApellidoChange("Pérez")
            viewModel.onContactoCorreoChange("ana@correo.cl")
            viewModel.onMensajeChange("Quiero saber más sobre los talleres")
            viewModel.onAceptaContactoChange(true)

            viewModel.validarContacto() shouldBe true

            enviado.captured.detalle shouldBe "Mensaje: Quiero saber más sobre los talleres"
        }
    }

    // ----envio al servidor ----------

    "si el servidor falla al enviar, se muestra el error y el formulario NO se borra" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.crearRegistro(any()) } throws IOException("sin red")
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onDonanteNombreChange("Ana")
            viewModel.onDonanteApellidoChange("Pérez")
            viewModel.onDonanteCorreoChange("ana@correo.cl")

            viewModel.validarDonacion()

            val envio = viewModel.envio.value
            envio.shouldBeInstanceOf<EnvioEstado.Error>()
            envio.mensaje shouldContain "No se pudo conectar con el servidor"
            viewModel.donacion.value.nombre shouldBe "Ana" // los datos siguen escritos
            viewModel.registros.value shouldBe emptyList()
        }
    }

    "mientras se envía no se puede enviar dos veces el mismo formulario" {
        val repo = mockk<RegistroRepository>()
        val respuestaServidor = CompletableDeferred<Registro>()
        coEvery { repo.crearRegistro(any()) } coAnswers { respuestaServidor.await() }
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.onDonanteNombreChange("Ana")
            viewModel.onDonanteApellidoChange("Pérez")
            viewModel.onDonanteCorreoChange("ana@correo.cl")

            viewModel.validarDonacion()
            viewModel.envio.value shouldBe EnvioEstado.Enviando

            viewModel.validarDonacion() // segundo toque del botón

            respuestaServidor.complete(registro(99, "Ana Pérez"))
            viewModel.envio.value shouldBe EnvioEstado.Enviado
            coVerify(exactly = 1) { repo.crearRegistro(any()) }
        }
    }

    "limpiarEnvio() debe dejar el envío inactivo" {
        val viewModel = ColaboraViewModel(repoQueGuarda())
        viewModel.onDonanteNombreChange("Ana")
        viewModel.onDonanteApellidoChange("Pérez")
        viewModel.onDonanteCorreoChange("ana@correo.cl")
        viewModel.validarDonacion()

        viewModel.limpiarEnvio()

        viewModel.envio.value shouldBe EnvioEstado.Inactivo
    }

    // ---cambiar estado ----------

    "cambiarEstado() debe reemplazar el registro con lo que respondió el servidor" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana, luis)
        coEvery { repo.actualizarRegistro(any()) } answers { firstArg() }
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.seleccionarRegistro(ana)

            viewModel.cambiarEstado(ana, "Cerrado")

            viewModel.registros.value.first { it.id == 1 }.estado shouldBe "Cerrado"
            viewModel.registroSeleccionado.value?.estado shouldBe "Cerrado" // el detalle se actualiza
            coVerify { repo.actualizarRegistro(match { it.id == 1 && it.estado == "Cerrado" }) }
        }
    }

    "cambiarEstado() al mismo estado no debe llamar al servidor" {
        val repo = mockk<RegistroRepository>()
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cambiarEstado(ana, "Nuevo")
            coVerify(exactly = 0) { repo.actualizarRegistro(any()) }
        }
    }

    "si falla cambiarEstado() se muestra el error y el estado no cambia" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana)
        coEvery { repo.actualizarRegistro(any()) } throws errorHttp(500)
        val viewModel = ColaboraViewModel(repo)

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.cambiarEstado(ana, "Contactado")

            viewModel.errorAccion.value shouldBe
                "No se pudo cambiar el estado. El servidor respondió con un error (500). Intenta de nuevo."
            viewModel.registros.value.first().estado shouldBe "Nuevo"

            viewModel.limpiarErrorAccion()
            viewModel.errorAccion.value shouldBe null
        }
    }

    // ----eliminar ----------

    "eliminarRegistro() debe quitarlo de la lista y volver atrás" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana, luis)
        coEvery { repo.eliminarRegistro(any()) } returns Unit
        val viewModel = ColaboraViewModel(repo)
        var volvio = false

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.eliminarRegistro(ana) { volvio = true }

            viewModel.registros.value shouldContainExactly listOf(luis)
            volvio shouldBe true
        }
    }

    "si falla eliminarRegistro() se muestra el error y el registro sigue en la lista" {
        val repo = mockk<RegistroRepository>()
        coEvery { repo.obtenerRegistros() } returns listOf(ana)
        coEvery { repo.eliminarRegistro(any()) } throws IOException("sin red")
        val viewModel = ColaboraViewModel(repo)
        var volvio = false

        runTest(testDispatcher) {
            viewModel.cargarRegistros()
            viewModel.eliminarRegistro(ana) { volvio = true }

            viewModel.errorAccion.value shouldBe
                "No se pudo eliminar. No se pudo conectar con el servidor. Revisa tu conexión e intenta de nuevo."
            viewModel.registros.value shouldContainExactly listOf(ana)
            volvio shouldBe false
        }
    }

    "la Factory debe crear el ColaboraViewModel con el repositorio" {
        val factory = ColaboraViewModel.ColaboraViewModelFactory(mockk())

        factory.create(ColaboraViewModel::class.java).shouldBeInstanceOf<ColaboraViewModel>()
    }
})
