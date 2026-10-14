package cl.duoc.appcorpaliv.ui.screens

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime. Composable
import cl.duoc.appcorpaliv.ui.screens.HomeScreenCompacta
import cl.duoc.appcorpaliv.ui.screens.HomeScreenExpandida
import cl.duoc.appcorpaliv.ui.screens.HomeScreenMediana
import cl.duoc.appcorpaliv.ui.utils.obtenerWindowSizeClass

@Composable
fun HomeScreen() {
    val windowSizeClass = obtenerWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompacta()
        WindowWidthSizeClass.Medium -> HomeScreenMediana()
        WindowWidthSizeClass.Expanded -> HomeScreenExpandida()
    }
}