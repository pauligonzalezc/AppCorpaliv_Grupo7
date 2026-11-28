package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.AttachFile
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.EquipoViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrosScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel,
    equipoViewModel : EquipoViewModel
) {
    val listaRegistros by colaboraViewModel.registros.collectAsState()
    val registrosFiltrados by colaboraViewModel.registrosFiltrados.collectAsState()
    val filtroEstado by colaboraViewModel.filtroEstado.collectAsState()
    val sesion by equipoViewModel.estado.collectAsState()
    val cargando by colaboraViewModel.cargandoRegistros.collectAsState()
    val errorCarga by colaboraViewModel.errorRegistros.collectAsState()

    LaunchedEffect(sesion.sesionIniciada) {
        if (sesion.sesionIniciada) colaboraViewModel.cargarRegistros()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de interés") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() })
                    { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
                    IconButton(onClick = { colaboraViewModel.cargarRegistros() }, enabled = !cargando)
                    { Icon(Icons.Rounded.Refresh, contentDescription = "Actualizar") }
                    IconButton(onClick = {
                        equipoViewModel.cerrarSesion()
                        viewModel.navigateTo(Screen.Home, popUpTo = Screen.Home, inclusive = true)
                    })
                    { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = "Cerrar sesión") }
                }
            )
        }
    ) { innerPadding ->
        if (!sesion.sesionIniciada) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(text = "Debes iniciar sesión como equipo para ver los registros", modifier = Modifier.padding(24.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // tabs: null = todos, las otras filtran por estado
                val pestanas = listOf(
                    null to "Todos · ${listaRegistros.size}",
                    "Nuevo" to "Nuevos · ${listaRegistros.count { it.estado == "Nuevo" }}",
                    "Contactado" to "Contactados · ${listaRegistros.count { it.estado == "Contactado" }}",
                    "Cerrado" to "Cerrados · ${listaRegistros.count { it.estado == "Cerrado" }}"
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pestanas.forEach { (estado, texto) ->
                        FilterChip(
                            selected = filtroEstado == estado,
                            onClick = { colaboraViewModel.onFiltroEstadoChange(estado) },
                            label = { Text(texto) }
                        )
                    }
                }


                if (errorCarga != null && listaRegistros.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = errorCarga ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { colaboraViewModel.cargarRegistros() }) { Text("Reintentar") }
                    }
                }

                if (cargando && listaRegistros.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (errorCarga != null && listaRegistros.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Rounded.CloudOff, contentDescription = null, modifier = Modifier.size(48.dp))
                        Text(text = errorCarga ?: "", style = MaterialTheme.typography.bodyMedium)
                        Button(onClick = { colaboraViewModel.cargarRegistros() }) { Text("Reintentar") }
                    }
                } else if (registrosFiltrados.isEmpty()) {
                    Text(
                        text = if (listaRegistros.isEmpty()) "Todavía no hay registros" else "No hay registros en esta pestaña",
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(registrosFiltrados, key = { it.id }) { registro ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        colaboraViewModel.seleccionarRegistro(registro)
                                        viewModel.navigateTo(Screen.RegistroDetalle)
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (registro.tipo) {
                                                "Donación única" -> Icons.Rounded.Favorite
                                                "Socio mensual" -> Icons.Rounded.CardMembership
                                                "Voluntariado" -> Icons.Rounded.Handshake
                                                else -> Icons.Rounded.Apartment
                                            },
                                            contentDescription = null
                                        )
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 12.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text(text = registro.nombre, style = MaterialTheme.typography.titleSmall)
                                        Text(text = registro.tipo, style = MaterialTheme.typography.bodySmall)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "N° ${registro.id}", style = MaterialTheme.typography.labelSmall)
                                            if (registro.adjunto != null) {
                                                Icon(
                                                    Icons.Rounded.AttachFile,
                                                    contentDescription = "Tiene adjunto",
                                                    modifier = Modifier
                                                        .padding(start = 6.dp)
                                                        .size(14.dp)
                                                )
                                            }
                                        }
                                    }

                                    EtiquetaEstado(registro.estado)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// etiqueta de color del estado, se usa en el listado y en el detalle
@Composable
fun EtiquetaEstado(estado: String) {
    Text(
        text = estado,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(
                when (estado) {
                    "Contactado" -> MaterialTheme.colorScheme.secondaryContainer
                    "Cerrado" -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.primaryContainer
                },
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
