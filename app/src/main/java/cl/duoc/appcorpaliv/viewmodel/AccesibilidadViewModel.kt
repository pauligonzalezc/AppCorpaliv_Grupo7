package cl.duoc.appcorpaliv.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.appcorpaliv.data.local.AccesibilidadDataStore
import cl.duoc.appcorpaliv.model.AccesibilidadUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccesibilidadViewModel(application: Application) : AndroidViewModel(application) {

    private val accesibilidadDataStore = AccesibilidadDataStore(context = application)

    private val _estado = MutableStateFlow(AccesibilidadUiState())
    val estado: StateFlow<AccesibilidadUiState> = _estado

    init {
        cargarPreferencias()
    }

    fun cargarPreferencias() {
        viewModelScope.launch {
            _estado.value = AccesibilidadUiState(
                tamanoTexto = accesibilidadDataStore.obtenerTamanoTexto().first() ?: 1f,
                altoContraste = accesibilidadDataStore.obtenerAltoContraste().first() ?: false,
                leerEnVozAlta = accesibilidadDataStore.obtenerLeerEnVozAlta().first() ?: false,
                reducirAnimaciones = accesibilidadDataStore.obtenerReducirAnimaciones().first() ?: false
            )
        }
    }

    fun onTamanoTextoChange(valor: Float) {
        _estado.update { it.copy(tamanoTexto = valor) }
    }

    fun onAltoContrasteChange(valor: Boolean) {
        _estado.update { it.copy(altoContraste = valor) }
    }

    fun onLeerEnVozAltaChange(valor: Boolean) {
        _estado.update { it.copy(leerEnVozAlta = valor) }
    }

    fun onReducirAnimacionesChange(valor: Boolean) {
        _estado.update { it.copy(reducirAnimaciones = valor) }
    }

    fun guardarPreferencias() {
        viewModelScope.launch {
            val estadoActual = _estado.value
            accesibilidadDataStore.guardarPreferencias(
                tamanoTexto = estadoActual.tamanoTexto,
                altoContraste = estadoActual.altoContraste,
                leerEnVozAlta = estadoActual.leerEnVozAlta,
                reducirAnimaciones = estadoActual.reducirAnimaciones
            )

            _estado.update { it.copy(mostrarMensaje = true) }

            delay(2000)

            _estado.update { it.copy(mostrarMensaje = false) }
        }
    }
}
