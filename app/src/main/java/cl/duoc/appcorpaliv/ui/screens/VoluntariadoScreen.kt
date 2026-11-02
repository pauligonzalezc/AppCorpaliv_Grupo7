package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoluntariadoScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val estado by colaboraViewModel.voluntariado.collectAsState()
    var paso by rememberSaveable { mutableStateOf(1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voluntariado") },
                navigationIcon = {
                    IconButton(onClick = { if (paso > 1) paso = paso - 1 else viewModel.navigateBack() })
                    { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    Text(
                        text = "$paso de 3",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(end = 16.dp)
                    )
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
            when (paso) {
                1 -> {
                    Text(text = "Tus datos", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = estado.nombre,
                        onValueChange = colaboraViewModel::onVoluntarioNombreChange,
                        label = { Text("Nombre completo *") },
                        isError = estado.errores.nombre != null,
                        supportingText = {
                            estado.errores.nombre?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = estado.telefono,
                        onValueChange = colaboraViewModel::onTelefonoChange,
                        label = { Text("Teléfono (+56) *") },
                        isError = estado.errores.telefono != null,
                        supportingText = {
                            estado.errores.telefono?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = estado.comuna,
                        onValueChange = colaboraViewModel::onComunaChange,
                        label = { Text("Comuna *") },
                        isError = estado.errores.comuna != null,
                        supportingText = {
                            estado.errores.comuna?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { if (colaboraViewModel.validarVoluntariado()) paso = 2 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente")
                    }
                }

                2 -> {
                    Text(text = "¿En qué te gustaría ayudar?", style = MaterialTheme.typography.titleMedium)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.talleres,
                            onCheckedChange = colaboraViewModel::onTalleresChange
                        )
                        Text("Talleres")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.eventos,
                            onCheckedChange = colaboraViewModel::onEventosChange
                        )
                        Text("Eventos")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.comunicaciones,
                            onCheckedChange = colaboraViewModel::onComunicacionesChange
                        )
                        Text("Comunicaciones")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.mentoria,
                            onCheckedChange = colaboraViewModel::onMentoriaChange
                        )
                        Text("Mentoría laboral")
                    }

                    Text(text = "Disponibilidad", style = MaterialTheme.typography.titleMedium)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.mananas,
                            onCheckedChange = colaboraViewModel::onMananasChange
                        )
                        Text("Mañanas")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.tardes,
                            onCheckedChange = colaboraViewModel::onTardesChange
                        )
                        Text("Tardes")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = estado.finesDeSemana,
                            onCheckedChange = colaboraViewModel::onFinesDeSemanaChange
                        )
                        Text("Fines de semana")
                    }

                    Button(
                        onClick = { paso = 3 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente")
                    }
                }

                3 -> {
                    Text(text = "Revisa tus datos", style = MaterialTheme.typography.titleMedium)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Nombre: ${estado.nombre}")
                            Text("Teléfono: +56 ${estado.telefono}")
                            Text("Comuna: ${estado.comuna}")
                            Text("Intereses:")
                            if (estado.talleres) {
                                Text("- Talleres")
                            }
                            if (estado.eventos) {
                                Text("- Eventos")
                            }
                            if (estado.comunicaciones) {
                                Text("- Comunicaciones")
                            }
                            if (estado.mentoria) {
                                Text("- Mentoría laboral")
                            }
                            Text("Disponibilidad:")
                            if (estado.mananas) {
                                Text("- Mañanas")
                            }
                            if (estado.tardes) {
                                Text("- Tardes")
                            }
                            if (estado.finesDeSemana) {
                                Text("- Fines de semana")
                            }
                        }
                    }

                    Button(
                        onClick = {
                            colaboraViewModel.enviarVoluntariado()
                            viewModel.navigateTo(Screen.Confirmacion)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enviar")
                    }
                }
            }
        }
    }
}
