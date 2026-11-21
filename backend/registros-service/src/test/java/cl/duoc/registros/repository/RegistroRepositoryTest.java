package cl.duoc.registros.repository;

import cl.duoc.registros.model.Registro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RegistroRepositoryTest {

    @Autowired
    private RegistroRepository repository;

    private Registro guardar(String nombre, String estado, long fecha) {
        Registro r = new Registro();
        r.setNombre(nombre);
        r.setTipo("Voluntariado");
        r.setEstado(estado);
        r.setFecha(fecha);
        return repository.save(r);
    }

    @Test
    void guardarAsignaId() {
        assertNotNull(guardar("Ana", "Nuevo", 1L).getId());
    }

    @Test
    void listaDelMasRecienteAlMasAntiguo() {
        guardar("Antiguo", "Nuevo", 100L);
        guardar("Reciente", "Cerrado", 300L);
        guardar("Medio", "Nuevo", 200L);

        List<Registro> todos = repository.findAllByOrderByFechaDesc();

        assertEquals(List.of("Reciente", "Medio", "Antiguo"), todos.stream().map(Registro::getNombre).toList());
    }

    @Test
    void filtraPorEstado() {
        guardar("Ana", "Nuevo", 100L);
        guardar("Luis", "Contactado", 200L);
        guardar("Eva", "Nuevo", 300L);

        List<Registro> nuevos = repository.findByEstadoOrderByFechaDesc("Nuevo");

        assertEquals(List.of("Eva", "Ana"), nuevos.stream().map(Registro::getNombre).toList());
        assertEquals(0, repository.findByEstadoOrderByFechaDesc("Cerrado").size());
    }
}
