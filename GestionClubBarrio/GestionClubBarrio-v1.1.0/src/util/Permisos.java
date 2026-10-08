package util;

public final class Permisos {

    // Constructor privado
    private Permisos() {
    }

    /* Indica si el usuario puede gestionar los usuarios y
       la configuración del sistema (solo administradores).
    */
    public static boolean puedeGestionarUsuarios() {
        return SesionUsuario.esAdministrador();
    }

    // Indica si el usuario puede registrar y modificar socios.
    public static boolean puedeGestionarSocios() {
        return tieneRol("Administrador", "Secretario");
    }

    // Indica si el usuario puede consultar los datos de los socios.
    public static boolean puedeConsultarSocios() {
        return tieneRol("Administrador", "Secretario", "Tesorero", "Consulta");
    }

    // Indica si el usuario puede registrar y modificar actividades deportivas.
    public static boolean puedeGestionarActividades() {
        return tieneRol("Administrador", "Secretario");
    }

    // Indica si el usuario puede consultar las actividades deportivas.
    public static boolean puedeConsultarActividades() {
        return tieneRol("Administrador", "Secretario", "Tesorero", "Consulta");
    }

    // Indica si el usuario puede registrar y modificar inscripciones a actividades.
    public static boolean puedeGestionarInscripciones() {
        return tieneRol("Administrador", "Secretario");
    }

    // Indica si el usuario puede registrar pagos de cuotas societarias y de actividades.
    public static boolean puedeGestionarPagos() {
        return tieneRol("Administrador", "Tesorero");
    }

    // Indica si el usuario puede consultar los pagos y su historial.
    public static boolean puedeConsultarPagos() {
        return tieneRol("Administrador", "Secretario", "Tesorero", "Consulta");
    }

    // Indica si el usuario puede consultar los estados de socios e inscripciones.
    public static boolean puedeConsultarEstados() {
        return tieneRol("Administrador", "Secretario", "Tesorero", "Consulta");
    }

    // Indica si el usuario puede consultar los reportes ya generados.
    public static boolean puedeConsultarReportes() {
        return tieneRol("Administrador", "Secretario", "Tesorero", "Consulta");
    }

    // Indica si el usuario puede generar nuevos reportes.
    public static boolean puedeGenerarReportes() {
        return tieneRol("Administrador", "Secretario", "Tesorero");
    }

    // Verifica si el rol del usuario con sesión activa se encuentra entre los roles permitidos.
    private static boolean tieneRol(String... rolesPermitidos) {
        if (!SesionUsuario.haySesionActiva()) {
            return false;
        }
        String rolActual = SesionUsuario.getRolActual();
        for (String rol : rolesPermitidos) {
            if (rol.equalsIgnoreCase(rolActual)) {
                return true;
            }
        }
        return false;
    }

}