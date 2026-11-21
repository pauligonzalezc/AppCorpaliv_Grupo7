package cl.duoc.appcorpaliv.model

sealed interface EnvioEstado {
    data object Inactivo : EnvioEstado
    data object Enviando : EnvioEstado
    data object Enviado : EnvioEstado
    data class Error(val mensaje: String) : EnvioEstado
}
