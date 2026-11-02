package cl.duoc.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.appcorpaliv.data.repository.RegistroRepository
import cl.duoc.appcorpaliv.model.ContactoErrores
import cl.duoc.appcorpaliv.model.ContactoUiState
import cl.duoc.appcorpaliv.model.DonacionErrores
import cl.duoc.appcorpaliv.model.DonacionUiState
import cl.duoc.appcorpaliv.model.Registro
import cl.duoc.appcorpaliv.model.RegistroUiState
import cl.duoc.appcorpaliv.model.VoluntariadoErrores
import cl.duoc.appcorpaliv.model.VoluntariadoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ColaboraViewModel(
    private val registroRepository: RegistroRepository
) : ViewModel() {

    private val _donacion = MutableStateFlow(DonacionUiState())
    val donacion: StateFlow<DonacionUiState> = _donacion

    private val _voluntariado = MutableStateFlow(VoluntariadoUiState())
    val voluntariado: StateFlow<VoluntariadoUiState> = _voluntariado

    private val _contacto = MutableStateFlow(ContactoUiState())
    val contacto: StateFlow<ContactoUiState> = _contacto

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro

    val registros: StateFlow<List<Registro>> = registroRepository.registros
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onEsSocioChange(valor: Boolean) {
        _donacion.update { it.copy(esSocio = valor) }
    }

    fun onMontoChange(valor: String) {
        _donacion.update { it.copy(monto = valor, errores = it.errores.copy(monto = null)) }
    }

    fun onDestinoChange(valor: String) {
        _donacion.update { it.copy(destino = valor) }
    }

    fun onDonanteNombreChange(valor: String) {
        _donacion.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onDonanteCorreoChange(valor: String) {
        _donacion.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun validarDonacion(): Boolean {
        val estadoActual = _donacion.value
        val errores = DonacionErrores(
            monto = if ((estadoActual.monto.toIntOrNull() ?: 0) < 2000) "Mínimo $2.000" else null,
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null
        )

        val hayErrores = listOfNotNull(
            errores.monto,
            errores.nombre,
            errores.correo
        ).isNotEmpty()

        _donacion.update { it.copy(errores = errores) }

        if (!hayErrores) {
            guardarRegistro(estadoActual.nombre, if (estadoActual.esSocio) "Socio mensual" else "Donación única")
            _donacion.value = DonacionUiState()
        }

        return !hayErrores
    }

    fun onVoluntarioNombreChange(valor: String) {
        _voluntariado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _voluntariado.update { it.copy(telefono = valor, errores = it.errores.copy(telefono = null)) }
    }

    fun onComunaChange(valor: String) {
        _voluntariado.update { it.copy(comuna = valor, errores = it.errores.copy(comuna = null)) }
    }

    fun onTalleresChange(valor: Boolean) {
        _voluntariado.update { it.copy(talleres = valor) }
    }

    fun onEventosChange(valor: Boolean) {
        _voluntariado.update { it.copy(eventos = valor) }
    }

    fun onComunicacionesChange(valor: Boolean) {
        _voluntariado.update { it.copy(comunicaciones = valor) }
    }

    fun onMentoriaChange(valor: Boolean) {
        _voluntariado.update { it.copy(mentoria = valor) }
    }

    fun onMananasChange(valor: Boolean) {
        _voluntariado.update { it.copy(mananas = valor) }
    }

    fun onTardesChange(valor: Boolean) {
        _voluntariado.update { it.copy(tardes = valor) }
    }

    fun onFinesDeSemanaChange(valor: Boolean) {
        _voluntariado.update { it.copy(finesDeSemana = valor) }
    }

    fun validarVoluntariado(): Boolean {
        val estadoActual = _voluntariado.value
        val errores = VoluntariadoErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            telefono = if (estadoActual.telefono.length != 9) "Debe tener 9 dígitos después de +56" else null,
            comuna = if (estadoActual.comuna.isBlank()) "Campo obligatorio" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.telefono,
            errores.comuna
        ).isNotEmpty()

        _voluntariado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun enviarVoluntariado() {
        guardarRegistro(_voluntariado.value.nombre, "Voluntariado")
        _voluntariado.value = VoluntariadoUiState()
    }

    fun onTipoChange(valor: String) {
        _contacto.update { it.copy(tipo = valor) }
    }

    fun onEmpresaChange(valor: String) {
        _contacto.update { it.copy(empresa = valor) }
    }

    fun onContactoNombreChange(valor: String) {
        _contacto.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onContactoCorreoChange(valor: String) {
        _contacto.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onMensajeChange(valor: String) {
        _contacto.update { it.copy(mensaje = valor, errores = it.errores.copy(mensaje = null)) }
    }

    fun onAceptaContactoChange(valor: Boolean) {
        _contacto.update { it.copy(aceptaContacto = valor, errores = it.errores.copy(aceptaContacto = null)) }
    }

    fun validarContacto(): Boolean {
        val estadoActual = _contacto.value
        val errores = ContactoErrores(
            nombre = if (estadoActual.nombre.isBlank()) "Campo obligatorio" else null,
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
            mensaje = if (estadoActual.mensaje.length < 20) "Escribe al menos 20 caracteres (llevas ${estadoActual.mensaje.length})" else null,
            aceptaContacto = if (!estadoActual.aceptaContacto) "Debes aceptar para continuar" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.mensaje,
            errores.aceptaContacto
        ).isNotEmpty()

        _contacto.update { it.copy(errores = errores) }

        if (!hayErrores) {
            guardarRegistro(estadoActual.nombre, estadoActual.tipo)
            _contacto.value = ContactoUiState()
        }

        return !hayErrores
    }

    private fun guardarRegistro(nombre: String, tipo: String) {
        _registro.value = RegistroUiState(nombre = nombre, tipo = tipo)
        viewModelScope.launch {
            registroRepository.insertarRegistro(
                Registro(
                    nombre = nombre,
                    tipo = tipo,
                    estado = "Recibido"
                )
            )
        }
    }

    class ColaboraViewModelFactory(private val registroRepository: RegistroRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ColaboraViewModel(registroRepository) as T
        }
    }
}
