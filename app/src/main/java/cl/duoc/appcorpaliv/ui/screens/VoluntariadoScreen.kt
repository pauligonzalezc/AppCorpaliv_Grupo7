package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import android.net.Uri
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
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel
import coil.compose.AsyncImage
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoluntariadoScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val estado by colaboraViewModel.voluntariado.collectAsState()
    var paso by rememberSaveable { mutableStateOf(1) }
    val context = LocalContext.current
    var uriCamara by remember { mutableStateOf<Uri?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            colaboraViewModel.onFotoChange(uri)
        }
    }

    val camaraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        if (exito) {
            colaboraViewModel.onFotoChange(uriCamara)
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
                        label = { Text("Nombre completo *") },
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
                                    Text(text = "Cámara o galería · opcional", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            AnimatedVisibility(visible = estado.foto != null) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AsyncImage(
                                        model = estado.foto,
                                        contentDescription = "Foto adjunta",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                    )
                                    TextButton(onClick = { colaboraViewModel.onFotoChange(null) }) {
                                        Text("Quitar foto")
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        val archivo = File(context.cacheDir, "foto_${System.currentTimeMillis()}.jpg")
                                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
                                        uriCamara = uri
                                        camaraLauncher.launch(uri)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.PhotoCamera, contentDescription = null)
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text("Cámara")
                                }
                                OutlinedButton(
                                    onClick = { galeriaLauncher.launch("image/*") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Rounded.PhotoLibrary, contentDescription = null)
                                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                    Text("Galería")
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
                            Text("Foto o CV: ${if (estado.foto != null) "Adjunta" else "Sin adjuntar"}")
                        }
                    }

                    if (estado.foto != null) {
                        AsyncImage(
                            model = estado.foto,
                            contentDescription = "Foto adjunta",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
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
