package cl.duoc.appcorpaliv

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cl.duoc.appcorpaliv.data.repository.RegistroRepository
import cl.duoc.appcorpaliv.navigation.AppNavigation
import cl.duoc.appcorpaliv.ui.theme.AppCorpaliv_Grupo7Theme
import cl.duoc.appcorpaliv.viewmodel.AccesibilidadViewModel
import cl.duoc.appcorpaliv.viewmodel.ColaboraViewModel
import cl.duoc.appcorpaliv.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {
    private val colaboraViewModel: ColaboraViewModel by viewModels {
        ColaboraViewModel.ColaboraViewModelFactory(RegistroRepository())
    }

    // pide permiso para conectarse a local, donde corre el microservicio
    private val permisoRedLocal = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private fun pedirPermisoRedLocal() {
        if (Build.VERSION.SDK_INT >= 37 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCAL_NETWORK) != PackageManager.PERMISSION_GRANTED
        ) {
            permisoRedLocal.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        splash.setOnExitAnimationListener { splashScreenView ->
            splashScreenView.view
                .animate()
                .alpha(0f)                 // fade out
                .setDuration(400L)
                .withEndAction { splashScreenView.remove() }
                .start()
        }

        enableEdgeToEdge()
        pedirPermisoRedLocal()
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
        AppNavigation(viewModel = viewModel, navController = navController, colaboraViewModel = ColaboraViewModel(RegistroRepository()), accesibilidadViewModel = viewModel())
    }
}
