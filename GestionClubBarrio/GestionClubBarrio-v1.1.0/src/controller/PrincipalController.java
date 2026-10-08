package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import service.ServicioConfiguracionEstados;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import util.Permisos;
import util.SesionUsuario;
import model.ConfiguracionSistema;
import util.Ventana;

import java.io.IOException;

public class PrincipalController {

    private Image escudo;
    private ConfiguracionSistema configuracion;
    @FXML
    private AnchorPane contenedor;
    @FXML
    private AnchorPane pantallaInicio;
    @FXML
    private ImageView imgEscudoMenu;
    @FXML
    private ImageView imgEscudoInicio;
    @FXML
    private Button btnGestionSocios;
    @FXML
    private Button btnPagos;
    @FXML
    private Button btnActividades;
    @FXML
    private Button btnEstados;
    @FXML
    private Button btnReportes;
    @FXML
    private Button btnUsuarios;
    @FXML
    private Button btnConfiguracion;
    @FXML
    private javafx.scene.layout.VBox menuLateral;
    @FXML
    private javafx.scene.control.Label lblNombreClub;
    @FXML
    private javafx.scene.control.Label lblNombreSistema;

    /* Inicializa la pantalla principal, actualiza los estados,
       carga la personalización y configura los permisos del usuario.
    */
    @FXML
    public void initialize() {
        actualizarEstadosAutomaticamente();
        cargarPersonalizacion();
        cargarEscudo();
        configurarPermisos();
    }

    // Muestra u oculta los botones del menú según los permisos del rol.
    private void configurarPermisos() {
        if (!SesionUsuario.haySesionActiva()) {
            return;
        }
        btnGestionSocios.setVisible(Permisos.puedeConsultarSocios());
        btnGestionSocios.setManaged(Permisos.puedeConsultarSocios());
        btnPagos.setVisible(Permisos.puedeConsultarPagos());
        btnPagos.setManaged(Permisos.puedeConsultarPagos());
        btnActividades.setVisible(Permisos.puedeConsultarActividades());
        btnActividades.setManaged(Permisos.puedeConsultarActividades());
        btnEstados.setVisible(Permisos.puedeConsultarEstados());
        btnEstados.setManaged(Permisos.puedeConsultarEstados());
        btnReportes.setVisible(Permisos.puedeConsultarReportes());
        btnReportes.setManaged(Permisos.puedeConsultarReportes());
        btnUsuarios.setVisible(Permisos.puedeGestionarUsuarios());
        btnUsuarios.setManaged(Permisos.puedeGestionarUsuarios());
        btnConfiguracion.setVisible(Permisos.puedeGestionarUsuarios());
        btnConfiguracion.setManaged(Permisos.puedeGestionarUsuarios());
    }

    // Muestra un mensaje cuando el rol no tiene permiso para una función.
    private void mostrarAccesoDenegado() {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Acceso restringido");
        alerta.setHeaderText(null);
        alerta.setContentText("Su rol no tiene permisos para utilizar esta función.");
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

    /* Ejecuta la actualización automática de estados de socios e inscripciones
       al ingresar a la pantalla principal del sistema.
    */
    private void actualizarEstadosAutomaticamente() {
        ServicioConfiguracionEstados servicio = new ServicioConfiguracionEstados();
        servicio.actualizarEstadosAutomaticamente();
    }

    // Carga la imagen del escudo y la asigna a elementos visuales.
    private void cargarEscudo() {
        escudo = Ventana.cargarEscudo();
        if (escudo == null) {
            return;
        }
        if (imgEscudoInicio != null) {
            imgEscudoInicio.setImage(escudo);
        }
        if (imgEscudoMenu != null) {
            imgEscudoMenu.setImage(escudo);
        }
    }

    // Abre el módulo de gestión de socios.
    @FXML
    private void abrirGestionSocios() {
        if (!Permisos.puedeConsultarSocios()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/Socios/GestionSocios.fxml");
    }

    // Abre el módulo de gestión de pagos.
    @FXML
    private void abrirPagos() {
        if (!Permisos.puedeConsultarPagos()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/Pagos/GestionPagos.fxml");
    }

    // Abre el módulo de actividades e inscripciones.
    @FXML
    private void abrirActividades() {
        if (!Permisos.puedeConsultarActividades()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/Actividades/RegistroInscripciones.fxml");
    }

    // Abre la pantalla de consulta de estados administrativos.
    @FXML
    private void abrirConsultaEstados() {
        if (!Permisos.puedeConsultarEstados()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/ConsultaEstados.fxml");
    }

    // Abre el módulo de generación de reportes.
    @FXML
    private void abrirReportes() {
        if (!Permisos.puedeConsultarReportes()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/Reportes.fxml");
    }

    // Abre el módulo de gestión de usuarios.
    @FXML
    private void abrirUsuarios() {
        if (!Permisos.puedeGestionarUsuarios()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/Usuario/RegistroUsuario.fxml");
    }

    // Abre la pantalla de configuración del sistema.
    @FXML
    private void abrirConfiguracion() {
        if (!Permisos.puedeGestionarUsuarios()) {
            mostrarAccesoDenegado();
            return;
        }
        cargarVista("/view/ConfiguracionSistema.fxml");
    }

    /* Carga una vista FXML dentro del contenedor principal.
       Permite navegar entre módulos sin abrir una nueva ventana.
    */
    private void cargarVista(String rutaFXML) {
        try {
            var url = getClass().getResource(rutaFXML);
            if (url == null) {
                System.err.println("No se encontró el archivo FXML: " + rutaFXML);
                return;
            }
            Parent vista = FXMLLoader.load(url);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            contenedor.getChildren().setAll(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Carga la personalización almacenada en la base de datos.
    private void cargarPersonalizacion() {
        configuracion = Ventana.cargarConfiguracion();
        if (configuracion == null) {
            System.err.println("No se pudo cargar la configuración del sistema.");
            return;
        }
        if (lblNombreClub != null) {
            lblNombreClub.setText(configuracion.getNombreClub());
        }
        if (lblNombreSistema != null) {
            lblNombreSistema.setText(configuracion.getNombreSistema());
        }
    }

    // Vuelve a mostrar la pantalla inicial dentro del contenedor principal.
    @FXML
    private void volverInicio() {
        if (pantallaInicio == null) {
            System.err.println("No se encontró pantallaInicio en PantallaPrincipal.fxml");
            return;
        }
        contenedor.getChildren().setAll(pantallaInicio);
        AnchorPane.setTopAnchor(pantallaInicio, 0.0);
        AnchorPane.setBottomAnchor(pantallaInicio, 0.0);
        AnchorPane.setLeftAnchor(pantallaInicio, 0.0);
        AnchorPane.setRightAnchor(pantallaInicio, 0.0);
    }

}