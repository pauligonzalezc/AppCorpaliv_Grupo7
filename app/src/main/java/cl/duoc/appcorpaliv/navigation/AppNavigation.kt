package cl.duoc.appcorpaliv.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AccessibilityNew
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.VolunteerActivism
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import cl.duoc.appcorpaliv.ui.screens.AccesibilidadScreen
import cl.duoc.appcorpaliv.ui.screens.CatalogoScreen
import cl.duoc.appcorpaliv.ui.screens.ColaboraScreen
import cl.duoc.appcorpaliv.ui.screens.EquipoScreen
import cl.duoc.appcorpaliv.ui.screens.HomeScreen
import cl.duoc.appcorpaliv.ui.screens.ImpactoScreen
import cl.duoc.appcorpaliv.ui.screens.ProblemaScreen
import cl.duoc.appcorpaliv.ui.screens.ProfileScreen
import cl.duoc.appcorpaliv.ui.screens.SettingsScreen
import cl.duoc.appcorpaliv.ui.screens.ProgramasScreen
import cl.duoc.appcorpaliv.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navController : NavHostController,
    viewModel : MainViewModel = viewModel()
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collectLatest { event ->
            when (event){
                is NavigationEvent.NavigateTo -> {
                    navController.navigate(event.route.route){
                        event.popUpToRoute?.let {
                            popUpTo(it.route){
                                inclusive = event.inclusive
                            }
                        }
                        launchSingleTop = event.singleTop
                        restoreState = true
                    }
                }
                is NavigationEvent.PopBackStack -> navController.popBackStack()
                is NavigationEvent.NavigateUp -> navController.navigateUp()
            }
        }
    }



    val items = listOf(Screen.Home,Screen.Programas,Screen.Impacto,Screen.Catalogo,Screen.Colabora)
    // La ruta que se está mostrando; así el ítem marcado siempre coincide con la pantalla
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Corpaliv")},
                actions = {
                    IconButton(onClick = { viewModel.navigateTo(Screen.Accesibilidad) })
                    { Icon(Icons.Rounded.AccessibilityNew, contentDescription = "Accesibilidad")}
                    IconButton(onClick = { viewModel.navigateTo(Screen.Equipo) })
                    { Icon(Icons.Rounded.AccountCircle, contentDescription = "Equipo")}
                }
            )
        },

        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    // Un ícono y un nombre para cada pantalla de la barra
                    val (icono, nombre) = when (screen) {
                        Screen.Home -> Icons.Rounded.Home to "Inicio"
                        Screen.Programas -> Icons.Rounded.School to "Programas"
                        Screen.Impacto -> Icons.Rounded.Insights to "Impacto"
                        Screen.Catalogo -> Icons.Rounded.Storefront to "Catálogo"
                        Screen.Colabora -> Icons.Rounded.VolunteerActivism to "Colabora"
                        else -> Icons.Rounded.Home to screen.route
                    }

                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = { viewModel.navigateTo(screen) },
                        label = { Text(nombre) },
                        icon = {
                            Icon(
                                imageVector = icono,
                                contentDescription = nombre
                            )
                        }
                    )
                }
            }
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(route = Screen.Home.route){
                HomeScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Programas.route){
                ProgramasScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Impacto.route){
                ImpactoScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Catalogo.route){
                CatalogoScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Colabora.route){
                ColaboraScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Profile.route){
                ProfileScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Settings.route){
                SettingsScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Equipo.route){
                EquipoScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Accesibilidad.route){
                AccesibilidadScreen(navController = navController, viewModel = viewModel)
            }
            composable(route = Screen.Problema.route){
                ProblemaScreen(navController = navController, viewModel = viewModel)
            }
        }
    }
}
