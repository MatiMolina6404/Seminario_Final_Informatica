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
import service.ServicioInscripcionActividad;
import util.Ventana;

import java.io.IOException;
import java.util.List;

public class BusquedaInscripcionesController {

    private ServicioInscripcionActividad servicioInscripcion;
    private Actividad actividadActual;

    @FXML
    private Label lblActividad;
    @FXML
    private ComboBox<String> cmbTipo;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<InscripcionActividad> tablaInscriptos;
    @FXML
    private TableColumn<InscripcionActividad, Integer> colId;
    @FXML
    private TableColumn<InscripcionActividad, String> colNombre;
    @FXML
    private TableColumn<InscripcionActividad, String> colApellido;
    @FXML
    private TableColumn<InscripcionActividad, String> colDni;
    @FXML
    private TableColumn<InscripcionActividad, String> colTipo;
    @FXML
    private TableColumn<InscripcionActividad, String> colEstado;

    /* Inicializa el controlador, configura el filtro por tipo de persona
       y vincula las columnas de la tabla con los atributos de InscripcionActividad.
    */
    @FXML
    public void initialize() {
        servicioInscripcion = new ServicioInscripcionActividad();
        cmbTipo.getItems().addAll("Todos", "Socios", "Participantes");
        cmbTipo.setValue("Todos");
        colId.setCellValueFactory(new PropertyValueFactory<>("idInscripcion"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoInscripcion"));
        tablaInscriptos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /* Recibe la actividad seleccionada desde otra pantalla y
       carga sus inscripciones asociadas en la tabla.
    */
    public void cargarActividad(Actividad actividad) {
        actividadActual = actividad;
        lblActividad.setText("Actividad: " + actividad.getNombreActividad());
        tablaInscriptos.setItems(FXCollections.observableArrayList(
                        servicioInscripcion.consultarPorActividad(actividad.getIdActividad())));
    }

    /* Filtra las inscripciones de la actividad actual según el texto ingresado y
       el tipo seleccionado: socios, participantes o todos.
    */
    @FXML
    private void buscarInscripciones() {
        List<InscripcionActividad> lista = servicioInscripcion.consultarPorActividad(
                        actividadActual.getIdActividad());
        String texto = txtBuscar.getText().toLowerCase();
        String tipo = cmbTipo.getValue();
        lista.removeIf(i -> {
            boolean coincideTipo = true;
            if ("Socios".equals(tipo)) {
                coincideTipo = i.getSocio() != null;
            }
            if ("Participantes".equals(tipo)) {
                coincideTipo = i.getParticipante() != null;
            }
            if (!coincideTipo) {
                return true;
            }
            String nombre;
            String dni;
            if (i.getSocio() != null) {
                nombre = i.getSocio().getNombreSocio() + " " + i.getSocio().getApellidoSocio();
                dni = i.getSocio().getDniSocio();
            } else {
                nombre = i.getParticipante().getNombreParticipante() + " "
                        + i.getParticipante().getApellidoParticipante();
                dni = i.getParticipante().getDniParticipante();
            }
            return !nombre.toLowerCase().contains(texto) && !dni.contains(texto);
        });
        tablaInscriptos.setItems(FXCollections.observableArrayList(lista));
    }

    // Abre la ventana para modificar la inscripción seleccionada.
    @FXML
    private void abrirModificar() {
        InscripcionActividad inscripcionSeleccionada =
                tablaInscriptos.getSelectionModel().getSelectedItem();
        if (inscripcionSeleccionada == null) {
            mostrarError("Seleccione una inscripción.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/Actividades/ModificarInscripcion.fxml"
            ));
            Parent root = loader.load();
            ModificarInscripcionController controller = loader.getController();
            controller.cargarInscripcion(inscripcionSeleccionada);
            Stage stage = Ventana.crearVentana(root, "Modificar Inscripción");
            stage.showAndWait();
            buscarInscripciones();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la modificación de inscripción.");
        }
    }

    // Muestra un mensaje de error al usuario.
    private void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

}