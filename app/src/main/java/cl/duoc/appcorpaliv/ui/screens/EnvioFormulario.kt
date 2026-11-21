package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cl.duoc.appcorpaliv.model.EnvioEstado
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel

// escucha el form enviado si confirma va a onEnviado

@Composable
fun EfectosEnvio(colaboraViewModel: ColaboraViewModel, onEnviado: () -> Unit) {
    val envio by colaboraViewModel.envio.collectAsState()

    LaunchedEffect(Unit) {
        if (envio != EnvioEstado.Enviando) colaboraViewModel.limpiarEnvio()
    }
    LaunchedEffect(envio) {
        if (envio == EnvioEstado.Enviado) {
            colaboraViewModel.limpiarEnvio()
            onEnviado()
        }
    }
}

@Composable
fun ErrorEnvio(envio: EnvioEstado) {
    if (envio is EnvioEstado.Error) {
        Text(
            text = envio.mensaje,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// "Enviando…" para esperar al servidor
@Composable
fun ContenidoBotonEnvio(envio: EnvioEstado, texto: String, icono: ImageVector? = null) {
    if (envio == EnvioEstado.Enviando) {
        CircularProgressIndicator(
            modifier = Modifier.size(18.dp),
            strokeWidth = 2.dp,
            color = LocalContentColor.current
        )
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        Text("Enviando…")
    } else {
        if (icono != null) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        }
        Text(texto)
    }
}
