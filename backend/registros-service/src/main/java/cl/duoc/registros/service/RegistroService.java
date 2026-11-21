package cl.duoc.registros.service;

import cl.duoc.registros.dto.RegistroRequest;
import cl.duoc.registros.exception.RegistroNoEncontradoException;
import cl.duoc.registros.model.Estados;
import cl.duoc.registros.model.Registro;
import cl.duoc.registros.repository.RegistroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistroService {

    private final RegistroRepository repository;

    public RegistroService(RegistroRepository repository) {
        this.repository = repository;
    }


    @Transactional(readOnly = true)
    public List<Registro> listar(String estado) {
        if (estado == null || estado.isBlank()) {
            return repository.findAllByOrderByFechaDesc();
        }
        return repository.findByEstadoOrderByFechaDesc(estado);
    }

    @Transactional(readOnly = true)
    public Registro buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RegistroNoEncontradoException(id));
    }

    // nuevo -> se pasa el id, la fecha y el estado nuevo
    @Transactional
    public Registro crear(RegistroRequest datos) {
        Registro registro = new Registro();
        copiarDatos(datos, registro);
        registro.setEstado(Estados.NUEVO);
        registro.setFecha(System.currentTimeMillis());
        return repository.save(registro);
    }

    // actualiza un registro existente
    @Transactional
    public Registro actualizar(Long id, RegistroRequest datos) {
        Registro registro = buscar(id);
        copiarDatos(datos, registro);
        if (datos.estado() != null) {
            registro.setEstado(datos.estado());
        }
        return repository.save(registro);
    }

    @Transactional
    public void eliminar(Long id) {
        Registro registro = buscar(id);
        repository.delete(registro);
    }

    private void copiarDatos(RegistroRequest datos, Registro registro) {
        registro.setNombre(datos.nombre().trim());
        registro.setTipo(datos.tipo().trim());
        registro.setCorreo(textoOVacio(datos.correo()));
        registro.setTelefono(textoOVacio(datos.telefono()));
        registro.setDetalle(datos.detalle() == null ? "" : datos.detalle());
        registro.setAdjuntoUri(datos.adjuntoUri());
        registro.setAdjuntoTipo(datos.adjuntoTipo());
        registro.setAdjuntoNombre(datos.adjuntoNombre());
    }

    private static String textoOVacio(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
