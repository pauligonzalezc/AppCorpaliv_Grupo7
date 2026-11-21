package cl.duoc.registros.repository;

import cl.duoc.registros.model.Registro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroRepository extends JpaRepository<Registro, Long> {

    List<Registro> findAllByOrderByFechaDesc();

    // registros de un estado (pestañas Nuevos, Contactados, Cerrados)
    List<Registro> findByEstadoOrderByFechaDesc(String estado);
}
