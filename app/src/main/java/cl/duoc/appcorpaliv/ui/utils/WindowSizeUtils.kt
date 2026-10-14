package cl.duoc.appcorpaliv.ui.utils


import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun obtenerWindowSizeClass(): WindowSizeClass {
    val activity = LocalActivity.current
    return if (activity != null) {
        calculateWindowSizeClass(activity)
    } else {
        // Solo se usa en Preview, donde no hay Activity
        val config = LocalConfiguration.current
        WindowSizeClass.calculateFromSize(
            DpSize(config.screenWidthDp.dp, config.screenHeightDp.dp)
        )
    }
}