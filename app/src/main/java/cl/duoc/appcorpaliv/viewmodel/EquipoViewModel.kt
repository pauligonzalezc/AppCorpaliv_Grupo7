package cl.duoc.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.appcorpaliv.model.LoginErrores
import cl.duoc.appcorpaliv.model.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EquipoViewModel : ViewModel() {

    private val correoEquipo = "equipo@demo.cl"
    private val claveEquipo = "Corpaliv2026"
    private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null, credenciales = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null, credenciales = null)) }
    }

    fun iniciarSesion(): Boolean {
        val estadoActual = _estado.value
        val errores = LoginErrores(
            correo = if (!estadoActual.correo.matches(regexCorreo)) "Correo inválido, por ejemplo: nombre@correo.cl" else null,
            clave = if (estadoActual.clave.length < 8) "Debe tener al menos 8 caracteres" else null
        )

        val hayErrores = listOfNotNull(
            errores.correo,
            errores.clave
        ).isNotEmpty()

        if (hayErrores) {
            _estado.update { it.copy(errores = errores) }
            return false
        }

        if (estadoActual.correo != correoEquipo || estadoActual.clave != claveEquipo) {
            _estado.update { it.copy(errores = LoginErrores(credenciales = "Correo o contraseña incorrectos")) }
            return false
        }

        _estado.value = LoginUiState(sesionIniciada = true)
        return true
    }

    fun cerrarSesion() {
        _estado.value = LoginUiState()
    }
}
