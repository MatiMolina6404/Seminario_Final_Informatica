# Sistema de Gestión Administrativa para Clubes de Barrio

## Segunda entrega — Versión 1.1.0

Sistema de gestión administrativa desarrollado como proyecto para el **Seminario Final de Informática**.

Esta versión corresponde a la segunda entrega del proyecto y amplía el prototipo presentado en la versión `1.0.0`.

# 📋 Funcionalidades principales

La versión `1.1.0` mantiene las funcionalidades desarrolladas durante la primera entrega y agrega nuevas herramientas de administración, seguridad y configuración.

# 👥 Gestión de usuarios

La versión `1.1.0` incorpora la administración de usuarios registrados.

Permite:

* Registrar usuarios.
* Modificar usuarios.
* Eliminar usuarios.
* Administrar los datos de los usuarios.
* Proteger la cuenta del administrador ante modificaciones o eliminación indebida.

---

# 🔐 Seguridad y permisos

Se incorporó un sistema de permisos basado en roles.

Los permisos determinan las operaciones que puede realizar cada usuario dentro del sistema.

El control se encuentra centralizado mediante la clase:

```text
Permisos
```

También se incorporó el manejo de sesión mediante:

```text
SesionUsuario
```

Esto permite identificar al usuario autenticado y controlar las operaciones disponibles.

---

## 🔑 Contraseñas

Se mejoró el almacenamiento de las contraseñas utilizando:

* Salt.
* Hash.

Las contraseñas no se almacenan directamente como texto plano.

También se incorporaron:

* Control de vencimiento de contraseñas.
* Bloqueo temporal ante múltiples intentos fallidos de inicio de sesión.

---

# ⚙️ Configuración del sistema

La versión `1.1.0` incorpora un módulo de configuración.

Permite modificar:

* Nombre del club.
* Nombre del sistema.
* Escudo institucional.
* Colores de la interfaz.

Esto permite adaptar la aplicación a las características particulares de cada club.

---

# 🗄️ Base de datos

El sistema utiliza **SQLite** para almacenar la información.

La versión `1.1.0` incorpora la posibilidad de seleccionar la base de datos utilizada por el sistema.

Esto evita que la aplicación cree una base de datos vacía cuando no encuentra automáticamente la base de datos original.

---

# 🔄 Estados automáticos

Los estados de socios e inscripciones se actualizan automáticamente a partir de la información registrada en los pagos.

También se incorporó la visualización de la fecha correspondiente al último pago registrado.

---

# ✅ Validaciones

Se reforzaron las validaciones del sistema.

Entre ellas:

* Datos obligatorios.
* Fechas de alta.
* Fechas de pago.
* Montos de pago.
* Fechas de registros.
* Historial de pagos.
* Tipos de reportes.
* Estados automáticos.

También se mejoró el manejo de errores relacionados con la base de datos.

---

# 🎨 Interfaz

Se incorporaron mejoras en la experiencia de usuario:

* Personalización visual.
* Configuración de colores.
* Escudo institucional.
* Mejor navegación entre módulos.
* Mensajes de confirmación.
* Mensajes informativos.
* Mensajes de error más específicos.
* Mejor presentación de estados y pagos.

---

# 🧹 Organización del código

Se reorganizó parte de la lógica interna para mejorar la separación de responsabilidades y el mantenimiento del proyecto.

Se trabajó principalmente sobre:

* Controladores.
* Servicios.
* DAO.
* Clases auxiliares.
* Configuración.
* Fechas.
* Sesión.
* Permisos.

---

# ▶️ Ejecución desde IntelliJ IDEA

Para ejecutar el proyecto desde el entorno de desarrollo se requiere:

* JDK `25.0.2`.
* JavaFX SDK `25.0.2`.
* Base de datos SQLite.

La clase principal de la aplicación es:

```text
Main
```

---

# 💻 Aplicación portable para Windows

La versión `1.1.0` también se encuentra preparada como aplicación portable mediante `jpackage`.

La aplicación incluye un runtime propio de Java.

Por lo tanto:

> **El usuario final no necesita tener Java instalado en Windows.**

La aplicación portable tiene la siguiente estructura:

```text
GestionClubBarrio/
│
├── GestionClubBarrio.exe
├── app/
└── runtime/
```

Para iniciar la aplicación se debe ejecutar:

```text
GestionClubBarrio.exe
```

Se deben conservar las carpetas `app` y `runtime` junto con el ejecutable.

---

# 📥 Instalación

1. Descargar la versión `1.1.0`.
2. Descomprimir el archivo.
3. Mantener la estructura completa de carpetas.
4. Ejecutar `GestionClubBarrio.exe`.

Se puede crear un acceso directo a `GestionClubBarrio.exe` para utilizarlo fuera de la carpeta.
No se debe mover el ejecutable fuera de la carpeta de la aplicación.

