package cl.duoc.appcorpaliv.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import cl.duoc.appcorpaliv.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi App Kotlin") })
        }
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
            Text(text = "¡Bienvenido!")

            Button(
                onClick = { /* acción futura */ },
                // Cambia de color el boton (fondo(containerColor), texto(contentColor), diabled fondo (disabledContainerColor), disabled texto(disabledContentColor))
                colors = ButtonDefaults.buttonColors(containerColor = Pink80, contentColor = Purple40)
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppCorpaliv_Grupo7Theme {
        HomeScreen()
    }
}