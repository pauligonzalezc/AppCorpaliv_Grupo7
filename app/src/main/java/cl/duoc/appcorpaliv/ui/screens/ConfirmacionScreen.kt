package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmacionScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val registro by colaboraViewModel.registro.collectAsState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn() + fadeIn()
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Registro enviado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(96.dp)
                )
            }

            Text(
                text = "¡Gracias, ${registro.nombre}!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Recibimos tu registro. El equipo te escribirá en un máximo de 3 días hábiles.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
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
                        Text(text = "Tipo", modifier = Modifier.weight(1f))
                        Text(text = registro.tipo, style = MaterialTheme.typography.titleSmall)
                    }
                    Row {
                        Text(text = "Estado", modifier = Modifier.weight(1f))
                        Text(text = "Recibido", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }

            Button(
                onClick = { viewModel.navigateTo(Screen.Registros, popUpTo = Screen.Home) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver mis registros")
            }

            OutlinedButton(
                onClick = { viewModel.navigateTo(Screen.Home, popUpTo = Screen.Home, inclusive = true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al inicio")
            }
        }
    }
}
