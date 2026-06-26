package util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.net.URL;

public class Ventana {

    private static final String rutaCSS = "/css/estilos.css";
    private static final String rutaIcono = "/images/escudo2.png";

    // Crea una escena JavaFX y le aplica la hoja de estilos CSS del sistema.
    public static Scene crearScene(Parent root) {
        Scene scene = new Scene(root);
        URL cssUrl = Ventana.class.getResource(rutaCSS);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("No se encontró el CSS: " + rutaCSS);
        }
        return scene;
    }

    // Crea una nueva ventana con título, escena, estilos e ícono configurados.
    public static Stage crearVentana(Parent root, String titulo) {
        Stage stage = new Stage();
        Scene scene = crearScene(root);
        configurarStage(stage, titulo);
        stage.setScene(scene);
        stage.setResizable(false);
        return stage;
    }

    // Configura el título y el ícono de una ventana.
    public static void configurarStage(Stage stage, String titulo) {
        stage.setTitle(titulo);
        URL iconoUrl = Ventana.class.getResource(rutaIcono);
        if (iconoUrl != null) {
            stage.getIcons().add(new Image(iconoUrl.toExternalForm()));
        } else {
            System.err.println("No se encontró el icono: " + rutaIcono);
        }
    }

    // Aplica el estilo CSS y el ícono institucional a las alertas del sistema.
    public static void aplicarEstiloAlerta(Dialog<?> alerta) {
        URL cssUrl = Ventana.class.getResource(rutaCSS);
        if (cssUrl != null) {
            alerta.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("No se encontró el CSS: " + rutaCSS);
        }
        URL iconoUrl = Ventana.class.getResource(rutaIcono);
        if (iconoUrl != null) {
            Image imagen = new Image(iconoUrl.toExternalForm());
            alerta.setOnShown(event -> {
                Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
                stage.getIcons().add(imagen);
            });
        } else {
            System.err.println("No se encontró el icono: " + rutaIcono);
        }
    }

}