package cl.duoc.appcorpaliv.navigation

sealed class Screen(val route : String) {

    data object Home : Screen("Inicio")
    data object Programas : Screen("Programas")
    data object Impacto : Screen("Impacto")
    data object Catalogo : Screen("Catalogo")
    data object Colabora : Screen("Colabora")
    data object Profile : Screen("Profile")
    data object Settings : Screen("Settings")
    data object Equipo : Screen("Equipo")
    data object Accesibilidad : Screen("Accesibilidad")

    data class Detail(val itemId : String) : Screen("detail_page/{itemId}"){

        fun buildRoute() : String {
            return route.replace("{itemId}",itemId)
        }
    }
}