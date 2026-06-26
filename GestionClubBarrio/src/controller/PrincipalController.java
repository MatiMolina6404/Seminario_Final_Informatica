package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import service.ServicioConfiguracionEstados;

import java.io.IOException;
import java.net.URL;

public class PrincipalController {

    private Image escudo;
    @FXML
    private AnchorPane contenedor;
    @FXML
    private AnchorPane pantallaInicio;
    @FXML
    private ImageView imgEscudoMenu;
    @FXML
    private ImageView imgEscudoInicio;

    /* Inicializa la pantalla principal, actualiza los estados
       y carga el escudo institucional en la interfaz.
    */
    @FXML
    public void initialize() {
        actualizarEstadosAutomaticamente();
        cargarEscudo();
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
        URL urlEscudo = getClass().getResource("/images/escudo2.png");
        if (urlEscudo == null) {
            System.err.println("No se encontró el escudo: /images/escudo2.png");
            return;
        }
        escudo = new Image(urlEscudo.toExternalForm());
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
        cargarVista("/view/Socios/GestionSocios.fxml");
    }

    // Abre el módulo de gestión de pagos.
    @FXML
    private void abrirPagos() {
        cargarVista("/view/Pagos/GestionPagos.fxml");
    }

    // Abre el módulo de actividades e inscripciones.
    @FXML
    private void abrirActividades() {
        cargarVista("/view/Actividades/RegistroInscripciones.fxml");
    }

    // Abre la pantalla de consulta de estados administrativos.
    @FXML
    private void abrirConsultaEstados() {
        cargarVista("/view/ConsultaEstados.fxml");
    }

    // Abre el módulo de generación de reportes.
    @FXML
    private void abrirReportes() {
        cargarVista("/view/Reportes.fxml");
    }

    // Abre el módulo de gestión de usuarios.
    @FXML
    private void abrirUsuarios() {
        cargarVista("/view/RegistroUsuario.fxml");
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