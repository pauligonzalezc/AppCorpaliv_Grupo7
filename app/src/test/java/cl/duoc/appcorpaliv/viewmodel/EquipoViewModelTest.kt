package cl.duoc.appcorpaliv.viewmodel

import cl.duoc.appcorpaliv.model.LoginUiState
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


class EquipoViewModelTest : StringSpec({

    "iniciarSesion() con las credenciales del equipo debe abrir la sesión" {
        val viewModel = EquipoViewModel()
        viewModel.onCorreoChange("admin@corpaliv.cl")
        viewModel.onClaveChange("admin1234")

        viewModel.iniciarSesion() shouldBe true

        viewModel.estado.value.sesionIniciada shouldBe true
        // por seguridad no queda guardada la contraseña escrita
        viewModel.estado.value.clave shouldBe ""
    }

    "iniciarSesion() con correo inválido y clave corta debe marcar ambos errores" {
        val viewModel = EquipoViewModel()
        viewModel.onCorreoChange("admin")
        viewModel.onClaveChange("123")

        viewModel.iniciarSesion() shouldBe false

        viewModel.estado.value.errores.correo shouldBe "Correo inválido, por ejemplo: nombre@correo.cl"
        viewModel.estado.value.errores.clave shouldBe "Debe tener al menos 8 caracteres"
        viewModel.estado.value.sesionIniciada shouldBe false
    }

    "iniciarSesion() con datos válidos pero incorrectos debe avisar credenciales incorrectas" {
        val viewModel = EquipoViewModel()
        viewModel.onCorreoChange("otro@corpaliv.cl")
        viewModel.onClaveChange("claveincorrecta")

        viewModel.iniciarSesion() shouldBe false

        viewModel.estado.value.errores.credenciales shouldBe "Correo o contraseña incorrectos"
        viewModel.estado.value.sesionIniciada shouldBe false
    }

    "escribir de nuevo debe limpiar los errores del campo" {
        val viewModel = EquipoViewModel()
        viewModel.onCorreoChange("otro@corpaliv.cl")
        viewModel.onClaveChange("claveincorrecta")
        viewModel.iniciarSesion()

        viewModel.onClaveChange("admin1234")

        viewModel.estado.value.errores.clave shouldBe null
        viewModel.estado.value.errores.credenciales shouldBe null
    }

    "cerrarSesion() debe volver al estado inicial" {
        val viewModel = EquipoViewModel()
        viewModel.onCorreoChange("admin@corpaliv.cl")
        viewModel.onClaveChange("admin1234")
        viewModel.iniciarSesion()

        viewModel.cerrarSesion()

        viewModel.estado.value shouldBe LoginUiState()
    }
})
