package service;

import dao.AdministradorDAO;
import model.Administrador;
import util.Seguridad;
import java.time.LocalDate;

public class ServicioAdministrador {

    private final AdministradorDAO administradorDAO;
    private static final int maxIntentos = 5;
    private static final int minutosBloqueo = 5;

    /* Constructor que inicializa el DAO de administradores y las estructuras
       utilizadas para controlar intentos fallidos y bloqueos temporales.
    */
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
        boolean claveCorrecta = Seguridad.verificarClave(
                clave, administrador.getSalt(), administrador.getClaveHash()
        );
        if (!claveCorrecta) {
            return "Usuario o contraseña incorrectos.";
        }
        if (claveVencida(administrador.getFechaCambioClave())) {
            return "La contraseña se encuentra vencida. Solicite el cambio al administrador.";
        }
        return "OK";
    }

    /* Registra un nuevo usuario del sistema.
       Genera el nombre de usuario, el salt y el hash de la contraseña antes de guardar los datos.
     */
    public boolean registrarAdministrador(String nombre, String apellido, String clave, String rol) {
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
        administrador.setClaveHash(hash);
        administrador.setSalt(salt);
        administrador.setRol(rol);
        administrador.setFechaCambioClave(LocalDate.now().toString());
        return administradorDAO.registrarAdministrador(administrador);
    }

    // Cambia la contraseña de un usuario existente.
    public boolean cambiarClave(String usuario, String nuevaClave) {
        if (usuario == null || usuario.isBlank()) {
            return false;
        }
        usuario = usuario.trim();
        if (!Seguridad.validarFormatoClave(nuevaClave)) {
            return false;
        }

        Administrador administrador = administradorDAO.buscarPorUsuario(usuario.trim());
        if (administrador == null) {
            return false;
        }
        String salt = Seguridad.generarSalt();
        String hash = Seguridad.generarHash(nuevaClave, salt);
        administrador.setClaveHash(hash);
        administrador.setSalt(salt);
        administrador.setFechaCambioClave(LocalDate.now().toString());
        return administradorDAO.cambiarClave(administrador);
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