package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.ServicioAdministrador;
import util.Ventana;

public class RegistroUsuarioController {

    private ServicioAdministrador servicioAdministrador;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtApellido;
    @FXML
    private PasswordField txtClave;
    @FXML
    private ComboBox<String> cmbRol;

    // Inicializa el servicio de administradores y carga los roles disponibles.
    @FXML
    public void initialize() {
        servicioAdministrador = new ServicioAdministrador();
        cmbRol.getItems().addAll("Administrador", "Tesorero", "Secretario", "Consulta");
        cmbRol.setValue("Consulta");
    }

    // Registra un nuevo usuario administrador a partir de los datos ingresados.
    @FXML
    private void registrarUsuario() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String clave = txtClave.getText();
        String rol = cmbRol.getValue();

        boolean registrado = servicioAdministrador.registrarAdministrador(nombre, apellido, clave, rol);
        if (registrado) {
            mostrarInformacion("Usuario registrado correctamente.\nUsuario generado: "
                    + nombre + apellido);
            limpiarCampos();
        } else {
            mostrarError("No se pudo registrar el usuario. Revise los requerimientos de la contraseña.");
        }
    }

    // Limpia los campos del formulario luego de registrar un usuario.
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtClave.clear();
        cmbRol.setValue("Consulta");
    }

    // Muestra un mensaje informativo al usuario.
    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
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