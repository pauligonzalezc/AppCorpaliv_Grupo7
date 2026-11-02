package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrosScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    colaboraViewModel : ColaboraViewModel
) {
    val listaRegistros by colaboraViewModel.registros.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis registros") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateBack() })
                    { Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver") }
                }
            )
        }
    ) { innerPadding ->
        if (listaRegistros.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(text = "Todavía no hay registros", modifier = Modifier.padding(24.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaRegistros, key = { it.id }) { registro ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "Registro N° ${registro.id}", style = MaterialTheme.typography.labelMedium)
                            Text(text = registro.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(text = "Tipo: ${registro.tipo}", style = MaterialTheme.typography.bodySmall)
                            Text(text = "Estado: ${registro.estado}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
