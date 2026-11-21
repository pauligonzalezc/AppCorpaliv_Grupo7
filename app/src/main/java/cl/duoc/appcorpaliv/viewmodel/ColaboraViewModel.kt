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
import cl.duoc.appcorpaliv.model.EnvioEstado
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
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

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

    // Registros que vienen del microservicio
    private val _registros = MutableStateFlow<List<Registro>>(emptyList())
    val registros: StateFlow<List<Registro>> = _registros

    private val _cargandoRegistros = MutableStateFlow(false)
    val cargandoRegistros: StateFlow<Boolean> = _cargandoRegistros

    // error al cargar la lista (se muestra con botón Reintentar)
    private val _errorRegistros = MutableStateFlow<String?>(null)
    val errorRegistros: StateFlow<String?> = _errorRegistros

    // error al cambiar estado o eliminar
    private val _errorAccion = MutableStateFlow<String?>(null)
    val errorAccion: StateFlow<String?> = _errorAccion

    // envío de los formularios
    private val _envio = MutableStateFlow<EnvioEstado>(EnvioEstado.Inactivo)
    val envio: StateFlow<EnvioEstado> = _envio

    // null = pestaña Todos, si no -> "Nuevo", "Contactado", "Cerrado"
    private val _filtroEstado = MutableStateFlow<String?>(null)
    val filtroEstado: StateFlow<String?> = _filtroEstado

    val registrosFiltrados: StateFlow<List<Registro>> = combine(_registros, _filtroEstado) { lista, filtro ->
        if (filtro == null) lista else lista.filter { it.estado == filtro }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private val _registroSeleccionadoId = MutableStateFlow<Int?>(null)

    // se recalcula con la lista, el detalle refleja al tiro un cambio de estado
    val registroSeleccionado: StateFlow<Registro?> = combine(_registros, _registroSeleccionadoId) { lista, id ->
        lista.firstOrNull { it.id == id }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    // llama la lista al ms al reintentar y abrir
    fun cargarRegistros() {
        viewModelScope.launch {
            _cargandoRegistros.value = true
            _errorRegistros.value = null
            try {
                _registros.value = registroRepository.obtenerRegistros()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _errorRegistros.value = mensajeDeError(e)
            } finally {
                _cargandoRegistros.value = false
            }
        }
    }

    fun limpiarErrorAccion() {
        _errorAccion.value = null
    }

    // despues de la confirmacjkn o el error de envio
    fun limpiarEnvio() {
        _envio.value = EnvioEstado.Inactivo
    }

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
                ).joinToString("\n"),
                alEnviar = { _donacion.value = DonacionUiState() }
            )
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
            adjunto = estadoActual.adjunto,
            alEnviar = { _voluntariado.value = VoluntariadoUiState() }
        )
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
                ).joinToString("\n"),
                alEnviar = { _contacto.value = ContactoUiState() }
            )
        }

        return !hayErrores
    }

    // envio del registro , se limpia -> confirmación solo esta ok
    private fun guardarRegistro(
        nombre: String,
        apellido: String,
        tipo: String,
        correo: String = "",
        telefono: String = "",
        detalle: String = "",
        adjunto: Adjunto? = null,
        alEnviar: () -> Unit = {}
    ) {
        if (_envio.value == EnvioEstado.Enviando) return
        _envio.value = EnvioEstado.Enviando
        viewModelScope.launch {
            try {
                val creado = registroRepository.crearRegistro(
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
                _registros.update { listOf(creado) + it }
                _registro.value = RegistroUiState(nombre = nombre.trim(), tipo = tipo)
                alEnviar()
                _envio.value = EnvioEstado.Enviado
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _envio.value = EnvioEstado.Error(mensajeDeError(e))
            }
        }
    }

    fun cambiarEstado(registro: Registro, nuevoEstado: String) {
        if (registro.estado == nuevoEstado) return
        _errorAccion.value = null
        viewModelScope.launch {
            try {
                val actualizado = registroRepository.actualizarRegistro(registro.copy(estado = nuevoEstado))
                _registros.update { lista -> lista.map { if (it.id == actualizado.id) actualizado else it } }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _errorAccion.value = "No se pudo cambiar el estado. ${mensajeDeError(e)}"
            }
        }
    }

    // elimina en el servidor
    fun eliminarRegistro(registro: Registro, alEliminar: () -> Unit = {}) {
        _errorAccion.value = null
        viewModelScope.launch {
            try {
                registroRepository.eliminarRegistro(registro)
                _registros.update { lista -> lista.filterNot { it.id == registro.id } }
                alEliminar()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _errorAccion.value = "No se pudo eliminar. ${mensajeDeError(e)}"
            }
        }
    }

    private fun mensajeDeError(e: Exception): String {
        println("Error al comunicarse con el microservicio: $e")
        return when (e) {
            is IOException -> "No se pudo conectar con el servidor. Revisa tu conexión e intenta de nuevo."
            is HttpException -> "El servidor respondió con un error (${e.code()}). Intenta de nuevo."
            else -> "Ocurrió un error inesperado. Intenta de nuevo."
        }
    }

    class ColaboraViewModelFactory(private val registroRepository: RegistroRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ColaboraViewModel(registroRepository) as T
        }
    }
}
