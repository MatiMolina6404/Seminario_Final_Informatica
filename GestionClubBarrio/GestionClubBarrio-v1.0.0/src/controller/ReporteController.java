package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Actividad;
import service.ServicioActividad;
import service.ServicioReporte;
import util.Impresion;
import util.Ventana;

import java.util.List;

public class ReporteController {

    private ServicioReporte servicioReporte;
    private ServicioActividad servicioActividad;
    @FXML
    private ComboBox<String> cmbTipoReporte;
    @FXML
    private ComboBox<Actividad> cmbActividad;
    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;

    /* Inicializa los servicios, carga los tipos de reporte disponibles y
       configura el selector de actividades.
    */
    @FXML
    public void initialize() {
        servicioReporte = new ServicioReporte();
        servicioActividad = new ServicioActividad();
        cmbTipoReporte.getItems().addAll(
                "Reporte de Socios",
                "Reporte de Cuotas Societarias",
                "Reporte de Actividades Deportivas",
                "Reporte General"
        );
        cmbTipoReporte.setValue("Reporte General");
        cargarActividades();
        cmbTipoReporte.setOnAction(e -> configurarActividad());
        configurarActividad();
    }

    // Carga las actividades disponibles para los reportes.
    private void cargarActividades() {
        Actividad general = new Actividad();
        general.setIdActividad(0);
        general.setNombreActividad("General");
        cmbActividad.getItems().add(general);
        List<Actividad> actividades = servicioActividad.consultarActividades("Todos","");
        cmbActividad.getItems().addAll(actividades);
        cmbActividad.setValue(general);
    }

    // Habilita el selector de actividad solo cuando se genera un reporte de actividades deportivas.
    private void configurarActividad() {
        String tipoReporte = cmbTipoReporte.getValue();
        boolean esReporteActividad = "Reporte de Actividades Deportivas".equals(tipoReporte);
        cmbActividad.setDisable(!esReporteActividad);
        if (!esReporteActividad) {
            cmbActividad.getSelectionModel().selectFirst();
        }
    }

    // Genera el reporte seleccionado a partir de los datos indicados por el usuario.
    @FXML
    private void generarReporte() {
        String tipoReporte = cmbTipoReporte.getValue();
        Actividad actividad = cmbActividad.getValue();
        String reporte = servicioReporte.generarReporte(
                        tipoReporte,
                        dpDesde.getValue(),
                        dpHasta.getValue(),
                        actividad
        );
        mostrarReporte(tipoReporte, reporte);
    }

    // Muestra el reporte generado dentro de un cuadro de diálogo.
    private void mostrarReporte(String titulo, String reporte) {
        Impresion.mostrarDocumento(titulo, reporte, cmbTipoReporte.getScene().getWindow());
    }

}