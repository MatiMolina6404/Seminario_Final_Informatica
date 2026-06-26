package controller.Pagos;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.Auxiliares.PagoHistorial;
import service.ServicioPago;
import util.Impresion;
import util.Ventana;

import java.util.List;

public class HistorialPagosController {

    private ServicioPago servicioPago;
    @FXML
    private CheckBox chkCuotaSocietaria;
    @FXML
    private CheckBox chkActividad;
    @FXML
    private ComboBox<String> cmbCampo;
    @FXML
    private TextField txtValor;
    @FXML
    private TableView<PagoHistorial> tablaPagos;
    @FXML
    private TableColumn<PagoHistorial, Integer> colIdPago;
    @FXML
    private TableColumn<PagoHistorial, String> colFechaPago;
    @FXML
    private TableColumn<PagoHistorial, String> colNombre;
    @FXML
    private TableColumn<PagoHistorial, String> colApellido;
    @FXML
    private TableColumn<PagoHistorial, String> colConcepto;
    @FXML
    private TableColumn<PagoHistorial, String> colPeriodo;
    @FXML
    private TableColumn<PagoHistorial, String> colFormaPago;
    @FXML
    private TableColumn<PagoHistorial, Double> colMonto;

    /* Inicializa el controlador, configura los filtros de búsqueda y
       vincula las columnas de la tabla con los datos del historial de pagos.
    */
    @FXML
    public void initialize() {
        servicioPago = new ServicioPago();
        cmbCampo.getItems().addAll("Todos", "ID", "Fecha", "Nombre", "Apellido",
                "Concepto", "Período", "Forma Pago", "Monto"
        );
        cmbCampo.setValue("Todos");
        colIdPago.setCellValueFactory(new PropertyValueFactory<>("idPago"));
        colFechaPago.setCellValueFactory(new PropertyValueFactory<>("fechaPago"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colConcepto.setCellValueFactory(new PropertyValueFactory<>("concepto"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        colFormaPago.setCellValueFactory(new PropertyValueFactory<>("formaPago"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        tablaPagos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        chkCuotaSocietaria.setSelected(true);
        chkActividad.setSelected(true);
        buscarPagos();
    }

    /* Busca pagos según el campo seleccionado, el valor ingresado y
       los tipos de pago marcados por el usuario.
    */
    @FXML
    private void buscarPagos() {
        String campo = cmbCampo.getValue();
        String valor = txtValor.getText();

        boolean incluirCuotas = chkCuotaSocietaria.isSelected();
        boolean incluirActividades = chkActividad.isSelected();
        List<PagoHistorial> resultados = servicioPago.consultarHistorialPagos
                        (campo, valor, incluirCuotas, incluirActividades);
        tablaPagos.getItems().setAll(resultados);
        tablaPagos.refresh();
    }

    // Genera y muestra un comprobante con los datos del pago seleccionado.
    @FXML
    private void emitirComprobante() {
        PagoHistorial pago = tablaPagos.getSelectionModel().getSelectedItem();
        if (pago == null) {
            mostrarError("Seleccione un pago.");
            return;
        }
        String comprobante = "COMPROBANTE DE PAGO\n\n"
                + "Nro. Pago: " + pago.getIdPago() + "\n"
                + "Fecha: " + pago.getFechaPago() + "\n"
                + "Nombre: " + pago.getNombre() + "\n"
                + "Apellido: " + pago.getApellido() + "\n"
                + "Concepto: " + pago.getConcepto() + "\n"
                + "Período: " + pago.getPeriodo() + "\n"
                + "Forma de pago: " + pago.getFormaPago() + "\n"
                + "Monto: $" + pago.getMonto();
        Impresion.mostrarDocumento("Comprobante de pago", comprobante,
                tablaPagos.getScene().getWindow());
    }

    // Muestra un mensaje de error al usuario.
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

}