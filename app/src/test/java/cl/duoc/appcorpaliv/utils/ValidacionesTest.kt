package cl.duoc.appcorpaliv.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidacionesTest {

    @Test
    fun filtrarNombre_quitaNumerosSimbolosYSaltosDeLinea() {
        assertEquals("Juan", Validaciones.filtrarNombre("Ju4an\n#"))
        assertEquals("AnaMaria", Validaciones.filtrarNombre("Ana\nMaria"))
    }

    @Test
    fun filtrarNombre_permiteApostrofeYGuion() {
        assertEquals("O'Higgins", Validaciones.filtrarNombre("O'Higgins"))
        assertEquals("O'Higgins", Validaciones.filtrarNombre("O’Higgins"))
        assertEquals("Pérez-Cotapos", Validaciones.filtrarNombre("Pérez-Cotapos"))
    }

    @Test
    fun filtrarNombre_noPermiteSeparadoresAlInicioNiRepetidos() {
        assertEquals("Ana Luz", Validaciones.filtrarNombre("  Ana  Luz"))
        assertEquals("Ana-Luz", Validaciones.filtrarNombre("-Ana--Luz"))
    }

    @Test
    fun filtrarNombre_cortaEnLargoMaximo() {
        assertEquals(Validaciones.LARGO_MAXIMO_NOMBRE, Validaciones.filtrarNombre("a".repeat(100)).length)
    }

    @Test
    fun validarNombre_aceptaNombresValidos() {
        assertNull(Validaciones.validarNombre("Paulina"))
        assertNull(Validaciones.validarNombre("María José "))
        assertNull(Validaciones.validarNombre("D'Angelo"))
        assertNull(Validaciones.validarNombre("Gonzalez-Castro"))
    }

    @Test
    fun validarNombre_rechazaVacioCortoOTerminadoEnSeparador() {
        assertEquals("El apellido es obligatorio", Validaciones.validarNombre("  ", "El apellido es obligatorio"))
        assertNotNull(Validaciones.validarNombre("A"))
        assertNotNull(Validaciones.validarNombre("Ana-"))
        assertNotNull(Validaciones.validarNombre("Ana2"))
    }
}
