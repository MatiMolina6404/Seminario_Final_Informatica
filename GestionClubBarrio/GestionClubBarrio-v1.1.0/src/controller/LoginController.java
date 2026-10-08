package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import service.ServicioAdministrador;
import util.Ventana;

import java.io.IOException;

public class LoginController {

    private ServicioAdministrador servicioAdministrador;
    private int intentosFallidos = 0;
    private long bloqueoHasta = 0;
    private static final int maxIntentos = 5;
    private static final long tiempoBloqueo = 5 * 60 * 1000;
    @FXML
    private ImageView imgEscudo;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtClave;
    @FXML
    private Label lblMensaje;
    @FXML
    private Label lblNombreSistema;

    // Inicializa el servicio de administrador y carga el escudo institucional.
    @FXML
    public void initialize() {
        servicioAdministrador = new ServicioAdministrador();
        cargarEscudo();
        cargarNombreSistema();
    }

    // Carga el escudo configurado en el sistema.
    private void cargarEscudo() {
        Image escudo = Ventana.cargarEscudo();
        if (escudo != null) {
            imgEscudo.setImage(escudo);
        } else {
            System.err.println("No se pudo cargar el escudo configurado.");
        }
    }

    // Carga el nombre del sistema configurado.
    private void cargarNombreSistema() {
        lblNombreSistema.setText(Ventana.obtenerNombreSistema());
    }

    /* Valida el inicio de sesión del usuario.
       Controla los intentos fallidos y el bloqueo temporal del acceso.
    */
    @FXML
    private void iniciarSesion() {
        long ahora = System.currentTimeMillis();
        if (ahora < bloqueoHasta) {
            long segundosRestantes = (bloqueoHasta - ahora) / 1000;
            lblMensaje.setText("Acceso bloqueado temporalmente. Intente nuevamente en "
                            + segundosRestantes + " segundos.");
            return;
        }
        String usuario = txtUsuario.getText();
        String clave = txtClave.getText();
        String resultado = servicioAdministrador.iniciarSesion(usuario, clave);
        if ("OK".equals(resultado)) {
            intentosFallidos = 0;
            bloqueoHasta = 0;
            abrirPantallaPrincipal();
        } else {
            if ("Usuario o contraseña incorrectos.".equals(resultado)) {
                intentosFallidos++;
                if (intentosFallidos >= maxIntentos) {
                    bloqueoHasta = System.currentTimeMillis() + tiempoBloqueo;
                    intentosFallidos = 0;
                    lblMensaje.setText("Demasiados intentos fallidos. " +
                            "Acceso bloqueado por 5 minutos.");
                } else {
                    int restantes = maxIntentos - intentosFallidos;
                    lblMensaje.setText(resultado + " Intentos restantes: " + restantes
                    );
                }
            } else {
                lblMensaje.setText(resultado);
            }
            txtClave.clear();
            txtClave.requestFocus();
        }
    }

    // Abre la pantalla principal del sistema luego de un inicio de sesión correcto.
    private void abrirPantallaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/PantallaPrincipal.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(Ventana.crearScene(root));
            Ventana.configurarStage(stage, Ventana.obtenerNombreSistema());
            stage.setResizable(false);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            lblMensaje.setText("No se pudo abrir la pantalla principal.");
        }
    }

}