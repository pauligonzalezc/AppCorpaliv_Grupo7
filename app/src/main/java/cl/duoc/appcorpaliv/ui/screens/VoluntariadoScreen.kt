package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.model.Adjunto
import cl.duoc.appcorpaliv.model.EnvioEstado
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.utils.ArchivosAdjuntos
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoluntariadoScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val estado by colaboraViewModel.voluntariado.collectAsState()
    val envio by colaboraViewModel.envio.collectAsState()
    EfectosEnvio(colaboraViewModel) { viewModel.navigateTo(Screen.Confirmacion) }
    var paso by rememberSaveable { mutableStateOf(1) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var errorAdjunto by rememberSaveable { mutableStateOf<String?>(null) }

    // adjuntar archivo: imagen o pdf
    val archivoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            scope.launch {
                val adjunto = ArchivosAdjuntos.copiarDocumento(context, uri)
                errorAdjunto = if (adjunto == null) "No se pudo adjuntar el archivo" else null
                if (adjunto != null) colaboraViewModel.onAdjuntoChange(adjunto)
            }
        }
    }

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
                        text = "$paso de 2",
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
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (paso) {
                1 -> {
                    OutlinedTextField(
                        value = estado.nombre,
                        onValueChange = colaboraViewModel::onVoluntarioNombreChange,
                        label = { Text("Nombre *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                        leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                        trailingIcon = {
                            if (estado.errores.nombre != null) {
                                Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        isError = estado.errores.nombre != null,
                        supportingText = {
                            estado.errores.nombre?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = estado.apellido,
                        onValueChange = colaboraViewModel::onVoluntarioApellidoChange,
                        label = { Text("Apellido *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
                        leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
                        trailingIcon = {
                            if (estado.errores.apellido != null) {
                                Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        isError = estado.errores.apellido != null,
                        supportingText = {
                            estado.errores.apellido?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = estado.telefono,
                        onValueChange = colaboraViewModel::onTelefonoChange,
                        label = { Text("Teléfono (+56) *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = { Icon(Icons.Rounded.Call, contentDescription = null) },
                        trailingIcon = {
                            if (estado.errores.telefono != null) {
                                Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
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
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done),
                        leadingIcon = { Icon(Icons.Rounded.LocationOn, contentDescription = null) },
                        trailingIcon = {
                            if (estado.errores.comuna != null) {
                                Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        isError = estado.errores.comuna != null,
                        supportingText = {
                            estado.errores.comuna?.let {
                                Text(it, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(text = "¿En qué te gustaría ayudar? *", style = MaterialTheme.typography.titleMedium)

                    OpcionCheckbox(texto = "Talleres", marcado = estado.talleres, onCambio = colaboraViewModel::onTalleresChange)

                    OpcionCheckbox(texto = "Eventos", marcado = estado.eventos, onCambio = colaboraViewModel::onEventosChange)

                    OpcionCheckbox(texto = "Comunicaciones", marcado = estado.comunicaciones, onCambio = colaboraViewModel::onComunicacionesChange)

                    OpcionCheckbox(texto = "Mentoría laboral", marcado = estado.mentoria, onCambio = colaboraViewModel::onMentoriaChange)

                    AnimatedVisibility(visible = estado.errores.intereses != null) {
                        Text(
                            text = estado.errores.intereses ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(text = "Disponibilidad *", style = MaterialTheme.typography.titleMedium)

                    OpcionCheckbox(texto = "Mañanas", marcado = estado.mananas, onCambio = colaboraViewModel::onMananasChange)

                    OpcionCheckbox(texto = "Tardes", marcado = estado.tardes, onCambio = colaboraViewModel::onTardesChange)

                    OpcionCheckbox(texto = "Fines de semana", marcado = estado.finesDeSemana, onCambio = colaboraViewModel::onFinesDeSemanaChange)

                    AnimatedVisibility(visible = estado.errores.disponibilidad != null) {
                        Text(
                            text = estado.errores.disponibilidad ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.AddAPhoto, contentDescription = null)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(text = "Agregar foto o CV", style = MaterialTheme.typography.titleSmall)
                                    Text(text = "Toma una foto o adjunta una imagen o PDF · opcional", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            AnimatedVisibility(visible = estado.adjunto != null) {
                                estado.adjunto?.let { adjunto ->
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        VistaPreviaAdjunto(adjunto)
                                        TextButton(onClick = { colaboraViewModel.onAdjuntoChange(null) }) {
                                            Text("Quitar adjunto")
                                        }
                                    }
                                }
                            }

                            errorAdjunto?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.navigateTo(Screen.Camara) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.PhotoCamera, contentDescription = null)
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text("Cámara")
                                }
                                OutlinedButton(
                                    onClick = { archivoLauncher.launch(arrayOf("image/*", "application/pdf")) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.AttachFile, contentDescription = null)
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text("Adjuntar")
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { if (colaboraViewModel.validarVoluntariado()) paso = 2 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente")
                    }
                }

                2 -> {
                    Text(text = "Revisa tus datos", style = MaterialTheme.typography.titleMedium)

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Nombre: ${estado.nombre.trim()} ${estado.apellido.trim()}")
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
                            Text("Foto o CV: ${estado.adjunto?.nombre ?: "Sin adjuntar"}")
                        }
                    }

                    estado.adjunto?.let { VistaPreviaAdjunto(it) }

                    ErrorEnvio(envio)

                    Button(
                        onClick = { colaboraViewModel.enviarVoluntariado() },
                        enabled = envio != EnvioEstado.Enviando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ContenidoBotonEnvio(envio, "Enviar")
                    }
                }
            }
        }
    }
}

@Composable
private fun VistaPreviaAdjunto(adjunto: Adjunto) {
    if (adjunto.esImagen) {
        AsyncImage(
            model = adjunto.uri,
            contentDescription = "Foto adjunta",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        )
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Description, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = adjunto.nombre, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
