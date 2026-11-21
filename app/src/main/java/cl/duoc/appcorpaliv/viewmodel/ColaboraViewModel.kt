package cl.duoc.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.appcorpaliv.data.repository.RegistroRepository
import cl.duoc.appcorpaliv.model.Adjunto
import cl.duoc.appcorpaliv.model.ContactoErrores
import cl.duoc.appcorpaliv.model.ContactoUiState
import cl.duoc.appcorpaliv.model.DonacionErrores
import cl.duoc.appcorpaliv.model.DonacionUiState
import cl.duoc.appcorpaliv.model.Registro
import cl.duoc.appcorpaliv.model.RegistroUiState
import cl.duoc.appcorpaliv.model.VoluntariadoErrores
import cl.duoc.appcorpaliv.model.VoluntariadoUiState
import cl.duoc.appcorpaliv.utils.Validaciones
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ColaboraViewModel(
    private val registroRepository: RegistroRepository
) : ViewModel() {

    private val regexCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

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

    // null = pestaña Todos, si no -> "Nuevo", "Contactado", "Cerrado"
    private val _filtroEstado = MutableStateFlow<String?>(null)
    val filtroEstado: StateFlow<String?> = _filtroEstado

    val registrosFiltrados: StateFlow<List<Registro>> = combine(registroRepository.registros, _filtroEstado) { lista, filtro ->
        if (filtro == null) lista else lista.filter { it.estado == filtro }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _registroSeleccionadoId = MutableStateFlow<Int?>(null)

    // se recalcula con la lista, el detalle refleja al tiro un cambio de estado
    val registroSeleccionado: StateFlow<Registro?> = combine(registroRepository.registros, _registroSeleccionadoId) { lista, id ->
        lista.firstOrNull { it.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onFiltroEstadoChange(estado: String?) {
        _filtroEstado.value = estado
    }

    fun seleccionarRegistro(registro: Registro) {
        _registroSeleccionadoId.value = registro.id
    }

    fun onEsSocioChange(valor: Boolean) {
        _donacion.update { it.copy(esSocio = valor) }
    }

    fun onMontoChange(valor: String) {
        _donacion.update { it.copy(monto = valor.filter { letra -> letra.isDigit() }, errores = it.errores.copy(monto = null)) }
    }

    fun onDestinoChange(valor: String) {
        _donacion.update { it.copy(destino = valor) }
    }

    fun onDonanteNombreChange(valor: String) {
        _donacion.update { it.copy(nombre = Validaciones.filtrarNombre(valor), errores = it.errores.copy(nombre = null)) }
    }

    fun onDonanteApellidoChange(valor: String) {
        _donacion.update { it.copy(apellido = Validaciones.filtrarNombre(valor), errores = it.errores.copy(apellido = null)) }
    }

    fun onDonanteCorreoChange(valor: String) {
        _donacion.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun validarDonacion(): Boolean {
        val estadoActual = _donacion.value
        val errores = DonacionErrores(
            monto = if ((estadoActual.monto.toIntOrNull() ?: 0) < 2000) "Monto mínimo $2.000" else null,
            nombre = Validaciones.validarNombre(estadoActual.nombre),
            apellido = Validaciones.validarNombre(estadoActual.apellido, "El apellido es obligatorio"),
            correo = if (!estadoActual.correo.trim().matches(regexCorreo)) "Correo inválido, por ejemplo: nombre@correo.cl" else null
        )

        val hayErrores = listOfNotNull(
            errores.monto,
            errores.nombre,
            errores.apellido,
            errores.correo
        ).isNotEmpty()

        _donacion.update { it.copy(errores = errores) }

        if (!hayErrores) {
            guardarRegistro(
                nombre = estadoActual.nombre,
                apellido = estadoActual.apellido,
                tipo = if (estadoActual.esSocio) "Socio mensual" else "Donación única",
                correo = estadoActual.correo.trim(),
                detalle = listOf(
                    "Monto: $${estadoActual.monto}${if (estadoActual.esSocio) " al mes" else ""}",
                    "Destino: ${estadoActual.destino}"
                ).joinToString("\n")
            )
            _donacion.value = DonacionUiState()
        }

        return !hayErrores
    }

    fun onVoluntarioNombreChange(valor: String) {
        _voluntariado.update { it.copy(nombre = Validaciones.filtrarNombre(valor), errores = it.errores.copy(nombre = null)) }
    }

    fun onVoluntarioApellidoChange(valor: String) {
        _voluntariado.update { it.copy(apellido = Validaciones.filtrarNombre(valor), errores = it.errores.copy(apellido = null)) }
    }

    fun onTelefonoChange(valor: String) {
        _voluntariado.update { it.copy(telefono = valor.filter { letra -> letra.isDigit() }.take(9), errores = it.errores.copy(telefono = null)) }
    }

    fun onComunaChange(valor: String) {
        _voluntariado.update { it.copy(comuna = Validaciones.filtrarNombre(valor), errores = it.errores.copy(comuna = null)) }
    }

    fun onTalleresChange(valor: Boolean) {
        _voluntariado.update { it.copy(talleres = valor, errores = it.errores.copy(intereses = null)) }
    }

    fun onEventosChange(valor: Boolean) {
        _voluntariado.update { it.copy(eventos = valor, errores = it.errores.copy(intereses = null)) }
    }

    fun onComunicacionesChange(valor: Boolean) {
        _voluntariado.update { it.copy(comunicaciones = valor, errores = it.errores.copy(intereses = null)) }
    }

    fun onMentoriaChange(valor: Boolean) {
        _voluntariado.update { it.copy(mentoria = valor, errores = it.errores.copy(intereses = null)) }
    }

    fun onMananasChange(valor: Boolean) {
        _voluntariado.update { it.copy(mananas = valor, errores = it.errores.copy(disponibilidad = null)) }
    }

    fun onTardesChange(valor: Boolean) {
        _voluntariado.update { it.copy(tardes = valor, errores = it.errores.copy(disponibilidad = null)) }
    }

    fun onFinesDeSemanaChange(valor: Boolean) {
        _voluntariado.update { it.copy(finesDeSemana = valor, errores = it.errores.copy(disponibilidad = null)) }
    }

    fun onAdjuntoChange(valor: Adjunto?) {
        _voluntariado.update { it.copy(adjunto = valor) }
    }

    fun validarVoluntariado(): Boolean {
        val estadoActual = _voluntariado.value
        val errores = VoluntariadoErrores(
            nombre = Validaciones.validarNombre(estadoActual.nombre),
            apellido = Validaciones.validarNombre(estadoActual.apellido, "El apellido es obligatorio"),
            telefono = if (estadoActual.telefono.length != 9) "Debe tener 9 dígitos después de +56" else null,
            comuna = Validaciones.validarNombre(estadoActual.comuna, "La comuna es obligatoria"),
            intereses = if (!estadoActual.talleres && !estadoActual.eventos && !estadoActual.comunicaciones && !estadoActual.mentoria) "Selecciona al menos una opción" else null,
            disponibilidad = if (!estadoActual.mananas && !estadoActual.tardes && !estadoActual.finesDeSemana) "Selecciona al menos una opción" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.apellido,
            errores.telefono,
            errores.comuna,
            errores.intereses,
            errores.disponibilidad
        ).isNotEmpty()

        _voluntariado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun enviarVoluntariado() {
        val estadoActual = _voluntariado.value
        val intereses = listOfNotNull(
            "Talleres".takeIf { estadoActual.talleres },
            "Eventos".takeIf { estadoActual.eventos },
            "Comunicaciones".takeIf { estadoActual.comunicaciones },
            "Mentoría laboral".takeIf { estadoActual.mentoria }
        )
        val disponibilidad = listOfNotNull(
            "Mañanas".takeIf { estadoActual.mananas },
            "Tardes".takeIf { estadoActual.tardes },
            "Fines de semana".takeIf { estadoActual.finesDeSemana }
        )
        guardarRegistro(
            nombre = estadoActual.nombre,
            apellido = estadoActual.apellido,
            tipo = "Voluntariado",
            telefono = "+56 ${estadoActual.telefono}",
            detalle = listOf(
                "Comuna: ${estadoActual.comuna.trim()}",
                "Intereses: ${intereses.joinToString(", ")}",
                "Disponibilidad: ${disponibilidad.joinToString(", ")}"
            ).joinToString("\n"),
            adjunto = estadoActual.adjunto
        )
        _voluntariado.value = VoluntariadoUiState()
    }

    fun onTipoChange(valor: String) {
        _contacto.update { it.copy(tipo = valor) }
    }

    fun onEmpresaChange(valor: String) {
        _contacto.update { it.copy(empresa = valor) }
    }

    fun onContactoNombreChange(valor: String) {
        _contacto.update { it.copy(nombre = Validaciones.filtrarNombre(valor), errores = it.errores.copy(nombre = null)) }
    }

    fun onContactoApellidoChange(valor: String) {
        _contacto.update { it.copy(apellido = Validaciones.filtrarNombre(valor), errores = it.errores.copy(apellido = null)) }
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
            nombre = Validaciones.validarNombre(estadoActual.nombre),
            apellido = Validaciones.validarNombre(estadoActual.apellido, "El apellido es obligatorio"),
            correo = if (!estadoActual.correo.trim().matches(regexCorreo)) "Correo inválido, por ejemplo: nombre@correo.cl" else null,
            mensaje = if (estadoActual.mensaje.length < 20) "Escribe al menos 20 caracteres (llevas ${estadoActual.mensaje.length})" else null,
            aceptaContacto = if (!estadoActual.aceptaContacto) "Debes aceptar para continuar" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.apellido,
            errores.correo,
            errores.mensaje,
            errores.aceptaContacto
        ).isNotEmpty()

        _contacto.update { it.copy(errores = errores) }

        if (!hayErrores) {
            guardarRegistro(
                nombre = estadoActual.nombre,
                apellido = estadoActual.apellido,
                tipo = estadoActual.tipo,
                correo = estadoActual.correo.trim(),
                detalle = listOfNotNull(
                    estadoActual.empresa.trim().takeIf { it.isNotEmpty() }?.let { "Empresa: $it" },
                    "Mensaje: ${estadoActual.mensaje.trim()}"
                ).joinToString("\n")
            )
            _contacto.value = ContactoUiState()
        }

        return !hayErrores
    }

    private fun guardarRegistro(
        nombre: String,
        apellido: String,
        tipo: String,
        correo: String = "",
        telefono: String = "",
        detalle: String = "",
        adjunto: Adjunto? = null
    ) {
        _registro.value = RegistroUiState(nombre = nombre.trim(), tipo = tipo)
        viewModelScope.launch {
            registroRepository.insertarRegistro(
                Registro(
                    nombre = "${nombre.trim()} ${apellido.trim()}",
                    tipo = tipo,
                    estado = "Nuevo",
                    correo = correo,
                    telefono = telefono,
                    detalle = detalle,
                    adjunto = adjunto
                )
            )
        }
    }

    fun cambiarEstado(registro: Registro, nuevoEstado: String) {
        viewModelScope.launch {
            registroRepository.actualizarRegistro(registro.copy(estado = nuevoEstado))
        }
    }

    fun eliminarRegistro(registro: Registro) {
        viewModelScope.launch {
            registroRepository.eliminarRegistro(registro)
        }
    }

    class ColaboraViewModelFactory(private val registroRepository: RegistroRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ColaboraViewModel(registroRepository) as T
        }
    }
}
