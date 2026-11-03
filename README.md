# AppCorpaliv_Grupo7

Aplicación móvil Android para **CORPALIV (Alba Lab)**, desarrollada en la asignatura DSY1105 Desarrollo de Aplicaciones Móviles.

La app reúne en un solo lugar el propósito de CORPALIV, sus programas, su impacto y las formas de colaborar (donación, voluntariado y contacto con empresas), para que el interés de un posible colaborador se convierta en un registro concreto que el equipo de vinculación pueda revisar.

## Integrantes

- Nombre Apellido
- Nombre Apellido

## Funcionalidades

- **Inicio:** propósito de la organización, programas e impacto, con accesos a cada sección.
- **Programas:** Escuela Especial Jan Van Dijk y Taller Protegido de Alba Lab.
- **Impacto:** indicadores, gráfico animado de inserciones laborales, historias y video.
- **Catálogo:** productos de Alba Lab y botón para cotizar.
- **Colabora:** formularios de donación, voluntariado (con foto desde cámara o galería) y contacto, con validaciones por campo.
- **Acceso equipo:** inicio de sesión para ver los registros recibidos.
- **Accesibilidad:** tamaño de texto ajustable para toda la app.

## Tecnologías

- Kotlin y Jetpack Compose con Material 3
- Arquitectura MVVM con StateFlow
- Navigation Compose
- Room para guardar los registros
- DataStore para guardar las preferencias de accesibilidad
- Cámara y galería del dispositivo

## Cómo ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/pauligonzalezc/AppCorpaliv_Grupo7.git
   ```
2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar a que Gradle sincronice las dependencias.
4. Ejecutar la app en un emulador o dispositivo con Android 7.0 (API 24) o superior.

## Credenciales de prueba

Para ver los registros recibidos, entra a **Acceso equipo** desde el ícono de cuenta en la pantalla de Inicio e ingresa:

| Campo | Valor |
|---|---|
| Correo institucional | `admin@corpaliv.cl` |
| Contraseña | `admin1234` |

Todos los datos de la app son ficticios, solo para probarla.
