package cl.duoc.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.appcorpaliv.model.Producto

class CatalogoViewModel : ViewModel() {

    val productos: List<Producto> = listOf(
        Producto(nombre = "Velas de soya", precio = "$6.500*", categoria = "Productos"),
        Producto(nombre = "Jabón artesanal", precio = "$3.900*", categoria = "Productos"),
        Producto(nombre = "Caja corporativa", precio = "Desde 20 u.", categoria = "Regalos"),
        Producto(nombre = "Ensobrado", precio = "Servicio", categoria = "Servicios")
    )
}
