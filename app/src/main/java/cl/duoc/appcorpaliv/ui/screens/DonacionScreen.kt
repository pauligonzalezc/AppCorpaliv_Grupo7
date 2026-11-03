package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonacionScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val estado by colaboraViewModel.donacion.collectAsState()
    val colorImpacto by animateColorAsState(
        targetValue = if (estado.esSocio) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donar") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() })
                    { Icon(Icons.Rounded.Close, contentDescription = "Cerrar") }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (estado.esSocio) {
                    OutlinedButton(
                        onClick = { colaboraViewModel.onEsSocioChange(false) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Donación única") }
                    Button(
                        onClick = { colaboraViewModel.onEsSocioChange(true) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Socio mensual") }
                } else {
                    Button(
                        onClick = { colaboraViewModel.onEsSocioChange(false) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Donación única") }
                    OutlinedButton(
                        onClick = { colaboraViewModel.onEsSocioChange(true) },
                        modifier = Modifier.weight(1f)
                    ) { Text("Socio mensual") }
                }
            }

            Text(
                text = if (estado.esSocio) "Monto mensual" else "Monto",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (estado.monto == "5000") {
                    Button(
                        onClick = { colaboraViewModel.onMontoChange("5000") },
                        modifier = Modifier.weight(1f)
                    ) { Text("$5000") }
                } else {
                    OutlinedButton(
                        onClick = { colaboraViewModel.onMontoChange("5000") },
                        modifier = Modifier.weight(1f)
                    ) { Text("$5000") }
                }
                if (estado.monto == "10000") {
                    Button(
                        onClick = { colaboraViewModel.onMontoChange("10000") },
                        modifier = Modifier.weight(1f)
                    ) { Text("$10000") }
                } else {
                    OutlinedButton(
                        onClick = { colaboraViewModel.onMontoChange("10000") },
                        modifier = Modifier.weight(1f)
                    ) { Text("$10000") }
                }
                if (estado.monto == "20000") {
                    Button(
                        onClick = { colaboraViewModel.onMontoChange("20000") },
                        modifier = Modifier.weight(1f)
                    ) { Text("$20000") }
                } else {
                    OutlinedButton(
                        onClick = { colaboraViewModel.onMontoChange("20000") },
                        modifier = Modifier.weight(1f)
                    ) { Text("$20000") }
                }
            }

            OutlinedTextField(
                value = estado.monto,
                onValueChange = colaboraViewModel::onMontoChange,
                label = { Text("Otro monto (CLP, mínimo $2.000)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Rounded.Payments, contentDescription = null) },
                trailingIcon = {
                    if (estado.errores.monto != null) {
                        Icon(Icons.Rounded.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.monto != null,
                supportingText = {
                    estado.errores.monto?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Destino del aporte",
                style = MaterialTheme.typography.titleSmall
            )

            if (estado.destino == "Taller Protegido") {
                Button(
                    onClick = { colaboraViewModel.onDestinoChange("Taller Protegido") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Taller Protegido") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onDestinoChange("Taller Protegido") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Taller Protegido") }
            }
            if (estado.destino == "Escuela Jan Van Dijk") {
                Button(
                    onClick = { colaboraViewModel.onDestinoChange("Escuela Jan Van Dijk") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Escuela Jan Van Dijk") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onDestinoChange("Escuela Jan Van Dijk") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Escuela Jan Van Dijk") }
            }
            if (estado.destino == "Donde más se necesite") {
                Button(
                    onClick = { colaboraViewModel.onDestinoChange("Donde más se necesite") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Donde más se necesite") }
            } else {
                OutlinedButton(
                    onClick = { colaboraViewModel.onDestinoChange("Donde más se necesite") },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Donde más se necesite") }
            }

            OutlinedTextField(
                value = estado.nombre,
                onValueChange = colaboraViewModel::onDonanteNombreChange,
                label = { Text("Nombre") },
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
                onValueChange = colaboraViewModel::onDonanteCorreoChange,
                label = { Text("Correo electrónico") },
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorImpacto)
            ) {
                Text(
                    text = "Con $${estado.monto}${if (estado.esSocio) " al mes" else ""} ayudas a cubrir los materiales de un puesto de trabajo en ${estado.destino}.*",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Button(
                onClick = {
                    if (colaboraViewModel.validarDonacion()) {
                        viewModel.navigateTo(Screen.Confirmacion)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar al pago")
            }
        }
    }
}

