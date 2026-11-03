package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.viewmodel.AccesibilidadViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccesibilidadScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    accesibilidadViewModel : AccesibilidadViewModel
) {
    val estado by accesibilidadViewModel.estado.collectAsState()

    val textoTamano = when {
        estado.tamanoTexto < 0.9f -> "Pequeño"
        estado.tamanoTexto < 1.1f -> "Normal"
        estado.tamanoTexto < 1.2f -> "Grande"
        else -> "Muy grande"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Accesibilidad") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() })
                    { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AccessibilityNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = "Ajusta la app a tu manera",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Puedes cambiar esto cuando quieras.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row {
                        Text(
                            text = "Tamaño del texto",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = textoTamano, style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "A", fontSize = 12.sp)
                        Slider(
                            value = estado.tamanoTexto,
                            onValueChange = accesibilidadViewModel::onTamanoTextoChange,
                            valueRange = 0.85f..1.3f,
                            steps = 2,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        )
                        Text(text = "A", fontSize = 22.sp)
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Alto contraste", style = MaterialTheme.typography.titleSmall)
                            Text(text = "Colores con contraste 7:1", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = estado.altoContraste,
                            onCheckedChange = accesibilidadViewModel::onAltoContrasteChange
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Leer en voz alta", style = MaterialTheme.typography.titleSmall)
                            Text(text = "Botón «Escuchar» en los textos", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = estado.leerEnVozAlta,
                            onCheckedChange = accesibilidadViewModel::onLeerEnVozAltaChange
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Reducir animaciones", style = MaterialTheme.typography.titleSmall)
                            Text(text = "Transiciones más simples", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(
                            checked = estado.reducirAnimaciones,
                            onCheckedChange = accesibilidadViewModel::onReducirAnimacionesChange
                        )
                    }
                }
            }

            Button(
                onClick = { accesibilidadViewModel.guardarPreferencias() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            AnimatedVisibility(visible = estado.mostrarMensaje) {
                Text(
                    text = "¡Preferencias guardadas!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
