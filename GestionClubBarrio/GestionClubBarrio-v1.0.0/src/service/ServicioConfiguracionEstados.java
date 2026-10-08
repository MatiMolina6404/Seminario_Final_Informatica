package service;

import dao.ConfiguracionEstadosDAO;
import model.Auxiliares.ConfiguracionEstados;
import model.Auxiliares.UltimoPago;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ServicioConfiguracionEstados {

    private final ConfiguracionEstadosDAO configuracionEstadosDAO;

    // Constructor que inicializa el DAO encargado de la configuración de los estados
    public ServicioConfiguracionEstados() {
        configuracionEstadosDAO = new ConfiguracionEstadosDAO();
    }

    // Ejecuta la actualización automática de estados.
    public void actualizarEstadosAutomaticamente() {
        ConfiguracionEstados configuracion = configuracionEstadosDAO.obtenerConfiguracion();
        actualizarEstadosSocios(configuracion);
        actualizarEstadosInscripciones(configuracion);
    }

    /* Actualiza el estado administrativo de los socios según los días transcurridos
       desde su último pago registrado.
     */
    private void actualizarEstadosSocios(ConfiguracionEstados configuracion) {
        List<UltimoPago> socios = configuracionEstadosDAO.consultarUltimoPagoSocios();

        for (UltimoPago socio : socios) {
            String fecha = socio.getUltimaFechaPago();
            if (fecha == null || fecha.isBlank()) {
                continue;
            }
            long diasSinPago = calcularDiasTranscurridos(fecha);
            String nuevoEstado;
            if (diasSinPago > configuracion.getDiasSocioInactivo()) {
                nuevoEstado = "Inactivo";
            } else if (diasSinPago > configuracion.getDiasSocioDeudor()) {
                nuevoEstado = "Deudor";
            } else {
                nuevoEstado = "Activo";
            }
            configuracionEstadosDAO.actualizarEstadoSocio(socio.getId(), nuevoEstado
            );
        }
    }

    /* Actualiza el estado de las inscripciones a actividades según los días transcurridos
       desde el último pago registrado.
     */
    private void actualizarEstadosInscripciones(ConfiguracionEstados configuracion) {
        List<UltimoPago> inscripciones = configuracionEstadosDAO.consultarUltimoPagoInscripciones();

        for (UltimoPago inscripcion : inscripciones) {
            String fecha = inscripcion.getUltimaFechaPago();
            if (fecha == null || fecha.isBlank()) {
                continue;
            }
            long diasSinPago = calcularDiasTranscurridos(fecha);
            String nuevoEstado;
            if (diasSinPago > configuracion.getDiasInscripcionInactiva()) {
                nuevoEstado = "Inactiva";
            } else {
                nuevoEstado = "Activa";
            }
            configuracionEstadosDAO.actualizarEstadoInscripcion(inscripcion.getId(), nuevoEstado
            );
        }
    }

    // Calcula la cantidad de días transcurridos entre una fecha dada y la fecha actual.
    private long calcularDiasTranscurridos(String fechaTexto) {
        LocalDate fecha = LocalDate.parse(fechaTexto);
        LocalDate hoy = LocalDate.now();
        return ChronoUnit.DAYS.between(fecha, hoy);
    }

}