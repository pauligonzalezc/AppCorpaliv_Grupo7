package cl.duoc.appcorpaliv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.appcorpaliv.ui.theme.AppCorpaliv_Grupo7Theme
import cl.duoc.appcorpaliv.ui.theme.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppCorpaliv_Grupo7Theme {
                HomeScreen()
            }
        }
        
    }
}

