package service;

import dao.ActividadDAO;
import model.Actividad;

import java.util.ArrayList;
import java.util.List;

public class ServicioActividad {

    private final ActividadDAO actividadDAO;

    // Constructor que inicializa el DAO encargado del acceso a datos de actividades.
    public ServicioActividad() {
        actividadDAO = new ActividadDAO();
    }

    // Valida que la actividad tenga los datos obligatorios antes de registrarla o modificarla.
    private boolean validarActividad(Actividad actividad) {
        if (actividad == null) {
            return false;
        }
        if (actividad.getNombreActividad() == null || actividad.getNombreActividad().isBlank()) {
            return false;
        }
        return actividad.getDescripcionActividad() != null && !actividad.getDescripcionActividad()
                .isBlank();
    }

    // Registra una nueva actividad si los datos ingresados son válidos.
    public boolean registrarActividad(Actividad actividad) {
        if (!validarActividad(actividad)) {
            return false;
        }
        limpiarDatosActividad(actividad);
        if (actividadDAO.existeActividad(actividad.getNombreActividad(), null)) {
            return false;
        }
        return actividadDAO.registrarActividad(actividad);
    }

    // Consulta actividades según el campo y valor indicados.
    public List<Actividad> consultarActividades(String campo, String valor) {
        if (campo == null || campo.isBlank()) {
            return new ArrayList<>();
        }
        if (!campo.equals("Todos") && (valor == null || valor.isBlank())) {
            return new ArrayList<>();
        }
        if ("ID".equals(campo) && !valor.matches("\\d+")) {
            return new ArrayList<>();
        }
        return actividadDAO.consultarActividades(campo, valor);
    }

    // Modifica una actividad existente si los datos son válidos.
    public boolean modificarActividad(Actividad actividad) {
        if (!validarActividad(actividad)) {
            return false;
        }
        limpiarDatosActividad(actividad);
        if (actividadDAO.existeActividad(actividad.getNombreActividad(), actividad.getIdActividad())) {
            return false;
        }
        return actividadDAO.modificarActividad(actividad);
    }

    // Devuelve todas las actividades registradas para mostrarlas en pantalla.
    public List<Actividad> listarActividadesPantalla() {
        return actividadDAO.listarActividades();
    }

    // Obtiene la cantidad de personas inscriptas en una actividad determinada.
    public int obtenerCantidadInscriptos(int idActividad) {
        return actividadDAO.contarInscriptos(idActividad);
    }

    // Elimina espacios innecesarios al inicio y al final de los datos de la actividad.
    private void limpiarDatosActividad(Actividad actividad) {
        actividad.setNombreActividad(actividad.getNombreActividad().trim());
        actividad.setDescripcionActividad(actividad.getDescripcionActividad().trim());
    }

}
