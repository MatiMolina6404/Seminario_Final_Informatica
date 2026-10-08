package service;

import dao.InscripcionActividadDAO;
import model.InscripcionActividad;
import java.time.LocalDate;
import java.util.List;

public class ServicioInscripcionActividad {

    private final InscripcionActividadDAO inscripcionDAO;

    // Constructor que inicializa el DAO encargado del acceso a datos de inscripciones.
    public ServicioInscripcionActividad() {
        inscripcionDAO = new InscripcionActividadDAO();
    }

    /* Valida que la inscripción tenga una actividad asociada
       y que corresponda a un socio o a un participante, pero no a ambos.
     */
    private boolean validarInscripcion(InscripcionActividad inscripcion) {
        if (inscripcion == null) {
            return false;
        }
        if (inscripcion.getActividad() == null
                || inscripcion.getActividad().getIdActividad() <= 0) {
            return false;
        }
        boolean tieneSocio = inscripcion.getSocio() != null
                        && inscripcion.getSocio().getIdSocio() > 0;
        boolean tieneParticipante = inscripcion.getParticipante() != null
                        && inscripcion.getParticipante().getIdParticipante() > 0;
        return tieneSocio != tieneParticipante;
    }

    // Registra una nueva inscripción si los datos son válidos.
    public boolean registrarInscripcion(InscripcionActividad inscripcion) {
        if (!validarInscripcion(inscripcion)) {
            return false;
        }
        if (inscripcionDAO.existeInscripcion(inscripcion)) {
            return false;
        }
        inscripcion.setFechaInscripcion(LocalDate.now().toString());
        inscripcion.setEstadoInscripcion("Inactiva");
        return inscripcionDAO.registrarInscripcion(inscripcion);
    }

    // Consulta todas las inscripciones registradas en el sistema.
    public List<InscripcionActividad> consultarInscripciones() {
        return inscripcionDAO.consultarInscripciones();
    }

    // Consulta las inscripciones correspondientes a una actividad específica.
    public List<InscripcionActividad> consultarPorActividad(int idActividad) {
        return inscripcionDAO.consultarPorActividad(idActividad);
    }

    // Modifica el estado de una inscripción existente.
    public boolean modificarInscripcion(InscripcionActividad inscripcion) {
        if (inscripcion == null) {
            return false;
        }
        if (inscripcion.getIdInscripcion() <= 0) {
            return false;
        }
        if (inscripcion.getEstadoInscripcion() == null ||
                inscripcion.getEstadoInscripcion().isBlank()) {
            return false;
        }
        return inscripcionDAO.modificarInscripcion(inscripcion);
    }

}