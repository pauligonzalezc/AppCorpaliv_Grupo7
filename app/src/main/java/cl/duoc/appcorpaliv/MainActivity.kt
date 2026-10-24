package cl.duoc.appcorpaliv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import cl.duoc.appcorpaliv.navigation.AppNavigation
import cl.duoc.appcorpaliv.ui.theme.AppCorpaliv_Grupo7Theme
import cl.duoc.appcorpaliv.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCorpaliv_Grupo7Theme {
                val viewModel : MainViewModel = viewModel()
                val navController = rememberNavController()

                AppNavigation(viewModel = viewModel, navController = navController)
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

        AppNavigation(viewModel = viewModel, navController = navController)
    }
}
