package controller.Socios;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Socio;
import service.ServicioSocio;
import util.Permisos;
import util.Ventana;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;


import java.io.IOException;

public class SocioController {

    private ServicioSocio servicioSocio;
    @FXML
    private TableView<Socio> tablaSocios;
    @FXML
    private TableColumn<Socio, Integer> colId;
    @FXML
    private TableColumn<Socio, String> colNombre;
    @FXML
    private TableColumn<Socio, String> colApellido;
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
    private DatePicker dpFechaAlta;

    /* Inicializa el servicio de socios, configura las columnas de la tabla y
       carga los socios registrados.
    */
    @FXML
    public void initialize() {
        servicioSocio = new ServicioSocio();
        colId.setCellValueFactory(new PropertyValueFactory<>("idSocio"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreSocio"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellidoSocio"));
        tablaSocios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        Ventana.configurarSelectorFechaRegistro(dpFechaAlta);
        cargarTabla();
    }

    // Carga en la tabla todos los socios registrados en el sistema.
    private void cargarTabla() {
        tablaSocios.getItems().setAll(servicioSocio.listarSociosPantalla());
        tablaSocios.refresh();
    }

    // Registra un nuevo socio a partir de los datos ingresados en el formulario.
    @FXML
    private void registrarSocio() {
        if (!Permisos.puedeGestionarSocios()) {
            mostrarError("Acceso denegado. No tiene permisos para registrar socios.");
            return;
        }
        LocalDate fechaAlta = dpFechaAlta.getValue();
        if (fechaAlta == null) {
            mostrarError("Seleccione la fecha de alta.");
            return;
        }
        Socio socio = new Socio();
        socio.setNombreSocio(txtNombre.getText());
        socio.setApellidoSocio(txtApellido.getText());
        socio.setDniSocio(txtDni.getText());
        socio.setTelefonoSocio(txtTelefono.getText());
        socio.setDireccionSocio(txtDireccion.getText());
        socio.setFechaAltaSocio(fechaAlta.toString());
        boolean registrado = servicioSocio.registrarSocio(socio);
        if (registrado) {
            mostrarInformacion("Socio registrado correctamente.");
            limpiarCampos();
            cargarTabla();
        } else {
            mostrarError("No se pudo registrar el socio. Verifique los datos ingresados.");
        }
    }

    // Abre la ventana de búsqueda de socios. Al cerrarla, actualiza la tabla principal.
    @FXML
    private void abrirBusqueda() {
        try {
            var url = getClass().getResource("/view/Socios/BusquedaSocios.fxml");
            if (url == null) {
                mostrarError(
                        "No se encontró el archivo FXML: /view/Socios/BusquedaSocios.fxml");
                return;
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Stage stage = Ventana.crearVentana(root, "Buscar Socio");
            stage.setOnHidden(e -> cargarTabla());
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo abrir la búsqueda de socios.");
        }
    }

    // Limpia los campos del formulario de registro.
    private void limpiarCampos() {
        txtNombre.clear();
        txtApellido.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        dpFechaAlta.setValue(LocalDate.now());
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