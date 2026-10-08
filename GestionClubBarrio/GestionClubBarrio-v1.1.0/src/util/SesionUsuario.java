package util;

import model.Administrador;

public final class SesionUsuario {

    private static Administrador usuarioActual;

    // Constructor privado.
    private SesionUsuario() {
    }

    // Inicia la sesión con el usuario autenticado.
    public static void iniciarSesion(Administrador usuario) {
        usuarioActual = usuario;
    }

    // Obtiene el usuario actualmente autenticado.
    public static Administrador getUsuarioActual() {
        return usuarioActual;
    }

    // Verifica si existe una sesión activa.
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    // Obtiene el rol del usuario actualmente autenticado.
    public static String getRolActual() {
        if (usuarioActual == null || usuarioActual.getRol() == null) {
            return "";
        }
        return usuarioActual.getRol();
    }

    // Verifica si el usuario actualmente autenticado es administrador.
    public static boolean esAdministrador() {
        return "Administrador".equalsIgnoreCase(getRolActual());
    }

}