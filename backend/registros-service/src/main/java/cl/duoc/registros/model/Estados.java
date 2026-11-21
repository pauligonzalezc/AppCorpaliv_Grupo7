package cl.duoc.registros.model;

import java.util.List;


public final class Estados {

    public static final String NUEVO = "Nuevo";
    public static final String CONTACTADO = "Contactado";
    public static final String CERRADO = "Cerrado";

    public static final List<String> TODOS = List.of(NUEVO, CONTACTADO, CERRADO);

    /** Expresión usada para validar el estado que llega en las peticiones. */
    public static final String PATRON = "Nuevo|Contactado|Cerrado";

    private Estados() {
    }
}
