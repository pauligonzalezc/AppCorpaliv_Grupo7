package cl.duoc.registros.dto;

import cl.duoc.registros.model.Registro;


public record RegistroResponse(
        Long id,
        String nombre,
        String tipo,
        String estado,
        String correo,
        String telefono,
        String detalle,
        String adjuntoUri,
        String adjuntoTipo,
        String adjuntoNombre,
        Long fecha
) {
    public static RegistroResponse desde(Registro registro) {
        return new RegistroResponse(
                registro.getId(),
                registro.getNombre(),
                registro.getTipo(),
                registro.getEstado(),
                registro.getCorreo(),
                registro.getTelefono(),
                registro.getDetalle(),
                registro.getAdjuntoUri(),
                registro.getAdjuntoTipo(),
                registro.getAdjuntoNombre(),
                registro.getFecha()
        );
    }
}
