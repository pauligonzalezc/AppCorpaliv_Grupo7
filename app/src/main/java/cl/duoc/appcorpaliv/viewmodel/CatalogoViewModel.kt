package cl.duoc.appcorpaliv.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.appcorpaliv.model.Producto

class CatalogoViewModel : ViewModel() {

    val productos: List<Producto> = listOf(
        Producto(nombre = "Velas de soya", precio = "$6.500*", categoria = "Productos"),
        Producto(nombre = "Jabón artesanal", precio = "$3.900*", categoria = "Productos"),
        Producto(nombre = "Cuaderno reciclado", precio = "$4.500*", categoria = "Productos"),
        Producto(nombre = "Bolsa de tela", precio = "$5.000*", categoria = "Productos"),
        Producto(nombre = "Ensobrado", precio = "Servicio", categoria = "Servicios"),
        Producto(nombre = "Armado de kits", precio = "Servicio", categoria = "Servicios"),
        Producto(nombre = "Etiquetado", precio = "Servicio", categoria = "Servicios"),
        Producto(nombre = "Caja corporativa", precio = "Desde 20 u.", categoria = "Regalos"),
        Producto(nombre = "Kit de bienvenida", precio = "Desde 10 u.", categoria = "Regalos")
    )
}
