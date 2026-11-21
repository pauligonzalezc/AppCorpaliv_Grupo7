package cl.duoc.registros.service;

import cl.duoc.registros.dto.RegistroRequest;
import cl.duoc.registros.exception.RegistroNoEncontradoException;
import cl.duoc.registros.model.Registro;
import cl.duoc.registros.repository.RegistroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroServiceTest {

    @Mock
    private RegistroRepository repository;

    @InjectMocks
    private RegistroService service;

    private static RegistroRequest solicitud(String estado) {
        return new RegistroRequest(" Ana O'Brien ", "Voluntariado", estado, " ana@correo.cl ", "+56 912345678",
                "Comuna: Maipú", "file:///foto.jpg", "image/jpeg", "foto.jpg");
    }

    private static Registro registro(Long id, String estado) {
        Registro r = new Registro();
        r.setId(id);
        r.setNombre("Ana O'Brien");
        r.setTipo("Voluntariado");
        r.setEstado(estado);
        r.setFecha(1000L);
        return r;
    }

    @Test
    void listarSinEstadoDevuelveTodos() {
        when(repository.findAllByOrderByFechaDesc()).thenReturn(List.of(registro(1L, "Nuevo"), registro(2L, "Cerrado")));

        assertEquals(2, service.listar(null).size());
        assertEquals(2, service.listar("  ").size());
        verify(repository, never()).findByEstadoOrderByFechaDesc(any());
    }

    @Test
    void listarConEstadoFiltra() {
        when(repository.findByEstadoOrderByFechaDesc("Contactado")).thenReturn(List.of(registro(3L, "Contactado")));

        List<Registro> resultado = service.listar("Contactado");

        assertEquals(1, resultado.size());
        assertEquals("Contactado", resultado.get(0).getEstado());
    }

    @Test
    void buscarDevuelveElRegistro() {
        when(repository.findById(1L)).thenReturn(Optional.of(registro(1L, "Nuevo")));

        assertEquals("Ana O'Brien", service.buscar(1L).getNombre());
    }

    @Test
    void buscarInexistenteLanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RegistroNoEncontradoException e = assertThrows(RegistroNoEncontradoException.class, () -> service.buscar(99L));
        assertEquals("No existe el registro N° 99", e.getMessage());
    }

    @Test
    void crearAsignaEstadoNuevoFechaYLimpiaTextos() {
        when(repository.save(any(Registro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        // aunque la app mande otro estado, un registro nuevo siempre parte en "Nuevo"
        Registro creado = service.crear(solicitud("Cerrado"));

        assertEquals("Nuevo", creado.getEstado());
        assertEquals("Ana O'Brien", creado.getNombre());
        assertEquals("ana@correo.cl", creado.getCorreo());
        assertEquals("foto.jpg", creado.getAdjuntoNombre());
        assertNotNull(creado.getFecha());
    }

    @Test
    void crearConCamposOpcionalesVaciosUsaTextoVacio() {
        when(repository.save(any(Registro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Registro creado = service.crear(new RegistroRequest("Luis", "Donación única", null, null, null, null, null, null, null));

        assertEquals("", creado.getCorreo());
        assertEquals("", creado.getTelefono());
        assertEquals("", creado.getDetalle());
        assertNull(creado.getAdjuntoUri());
    }

    @Test
    void actualizarCambiaElEstado() {
        when(repository.findById(1L)).thenReturn(Optional.of(registro(1L, "Nuevo")));
        when(repository.save(any(Registro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Registro actualizado = service.actualizar(1L, solicitud("Contactado"));

        assertEquals("Contactado", actualizado.getEstado());
        assertEquals(1000L, actualizado.getFecha(), "la fecha de recepción no cambia");
    }

    @Test
    void actualizarSinEstadoMantieneElActual() {
        when(repository.findById(1L)).thenReturn(Optional.of(registro(1L, "Cerrado")));
        when(repository.save(any(Registro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        assertEquals("Cerrado", service.actualizar(1L, solicitud(null)).getEstado());
    }

    @Test
    void actualizarInexistenteLanzaExcepcion() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.actualizar(5L, solicitud("Cerrado")));
        verify(repository, never()).save(any());
    }

    @Test
    void eliminarBorraElRegistro() {
        Registro existente = registro(1L, "Nuevo");
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.eliminar(1L);

        ArgumentCaptor<Registro> captor = ArgumentCaptor.forClass(Registro.class);
        verify(repository).delete(captor.capture());
        assertEquals(1L, captor.getValue().getId());
    }

    @Test
    void eliminarInexistenteLanzaExcepcion() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(RegistroNoEncontradoException.class, () -> service.eliminar(7L));
        verify(repository, never()).delete(any());
    }
}
