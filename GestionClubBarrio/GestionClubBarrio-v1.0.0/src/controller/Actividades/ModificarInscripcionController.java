package controller.Actividades;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.InscripcionActividad;
import model.Participante;
import service.ServicioInscripcionActividad;
import util.Ventana;

public class ModificarInscripcionController {

    private ServicioInscripcionActividad servicioInscripcion;
    private InscripcionActividad inscripcionActual;

    @FXML
    private TextField txtIdInscripcion;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private TextField txtDni;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtDireccion;
    @FXML
    private ComboBox<String> cmbEstado;

    // Inicializa el servicio de inscripciones y carga los estados disponibles.
    @FXML
    public void initialize() {
        servicioInscripcion = new ServicioInscripcionActividad();
        cmbEstado.getItems().addAll("Activa", "Inactiva");
    }

    /* Carga en pantalla los datos de la inscripción seleccionada.
       Si corresponde a un socio, sus datos personales se muestran solo para consulta.
    */
    public void cargarInscripcion(InscripcionActividad inscripcion) {
        inscripcionActual = inscripcion;
        txtIdInscripcion.setText(String.valueOf(inscripcion.getIdInscripcion()));
        txtIdInscripcion.setDisable(true);
        cmbEstado.setValue(inscripcion.getEstadoInscripcion());
        if (inscripcion.getSocio() != null) {
            txtNombre.setText(inscripcion.getSocio().getNombreSocio());
            txtApellido.setText(inscripcion.getSocio().getApellidoSocio());
            txtDni.setText(inscripcion.getSocio().getDniSocio());
            txtTelefono.setText(inscripcion.getSocio().getTelefonoSocio());
            txtDireccion.setText(inscripcion.getSocio().getDireccionSocio());
            txtNombre.setDisable(true);
            txtApellido.setDisable(true);
            txtDni.setDisable(true);
            txtTelefono.setDisable(true);
            txtDireccion.setDisable(true);
        } else {
            Participante p = inscripcion.getParticipante();
            txtNombre.setText(p.getNombreParticipante());
            txtApellido.setText(p.getApellidoParticipante());
            txtDni.setText(p.getDniParticipante());
            txtTelefono.setText(p.getTelefonoParticipante());
            txtDireccion.setText(p.getDireccionParticipante());
        }
    }

    // Guarda el nuevo estado de la inscripción y cierra la ventana si la modificación fue exitosa.
    @FXML
    private void guardarCambios() {
        inscripcionActual.setEstadoInscripcion(cmbEstado.getValue());
        boolean modificada = servicioInscripcion.modificarInscripcion(inscripcionActual);
        if (modificada) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Información");
            alerta.setHeaderText(null);
            alerta.setContentText("Inscripción modificada correctamente.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
            Stage stage = (Stage) cmbEstado.getScene().getWindow();
            stage.close();
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No fue posible modificar la inscripción.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
        }
    }

}