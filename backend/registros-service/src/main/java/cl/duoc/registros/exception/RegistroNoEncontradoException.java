package cl.duoc.registros.exception;

public class RegistroNoEncontradoException extends RuntimeException {

    public RegistroNoEncontradoException(Long id) {
        super("No existe el registro N° " + id);
    }
}
