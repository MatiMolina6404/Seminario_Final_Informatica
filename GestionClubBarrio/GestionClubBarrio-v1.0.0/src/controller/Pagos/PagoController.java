package controller.Pagos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.InscripcionActividad;
import model.PagoActividad;
import model.PagoCuotaSocietaria;
import model.Participante;
import model.Socio;
import service.ServicioInscripcionActividad;
import service.ServicioPago;
import service.ServicioSocio;
import service.ServicioConfiguracionEstados;
import util.Ventana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PagoController {

    private ServicioPago servicioPago;
    private ServicioSocio servicioSocio;
    private ServicioInscripcionActividad servicioInscripcion;
    private ServicioConfiguracionEstados servicioConfiguracionEstados;
    private Socio socioSeleccionado;
    private InscripcionActividad inscripcionSeleccionada;
    @FXML
    private ComboBox<String> cmbConceptoPago;
    @FXML
    private ComboBox<String> cmbTipoPago;
    @FXML
    private Label lblBuscarPor;
    @FXML
    private Label lblActividad;
    @FXML
    private TextField txtIdBusqueda;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtActividad;
    @FXML
    private TextField txtPeriodo;
    @FXML
    private TextField txtMonto;

    // Inicializa los servicios y carga las opciones de pago.
    @FXML
    public void initialize() {
        servicioPago = new ServicioPago();
        servicioSocio = new ServicioSocio();
        servicioInscripcion = new ServicioInscripcionActividad();
        servicioConfiguracionEstados = new ServicioConfiguracionEstados();
        cmbConceptoPago.getItems().addAll("Cuota societaria", "Actividad deportiva");
        cmbTipoPago.getItems().addAll("Efectivo", "Transferencia");
        cmbConceptoPago.setValue("Cuota societaria");
        cmbTipoPago.setValue("Efectivo");
        cmbConceptoPago.setOnAction(e -> cambiarConceptoPago());
        cambiarConceptoPago();
    }

    /* Adapta la pantalla según el concepto de pago seleccionado.
       Para cuotas societarias oculta la actividad; para pagos de actividades la muestra.
    */
    private void cambiarConceptoPago() {
        limpiarDatosEncontrados();
        String concepto = cmbConceptoPago.getValue();
        lblBuscarPor.setText("DNI:");
        txtIdBusqueda.setPromptText("Ingrese DNI");
        if ("Cuota societaria".equals(concepto)) {
            lblActividad.setVisible(false);
            lblActividad.setManaged(false);
            txtActividad.setVisible(false);
            txtActividad.setManaged(false);
        } else {
            lblActividad.setVisible(true);
            lblActividad.setManaged(true);
            txtActividad.setVisible(true);
            txtActividad.setManaged(true);
        }
    }

    // Busca el registro asociado al DNI ingresado.
    @FXML
    private void buscarRegistro() {
        String dni = txtIdBusqueda.getText();
        if (dni == null || dni.isBlank()) {
            mostrarError("Ingrese un DNI para buscar.");
            return;
        }
        dni = dni.trim();
        String concepto = cmbConceptoPago.getValue();
        if ("Cuota societaria".equals(concepto)) {
            buscarSocioPorDni(dni);
        } else {
            buscarInscripcionPorDni(dni);
        }
    }

    // Busca un socio por DNI para registrar el pago de una cuota societaria.
    private void buscarSocioPorDni(String dni) {
        var socio = servicioSocio.buscarPorDni(dni);
        if (socio.isEmpty()) {
            mostrarError("No existe un socio con ese DNI.");
            limpiarDatosEncontrados();
            return;
        }
        socioSeleccionado = socio.get();
        inscripcionSeleccionada = null;
        txtNombre.setText(socioSeleccionado.getNombreSocio());
        txtApellido.setText(socioSeleccionado.getApellidoSocio());
    }

    // Busca inscripciones asociadas a un DNI para registrar el pago de una actividad.
    private void buscarInscripcionPorDni(String dni) {
        List<InscripcionActividad> inscripciones = servicioInscripcion.consultarInscripciones();
        List<InscripcionActividad> coincidencias = new ArrayList<>();
        for (InscripcionActividad inscripcion : inscripciones) {
            if (inscripcion.getSocio() != null
                    && dni.equals(inscripcion.getSocio().getDniSocio())) {
                coincidencias.add(inscripcion);
            }
            if (inscripcion.getParticipante() != null
                    && dni.equals(inscripcion.getParticipante().getDniParticipante())) {
                coincidencias.add(inscripcion);
            }
        }
        if (coincidencias.isEmpty()) {
            mostrarError("No existe una inscripción asociada a ese DNI.");
            limpiarDatosEncontrados();
            return;
        }
        if (coincidencias.size() == 1) {
            cargarInscripcionSeleccionada(coincidencias.getFirst());
            return;
        }
        ChoiceDialog<InscripcionActividad> dialogo =
                new ChoiceDialog<>(coincidencias.get(0), coincidencias);
        dialogo.setTitle("Seleccionar inscripción");
        dialogo.setHeaderText("La persona tiene más de una inscripción.");
        dialogo.setContentText("Seleccione la inscripción a pagar:");
        Ventana.aplicarEstiloAlerta(dialogo);
        dialogo.showAndWait().ifPresent(this::cargarInscripcionSeleccionada);
    }

    // Registra el pago según el concepto seleccionado. Valida período, tipo de pago y monto.
    @FXML
    private void registrarPago() {
        String concepto = cmbConceptoPago.getValue();
        if (concepto == null || concepto.isBlank()) {
            mostrarError("Seleccione el concepto de pago.");
            return;
        }
        if (txtPeriodo.getText() == null || txtPeriodo.getText().isBlank()) {
            mostrarError("Ingrese el período.");
            return;
        }
        if (cmbTipoPago.getValue() == null || cmbTipoPago.getValue().isBlank()) {
            mostrarError("Seleccione el tipo de pago.");
            return;
        }
        double monto;
        try {
            monto = Double.parseDouble(txtMonto.getText().trim().replace(",", ".")
            );
        } catch (Exception e) {
            mostrarError("Ingrese un monto válido.");
            return;
        }
        if (monto <= 0) {
            mostrarError("El monto debe ser mayor a cero.");
            return;
        }

        boolean registrado;
        if ("Cuota societaria".equals(concepto)) {
            registrado = registrarPagoCuotaSocietaria(monto);
        } else {
            registrado = registrarPagoActividad(monto);
        }
        if (registrado) {
            servicioConfiguracionEstados.actualizarEstadosAutomaticamente();
            mostrarInformacion("Pago registrado correctamente.");
            limpiarCamposPago();
        } else {
            mostrarError("No se pudo registrar el pago.");
        }
    }

    // Crea y registra un pago de cuota societaria asociado al socio seleccionado.
    private boolean registrarPagoCuotaSocietaria(double monto) {
        if (socioSeleccionado == null) {
            mostrarError("Debe buscar y seleccionar un socio.");
            return false;
        }
        PagoCuotaSocietaria pago = new PagoCuotaSocietaria();
        pago.setMontoPago(monto);
        pago.setTipoPago(cmbTipoPago.getValue());
        pago.setPeriodoCuotaSoc(txtPeriodo.getText());
        pago.setSocio(socioSeleccionado);
        return servicioPago.registrarPagoCuotaSocietaria(pago);
    }

    // Crea y registra un pago de actividad asociado a la inscripción seleccionada.
    private boolean registrarPagoActividad(double monto) {
        if (inscripcionSeleccionada == null) {
            mostrarError("Debe buscar y seleccionar una inscripción.");
            return false;
        }
        PagoActividad pago = new PagoActividad();
        pago.setMontoPago(monto);
        pago.setTipoPago(cmbTipoPago.getValue());
        pago.setPeriodoCuotaAct(txtPeriodo.getText());
        pago.setInscripcionActividad(inscripcionSeleccionada);
        return servicioPago.registrarPagoActividad(pago);
    }

    // Abre la ventana de historial de pagos.
    @FXML
    private void abrirHistorialPagos() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/Pagos/HistorialPagos.fxml"
            ));
            Parent root = loader.load();
            Stage stage = Ventana.crearVentana(root, "Historial de pagos");
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir el historial de pagos.");
        }
    }

    // Limpia los datos de socio o inscripción encontrados en la búsqueda.
    private void limpiarDatosEncontrados() {
        socioSeleccionado = null;
        inscripcionSeleccionada = null;
        txtIdBusqueda.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtActividad.clear();
    }

    // Limpia los campos del formulario luego de registrar un pago.
    private void limpiarCamposPago() {
        limpiarDatosEncontrados();
        txtPeriodo.clear();
        txtMonto.clear();
        cmbTipoPago.setValue("Efectivo");
    }

    // Muestra un mensaje informativo al usuario.
    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

    // Muestra un mensaje de error al usuario.
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

    // Carga en pantalla los datos de la inscripción seleccionada.
    private void cargarInscripcionSeleccionada(InscripcionActividad inscripcion) {
        inscripcionSeleccionada = inscripcion;
        socioSeleccionado = null;
        if (inscripcion.getSocio() != null) {
            Socio socio = inscripcion.getSocio();
            txtNombre.setText(socio.getNombreSocio());
            txtApellido.setText(socio.getApellidoSocio());
        } else {
            Participante participante = inscripcion.getParticipante();
            txtNombre.setText(participante.getNombreParticipante());
            txtApellido.setText(participante.getApellidoParticipante());
        }
        if (inscripcion.getActividad() != null) {
            txtActividad.setText(inscripcion.getActividad().getNombreActividad());
        }
    }

}