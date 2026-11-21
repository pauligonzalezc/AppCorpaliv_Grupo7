package cl.duoc.registros.dto;

import cl.duoc.registros.model.Estados;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// info que envia la app para crear o actualizar 

public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "El tipo es obligatorio")
        @Size(max = 60, message = "El tipo no puede superar 60 caracteres")
        String tipo,

        @Pattern(regexp = Estados.PATRON, message = "El estado debe ser Nuevo, Contactado o Cerrado")
        String estado,

        @Email(message = "Correo inválido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String correo,

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,

        String detalle,

        @Size(max = 500, message = "La ubicación del adjunto es demasiado larga")
        String adjuntoUri,

        @Size(max = 100, message = "El tipo del adjunto es demasiado largo")
        String adjuntoTipo,

        @Size(max = 255, message = "El nombre del adjunto es demasiado largo")
        String adjuntoNombre
) {
}
