package controller.Actividades;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.InscripcionActividad;
import model.Participante;
import service.ServicioParticipante;
import util.Permisos;
import util.Ventana;

public class ModificarInscripcionController {

    private ServicioParticipante servicioParticipante;
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

    // Inicializa el servicio de participantes.
    @FXML
    public void initialize() {
        servicioParticipante = new ServicioParticipante();
    }

    // Carga los datos del participante de la inscripción seleccionada.
    public void cargarInscripcion(InscripcionActividad inscripcion) {
        inscripcionActual = inscripcion;
        txtIdInscripcion.setText(String.valueOf(inscripcion.getIdInscripcion()));
        txtIdInscripcion.setDisable(true);
        Participante participante = inscripcion.getParticipante();
        if (participante == null) {
            return;
        }
        txtNombre.setText(participante.getNombreParticipante());
        txtApellido.setText(participante.getApellidoParticipante());
        txtDni.setText(participante.getDniParticipante());
        txtTelefono.setText(participante.getTelefonoParticipante());
        txtDireccion.setText(participante.getDireccionParticipante());
    }

    // Guarda los cambios de los datos del participante.
    @FXML
    private void guardarCambios() {
        if (!Permisos.puedeGestionarInscripciones()) {
            mostrarError("No tiene permisos para modificar inscripciones.");
            return;
        }
        if (inscripcionActual == null || inscripcionActual.getParticipante() == null) {
            mostrarError("No se encontró el participante asociado a la inscripción.");
            return;
        }
        Participante datos = new Participante();
        datos.setIdParticipante(inscripcionActual.getParticipante().getIdParticipante());
        datos.setNombreParticipante(txtNombre.getText());
        datos.setApellidoParticipante(txtApellido.getText());
        datos.setDniParticipante(txtDni.getText());
        datos.setTelefonoParticipante(txtTelefono.getText());
        datos.setDireccionParticipante(txtDireccion.getText());
        if (servicioParticipante.modificarParticipante(datos)) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Información");
            alerta.setHeaderText(null);
            alerta.setContentText("Datos del participante modificados correctamente.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
            Stage stage = (Stage) txtIdInscripcion.getScene().getWindow();
            stage.close();
        } else {
            mostrarError("No fue posible modificar los datos del participante. "
                    + "Verifique que los datos estén completos y que el DNI no esté "
                    + "registrado para otro participante o socio.");
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