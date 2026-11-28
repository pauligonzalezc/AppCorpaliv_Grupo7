package cl.duoc.appcorpaliv.data.remote

import cl.duoc.appcorpaliv.model.Adjunto
import cl.duoc.appcorpaliv.model.Registro
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


class RegistroDtoTest : StringSpec({

    "toDomain() debe convertir un DTO completo con su adjunto" {
        val dto = RegistroDto(
            id = 4, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Contactado",
            correo = "ana@correo.cl", telefono = "+56 912345678", detalle = "Comuna: Ñuñoa",
            adjuntoUri = "file:///foto.jpg", adjuntoTipo = "image/jpeg", adjuntoNombre = "foto.jpg",
            fecha = 500L
        )

        val registro = dto.toDomain()

        registro.id shouldBe 4
        registro.estado shouldBe "Contactado"
        registro.correo shouldBe "ana@correo.cl"
        registro.fecha shouldBe 500L
        registro.adjunto shouldBe Adjunto(uri = "file:///foto.jpg", tipo = "image/jpeg", nombre = "foto.jpg")
        registro.adjunto?.esImagen shouldBe true
    }

    "toDomain() debe usar valores por defecto cuando faltan campos" {
        val registro = RegistroDto(nombre = "Luis", tipo = "Donación única").toDomain()

        registro.id shouldBe 0
        registro.estado shouldBe "Nuevo"
        registro.correo shouldBe ""
        registro.telefono shouldBe ""
        registro.detalle shouldBe ""
        registro.adjunto shouldBe null
        registro.fecha shouldBe 0L
    }

    "toDomain() debe poner un nombre genérico al adjunto si no viene" {
        val registro = RegistroDto(
            nombre = "Luis", tipo = "Voluntariado",
            adjuntoUri = "file:///doc.pdf", adjuntoTipo = "application/pdf"
        ).toDomain()

        registro.adjunto?.nombre shouldBe "Archivo adjunto"
        registro.adjunto?.esPdf shouldBe true
    }

    "toDto() debe copiar los datos y el adjunto pero no el id ni la fecha" {
        val registro = Registro(
            id = 9, nombre = "Ana Pérez", tipo = "Voluntariado", estado = "Nuevo",
            adjunto = Adjunto(uri = "file:///doc.pdf", tipo = "application/pdf", nombre = "cv.pdf"),
            fecha = 999L
        )

        val dto = registro.toDto()

        dto.id shouldBe null
        dto.fecha shouldBe null
        dto.nombre shouldBe "Ana Pérez"
        dto.adjuntoUri shouldBe "file:///doc.pdf"
        dto.adjuntoTipo shouldBe "application/pdf"
        dto.adjuntoNombre shouldBe "cv.pdf"
    }

    "toDto() sin adjunto debe dejar los campos del adjunto vacíos" {
        val dto = Registro(nombre = "Luis", tipo = "Donación única", estado = "Nuevo").toDto()

        dto.adjuntoUri shouldBe null
        dto.adjuntoTipo shouldBe null
        dto.adjuntoNombre shouldBe null
    }
})
