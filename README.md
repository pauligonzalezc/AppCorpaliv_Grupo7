# AppCorpaliv_Grupo7

Aplicación móvil Android para **CORPALIV (Alba Lab)**, desarrollada en la asignatura DSY1105 Desarrollo de Aplicaciones Móviles.

La app reúne en un solo lugar el propósito de CORPALIV, sus programas, su impacto y las formas de colaborar (donación, voluntariado y contacto con empresas), para que el interés de un posible colaborador se convierta en un registro concreto que el equipo de vinculación pueda revisar y gestionar.

El proyecto tiene dos partes:

| Carpeta | Qué es |
|---|---|
| `app/` | App móvil en Kotlin y Jetpack Compose |
| `backend/registros-service/` | Microservicio en Spring Boot con MySQL que guarda los registros |

## Integrantes

- Paulina Gonzalez
- Marcelo Martabit

## Funcionalidades

- **Inicio:** propósito de la organización, programas e impacto, con accesos a cada sección.
- **Programas:** Escuela Especial Jan Van Dijk y Taller Protegido de Alba Lab.
- **Impacto:** indicadores, gráfico animado de inserciones laborales, historias y video.
- **Catálogo:** productos de Alba Lab y botón para cotizar.
- **Colabora:** formularios de donación, voluntariado y contacto, con validaciones por campo. En voluntariado se puede adjuntar una foto tomada con la cámara de la app (CameraX) o un archivo (imagen o PDF).
- **Acceso equipo:** inicio de sesión para el equipo de vinculación.
- **Registros:** lista de los registros recibidos desde el microservicio, con pestañas por estado (Nuevos, Contactados, Cerrados). En el detalle de cada registro se ve el adjunto y, desde el menú de opciones, se cambia el estado (con confirmación) o se elimina.
- **Accesibilidad:** tamaño de texto ajustable para toda la app.

## Tecnologías

**App móvil**

- Kotlin y Jetpack Compose con Material 3
- Arquitectura MVVM con StateFlow
- Navigation Compose
- Retrofit y Gson para consumir el microservicio
- DataStore para guardar las preferencias de accesibilidad
- CameraX para la cámara propia de la app
- Pruebas: Kotest, MockK, kotlinx-coroutines-test, JUnit 5 y Compose UI Test

**Microservicio**

- Spring Boot 4.1 y Java 21
- Spring Data JPA con MySQL
- Pruebas: JUnit 5, Mockito y MockMvc, con H2 en memoria

## Endpoints

### Microservicio de registros (`registros-service`)

URL base desde el emulador: `http://10.0.2.2:8080/`

| Método | Ruta | Descripción | Uso en la app |
|---|---|---|---|
| GET | `/api/registros` | Lista todos los registros, del más reciente al más antiguo | Pantalla Registros |
| GET | `/api/registros?estado=Nuevo` | Lista por estado (`Nuevo`, `Contactado`, `Cerrado`) | — |
| GET | `/api/registros/{id}` | Obtiene un registro | — |
| POST | `/api/registros` | Crea un registro (queda en estado `Nuevo`) | Enviar donación, voluntariado o contacto |
| PUT | `/api/registros/{id}` | Actualiza un registro | Cambiar el estado desde el detalle |
| DELETE | `/api/registros/{id}` | Elimina un registro | Eliminar desde el detalle |

Ejemplo de cuerpo para POST y PUT:

```json
{
  "nombre": "Ana Pérez",
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

El adjunto (foto o PDF) queda guardado en el teléfono que envió el registro; el microservicio solo guarda su ubicación, tipo y nombre. Más detalles en [backend/registros-service/README.md](backend/registros-service/README.md).

### API externa

Pendiente.

## Pasos para ejecutar

### 1. Base de datos (MySQL)

1. Instalar [XAMPP](https://www.apachefriends.org/) (o tener MySQL en `localhost:3306`).
2. Abrir XAMPP y encender **MySQL Database** (en macOS: *Manage Servers* → *Start*).

No hace falta crear la base: el microservicio crea la base `corpaliv` y la tabla `registros` la primera vez. Se conecta con el usuario `root` sin contraseña (el de XAMPP por defecto); si es otro, definir las variables de entorno `DB_USER` y `DB_PASSWORD`.

### 2. Microservicio

**Con IntelliJ:** abrir la carpeta `backend/registros-service`, esperar que Maven descargue las dependencias y ejecutar `RegistrosServiceApplication`.

**Desde la terminal:**

```bash
cd backend/registros-service
./mvnw spring-boot:run
```

Queda listo cuando aparece `Tomcat started on port 8080`. Para comprobarlo, abrir en el navegador <http://localhost:8080/api/registros>.

### 3. App móvil

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/pauligonzalezc/AppCorpaliv_Grupo7.git
   ```
