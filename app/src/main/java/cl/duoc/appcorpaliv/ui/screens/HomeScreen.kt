package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime. Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import cl.duoc.appcorpaliv.R
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.ui.utils.obtenerWindowSizeClass
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel()
) {

    Scaffold(
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            // Separa cada elemento de la columna con 20dp de forma uniforme y centrado en el medio
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            // Alinea en forma horizontal en el Centro
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = _root_ide_package_.cl.duoc.appcorpaliv.ui.theme.Purple40)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Tuki")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Inclusión real para personas con discapacidad múltiple")
                }
            }

            Text(
                text = "¡Bienvenido!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                onClick = { viewModel.navigateTo(Screen.Settings) },
                // Cambia de color el boton (fondo(containerColor), texto(contentColor), diabled fondo (disabledContainerColor), disabled texto(disabledContentColor))
                colors = ButtonDefaults.buttonColors(containerColor = _root_ide_package_.cl.duoc.appcorpaliv.ui.theme.Pink80, contentColor = _root_ide_package_.cl.duoc.appcorpaliv.ui.theme.Purple40)
            ) {
                Text("Presióname")
            }

            Row(verticalAlignment = Alignment.CenterVertically){
                var checkedState by remember { mutableStateOf(false) }
                Checkbox(
                    checked = checkedState,
                    onCheckedChange = {checkedState = it},
                )
                Text("Checkbox")
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Fit
            )

            Text("¡Bienvenido a la Pagina de Inicio (MVVM)!")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.navigateTo(Screen.Settings)}){
                Text("Ir a Configuración")
            }

        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val viewModel : MainViewModel = viewModel()
    val navController = rememberNavController()

    HomeScreen(viewModel = viewModel, navController = navController)
}

