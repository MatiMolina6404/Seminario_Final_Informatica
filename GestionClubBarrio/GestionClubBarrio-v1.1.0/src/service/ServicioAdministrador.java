package service;

import dao.AdministradorDAO;
import model.Administrador;
import util.Permisos;
import util.Seguridad;
import util.SesionUsuario;

import java.time.LocalDate;
import java.util.List;

public class ServicioAdministrador {

    private final AdministradorDAO administradorDAO;

    // Constructor que inicializa el DAO encargado del acceso a datos de administradores.
    public ServicioAdministrador() {
        administradorDAO = new AdministradorDAO();
    }

    // Valida las credenciales ingresadas por el administrador para iniciar sesión.
    public String iniciarSesion(String usuario, String clave) {
        if (usuario == null || usuario.isBlank()) {
            return "Debe ingresar el usuario.";
        }
        if (clave == null || clave.isBlank()) {
            return "Debe ingresar la contraseña.";
        }
        usuario = usuario.trim();
        Administrador administrador = administradorDAO.buscarPorUsuario(usuario);
        if (administrador == null) {
            return "Usuario o contraseña incorrectos.";
        }
        boolean claveCorrecta = Seguridad.verificarClave(clave, administrador.getSalt(),
                administrador.getClaveHash());
        if (!claveCorrecta) {
            return "Usuario o contraseña incorrectos.";
        }
        boolean esAdministrador = "Administrador".equalsIgnoreCase(administrador.getRol());
        if (!esAdministrador && claveVencida(administrador.getFechaCambioClave())) {
            return "La contraseña se encuentra vencida. Solicite el cambio al administrador.";
        }
        // Inicia la sesión con el usuario autenticado.
        SesionUsuario.iniciarSesion(administrador);
        return "OK";
    }

    /* Registra un nuevo usuario del sistema.
       Genera el nombre de usuario, el salt y el hash de la contraseña antes de guardar los datos.
    */
    public boolean registrarAdministrador(String nombre, String apellido,
                                          String clave, String rol) {
        if (!Permisos.puedeGestionarUsuarios()) {
            return false;
        }
        if (nombre == null || nombre.isBlank()) {
            return false;
        }
        if (apellido == null || apellido.isBlank()) {
            return false;
        }
        nombre = nombre.trim();
        apellido = apellido.trim();
        if (!Seguridad.validarFormatoClave(clave)) {
            return false;
        }
        if (rol == null || rol.isBlank()) {
            rol = "Consulta";
        }
        String usuario = Seguridad.generarUsuario(nombre, apellido);
        if (administradorDAO.existeUsuario(usuario)) {
            return false;
        }
        String salt = Seguridad.generarSalt();
        String hash = Seguridad.generarHash(clave, salt);
        Administrador administrador = new Administrador();
        administrador.setUsuario(usuario);
        administrador.setNombre(nombre);
        administrador.setApellido(apellido);
        administrador.setClaveHash(hash);
        administrador.setSalt(salt);
        administrador.setRol(rol);
        administrador.setFechaCambioClave(LocalDate.now().toString());
        return administradorDAO.registrarAdministrador(administrador);
    }

    // Lista los usuarios registrados cuando el usuario tiene permisos de administración.
    public List<Administrador> listarAdministradores() {
        if (!Permisos.puedeGestionarUsuarios()) {
            return List.of();
        }
        return administradorDAO.listarAdministradores();
    }

    // Elimina un usuario registrado, excepto el usuario que tiene la sesión activa.
    public boolean eliminarAdministrador(int idAdministrador) {
        if (!Permisos.puedeGestionarUsuarios()) {
            return false;
        }
        Administrador usuarioActual = SesionUsuario.getUsuarioActual();
        if (usuarioActual == null) {
            return false;
        }
        if (idAdministrador == usuarioActual.getIdAdministrador()) {
            return false;
        }
        return administradorDAO.eliminarAdministrador(idAdministrador);
    }

    // Modifica los datos personales y el rol de un usuario.
    public boolean modificarAdministrador(Administrador administrador, String nuevaClave) {
        if (!Permisos.puedeGestionarUsuarios()) {
            return false;
        }
        if (administrador == null) {
            return false;
        }
        Administrador usuarioActual = SesionUsuario.getUsuarioActual();
        if (usuarioActual != null &&
                administrador.getIdAdministrador() == usuarioActual.getIdAdministrador()) {
            administrador.setRol(usuarioActual.getRol());
        }
        if (administrador.getNombre() == null || administrador.getNombre().isBlank()) {
            return false;
        }
        if (administrador.getApellido() == null || administrador.getApellido().isBlank()) {
            return false;
        }
        if (administrador.getRol() == null || administrador.getRol().isBlank()) {
            return false;
        }
        administrador.setNombre(administrador.getNombre().trim());
        administrador.setApellido(administrador.getApellido().trim());
        if (nuevaClave != null && !nuevaClave.isBlank()) {
            if (!Seguridad.validarFormatoClave(nuevaClave)) {
                return false;
            }
            String salt = Seguridad.generarSalt();
            String hash = Seguridad.generarHash(nuevaClave, salt);
            administrador.setSalt(salt);
            administrador.setClaveHash(hash);
            administrador.setFechaCambioClave(LocalDate.now().toString());
            return administradorDAO.modificarAdministradorConClave(administrador);
        }
        return administradorDAO.modificarAdministrador(administrador);
    }

    // Verifica si la contraseña se encuentra vencida (en este caso, +90 días desde el último cambio).
    private boolean claveVencida(String fechaCambioTexto) {
        if (fechaCambioTexto == null || fechaCambioTexto.isBlank()) {
            return true;
        }
        LocalDate fechaUltimoCambio = LocalDate.parse(fechaCambioTexto);
        return fechaUltimoCambio.plusDays(90).isBefore(LocalDate.now());
    }

}