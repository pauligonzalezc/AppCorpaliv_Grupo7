package cl.duoc.registros.controller;

import cl.duoc.registros.dto.RegistroRequest;
import cl.duoc.registros.exception.RegistroNoEncontradoException;
import cl.duoc.registros.model.Registro;
import cl.duoc.registros.service.RegistroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistroController.class)
class RegistroControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private RegistroService service;

    private static final String JSON_VALIDO = """
            {
              "nombre": "Ana O'Brien",
              "tipo": "Voluntariado",
              "correo": "ana@correo.cl",
              "telefono": "+56 912345678",
              "detalle": "Comuna: Maipú",
              "adjuntoUri": "file:///foto.jpg",
              "adjuntoTipo": "image/jpeg",
              "adjuntoNombre": "foto.jpg"
            }
            """;

    private static Registro registro(Long id, String estado) {
        Registro r = new Registro();
        r.setId(id);
        r.setNombre("Ana O'Brien");
        r.setTipo("Voluntariado");
        r.setEstado(estado);
        r.setCorreo("ana@correo.cl");
        r.setTelefono("+56 912345678");
        r.setDetalle("Comuna: Maipú");
        r.setAdjuntoNombre("foto.jpg");
        r.setFecha(1000L);
        return r;
    }

    @Test
    void listarDevuelveLosRegistros() throws Exception {
        when(service.listar(null)).thenReturn(List.of(registro(1L, "Nuevo"), registro(2L, "Cerrado")));

        mvc.perform(get("/api/registros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Ana O'Brien"))
                .andExpect(jsonPath("$[1].estado").value("Cerrado"));
    }

    @Test
    void listarPorEstadoPasaElFiltro() throws Exception {
        when(service.listar("Contactado")).thenReturn(List.of(registro(3L, "Contactado")));

        mvc.perform(get("/api/registros").param("estado", "Contactado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3));
    }

    @Test
    void buscarDevuelveElRegistro() throws Exception {
        when(service.buscar(1L)).thenReturn(registro(1L, "Nuevo"));

        mvc.perform(get("/api/registros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Voluntariado"))
                .andExpect(jsonPath("$.adjuntoNombre").value("foto.jpg"))
                .andExpect(jsonPath("$.fecha").value(1000));
    }

    @Test
    void buscarInexistenteResponde404() throws Exception {
        when(service.buscar(99L)).thenThrow(new RegistroNoEncontradoException(99L));

        mvc.perform(get("/api/registros/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe el registro N° 99"));
    }

    @Test
    void crearResponde201() throws Exception {
        when(service.crear(any(RegistroRequest.class))).thenReturn(registro(1L, "Nuevo"));

        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("Nuevo"));
    }

    @Test
    void crearSinNombreResponde400ConElCampo() throws Exception {
        String json = """
                { "nombre": "  ", "tipo": "Voluntariado", "correo": "no-es-correo" }
                """;

        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.nombre").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.campos.correo").value("Correo inválido"));
        verify(service, never()).crear(any());
    }

    @Test
    void crearConJsonMalFormadoResponde400() throws Exception {
        mvc.perform(post("/api/registros").contentType(MediaType.APPLICATION_JSON).content("{ esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El cuerpo de la petición no es un JSON válido"));
    }

    @Test
    void actualizarDevuelveElRegistroActualizado() throws Exception {
        when(service.actualizar(eq(1L), any(RegistroRequest.class))).thenReturn(registro(1L, "Contactado"));
        String json = JSON_VALIDO.replace("\"tipo\": \"Voluntariado\",", "\"tipo\": \"Voluntariado\", \"estado\": \"Contactado\",");

        mvc.perform(put("/api/registros/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("Contactado"));
    }

    @Test
    void actualizarConEstadoInvalidoResponde400() throws Exception {
        String json = JSON_VALIDO.replace("\"tipo\": \"Voluntariado\",", "\"tipo\": \"Voluntariado\", \"estado\": \"Perdido\",");

        mvc.perform(put("/api/registros/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.estado").value("El estado debe ser Nuevo, Contactado o Cerrado"));
    }

    @Test
    void eliminarResponde204() throws Exception {
        mvc.perform(delete("/api/registros/1"))
                .andExpect(status().isNoContent());
        verify(service).eliminar(1L);
    }

    @Test
    void eliminarInexistenteResponde404() throws Exception {
        doThrow(new RegistroNoEncontradoException(8L)).when(service).eliminar(8L);

        mvc.perform(delete("/api/registros/8"))
                .andExpect(status().isNotFound());
    }
}
