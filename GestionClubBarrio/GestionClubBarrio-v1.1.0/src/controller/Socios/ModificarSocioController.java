package controller.Socios;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Socio;
import service.ServicioSocio;
import util.Permisos;
import util.Ventana;

public class ModificarSocioController {

    private Socio socio;
    private final ServicioSocio servicioSocio = new ServicioSocio();
    @FXML
    private TextField txtIdSocio;
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

    // Inicializa la pantalla de modificación y carga los estados disponibles.
    @FXML
    public void initialize() {
        txtIdSocio.setEditable(false);
    }

    /* Recibe el socio seleccionado desde la pantalla de búsqueda y
       carga sus datos en los campos del formulario.
    */
    public void cargarSocio(Socio socio) {
        this.socio = socio;
        txtIdSocio.setText(String.valueOf(socio.getIdSocio()));
        txtNombre.setText(socio.getNombreSocio());
        txtApellido.setText(socio.getApellidoSocio());
        txtDni.setText(socio.getDniSocio());
        txtTelefono.setText(socio.getTelefonoSocio());
        txtDireccion.setText(socio.getDireccionSocio());
    }

    // Guarda los cambios realizados sobre el socio seleccionado.
    @FXML
    private void guardarCambios() {
        if (!Permisos.puedeGestionarSocios()) {
            mostrarError("Acceso denegado. No tiene permisos para modificar socios.");
            return;
        }
        socio.setNombreSocio(txtNombre.getText());
        socio.setApellidoSocio(txtApellido.getText());
        socio.setDniSocio(txtDni.getText());
        socio.setTelefonoSocio(txtTelefono.getText());
        socio.setDireccionSocio(txtDireccion.getText());
        boolean modificado = servicioSocio.modificarSocio(socio);
        if (modificado) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Información");
            alerta.setHeaderText(null);
            alerta.setContentText("Socio modificado correctamente.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.close();
        } else {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("No fue posible modificar el socio. Revisar los datos ingresados.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
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