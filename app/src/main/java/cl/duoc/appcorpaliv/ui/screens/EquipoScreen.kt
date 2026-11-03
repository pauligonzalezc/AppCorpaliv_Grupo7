package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.viewmodel.EquipoViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    equipoViewModel : EquipoViewModel
) {
    val estado by equipoViewModel.estado.collectAsState()
    var verClave by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
                .imePadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Badge,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = "Acceso equipo",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Solo para el equipo de vinculación de CORPALIV y Alba Lab.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = estado.correo,
                onValueChange = equipoViewModel::onCorreoChange,
                label = { Text("Correo institucional") },
                leadingIcon = { Icon(Icons.Rounded.Email, contentDescription = null) },
                trailingIcon = {
                    if (estado.errores.correo != null) {
                        Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.correo != null,
                supportingText = {
                    estado.errores.correo?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.clave,
                onValueChange = equipoViewModel::onClaveChange,
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Rounded.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { verClave = !verClave }) {
                        Icon(
                            imageVector = if (verClave) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                            contentDescription = if (verClave) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                isError = estado.errores.clave != null,
                supportingText = {
                    estado.errores.clave?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(visible = estado.errores.credenciales != null) {
                Text(
                    text = estado.errores.credenciales ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = {
                    if (equipoViewModel.iniciarSesion()) {
                        viewModel.navigateTo(Screen.Registros, popUpTo = Screen.Equipo, inclusive = true)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }
        }
    }
}
