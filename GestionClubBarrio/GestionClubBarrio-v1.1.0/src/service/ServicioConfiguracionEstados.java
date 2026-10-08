package service;

import dao.ConfiguracionEstadosDAO;
import model.Auxiliares.ConfiguracionEstados;
import model.Auxiliares.UltimoPago;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import util.Permisos;

public class ServicioConfiguracionEstados {

    private final ConfiguracionEstadosDAO configuracionEstadosDAO;
    private static final int MAX_DIAS = 365;

    // Constructor que inicializa el DAO encargado de la configuración de los estados.
    public ServicioConfiguracionEstados() {
        configuracionEstadosDAO = new ConfiguracionEstadosDAO();
    }

    // Obtiene los días configurados para el vencimiento de cuotas.
    public ConfiguracionEstados obtenerConfiguracion() {
        return configuracionEstadosDAO.obtenerConfiguracion();
    }

    // Obtiene la fecha del último pago registrado de cada socio.
    public List<UltimoPago> consultarUltimoPagoSocios() {
        return configuracionEstadosDAO.consultarUltimoPagoSocios();
    }

    // Obtiene la fecha del último pago registrado de cada inscripción.
    public List<UltimoPago> consultarUltimoPagoInscripciones() {
        return configuracionEstadosDAO.consultarUltimoPagoInscripciones();
    }

    // Valida y guarda los días que determinan el vencimiento de las cuotas.
    public String modificarConfiguracion(String diasDeudor, String diasInactivo,
                                         String diasInscripcion) {
        if (!Permisos.puedeGestionarUsuarios()) {
            return "No tiene permisos para modificar los vencimientos.";
        }
        int deudor;
        int inactivo;
        int inscripcion;
        try {
            deudor = Integer.parseInt(diasDeudor.trim());
            inactivo = Integer.parseInt(diasInactivo.trim());
            inscripcion = Integer.parseInt(diasInscripcion.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return "Los vencimientos deben ser números enteros.";
        }
        if (deudor < 1 || inactivo < 1 || inscripcion < 1
                || deudor > MAX_DIAS || inactivo > MAX_DIAS || inscripcion > MAX_DIAS) {
            return "Los vencimientos deben estar entre 1 y " + MAX_DIAS + " días.";
        }
        if (inactivo <= deudor) {
            return "Los días para pasar a socio inactivo deben ser mayores "
                    + "que los de socio deudor.";
        }
        ConfiguracionEstados configuracion = new ConfiguracionEstados(deudor, inactivo, inscripcion);
        if (configuracionEstadosDAO.modificarConfiguracion(configuracion)) {
            return "OK";
        }
        return "No se pudieron guardar los vencimientos.";
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
        Map<Integer, String> estados = new HashMap<>();
        for (UltimoPago socio : socios) {
            String fecha = socio.getUltimaFechaPago();
            if (fecha == null || fecha.isBlank()) {
                estados.put(socio.getId(), "Inactivo");
                continue;
            }
            Long diasSinPago = calcularDiasTranscurridos(fecha);
            if (diasSinPago == null) {
                continue;
            }
            String nuevoEstado;
            if (diasSinPago > configuracion.getDiasSocioInactivo()) {
                nuevoEstado = "Inactivo";
            } else if (diasSinPago > configuracion.getDiasSocioDeudor()) {
                nuevoEstado = "Deudor";
            } else {
                nuevoEstado = "Activo";
            }
            estados.put(socio.getId(), nuevoEstado);
        }
        if (!estados.isEmpty()) {
            configuracionEstadosDAO.actualizarEstadoSocio(estados);
        }
    }

    /* Actualiza el estado de las inscripciones a actividades según los días transcurridos
       desde el último pago registrado.
    */
    private void actualizarEstadosInscripciones(ConfiguracionEstados configuracion) {
        List<UltimoPago> inscripciones = configuracionEstadosDAO.consultarUltimoPagoInscripciones();
        Map<Integer, String> estados = new HashMap<>();
        for (UltimoPago inscripcion : inscripciones) {
            String fecha = inscripcion.getUltimaFechaPago();
            if (fecha == null || fecha.isBlank()) {
                estados.put(inscripcion.getId(), "Inactiva");
                continue;
            }
            Long diasSinPago = calcularDiasTranscurridos(fecha);
            if (diasSinPago == null) {
                continue;
            }
            String nuevoEstado =
                    diasSinPago > configuracion.getDiasInscripcionInactiva() ? "Inactiva" : "Activa";
            estados.put(inscripcion.getId(), nuevoEstado);
        }
        if (!estados.isEmpty()) {
            configuracionEstadosDAO.actualizarEstadoInscripcion(estados);
        }
    }

    // Calcula la cantidad de días transcurridos entre una fecha dada y la fecha actual.
    private Long calcularDiasTranscurridos(String fechaTexto) {
        try {
            LocalDate fecha = LocalDate.parse(fechaTexto);
            LocalDate hoy = LocalDate.now();
            return ChronoUnit.DAYS.between(fecha, hoy);
        } catch (RuntimeException e) {
            System.err.println("Fecha inválida encontrada al recalcular estados: " + fechaTexto);
            return null;
        }
    }

}