package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.CardMembership
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Handshake
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.model.Registro
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
    val sesion by equipoViewModel.estado.collectAsState()
    var registroAEliminar by remember { mutableStateOf<Registro?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de interés") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() })
                    { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver") }
                },
                actions = {
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
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                Text(
                    text = "Nuevos · ${listaRegistros.count { it.estado == "Nuevo" }}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
                Text(
                    text = "Contactados · ${listaRegistros.count { it.estado == "Contactado" }}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
                Text(
                    text = "Cerrados · ${listaRegistros.count { it.estado == "Cerrado" }}",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
                }

                if (listaRegistros.isEmpty()) {
                    Text(text = "Todavía no hay registros", modifier = Modifier.padding(vertical = 24.dp))
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listaRegistros, key = { it.id }) { registro ->
                            var menuAbierto by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp),
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
                                        Text(text = "N° ${registro.id}", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Text(
                                        text = registro.estado,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier
                                            .background(
                                                when (registro.estado) {
                                                    "Contactado" -> MaterialTheme.colorScheme.secondaryContainer
                                                    "Cerrado" -> MaterialTheme.colorScheme.tertiaryContainer
                                                    else -> MaterialTheme.colorScheme.primaryContainer
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )

                                    Box {
                                        IconButton(onClick = { menuAbierto = true }) {
                                            Icon(Icons.Rounded.MoreVert, contentDescription = "Opciones del registro")
                                        }
                                        DropdownMenu(
                                            expanded = menuAbierto,
                                            onDismissRequest = { menuAbierto = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Marcar como Nuevo") },
                                                onClick = {
                                                    colaboraViewModel.cambiarEstado(registro, "Nuevo")
                                                    menuAbierto = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Marcar como Contactado") },
                                                onClick = {
                                                    colaboraViewModel.cambiarEstado(registro, "Contactado")
                                                    menuAbierto = false
                                                }
                                            )
                                            DropdownMenuItem(
                                                text = { Text("Marcar como Cerrado") },
                                                onClick = {
                                                    colaboraViewModel.cambiarEstado(registro, "Cerrado")
                                                    menuAbierto = false
                                                }
                                            )
                                            HorizontalDivider()
                                            DropdownMenuItem(
                                                text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                                onClick = {
                                                    registroAEliminar = registro
                                                    menuAbierto = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    registroAEliminar?.let { registro ->
        AlertDialog(
            onDismissRequest = { registroAEliminar = null },
            title = { Text("Eliminar registro") },
            text = { Text("¿Seguro que quieres eliminar el registro de ${registro.nombre}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    colaboraViewModel.eliminarRegistro(registro)
                    registroAEliminar = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { registroAEliminar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