2. Abrir la carpeta del proyecto en Android Studio y esperar que Gradle sincronice.
3. Ejecutar la app en un emulador con Android 7.0 (API 24) o superior.
4. En Android 17, aceptar el permiso de **dispositivos de la red local** que pide la app al abrirse; sin él no se puede conectar al microservicio.

**En un teléfono físico:** el teléfono debe estar en el mismo Wi-Fi que el computador. Cambiar `BASE_URL` en `app/src/main/java/cl/duoc/appcorpaliv/data/remote/RetrofitInstance.kt` por la IP del computador (por ejemplo `http://192.168.1.82:8080/`) y agregar esa IP en `app/src/main/res/xml/network_security_config.xml`.

### Credenciales de prueba

Para ver los registros recibidos, entrar a **Acceso equipo** desde el ícono de cuenta en la pantalla de Inicio:

| Campo | Valor |
|---|---|
| Correo institucional | `admin@corpaliv.cl` |
| Contraseña | `admin1234` |

Todos los datos de la app son ficticios, solo para probarla.

## Pruebas

### App móvil

Las pruebas unitarias están en `app/src/test` y no necesitan emulador ni microservicio: el repositorio se reemplaza por un mock (MockK).

| Archivo | Qué prueba |
|---|---|
| `ColaboraViewModelTest` | Carga de registros (éxito, sin conexión, error del servidor), filtros, validación y envío de los formularios, doble envío, cambio de estado y eliminación |
| `RegistroRepositoryTest` | Llamadas a la API simulada: listar, crear, actualizar y eliminar (204, 404 y 500) |
| `RegistroDtoTest` | Conversión entre los datos del microservicio y los de la app |
| `EquipoViewModelTest` | Inicio y cierre de sesión del equipo |
| `MainViewModelTest`, `CatalogoViewModelTest` | Navegación y catálogo |
| `ValidacionesTest` | Validación de nombres |

```bash
./gradlew testDebugUnitTest
```

Cobertura de la lógica (ViewModel, repositorio, datos remotos y validaciones): **81,4 % de las líneas** (98,9 % sin contar las clases que dependen del teléfono: cámara, archivos y DataStore). Para generar el reporte:

```bash
./gradlew createDebugUnitTestCoverageReport
```

El reporte queda en `app/build/reports/coverage/test/debug/index.html`. También se puede ver en Android Studio con clic derecho en `app/src/test/java` → *Run 'Tests in…' with Coverage*.

Las pruebas de interfaz (`app/src/androidTest`, con Compose UI Test) revisan la lista de registros, las pestañas y la confirmación al cambiar el estado. Necesitan un emulador encendido:

```bash
./gradlew connectedDebugAndroidTest
```

### Microservicio

```bash
cd backend/registros-service
./mvnw test
```

Usa H2 en memoria, así que no necesita MySQL. El reporte de cobertura (JaCoCo) queda en `target/site/jacoco/index.html`.

## APK firmado

El APK de publicación se firma con una llave propia (`.jks`), distinta de la llave de pruebas que Android Studio usa en debug.

Pasos en Android Studio:

1. **Build → Generate Signed App Bundle or APK…** → **APK**.
2. Crear la llave (**Create new…**) o elegir una existente: ruta del `.jks`, contraseña, alias y contraseña de la llave.
3. Elegir la variante **release** y presionar **Create**.
4. El APK queda en `app/release/app-release.apk`.
