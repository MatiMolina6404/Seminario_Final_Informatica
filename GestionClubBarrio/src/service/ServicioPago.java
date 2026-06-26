package service;

import dao.PagoDAO;
import model.Pago;
import model.PagoActividad;
import model.PagoCuotaSocietaria;
import model.Auxiliares.PagoHistorial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServicioPago {

    private final PagoDAO pagoDAO;

    // Constructor que inicializa el DAO encargado del acceso a datos de pagos.
    public ServicioPago() {
        pagoDAO = new PagoDAO();
    }

    // Valida los datos comunes de cualquier tipo de pago.
    private boolean validarPago(Pago pago) {
        if (pago == null) {
            return false;
        }
        if (pago.getMontoPago() <= 0) {
            return false;
        }
        return pago.getTipoPago() != null && !pago.getTipoPago().isBlank();
    }

    // Asigna la fecha actual al pago si no fue cargada previamente.
    private void asignarFechaActual(Pago pago) {
        if (pago.getFechaPago() == null || pago.getFechaPago().isBlank()) {
            pago.setFechaPago(LocalDate.now().toString());
        }
    }

    // Registra un pago de cuota societaria si los datos son válidos.
    public boolean registrarPagoCuotaSocietaria(PagoCuotaSocietaria pago) {
        if (!validarPago(pago)) {
            return false;
        }
        if (pago.getPeriodoCuotaSoc() == null
                || pago.getPeriodoCuotaSoc().isBlank()) {
            return false;
        }
        if (pago.getSocio() == null
                || pago.getSocio().getIdSocio() <= 0) {
            return false;
        }
        asignarFechaActual(pago);
        return pagoDAO.registrarPagoCuotaSocietaria(pago);
    }

    // Registra un pago de actividad si los datos son válidos.
    public boolean registrarPagoActividad(PagoActividad pago) {
        if (!validarPago(pago)) {
            return false;
        }
        if (pago.getPeriodoCuotaAct() == null
                || pago.getPeriodoCuotaAct().isBlank()) {
            return false;
        }
        if (pago.getInscripcionActividad() == null
                || pago.getInscripcionActividad().getIdInscripcion() <= 0) {
            return false;
        }
        asignarFechaActual(pago);
        return pagoDAO.registrarPagoActividad(pago);
    }

    // Construye un historial unificado de pagos de cuotas societarias, de actividades o de ambos.
    public List<PagoHistorial> consultarHistorialPagos(String campo, String valor,
            boolean incluirCuotas, boolean incluirActividades) {

        List<PagoHistorial> historial = new ArrayList<>();
        if (incluirCuotas) {
            List<PagoCuotaSocietaria> pagosCuotas = pagoDAO.consultarPagosCuotaSocietaria();
            for (PagoCuotaSocietaria pago : pagosCuotas) {
                PagoHistorial pagoHistorial = new PagoHistorial();
                pagoHistorial.setIdPago(pago.getIdPago());
                pagoHistorial.setFechaPago(pago.getFechaPago());
                pagoHistorial.setNombre(pago.getNombreSocio());
                pagoHistorial.setApellido(pago.getApellidoSocio());
                pagoHistorial.setConcepto("Cuota Socio");
                pagoHistorial.setPeriodo(pago.getPeriodoCuotaSoc());
                pagoHistorial.setFormaPago(pago.getTipoPago());
                pagoHistorial.setMonto(pago.getMontoPago());
                historial.add(pagoHistorial);
            }
        }
        if (incluirActividades) {
            List<PagoActividad> pagosActividad = pagoDAO.consultarPagosActividad();
            for (PagoActividad pago : pagosActividad) {

                PagoHistorial pagoHistorial = new PagoHistorial();
                pagoHistorial.setIdPago(pago.getIdPago());
                pagoHistorial.setFechaPago(pago.getFechaPago());
                pagoHistorial.setConcepto(pago.getNombreActividad());
                pagoHistorial.setPeriodo(pago.getPeriodoCuotaAct());
                pagoHistorial.setFormaPago(pago.getTipoPago());
                pagoHistorial.setMonto(pago.getMontoPago());
                // Se completa la persona asociada al pago, ya sea socio o participante.
                if (pago.getInscripcionActividad() != null) {
                    if (pago.getInscripcionActividad().getSocio() != null) {
                        pagoHistorial.setNombre(pago.getInscripcionActividad()
                                        .getSocio()
                                        .getNombreSocio());
                        pagoHistorial.setApellido(pago.getInscripcionActividad()
                                        .getSocio()
                                        .getApellidoSocio());
                    } else if (pago.getInscripcionActividad().getParticipante() != null) {
                        pagoHistorial.setNombre(pago.getInscripcionActividad()
                                        .getParticipante()
                                        .getNombreParticipante());
                        pagoHistorial.setApellido(pago.getInscripcionActividad()
                                        .getParticipante()
                                        .getApellidoParticipante());
                    }
                }
                historial.add(pagoHistorial);
            }
        }
        historial = filtrarHistorial(historial, campo, valor);
        historial.sort((p1, p2) ->
                Integer.compare(p2.getIdPago(), p1.getIdPago()));
        return historial;
    }

    // Filtra el historial de pagos según el campo y valor indicados por el usuario.
    private List<PagoHistorial> filtrarHistorial(List<PagoHistorial> historial,
                                                 String campo, String valor) {

        if (campo == null || campo.equals("Todos")) {
            return historial;
        }
        if (valor == null || valor.isBlank()) {
            return historial;
        }

        String busqueda = valor.toLowerCase();
        historial.removeIf(pago -> {
            if ("ID".equals(campo)) {
                return !String.valueOf(pago.getIdPago()).equals(busqueda);
            }
            if ("Nombre".equals(campo)) {
                return pago.getNombre() == null
                        || !pago.getNombre().toLowerCase().contains(busqueda);
            }
            if ("Apellido".equals(campo)) {
                return pago.getApellido() == null
                        || !pago.getApellido().toLowerCase().contains(busqueda);
            }
            if ("Concepto".equals(campo)) {
                return pago.getConcepto() == null
                        || !pago.getConcepto().toLowerCase().contains(busqueda);
            }
            if ("Período".equals(campo)) {
                return pago.getPeriodo() == null
                        || !pago.getPeriodo().toLowerCase().contains(busqueda);
            }
            if ("Forma Pago".equals(campo)) {
                return pago.getFormaPago() == null ||
                        !pago.getFormaPago().toLowerCase().contains(busqueda);
            }
            if ("Monto".equals(campo)) {
                return !String.valueOf(pago.getMonto()).equals(busqueda);
            }
            return false;
        });
        return historial;
    }

}