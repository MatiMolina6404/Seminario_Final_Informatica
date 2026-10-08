package controller.Actividades;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Actividad;
import service.ServicioActividad;
import util.Permisos;
import util.Ventana;

public class ActividadController {

    private ServicioActividad servicioActividad;
    @FXML
    private TableView<Actividad> tablaActividades;
    @FXML
    private TableColumn<Actividad, Integer> colIdAct;
    @FXML
    private TableColumn<Actividad, String> colNombreAct;
    @FXML
    private TableColumn<Actividad, String> colDescr;
    @FXML
    private TableColumn<Actividad, Integer> colCantParticipantes;
    @FXML
    private TextField txtRegNombre;
    @FXML
    private TextField txtRegDescrip;

    /* Inicializa el controlador, configura las columnas de la tabla y
       carga las actividades registradas.
    */
    @FXML
    public void initialize() {
        servicioActividad = new ServicioActividad();
        colIdAct.setCellValueFactory(new PropertyValueFactory<>("idActividad"));
        colNombreAct.setCellValueFactory(new PropertyValueFactory<>("nombreActividad"));
        colDescr.setCellValueFactory(new PropertyValueFactory<>("descripcionActividad"));
        colCantParticipantes.setCellValueFactory(new PropertyValueFactory<>("cantidadParticipantes"));
        tablaActividades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        cargarTabla();
        // Al seleccionar una actividad, se muestran sus datos en los campos del formulario.
        tablaActividades.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior,
                              seleccionada) -> {
                    if (seleccionada != null) {
                        txtRegNombre.setText(seleccionada.getNombreActividad());
                        txtRegDescrip.setText(seleccionada.getDescripcionActividad());
                    }
                });
    }

    // Carga en la tabla todas las actividades registradas.
    public void cargarTabla() {
        tablaActividades.getItems().setAll(servicioActividad.listarActividadesPantalla());
    }

    // Toma los datos ingresados en pantalla y solicita el registro de una nueva actividad.
    @FXML
    private void registrarActividad() {
        if (!Permisos.puedeGestionarActividades()) {
            mostrarError("No tiene permisos para registrar actividades.");
            return;
        }
        Actividad actividad = new Actividad();
        actividad.setNombreActividad(txtRegNombre.getText());
        actividad.setDescripcionActividad(txtRegDescrip.getText());
        boolean registrado = servicioActividad.registrarActividad(actividad);
        if (registrado) {
            mostrarInformacion("Actividad registrada correctamente.");
            limpiarCampos();
            cargarTabla();
        } else {
            mostrarError("No se pudo registrar la actividad.");
        }
    }

    // Modifica la actividad seleccionada con los datos ingresados en el formulario.
    @FXML
    private void modificarActividad() {
        if (!Permisos.puedeGestionarActividades()) {
            mostrarError("No tiene permisos para modificar actividades.");
            return;
        }
        Actividad actividad = tablaActividades.getSelectionModel().getSelectedItem();
        if (actividad == null) {
            mostrarError("Seleccione una actividad.");
            return;
        }
        actividad.setNombreActividad(txtRegNombre.getText());
        actividad.setDescripcionActividad(txtRegDescrip.getText());
        boolean modificada = servicioActividad.modificarActividad(actividad);
        if (modificada) {
            mostrarInformacion("Actividad modificada correctamente.");
            limpiarCampos();
            cargarTabla();
        } else {
            mostrarError("No se pudo modificar la actividad.");
        }
    }

    // Limpia los campos del formulario y quita la selección de la tabla.
    private void limpiarCampos() {
        txtRegNombre.clear();
        txtRegDescrip.clear();
        tablaActividades.getSelectionModel().clearSelection();
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

}