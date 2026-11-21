package cl.duoc.registros.controller;

import cl.duoc.registros.dto.RegistroRequest;
import cl.duoc.registros.dto.RegistroResponse;
import cl.duoc.registros.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    private final RegistroService service;

    public RegistroController(RegistroService service) {
        this.service = service;
    }

    // GET /api/registros y GET /api/registros?estado=Nuevo
    @GetMapping
    public List<RegistroResponse> listar(@RequestParam(required = false) String estado) {
        return service.listar(estado).stream().map(RegistroResponse::desde).toList();
    }

    // GET /api/registros/{id}
    @GetMapping("/{id}")
    public RegistroResponse buscar(@PathVariable Long id) {
        return RegistroResponse.desde(service.buscar(id));
    }

  // POST /api/registros
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse crear(@Valid @RequestBody RegistroRequest datos) {
        return RegistroResponse.desde(service.crear(datos));
    }

   //PUT /api/registros/{id}
    @PutMapping("/{id}")
    public RegistroResponse actualizar(@PathVariable Long id, @Valid @RequestBody RegistroRequest datos) {
        return RegistroResponse.desde(service.actualizar(id, datos));
    }

   //DELETE /api/registros/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
