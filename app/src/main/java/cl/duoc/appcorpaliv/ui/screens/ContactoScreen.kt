package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Business
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun ContactoScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val estado by colaboraViewModel.contacto.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Empresas y contacto") },
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
            Text(
                text = "¿Qué te interesa?",
                style = MaterialTheme.typography.titleSmall
            )

            if (estado.tipo == "Alianza inclusión laboral") {
                Button(
                    onClick = { colaboraViewModel.onTipoChange("Alianza inclusión laboral") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Alianza inclusión laboral") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onTipoChange("Alianza inclusión laboral") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Alianza inclusión laboral") }
            }
            if (estado.tipo == "Donación RSE") {
                Button(
                    onClick = { colaboraViewModel.onTipoChange("Donación RSE") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Donación RSE") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onTipoChange("Donación RSE") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Donación RSE") }
            }
            if (estado.tipo == "Comprar productos") {
                Button(
                    onClick = { colaboraViewModel.onTipoChange("Comprar productos") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Comprar productos") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onTipoChange("Comprar productos") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Comprar productos") }
            }
            if (estado.tipo == "Capacitación") {
                Button(
                    onClick = { colaboraViewModel.onTipoChange("Capacitación") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Capacitación") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onTipoChange("Capacitación") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Capacitación") }
            }
            if (estado.tipo == "Otro") {
                Button(
                    onClick = { colaboraViewModel.onTipoChange("Otro") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Otro") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onTipoChange("Otro") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Otro") }
            }

            OutlinedTextField(
                value = estado.empresa,
                onValueChange = colaboraViewModel::onEmpresaChange,
                label = { Text("Empresa u organización") },
                leadingIcon = { Icon(Icons.Rounded.Business, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = colaboraViewModel::onContactoNombreChange,
                label = { Text("Nombre de contacto *") },
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
                value = estado.correo,
                onValueChange = colaboraViewModel::onContactoCorreoChange,
                label = { Text("Correo *") },
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
                value = estado.mensaje,
                onValueChange = colaboraViewModel::onMensajeChange,
                label = { Text("Mensaje *") },
                leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                trailingIcon = {
                    if (estado.errores.mensaje != null) {
                        Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.mensaje != null,
                supportingText = {
                    estado.errores.mensaje?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = estado.aceptaContacto,
                    onCheckedChange = colaboraViewModel::onAceptaContactoChange
                )
                Spacer(Modifier.width(8.dp))
                Text("Acepto que CORPALIV me contacte")
            }

            AnimatedVisibility(visible = estado.errores.aceptaContacto != null) {
                Text(
                    text = estado.errores.aceptaContacto ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    if (colaboraViewModel.validarContacto()) {
                        viewModel.navigateTo(Screen.Confirmacion)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("Enviar")
            }
        }
    }
}
