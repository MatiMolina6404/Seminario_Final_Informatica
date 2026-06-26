package controller.Actividades;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Actividad;
import model.InscripcionActividad;
import model.Participante;
import model.Socio;
import service.ServicioActividad;
import service.ServicioInscripcionActividad;
import service.ServicioParticipante;
import service.ServicioSocio;
import util.Ventana;

import java.io.IOException;
import java.util.Optional;

public class InscripcionController {

    private ServicioInscripcionActividad servicioInscripcion;
    private ServicioActividad servicioActividad;
    private ServicioParticipante servicioParticipante;
    private ServicioSocio servicioSocio;

    @FXML
    private CheckBox chkRegistrado;
    @FXML
    private TextField txtDniRegistrado;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtDNI;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<Actividad> cmbActividad;
    @FXML
    private TableView<Actividad> tablaActividades;
    @FXML
    private TableColumn<Actividad, Integer> colID;
    @FXML
    private TableColumn<Actividad, String> colNombre;
    @FXML
    private TableColumn<Actividad, Integer> colCantParticipantes;

    /* Inicializa los servicios, configura la tabla de actividades y
       prepara el comportamiento de los campos de inscripción.
    */
    @FXML
    public void initialize() {
        servicioInscripcion = new ServicioInscripcionActividad();
        servicioActividad = new ServicioActividad();
        servicioParticipante = new ServicioParticipante();
        servicioSocio = new ServicioSocio();
        colID.setCellValueFactory(new PropertyValueFactory<>("idActividad"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreActividad"));
        colCantParticipantes.setCellValueFactory(new PropertyValueFactory<>(
                "cantidadParticipantes"));
        tablaActividades.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        cargarActividades();
        txtDniRegistrado.setDisable(true);

        // Permite buscar una persona registrada al presionar Enter en el campo DNI.
        txtDniRegistrado.setOnAction(e -> buscarRegistrado());

        // Al salir del campo DNI, busca automáticamente si el check de registrado está activo.
        txtDniRegistrado.focusedProperty().addListener((obs,
                                                        oldValue, newValue) -> {
            if (!newValue && chkRegistrado.isSelected()) {
                buscarRegistrado();
            }
        });

        // Habilita o deshabilita los campos según si la persona ya está registrada.
        chkRegistrado.selectedProperty().addListener((obs,
                                                      oldV, seleccionado) -> {
            txtDniRegistrado.setDisable(!seleccionado);
            txtNombre.clear();
            txtApellido.clear();
            txtDNI.clear();
            txtTelefono.clear();
            txtDireccion.clear();
            txtDniRegistrado.clear();
            txtNombre.setDisable(seleccionado);
            txtApellido.setDisable(seleccionado);
            txtDNI.setDisable(seleccionado);
            txtTelefono.setDisable(seleccionado);
            txtDireccion.setDisable(seleccionado);
        });
    }

    // Busca una persona registrada por su DNI.
    private void buscarRegistrado() {
        String dni = txtDniRegistrado.getText().trim();
        if (dni.isEmpty()) {
            return;
        }
        Optional<Socio> socio = servicioSocio.buscarPorDni(dni);
        if (socio.isPresent()) {
            txtNombre.setText(socio.get().getNombreSocio());
            txtApellido.setText(socio.get().getApellidoSocio());
            txtDNI.setText(socio.get().getDniSocio());
            txtTelefono.setText(socio.get().getTelefonoSocio());
            txtDireccion.setText(socio.get().getDireccionSocio());
            return;
        }
        Optional<Participante> participante = servicioParticipante.buscarPorDni(dni);
        if (participante.isPresent()) {
            txtNombre.setText(participante.get().getNombreParticipante());
            txtApellido.setText(participante.get().getApellidoParticipante());
            txtDNI.setText(participante.get().getDniParticipante());
            txtTelefono.setText(participante.get().getTelefonoParticipante());
            txtDireccion.setText(participante.get().getDireccionParticipante());
        } else {
            mostrarError("No existe un deportista registrado con ese DNI.");
            txtDniRegistrado.clear();
        }
    }

    // Carga las actividades disponibles en el combo y en la tabla.
    private void cargarActividades() {
        cmbActividad.setItems(FXCollections.observableArrayList(
                servicioActividad.listarActividadesPantalla()));
        tablaActividades.getItems().setAll(servicioActividad.listarActividadesPantalla());
    }

    /* Registra una inscripción a una actividad deportiva.
       Puede asociar la inscripción a un socio o a un participante existente,
       o registrar un nuevo participante antes de inscribirlo.
    */
    @FXML
    private void registrarInscripcion() {
        Actividad actividad = cmbActividad.getValue();
        if (actividad == null) {
            mostrarError("Seleccione una actividad.");
            return;
        }
        InscripcionActividad inscripcion = new InscripcionActividad();
        inscripcion.setActividad(actividad);
        inscripcion.setEstadoInscripcion("Activa");
        if (chkRegistrado.isSelected()) {
            String dni = txtDniRegistrado.getText();
            Optional<Socio> socio = servicioSocio.buscarPorDni(dni);
            if (socio.isPresent()) {
                inscripcion.setSocio(socio.get());
            } else {
                Optional<Participante> participante = servicioParticipante.buscarPorDni(dni);
                if (participante.isPresent()) {
                    inscripcion.setParticipante(participante.get());
                } else {
                    mostrarError("No existe un deportista registrado con ese DNI.");
                    return;
                }
            }
        } else {
            Participante participante = new Participante();
            participante.setNombreParticipante(txtNombre.getText());
            participante.setApellidoParticipante(txtApellido.getText());
            participante.setDniParticipante(txtDNI.getText());
            participante.setTelefonoParticipante(txtTelefono.getText());
            participante.setDireccionParticipante(txtDireccion.getText());
            boolean registrado = servicioParticipante.registrarParticipante(participante);
            if (!registrado) {
                mostrarError("No se pudo registrar el participante.");
                return;
            }
            Optional<Participante> nuevo = servicioParticipante.buscarPorDni(
                    participante.getDniParticipante());
            nuevo.ifPresent(inscripcion::setParticipante
            );
        }
        boolean ok = servicioInscripcion.registrarInscripcion(inscripcion);
        if (ok) {
            mostrarInformacion("Inscripción registrada correctamente.");
            limpiarCampos();
            cargarActividades();
        } else {
            mostrarError("No se pudo registrar la inscripción.");
        }
    }

    // Abre la ventana que muestra las inscripciones de la actividad seleccionada.
    @FXML
    private void abrirBusquedaInscripciones() {
        Actividad actividadSeleccionada = tablaActividades.getSelectionModel().getSelectedItem();
        if (actividadSeleccionada == null) {
            mostrarError("Seleccione una actividad de la tabla.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/Actividades/BuscarInscripciones.fxml"
            ));
            Parent root = loader.load();
            BusquedaInscripcionesController controller = loader.getController();
            controller.cargarActividad(actividadSeleccionada);
            Stage stage = Ventana.crearVentana(root,
                    "Inscriptos - " + actividadSeleccionada.getNombreActividad()
            );
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la búsqueda de inscriptos.");
        }
    }

    /* Abre la ventana de gestión de actividades.
       Al cerrar, actualiza la tabla de actividades disponibles.
    */
    @FXML
    private void abrirGestionActividades() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/Actividades/GestionActividades.fxml"
            ));
            Parent root = loader.load();
            Stage stage = Ventana.crearVentana(root, "Gestión Actividades");
            stage.setOnHidden(e -> cargarActividades());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Limpia los campos del formulario y quita la selección de actividad.
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtDNI.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtDniRegistrado.clear();
        cmbActividad.getSelectionModel().clearSelection();
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