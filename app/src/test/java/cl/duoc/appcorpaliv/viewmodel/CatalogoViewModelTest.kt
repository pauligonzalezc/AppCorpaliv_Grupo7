package cl.duoc.appcorpaliv.viewmodel

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize

class CatalogoViewModelTest : StringSpec({

    "el catálogo debe tener los 4 productos de Alba Lab con su precio" {
        val viewModel = CatalogoViewModel()

        viewModel.productos shouldHaveSize 4
        viewModel.productos.map { it.nombre } shouldContainExactly
            listOf("Bolsa de tela", "Farol", "Guatero", "Difusor")
        viewModel.productos.map { it.precio } shouldContainExactly
            listOf("$5.000", "$8.900", "$7.500", "$6.900")
    }
})
