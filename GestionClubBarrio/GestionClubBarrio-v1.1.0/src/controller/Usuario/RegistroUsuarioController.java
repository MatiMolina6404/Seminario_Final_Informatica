package controller.Usuario;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Administrador;
import service.ServicioAdministrador;
import util.SesionUsuario;
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
    @FXML
    private TableView<Administrador> tablaUsuarios;
    @FXML
    private TableColumn<Administrador, Integer> colIdUsuario;
    @FXML
    private TableColumn<Administrador, String> colUsuario;
    @FXML
    private TableColumn<Administrador, String> colRol;

    // Inicializa el servicio de administradores, configura las columnas y carga los usuarios.
    @FXML
    public void initialize() {
        servicioAdministrador = new ServicioAdministrador();
        cmbRol.getItems().addAll("Administrador", "Tesorero", "Secretario", "Consulta");
        cmbRol.setValue("Consulta");
        colIdUsuario.setCellValueFactory(new PropertyValueFactory<>("idAdministrador"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        cargarUsuarios();
        tablaUsuarios.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs,
                              anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        txtNombre.setText(seleccionado.getNombre());
                        txtApellido.setText(seleccionado.getApellido());
                        cmbRol.setValue(seleccionado.getRol());
                        txtClave.clear();
                        configurarEdicionRol(seleccionado);
                    }
                });
    }

    // Carga en la tabla todos los usuarios registrados.
    private void cargarUsuarios() {
        tablaUsuarios.getItems().setAll(servicioAdministrador.listarAdministradores());
    }

    // Registra un nuevo usuario administrador a partir de los datos ingresados.
    @FXML
    private void registrarUsuario() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String clave = txtClave.getText();
        String rol = cmbRol.getValue();
        boolean registrado = servicioAdministrador.registrarAdministrador(
                nombre, apellido, clave, rol);
        if (registrado) {
            mostrarInformacion("Usuario registrado correctamente.");
            limpiarCampos();
            cargarUsuarios();
        } else {
            mostrarError("No se pudo registrar el usuario. " +
                    "Revise los datos ingresados y los requerimientos de la contraseña.");
        }
    }

    // Modifica los datos del usuario seleccionado en la tabla.
    @FXML
    private void modificarUsuario() {
        Administrador seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un usuario de la lista.");
            return;
        }
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String nuevaClave = txtClave.getText();
        String rol = cmbRol.getValue();
        seleccionado.setNombre(nombre);
        seleccionado.setApellido(apellido);
        seleccionado.setRol(rol);
        boolean modificado = servicioAdministrador.modificarAdministrador(seleccionado, nuevaClave);
        if (modificado) {
            mostrarInformacion("Usuario modificado correctamente.");
            limpiarCampos();
            cargarUsuarios();
        } else {
            mostrarError("No se pudo modificar el usuario.");
        }
    }

    // Elimina el usuario seleccionado de la lista.
    @FXML
    private void eliminarUsuario() {
        Administrador seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Seleccione un usuario de la lista.");
            return;
        }
        Administrador usuarioActual = SesionUsuario.getUsuarioActual();
        if (usuarioActual != null &&
                seleccionado.getIdAdministrador() == usuarioActual.getIdAdministrador()) {
            mostrarError("No puede eliminar el usuario con el que inició sesión.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar el usuario seleccionado?",
                ButtonType.YES,
                ButtonType.NO
        );
        confirmacion.setTitle("Eliminar usuario");
        confirmacion.setHeaderText(null);
        Ventana.aplicarEstiloAlerta(confirmacion);
        confirmacion.showAndWait().ifPresent(resultado -> {
            if (resultado == ButtonType.YES) {
                boolean eliminado = servicioAdministrador.eliminarAdministrador(
                        seleccionado.getIdAdministrador());
                if (eliminado) {
                    mostrarInformacion("Usuario eliminado correctamente.");
                    limpiarCampos();
                    cargarUsuarios();
                } else {
                    mostrarError("No se pudo eliminar el usuario.");
                }
            }
        });
    }

    // Controla el acceso al cambio de rol del usuario seleccionado.
    private void configurarEdicionRol(Administrador usuario) {
        Administrador usuarioActual = SesionUsuario.getUsuarioActual();
        if (usuarioActual != null &&
                usuario.getIdAdministrador() == usuarioActual.getIdAdministrador()) {
            cmbRol.setDisable(true);
            cmbRol.setValue(usuarioActual.getRol());
        } else {
            cmbRol.setDisable(false);
        }
    }

    // Limpia los campos del formulario y quita la selección de la tabla.
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtClave.clear();
        cmbRol.setValue("Consulta");
        cmbRol.setDisable(false);
        tablaUsuarios.getSelectionModel().clearSelection();
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