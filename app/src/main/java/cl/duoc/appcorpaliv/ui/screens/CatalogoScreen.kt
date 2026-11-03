package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.RequestQuote
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cl.duoc.appcorpaliv.navigation.Screen
import cl.duoc.appcorpaliv.ui.utils.obtenerWindowSizeClass
import cl.duoc.appcorpaliv.viewmodel.CatalogoViewModel
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    navController : NavController,
    viewModel : MainViewModel = viewModel(),
    catalogoViewModel : CatalogoViewModel,
    colaboraViewModel : ColaboraViewModel
) {
    val windowSizeClass = obtenerWindowSizeClass()
    val columnas = if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) 2 else 4

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo Alba Lab") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    colaboraViewModel.onTipoChange("Comprar productos")
                    viewModel.navigateTo(Screen.Contacto)
                },
                icon = { Icon(Icons.Rounded.RequestQuote, contentDescription = null) },
                text = { Text("Cotizar") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Verified, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Cada compra financia puestos en el Taller Protegido.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(columnas),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(catalogoViewModel.productos, key = { it.nombre }) { producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(
                                    when (producto.categoria) {
                                        "Servicios" -> MaterialTheme.colorScheme.tertiaryContainer
                                        "Regalos" -> MaterialTheme.colorScheme.secondaryContainer
                                        else -> MaterialTheme.colorScheme.primaryContainer
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (producto.categoria) {
                                    "Servicios" -> Icons.Rounded.Handyman
                                    "Regalos" -> Icons.Rounded.Redeem
                                    else -> Icons.Rounded.ShoppingBag
                                },
                                contentDescription = null,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = producto.nombre, style = MaterialTheme.typography.titleSmall)
                            Text(text = producto.precio, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Text(
                text = "* Precios de referencia. No hay pago dentro de la app: toca Cotizar y el equipo te contactará.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }
    }
}
