# TaskColab App

Aplicacion movil Android para gestionar proyectos, tableros Kanban, tareas, usuarios, reportes y chat colaborativo de TaskColab. Esta version consume el backend PHP/MySQL del proyecto web ubicado normalmente en `C:\xampp\htdocs\PROYECTO_GESTOR_TAREAS`.

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
| Administrador | `admin@taskcolab.com` | `Admin1234` |
| Usuario | `usuario@taskcolab.com` | `Usuario1234` |

La cuenta administradora debe registrarse marcando la opcion de administrador. La cuenta de usuario normal debe registrarse sin marcar esa opcion.

## Requisitos

- Android Studio.
- JDK 17.
- Android SDK con `compileSdk 34`.
- XAMPP con Apache y MySQL activos.
- Backend TaskColab en `C:\xampp\htdocs\PROYECTO_GESTOR_TAREAS`.
- Base de datos importada desde los scripts SQL del backend.

## Configuracion del backend

La app obtiene la URL de la API desde `local.properties` con la propiedad:

```properties
taskcolab.apiBaseUrl=http://127.0.0.1:8080/PROYECTO_GESTOR_TAREAS/assets/api/
```

Para probar en emulador Android, normalmente conviene usar:

```properties
taskcolab.apiBaseUrl=http://10.0.2.2/PROYECTO_GESTOR_TAREAS/assets/api/
```

Para probar en telefono fisico, usar la IP local de la computadora donde corre XAMPP:

```properties
taskcolab.apiBaseUrl=http://TU_IP_LOCAL/PROYECTO_GESTOR_TAREAS/assets/api/
```

La URL debe terminar en `/assets/api/`.

## Instalacion

1. Abrir el backend en XAMPP y activar Apache/MySQL.
2. Importar la base de datos desde los archivos de `SQL/` del proyecto web.
3. Abrir este proyecto en Android Studio.
4. Revisar `local.properties` y ajustar `taskcolab.apiBaseUrl`.
5. Sincronizar Gradle.
6. Ejecutar la app en emulador o dispositivo.

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
