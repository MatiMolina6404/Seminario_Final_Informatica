# Seminario Final de Informática

Repositorio correspondiente al proyecto desarrollado para la asignatura **Seminario Final de Informática**.

## 📌 Proyecto

### Sistema de Gestión Administrativa para Clubes de Barrio

Sistema de escritorio desarrollado para la administración y gestión de la información de un club de barrio.

El proyecto fue desarrollado de manera incremental, incorporando nuevas funcionalidades, mejoras de seguridad, validaciones y herramientas de configuración en cada entrega.

---

## 📚 Entregas

### 🟢 Entrega 1 — v1.0.0

Primera versión funcional del sistema.

Incluye las funcionalidades principales para:

* Gestión de socios.
* Gestión de inscripciones.
* Registro y consulta de pagos.
* Historial de pagos.
* Consulta de estados.
* Generación de reportes.
* Persistencia de información mediante SQLite.
* Interfaz gráfica desarrollada con JavaFX.

### 🔵 Entrega 2 — v1.1.0

Segunda versión del sistema, desarrollada sobre el prototipo presentado en la primera entrega.

Incorpora:

* Gestión de usuarios.
* Gestión de permisos por rol.
* Manejo de sesiones.
* Mejoras de seguridad.
* Almacenamiento seguro de contraseñas mediante salt y hash.
* Control de vencimiento de contraseñas.
* Bloqueo temporal ante intentos fallidos de inicio de sesión.
* Configuración del sistema.
* Personalización del nombre del club, nombre del sistema, escudo y colores.
* Selección de la base de datos SQLite.
* Actualización automática de estados según pagos registrados.
* Visualización del último pago registrado.
* Nuevas validaciones de datos y fechas.
* Mejoras en reportes.
* Mejoras en la interfaz y navegación.
* Correcciones y mejoras generales.

---

## 🛠️ Tecnologías utilizadas

* Java 25.0.2
* JavaFX 25.0.2
* SQLite
* JDBC
* IntelliJ IDEA
* Git
* GitHub
* jpackage

---

## 📁 Estructura del repositorio

```text
Seminario_Final_Informatica/
│
├── GestionClubBarrio/
│   ├── GestionClubBarrio_1.0.0/
│   └── GestionClubBarrio_1.1.0/
│
└── README.md
```

Cada versión contiene los archivos correspondientes a la entrega realizada.

---

## 💻 Aplicación portable

A partir de la versión `1.1.0`, el sistema se encuentra preparado como aplicación portable para Windows.

La aplicación incluye su propio runtime de Java, por lo que **no es necesario instalar Java previamente en el equipo donde se ejecuta**.

La aplicación se inicia mediante:

```text
GestionClubBarrio.exe
```

---

## 📦 Descargas

Las versiones entregadas del sistema se encuentran disponibles en la sección **Releases** del repositorio.

Cada release identifica la versión correspondiente mediante su número de versión y tag.

---

## 👨‍💻 Proyecto académico

**Seminario Final de Informática**

**Sistema de Gestión Administrativa para Clubes de Barrio**

**Versión actual:** `1.1.0`
