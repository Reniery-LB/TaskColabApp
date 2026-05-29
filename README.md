# TaskColab App

Aplicacion movil Android para gestionar proyectos, tableros Kanban, tareas, usuarios, reportes y chat colaborativo de TaskColab. Esta version consume el backend PHP/MySQL desplegado en produccion.

## Funcionalidades

- Autenticacion con cuenta administradora o usuario normal.
- Gestion de proyectos activos, pausados y archivados.
- Creacion de proyectos con fecha objetivo y selector visual de color.
- Tablero Kanban con estados `Pendiente`, `En Proceso` y `Completado`.
- Tarjetas con prioridad, descripcion, fecha limite y todos los integrantes asignados visibles.
- Vista `Mis Tareas` con asignados destacados y tarjetas expansibles.
- Chat general por proyecto y chats privados entre usuarios.
- Reportes con metricas de tareas, productividad y exportacion PDF.
- Perfil con avatar, cambio de nombre, cambio de contrasena y eliminacion de cuenta.
- Boton de cerrar sesion en el header de los modulos principales.
- Alertas de confirmacion antes de acciones destructivas como eliminar chats, proyectos, tarjetas, tareas o usuarios.

## Credenciales de prueba

Si la base de datos ya tiene estas cuentas, se pueden usar directamente. Si la base esta limpia, registrarlas desde la pantalla de Registro usando los mismos datos.

| Rol | Correo | Contrasena |
| --- | --- | --- |
| Administrador | `admin@gmail.com` | `Admin1234` |
| Usuario | `usuario@gmail.com` | `Usuario1234` |

La cuenta administradora debe registrarse marcando la opcion de administrador. La cuenta de usuario normal debe registrarse sin marcar esa opcion.

## Requisitos

- Android Studio.
- JDK 17.
- Android SDK con `compileSdk 34`.
- Backend TaskColab publicado en Hostinger.
- Base de datos de produccion importada en Hostinger.

## Configuracion del backend

La app obtiene la URL de la API desde `local.properties` con la propiedad:

```properties
taskcolab.apiBaseUrl=https://taskcolab.com/assets/api/
```

Para volver temporalmente a local en emulador Android, normalmente conviene usar:

```properties
taskcolab.apiBaseUrl=http://10.0.2.2/PROYECTO_GESTOR_TAREAS/assets/api/
```

Para volver temporalmente a local en telefono fisico, usar la IP local de la computadora donde corre XAMPP:

```properties
taskcolab.apiBaseUrl=http://TU_IP_LOCAL/PROYECTO_GESTOR_TAREAS/assets/api/
```

La URL debe terminar en `/assets/api/`.

## Instalacion

1. Verificar que `https://taskcolab.com/assets/api/auth/login.php` este disponible en produccion.
2. Abrir este proyecto en Android Studio.
3. Revisar `local.properties` y confirmar `taskcolab.apiBaseUrl=https://taskcolab.com/assets/api/`.
4. Sincronizar Gradle.
5. Ejecutar la app en emulador o dispositivo.

Tambien se puede compilar desde terminal:

```powershell
.\gradlew.bat assembleDebug
```

## Estructura principal

```text
app/src/main/java/com/taskcolab/app/
├── core/                 # Navegacion y componentes compartidos
├── data/                 # API, DTOs, repositorios y sesion
├── domain/               # Modelos de dominio
└── feature/              # Pantallas por modulo
    ├── auth/             # Login y registro
    ├── boards/           # Tablero Kanban
    ├── chat/             # Chat general y privado
    ├── profile/          # Perfil
    ├── projects/         # Proyectos
    ├── reports/          # Reportes
    ├── tasks/            # Mis Tareas
    └── users/            # Usuarios
```

## Modulos

**Proyectos**
Permite crear proyectos, seleccionar el proyecto activo, abrir tablero o chat, pausar, archivar, restaurar y eliminar proyectos archivados con confirmacion.

**Tablero**
Muestra tareas por estado. Cada tarjeta presenta prioridad, fecha limite, descripcion, integrantes asignados completos y acciones para mover o eliminar.

**Mis Tareas**
Lista las tareas del usuario en tarjetas. Incluye estado, asignados con estilo destacado, seleccion multiple para eliminar y confirmacion antes de borrar.

**Chat**
Permite abrir conversaciones del proyecto y chats privados. La conversacion se abre solo al seleccionarla.

**Usuarios**
Permite consultar, crear, editar y eliminar usuarios con confirmacion.

**Reportes**
Muestra indicadores de tareas y productividad, ademas de exportacion PDF.

**Perfil**
Permite actualizar avatar, nombre, contrasena y gestionar la cuenta.

## Equipo

- Keyra Yariely Grijalva Ochoa
- Reniery Lucero Beltran

## Verificacion

Ultima verificacion realizada:

```powershell
.\gradlew.bat assembleDebug
```

Resultado: compilacion exitosa.
