package controller;

import dao.ConfiguracionSistemaDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ConfiguracionSistema;
import model.Auxiliares.ConfiguracionEstados;
import service.ServicioConfiguracionEstados;
import util.Permisos;
import util.Ventana;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ConfiguracionSistemaController {

    private final ServicioConfiguracionEstados servicioEstados = new ServicioConfiguracionEstados();
    private final ConfiguracionSistemaDAO configuracionDAO = new ConfiguracionSistemaDAO();
    private ConfiguracionSistema configuracion;
    private File archivoEscudoNuevo;
    @FXML
    private TextField txtNombreClub;
    @FXML
    private TextField txtNombreSistema;
    @FXML
    private ImageView imgEscudo;
    // Color terciario: fondo de todas las pantallas.
    @FXML
    private ColorPicker colorFondo;
    // Color secundario: menú, cuerpo de tablas y campos de formularios.
    @FXML
    private ColorPicker colorMenu;
    // Color principal: botones y encabezados de tablas.
    @FXML
    private ColorPicker colorPrincipal;
    @FXML
    private TextField txtDiasDeudor;
    @FXML
    private TextField txtDiasInactivo;
    @FXML
    private TextField txtDiasInscripcion;

    // Inicializa la configuración del sistema y carga los datos almacenados.
    @FXML
    public void initialize() {
        if (!Permisos.puedeGestionarUsuarios()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarConfiguracion();
    }

    // Carga la configuración almacenada en la base de datos.
    private void cargarConfiguracion() {
        configuracion = configuracionDAO.obtenerConfiguracion();
        if (configuracion == null) {
            mostrarError("No se pudo cargar la configuración del sistema.");
            return;
        }
        txtNombreClub.setText(configuracion.getNombreClub());
        txtNombreSistema.setText(configuracion.getNombreSistema());
        colorFondo.setValue(javafx.scene.paint.Color.web(configuracion.getColorFondo()));
        colorMenu.setValue(javafx.scene.paint.Color.web(configuracion.getColorMenu()));
        colorPrincipal.setValue(javafx.scene.paint.Color.web(configuracion.getColorPrincipal()));
        ConfiguracionEstados estados = servicioEstados.obtenerConfiguracion();
        if (estados != null) {
            txtDiasDeudor.setText(String.valueOf(estados.getDiasSocioDeudor()));
            txtDiasInactivo.setText(String.valueOf(estados.getDiasSocioInactivo()));
            txtDiasInscripcion.setText(String.valueOf(estados.getDiasInscripcionInactiva()));
        }
        Image escudo = Ventana.cargarEscudo();
        if (escudo != null) {
            imgEscudo.setImage(escudo);
        }
    }

    // Permite seleccionar una nueva imagen para el escudo (se guarda al confirmar los cambios).
    @FXML
    private void seleccionarEscudo() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar escudo del club");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File archivo = selector.showOpenDialog(imgEscudo.getScene().getWindow());
        if (archivo == null) {
            return;
        }
        Image vistaPrevia = new Image(archivo.toURI().toString());
        if (vistaPrevia.isError()) {
            mostrarError("No se pudo leer la imagen seleccionada.");
            return;
        }
        archivoEscudoNuevo = archivo;
        imgEscudo.setImage(vistaPrevia);
    }

    // Copia el escudo elegido a la carpeta "escudo" y actualiza la ruta en la configuración.
    private boolean guardarEscudoNuevo() {
        if (archivoEscudoNuevo == null) {
            return true;
        }
        try {
            String nombre = archivoEscudoNuevo.getName().toLowerCase();
            int punto = nombre.lastIndexOf('.');
            String extension = punto >= 0 ? nombre.substring(punto) : ".png";
            Path carpeta = Path.of("escudo");
            Files.createDirectories(carpeta);
            Files.copy(archivoEscudoNuevo.toPath(),
                    carpeta.resolve("escudo" + extension),
                    StandardCopyOption.REPLACE_EXISTING);
            configuracion.setRutaEscudo("escudo/escudo" + extension);
            archivoEscudoNuevo = null;
            return true;
        } catch (IOException e) {
            mostrarError("No se pudo copiar la imagen seleccionada.");
            return false;
        }
    }

    // Guarda la configuración modificada por el administrador y recarga la pantalla principal.
    @FXML
    private void guardarConfiguracion() {
        if (!Permisos.puedeGestionarUsuarios()) {
            mostrarAccesoDenegado();
            return;
        }
        if (txtNombreClub.getText().isBlank() || txtNombreSistema.getText().isBlank()) {
            mostrarError("Complete el nombre del club y del sistema.");
            return;
        }
        String resultado = servicioEstados.modificarConfiguracion(
                txtDiasDeudor.getText(), txtDiasInactivo.getText(), txtDiasInscripcion.getText());
        if (!"OK".equals(resultado)) {
            mostrarError(resultado);
            return;
        }
        if (!guardarEscudoNuevo()) {
            return;
        }
        configuracion.setNombreClub(txtNombreClub.getText().trim());
        configuracion.setNombreSistema(txtNombreSistema.getText().trim());
        configuracion.setColorFondo("#" + colorFondo.getValue().toString().substring(2));
        configuracion.setColorMenu("#" + colorMenu.getValue().toString().substring(2));
        configuracion.setColorPrincipal("#" + colorPrincipal.getValue().toString().substring(2));
        if (configuracionDAO.modificarConfiguracion(configuracion)) {
            Ventana.invalidarConfiguracionCache();
            mostrarInformacion("La configuración se guardó correctamente.");
            cargarVistaPrincipal();
        } else {
            mostrarError("No se pudo guardar la configuración.");
        }
    }

    // Vuelve a la pantalla principal.
    @FXML
    private void volver() {
        cargarVistaPrincipal();
    }

    /*
    Vuelve a la pantalla principal reemplazando la escena de la ventana.
    Así no se duplica el menú lateral y se vuelven a leer colores y escudo guardados.
    */
    private void cargarVistaPrincipal() {
        try {
            var url = getClass().getResource("/view/PantallaPrincipal.fxml");
            if (url == null) {
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) imgEscudo.getScene().getWindow();
            stage.setScene(Ventana.crearScene(root));
            Ventana.configurarStage(stage, Ventana.obtenerNombreSistema());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Muestra un mensaje de acceso denegado.
    private void mostrarAccesoDenegado() {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Acceso restringido");
        alerta.setHeaderText(null);
        alerta.setContentText("Su rol no tiene permisos para utilizar esta función.");
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

    // Muestra un mensaje informativo al usuario.
    private void mostrarInformacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Configuración");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

}