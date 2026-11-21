# registros-service

Microservicio Spring Boot que guarda los registros de interés de la app Corpaliv
(donaciones, socios, voluntariado y contacto) en MySQL.

- Spring Boot 4.1 · Java 21 · Spring Data JPA · MySQL
- Puerto: `8080`

## Cómo levantarlo

1. Tener MySQL corriendo en `localhost:3306`. La base `corpaliv` y la tabla `registros` se crean solas.
2. Si tu usuario de MySQL no es `root` sin clave, define las variables de entorno:
   ```bash
   export DB_USER=tu_usuario
   export DB_PASSWORD=tu_clave
   ```
3. Desde esta carpeta:
   ```bash
   ./mvnw spring-boot:run
   ```
   o abrir la carpeta en IntelliJ y ejecutar `RegistrosServiceApplication`.

Desde el emulador de Android el servidor se ve como `http://10.0.2.2:8080/`.

## Endpoints

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/registros` | Lista todos, del más reciente al más antiguo | 200 |
| GET | `/api/registros?estado=Nuevo` | Lista por estado (`Nuevo`, `Contactado`, `Cerrado`) | 200 |
| GET | `/api/registros/{id}` | Un registro | 200 / 404 |
| POST | `/api/registros` | Crea un registro (queda en estado `Nuevo`) | 201 / 400 |
| PUT | `/api/registros/{id}` | Actualiza un registro (por ejemplo su estado) | 200 / 400 / 404 |
| DELETE | `/api/registros/{id}` | Elimina un registro | 204 / 404 |

Ejemplo de cuerpo para POST / PUT:

```json
{
  "nombre": "Ana O'Brien",
  "tipo": "Voluntariado",
  "estado": "Contactado",
  "correo": "ana@correo.cl",
  "telefono": "+56 912345678",
  "detalle": "Comuna: Maipú\nIntereses: Talleres",
  "adjuntoUri": "file:///data/user/0/cl.duoc.appcorpaliv/files/adjuntos/foto.jpg",
  "adjuntoTipo": "image/jpeg",
  "adjuntoNombre": "foto.jpg"
}
```

`nombre` y `tipo` son obligatorios. `estado` es opcional (en POST siempre queda `Nuevo`).
El adjunto (foto o PDF) queda guardado en el teléfono que envió el registro; el servicio solo guarda su ubicación y nombre.

Errores de validación (400):

```json
{ "error": "Datos inválidos", "campos": { "nombre": "El nombre es obligatorio" } }
```

## Tests y cobertura

Los tests usan H2 en memoria, así que no necesitan MySQL:

```bash
./mvnw test
```

El reporte de cobertura (JaCoCo) queda en `target/site/jacoco/index.html`.
