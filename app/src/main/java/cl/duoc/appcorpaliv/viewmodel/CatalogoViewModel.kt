package cl.duoc.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.appcorpaliv.R
import cl.duoc.appcorpaliv.model.Producto

class CatalogoViewModel : ViewModel() {

    val productos: List<Producto> = listOf(
        Producto(nombre = "Bolsa de tela", precio = "$5.000", imagen = R.drawable.bolsa_alba_lab),
        Producto(nombre = "Farol", precio = "$8.900", imagen = R.drawable.farol_alba_lab),
        Producto(nombre = "Guatero", precio = "$7.500", imagen = R.drawable.guatero_alba_lab),
        Producto(nombre = "Difusor", precio = "$6.900", imagen = R.drawable.difusor_alba_lab)
    )
}
