package service;

import dao.ReporteDAO;
import model.Actividad;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ServicioReporte {

    private final ReporteDAO reporteDAO;

    // Constructor que inicializa el DAO encargado de obtener los datos para los reportes.
    public ServicioReporte() {
        reporteDAO = new ReporteDAO();
    }

    /* Genera el reporte solicitado según el tipo seleccionado y el período indicado.
       También registra en la base de datos que el reporte fue generado.
    */
    public String generarReporte(String tipoReporte, LocalDate desde, LocalDate hasta,
                                 Actividad actividad) {
        if (tipoReporte == null || tipoReporte.isBlank()) {
            return "Debe seleccionar un tipo de reporte.";
        }
        if (desde == null || hasta == null) {
            return "Debe seleccionar el período del reporte.";
        }
        if (hasta.isBefore(desde)) {
            return "La fecha hasta no puede ser anterior a la fecha desde.";
        }
        String desdeTexto = desde.toString();
        String hastaTexto = hasta.toString();
        String reporte;
        if ("Reporte de Socios".equals(tipoReporte)) {
            reporte = generarReporteSocios(desdeTexto, hastaTexto);
        } else if ("Reporte de Cuotas Societarias".equals(tipoReporte)) {
            reporte = generarReporteCuotas(desde, hasta);
        } else if ("Reporte de Actividades Deportivas".equals(tipoReporte)) {
            reporte = generarReporteActividades(desde, hasta, actividad);
        } else if ("Reporte General".equals(tipoReporte)) {
            reporte = generarReporteGeneral(desdeTexto, hastaTexto);
        } else {
            return "Tipo de reporte no válido.";
        }
        reporteDAO.registrarReporteGenerado(tipoReporte,
                desdeTexto + " a " + hastaTexto);
        return reporte;
    }

    /* Genera un reporte con la cantidad de socios registrados y
       su distribución según estado administrativo.
    */
    private String generarReporteSocios(String desde, String hasta) {
        int totalSocios = reporteDAO.contarSocios(desde, hasta);
        int activos = reporteDAO.contarSociosPorEstado("Activo", desde, hasta);
        int deudores = reporteDAO.contarSociosPorEstado("Deudor", desde, hasta);
        int inactivos = reporteDAO.contarSociosPorEstado("Inactivo", desde, hasta);
        return "REPORTE DE SOCIOS\n\n"
                + "Período: " + desde + " a " + hasta + "\n\n"
                + "Total de socios: " + totalSocios + "\n"
                + "Cantidad de socios activos: " + activos + "\n"
                + "Cantidad de socios deudores: " + deudores + "\n"
                + "Cantidad de socios inactivos: " + inactivos;
    }

    // Genera un reporte financiero de cuotas societarias.
    private String generarReporteCuotas(LocalDate desde, LocalDate hasta) {

        String desdeTexto = desde.toString();
        String hastaTexto = hasta.toString();
        LocalDate[] periodoAnterior = calcularPeriodoAnterior(desde, hasta);
        String anteriorDesde = periodoAnterior[0].toString();
        String anteriorHasta = periodoAnterior[1].toString();

        int cantidadPagos = reporteDAO.contarPagosCuotas(desdeTexto, hastaTexto);
        double total = reporteDAO.sumarCuotas(desdeTexto, hastaTexto);
        double totalAnterior = reporteDAO.sumarCuotas(anteriorDesde, anteriorHasta);
        double balance = total - totalAnterior;
        double transferencias = reporteDAO.sumarCuotasPorTipoPago(
                desdeTexto, hastaTexto, "Transferencia");
        double efectivo = reporteDAO.sumarCuotasPorTipoPago(
                desdeTexto, hastaTexto, "Efectivo");
        return "REPORTE DE CUOTAS SOCIETARIAS\n\n"
                + "Período: " + desdeTexto + " a " + hastaTexto + "\n\n"
                + "Cantidad de pagos registrados: " + cantidadPagos + "\n"
                + "Total recaudado por cuotas societarias: " + formatearMonto(total) + "\n"
                + "Balance contra el período anterior: " + formatearMonto(balance) + "\n"
                + "Total período anterior: " + formatearMonto(totalAnterior) + "\n"
                + "Transferencias: " + formatearMonto(transferencias) + "\n"
                + "Pagos en efectivo: " + formatearMonto(efectivo);
    }

    // Genera un reporte financiero de actividades deportivas.
    private String generarReporteActividades(LocalDate desde, LocalDate hasta, Actividad actividad) {

        String desdeTexto = desde.toString();
        String hastaTexto = hasta.toString();
        Integer idActividad = null;
        String actividadTexto = "General";

        if (actividad != null && actividad.getIdActividad() > 0) {
            idActividad = actividad.getIdActividad();
            actividadTexto = actividad.getNombreActividad();
        }
        LocalDate[] periodoAnterior = calcularPeriodoAnterior(desde, hasta);
        String anteriorDesde = periodoAnterior[0].toString();
        String anteriorHasta = periodoAnterior[1].toString();
        int cantidadPagos = reporteDAO.contarPagosActividades(
                desdeTexto, hastaTexto, idActividad);
        double total = reporteDAO.sumarActividades(
                desdeTexto, hastaTexto, idActividad);
        double totalAnterior = reporteDAO.sumarActividades(
                anteriorDesde, anteriorHasta, idActividad);
        double balance = total - totalAnterior;
        double transferencias = reporteDAO.sumarActividadesPorTipoPago(
                desdeTexto, hastaTexto, "Transferencia", idActividad);
        double efectivo = reporteDAO.sumarActividadesPorTipoPago(
                desdeTexto, hastaTexto, "Efectivo", idActividad);
        return "REPORTE DE ACTIVIDADES DEPORTIVAS\n\n"
                + "Período: " + desdeTexto + " a " + hastaTexto + "\n"
                + "Actividad: " + actividadTexto + "\n\n"
                + "Cantidad de pagos registrados: " + cantidadPagos + "\n"
                + "Total recaudado: " + formatearMonto(total) + "\n"
                + "Balance contra el período anterior: " + formatearMonto(balance) + "\n"
                + "Total período anterior: " + formatearMonto(totalAnterior) + "\n"
                + "Transferencias: " + formatearMonto(transferencias) + "\n"
                + "Pagos en efectivo: " + formatearMonto(efectivo);
    }

    /* Genera un reporte general con información resumida de socios,
       cuotas societarias, inscripciones e ingresos por actividades.
    */
    private String generarReporteGeneral(String desde, String hasta) {
        int sociosRegistrados = reporteDAO.contarSocios(desde, hasta);
        double ingresosCuotas = reporteDAO.sumarCuotas(desde, hasta);
        int cantidadInscripciones = reporteDAO.contarInscripciones(desde, hasta);
        double ingresosActividades = reporteDAO.sumarActividades(desde, hasta, null);
        return "REPORTE GENERAL\n\n"
                + "Período: " + desde + " a " + hasta + "\n\n"
                + "Cantidad de socios registrados: " + sociosRegistrados + "\n"
                + "Ingresos por cuotas societarias: " + formatearMonto(ingresosCuotas) + "\n"
                + "Cantidad de inscripciones en actividades: " + cantidadInscripciones + "\n"
                + "Ingresos por actividades: " + formatearMonto(ingresosActividades);
    }

    /* Calcula un período anterior con la misma duración que el período seleccionado.
       Se utiliza para comparar ingresos entre períodos.
    */
    private LocalDate[] calcularPeriodoAnterior(LocalDate desde, LocalDate hasta) {
        long diasPeriodo = ChronoUnit.DAYS.between(desde, hasta) + 1;
        LocalDate hastaAnterior = desde.minusDays(1);
        LocalDate desdeAnterior = hastaAnterior.minusDays(diasPeriodo - 1);
        return new LocalDate[]{desdeAnterior, hastaAnterior};
    }

    // Formatea un valor numérico como monto monetario.
    private String formatearMonto(double monto) {
        return "$" + String.format("%.2f", monto);
    }

}