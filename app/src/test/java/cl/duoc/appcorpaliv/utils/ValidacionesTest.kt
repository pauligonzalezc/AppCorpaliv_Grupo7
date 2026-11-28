package cl.duoc.appcorpaliv.utils

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ValidacionesTest : StringSpec({

    "filtrarNombre quita números, símbolos y saltos de línea" {
        Validaciones.filtrarNombre("Ju4an\n#") shouldBe "Juan"
        Validaciones.filtrarNombre("Ana\nMaria") shouldBe "AnaMaria"
    }

    "filtrarNombre permite apóstrofe y guion" {
        Validaciones.filtrarNombre("O'Higgins") shouldBe "O'Higgins"
        Validaciones.filtrarNombre("O’Higgins") shouldBe "O'Higgins"
        Validaciones.filtrarNombre("Pérez-Cotapos") shouldBe "Pérez-Cotapos"
    }

    "filtrarNombre no permite separadores al inicio ni repetidos" {
        Validaciones.filtrarNombre("  Ana  Luz") shouldBe "Ana Luz"
        Validaciones.filtrarNombre("-Ana--Luz") shouldBe "Ana-Luz"
    }

    "filtrarNombre corta en el largo máximo" {
        Validaciones.filtrarNombre("a".repeat(100)).length shouldBe Validaciones.LARGO_MAXIMO_NOMBRE
    }

    "validarNombre acepta nombres válidos" {
        Validaciones.validarNombre("Paulina") shouldBe null
        Validaciones.validarNombre("María José ") shouldBe null
        Validaciones.validarNombre("D'Angelo") shouldBe null
        Validaciones.validarNombre("Gonzalez-Castro") shouldBe null
    }

    "validarNombre rechaza vacío, corto o terminado en separador" {
        Validaciones.validarNombre("  ", "El apellido es obligatorio") shouldBe "El apellido es obligatorio"
        Validaciones.validarNombre("A") shouldNotBe null
        Validaciones.validarNombre("Ana-") shouldNotBe null
        Validaciones.validarNombre("Ana2") shouldNotBe null
    }
})
