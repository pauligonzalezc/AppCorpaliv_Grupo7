package cl.duoc.appcorpaliv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cl.duoc.appcorpaliv.data.local.AppDataBase
import cl.duoc.appcorpaliv.data.repository.RegistroRepository
import cl.duoc.appcorpaliv.navigation.AppNavigation
import cl.duoc.appcorpaliv.ui.theme.AppCorpaliv_Grupo7Theme
import cl.duoc.appcorpaliv.viewmodel.AccesibilidadViewModel
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {
    private val colaboraViewModel: ColaboraViewModel by viewModels {
        val database = AppDataBase.getInstance(applicationContext)
        val repository = RegistroRepository(database.registroDao())
        ColaboraViewModel.ColaboraViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCorpaliv_Grupo7Theme {
                val viewModel : MainViewModel = viewModel()
                val accesibilidadViewModel : AccesibilidadViewModel = viewModel()
                val navController = rememberNavController()
                val accesibilidad by accesibilidadViewModel.estado.collectAsState()
                val densidad = LocalDensity.current

                CompositionLocalProvider(
                    LocalDensity provides Density(densidad.density, densidad.fontScale * accesibilidad.tamanoTexto)
                ) {
                    AppNavigation(viewModel = viewModel, navController = navController, colaboraViewModel = colaboraViewModel, accesibilidadViewModel = accesibilidadViewModel)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppCorpaliv_Grupo7Theme {
        val viewModel : MainViewModel = viewModel()
        val navController = rememberNavController()
        val repository = RegistroRepository(AppDataBase.getInstance(LocalContext.current).registroDao())

        AppNavigation(viewModel = viewModel, navController = navController, colaboraViewModel = ColaboraViewModel(repository), accesibilidadViewModel = viewModel())
    }
}
