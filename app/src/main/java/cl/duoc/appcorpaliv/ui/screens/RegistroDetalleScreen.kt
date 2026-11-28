package cl.duoc.appcorpaliv.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.model.Adjunto
import cl.duoc.appcorpaliv.utils.ArchivosAdjuntos
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.EquipoViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroDetalleScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel,
    equipoViewModel : EquipoViewModel
) {
    val registro by colaboraViewModel.registroSeleccionado.collectAsState()
    val sesion by equipoViewModel.estado.collectAsState()
    val errorAccion by colaboraViewModel.errorAccion.collectAsState()
    var confirmarEliminar by remember { mutableStateOf(false) }
    var menuAbierto by remember { mutableStateOf(false) }
    var estadoPorConfirmar by remember { mutableStateOf<String?>(null) }

   
    LaunchedEffect(Unit) { colaboraViewModel.limpiarErrorAccion() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del registro") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() })
                    { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { innerPadding ->
        val actual = registro
        if (!sesion.sesionIniciada || actual == null) {
            Text(
                text = if (!sesion.sesionIniciada) "Debes iniciar sesión como equipo para ver el registro" else "Este registro ya no existe",
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(24.dp)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // encabezado

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (actual.tipo) {
                            "Donación única" -> Icons.Rounded.Favorite
                            "Socio mensual" -> Icons.Rounded.CardMembership
                            "Voluntariado" -> Icons.Rounded.Handshake
                            else -> Icons.Rounded.Apartment
                        },
                        contentDescription = null
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = actual.nombre, style = MaterialTheme.typography.titleLarge)
                    Text(text = "${actual.tipo} · N° ${actual.id}", style = MaterialTheme.typography.bodyMedium)
                }
            }

           

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Estado:", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.width(8.dp))
                    EtiquetaEstado(actual.estado)
                    Spacer(Modifier.weight(1f))

                    Box {
                        IconButton(onClick = { menuAbierto = true }) {
                            Icon(Icons.Rounded.MoreVert, contentDescription = "Opciones del registro")
                        }
                        DropdownMenu(
                            expanded = menuAbierto,
                            onDismissRequest = { menuAbierto = false }
                        ) {
                            listOf("Nuevo", "Contactado", "Cerrado").forEach { estado ->
                                DropdownMenuItem(
                                    text = { Text("Marcar como $estado") },
                                    leadingIcon = {
                                        if (actual.estado == estado) Icon(Icons.Rounded.Check, contentDescription = "Estado actual")
                                    },
                                    onClick = {
                                        menuAbierto = false
                                        if (actual.estado != estado) estadoPorConfirmar = estado
                                    }
                                )
                            }
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Eliminar registro", color = MaterialTheme.colorScheme.error) },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                },
                                onClick = {
                                    menuAbierto = false
                                    confirmarEliminar = true
                                }
                            )
                        }
                    }
                }
            }

            errorAccion?.let { mensaje ->
                Text(text = mensaje, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            // datos de contacto

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilaDato(Icons.Rounded.Event, "Recibido", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(actual.fecha)))
                    if (actual.correo.isNotBlank()) FilaDato(Icons.Rounded.Email, "Correo", actual.correo)
                    if (actual.telefono.isNotBlank()) FilaDato(Icons.Rounded.Call, "Teléfono", actual.telefono)

                    actual.detalle.lines().filter { it.isNotBlank() }.forEach { linea ->
                        val etiqueta = linea.substringBefore(": ", "")
                        val valor = linea.substringAfter(": ")
                        FilaDato(Icons.Rounded.Description, etiqueta.ifEmpty { "Detalle" }, valor)
                    }
                }
            }

            // adjunto

            Text(text = "Foto o documento adjunto", style = MaterialTheme.typography.titleSmall)
            val adjunto = actual.adjunto
            if (adjunto == null) {
                Text(text = "Sin adjuntar", style = MaterialTheme.typography.bodyMedium)
            } else {
                VistaAdjunto(adjunto)
            }
        }

        estadoPorConfirmar?.let { nuevoEstado ->
            AlertDialog(
                onDismissRequest = { estadoPorConfirmar = null },
                title = { Text("Cambiar estado") },
                text = { Text("¿Quieres cambiar el estado de ${actual.nombre} de ${actual.estado} a $nuevoEstado?") },
                confirmButton = {
                    TextButton(onClick = {
                        estadoPorConfirmar = null
                        colaboraViewModel.cambiarEstado(actual, nuevoEstado)
                    }) { Text("Cambiar") }
                },
                dismissButton = {
                    TextButton(onClick = { estadoPorConfirmar = null }) { Text("Cancelar") }
                }
            )
        }

        if (confirmarEliminar) {
            AlertDialog(
                onDismissRequest = { confirmarEliminar = false },
                title = { Text("Eliminar registro") },
                text = { Text("¿Seguro que quieres eliminar el registro de ${actual.nombre}? Esta acción no se puede deshacer.") },
                confirmButton = {
                    TextButton(onClick = {
                        confirmarEliminar = false
                        colaboraViewModel.eliminarRegistro(actual) { viewModel.navigateBack() }
                    }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

@Composable
private fun FilaDato(icono: ImageVector, etiqueta: String, valor: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(text = etiqueta, style = MaterialTheme.typography.labelMedium)
            Text(text = valor, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun VistaAdjunto(adjunto: Adjunto) {
    val context = LocalContext.current

    when {
        adjunto.esImagen -> {
            AsyncImage(
                model = adjunto.uri,
                contentDescription = "Foto adjunta",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 320.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { ArchivosAdjuntos.abrir(context, adjunto) }
            )
        }

        adjunto.esPdf -> {
            val vistaPrevia by produceState<Bitmap?>(initialValue = null, adjunto.uri) {
                value = ArchivosAdjuntos.primeraPaginaPdf(adjunto)
            }
            vistaPrevia?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Primera página de ${adjunto.nombre}",
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { ArchivosAdjuntos.abrir(context, adjunto) }
                )
            }
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Description, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = adjunto.nombre, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }

    OutlinedButton(
        onClick = { ArchivosAdjuntos.abrir(context, adjunto) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null)
        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
        Text(if (adjunto.esImagen) "Ver foto completa" else "Abrir documento")
    }
}
