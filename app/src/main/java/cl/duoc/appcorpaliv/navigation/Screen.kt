package cl.duoc.appcorpaliv.navigation

sealed class Screen(val route : String) {

    data object Home : Screen("Inicio")
    data object Programas : Screen("Programas")
    data object Impacto : Screen("Impacto")
    data object Catalogo : Screen("Catalogo")
    data object Colabora : Screen("Colabora")
    data object Profile : Screen("profile_page")
    data object Settings : Screen("settings_page")

    data class Detail(val itemId : String) : Screen("detail_page/{itemId}"){

        fun buildRoute() : String {
            return route.replace("{itemId}",itemId)
        }
    }
}