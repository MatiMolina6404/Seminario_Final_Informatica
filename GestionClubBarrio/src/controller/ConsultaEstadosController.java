package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.InscripcionActividad;
import model.Socio;
import service.ServicioConfiguracionEstados;
import service.ServicioInscripcionActividad;
import service.ServicioSocio;

import java.util.List;

public class ConsultaEstadosController {

    private ServicioSocio servicioSocio;
    private ServicioInscripcionActividad servicioInscripcion;
    private ServicioConfiguracionEstados servicioConfiguracionEstados;
    @FXML
    private ComboBox<String> cmbTipoRegistro;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TableView<Socio> tablaSocios;
    @FXML
    private TableColumn<Socio, Integer> colIdSocio;
    @FXML
    private TableColumn<Socio, String> colNombreSocio;
    @FXML
    private TableColumn<Socio, String> colApellidoSocio;
    @FXML
    private TableColumn<Socio, String> colDniSocio;
    @FXML
    private TableColumn<Socio, String> colEstadoSocio;
    @FXML
    private TableView<InscripcionActividad> tablaInscripciones;
    @FXML
    private TableColumn<InscripcionActividad, Integer> colIdInscripcion;
    @FXML
    private TableColumn<InscripcionActividad, String> colNombreInscripcion;
    @FXML
    private TableColumn<InscripcionActividad, String> colApellidoInscripcion;
    @FXML
    private TableColumn<InscripcionActividad, String> colDniInscripcion;
    @FXML
    private TableColumn<InscripcionActividad, String> colActividad;
    @FXML
    private TableColumn<InscripcionActividad, String> colEstadoInscripcion;

    /* Inicializa los servicios, configura los combos de búsqueda y
       vincula las columnas de las tablas con sus modelos.
    */
    @FXML
    public void initialize() {
        servicioSocio = new ServicioSocio();
        servicioInscripcion = new ServicioInscripcionActividad();
        servicioConfiguracionEstados = new ServicioConfiguracionEstados();
        cmbTipoRegistro.getItems().addAll("Socios", "Actividades");
        cmbTipoRegistro.setValue("Socios");
        cmbTipoRegistro.setOnAction(e -> configurarEstados());
        cmbEstado.setOnAction(e -> buscarRegistros());

        colIdSocio.setCellValueFactory(new PropertyValueFactory<>("idSocio"));
        colNombreSocio.setCellValueFactory(new PropertyValueFactory<>("nombreSocio"));
        colApellidoSocio.setCellValueFactory(new PropertyValueFactory<>("apellidoSocio"));
        colDniSocio.setCellValueFactory(new PropertyValueFactory<>("dniSocio"));
        colEstadoSocio.setCellValueFactory(new PropertyValueFactory<>("estadoSocio"));
        tablaSocios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colIdInscripcion.setCellValueFactory(new PropertyValueFactory<>("idInscripcion"));
        colNombreInscripcion.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoInscripcion.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDniInscripcion.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colActividad.setCellValueFactory(new PropertyValueFactory<>("nombreActividad"));
        colEstadoInscripcion.setCellValueFactory(new PropertyValueFactory<>("estadoInscripcion"));
        tablaInscripciones.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        configurarEstados();
    }

    /* Configura los estados disponibles según el tipo de registro seleccionado.
       Alterna la visibilidad entre la tabla de socios y la tabla de inscripciones.
     */
    private void configurarEstados() {
        cmbEstado.setOnAction(null);
        cmbEstado.getItems().clear();
        String tipo = cmbTipoRegistro.getValue();

        if ("Socios".equals(tipo)) {
            cmbEstado.getItems().addAll("Activo", "Deudor", "Inactivo");
            cmbEstado.setValue("Activo");
            tablaSocios.setVisible(true);
            tablaSocios.setManaged(true);
            tablaInscripciones.setVisible(false);
            tablaInscripciones.setManaged(false);
        } else {
            cmbEstado.getItems().addAll("Activa", "Inactiva");
            cmbEstado.setValue("Activa");
            tablaSocios.setVisible(false);
            tablaSocios.setManaged(false);
            tablaInscripciones.setVisible(true);
            tablaInscripciones.setManaged(true);
        }
        cmbEstado.setOnAction(e -> buscarRegistros());
        buscarRegistros();
    }

    /* Actualiza automáticamente los estados y muestra los registros
       que coinciden con el tipo y estado seleccionados.
    */
    @FXML
    private void buscarRegistros() {
        String tipo = cmbTipoRegistro.getValue();
        String estado = cmbEstado.getValue();

        if (tipo == null || estado == null) {
            return;
        }
        servicioConfiguracionEstados.actualizarEstadosAutomaticamente();
        if ("Socios".equals(tipo)) {
            List<Socio> socios = servicioSocio.consultarSocios("Estado", estado);
            tablaSocios.getItems().setAll(socios);
            tablaSocios.refresh();
        } else {
            List<InscripcionActividad> inscripciones = servicioInscripcion.consultarInscripciones();
            inscripciones.removeIf(inscripcion ->
                    !estado.equals(inscripcion.getEstadoInscripcion())
            );
            tablaInscripciones.getItems().setAll(inscripciones);
            tablaInscripciones.refresh();
        }
    }

}