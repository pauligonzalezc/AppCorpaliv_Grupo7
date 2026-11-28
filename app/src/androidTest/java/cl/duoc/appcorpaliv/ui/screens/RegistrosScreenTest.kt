package cl.duoc.appcorpaliv.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import cl.duoc.appcorpaliv.data.remote.RegistroApiService
import cl.duoc.appcorpaliv.data.remote.RegistroDto
import cl.duoc.appcorpaliv.data.repository.RegistroRepository
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.EquipoViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

// api falsa que entrega datos fijos en vez de llamar al microservicio
class FakeRegistroApi(private val registros: MutableList<RegistroDto>) : RegistroApiService {
    override suspend fun obtenerRegistros() = registros.toList()
    override suspend fun obtenerRegistro(id: Int) = registros.first { it.id == id }
    override suspend fun crearRegistro(registro: RegistroDto) = registro.copy(id = registros.size + 1)
    override suspend fun actualizarRegistro(id: Int, registro: RegistroDto) = registro.copy(id = id)
    override suspend fun eliminarRegistro(id: Int): Response<Unit> = Response.success(204, Unit)
}

class RegistrosScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val fakeRegistros = mutableListOf(
        RegistroDto(id = 1, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Nuevo", fecha = 1L),
        RegistroDto(id = 2, nombre = "Luis Soto", tipo = "Donación única", estado = "Contactado", fecha = 2L)
    )

    // equipo con la sesión iniciada, como si hubiera entrado el admin
    private fun equipoConSesion() = EquipoViewModel().apply {
        onCorreoChange("admin@corpaliv.cl")
        onClaveChange("admin1234")
        iniciarSesion()
    }

    @Test
    fun la_lista_debe_mostrar_los_registros_que_entrega_el_servidor() {
        val colaboraViewModel = ColaboraViewModel(RegistroRepository(FakeRegistroApi(fakeRegistros)))

        composeRule.setContent {
            RegistrosScreen(
                navController = rememberNavController(),
                viewModel = MainViewModel(),
                colaboraViewModel = colaboraViewModel,
                equipoViewModel = equipoConSesion()
            )
        }

        composeRule.onNodeWithText("Ana Pérez").assertIsDisplayed()
        composeRule.onNodeWithText("Luis Soto").assertIsDisplayed()
        composeRule.onNodeWithText("Todos · 2").assertIsDisplayed()
        composeRule.onNodeWithText("Contactados · 1").assertIsDisplayed()
    }

    @Test
    fun la_pestana_contactados_debe_mostrar_solo_esos_registros() {
        val colaboraViewModel = ColaboraViewModel(RegistroRepository(FakeRegistroApi(fakeRegistros)))

        composeRule.setContent {
            RegistrosScreen(
                navController = rememberNavController(),
                viewModel = MainViewModel(),
                colaboraViewModel = colaboraViewModel,
                equipoViewModel = equipoConSesion()
            )
        }

        composeRule.onNodeWithText("Contactados · 1").performClick()

        composeRule.onNodeWithText("Luis Soto").assertIsDisplayed()
        composeRule.onNodeWithText("Ana Pérez").assertDoesNotExist()
    }

    @Test
    fun sin_sesion_debe_pedir_iniciar_sesion() {
        val colaboraViewModel = ColaboraViewModel(RegistroRepository(FakeRegistroApi(fakeRegistros)))

        composeRule.setContent {
            RegistrosScreen(
                navController = rememberNavController(),
                viewModel = MainViewModel(),
                colaboraViewModel = colaboraViewModel,
                equipoViewModel = EquipoViewModel()
            )
        }

        composeRule.onNodeWithText("Debes iniciar sesión como equipo para ver los registros").assertIsDisplayed()
    }

    @Test
    fun cambiar_el_estado_en_el_detalle_debe_pedir_confirmacion() {
        val colaboraViewModel = ColaboraViewModel(RegistroRepository(FakeRegistroApi(fakeRegistros)))
        colaboraViewModel.cargarRegistros()
        composeRule.waitUntil { colaboraViewModel.registros.value.isNotEmpty() }
        colaboraViewModel.seleccionarRegistro(colaboraViewModel.registros.value.first { it.id == 1 })

        composeRule.setContent {
            RegistroDetalleScreen(
                navController = rememberNavController(),
                viewModel = MainViewModel(),
                colaboraViewModel = colaboraViewModel,
                equipoViewModel = equipoConSesion()
            )
        }

        composeRule.onNodeWithContentDescription("Opciones del registro").performClick()
        composeRule.onNodeWithText("Marcar como Cerrado").performClick()

        // aparece la confirmación y el estado todavía no cambia
        composeRule.onNodeWithText("¿Quieres cambiar el estado de Ana Pérez de Nuevo a Cerrado?").assertIsDisplayed()
        composeRule.onNodeWithText("Cambiar").performClick()

        composeRule.onNodeWithText("Cerrado").assertIsDisplayed()
    }
}
