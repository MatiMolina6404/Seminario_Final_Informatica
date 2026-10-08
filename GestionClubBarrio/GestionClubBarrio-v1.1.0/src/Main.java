import database.ConexionSQLite;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import util.Ventana;

public class Main extends Application {

    /* Metodo principal de inicio de la aplicación JavaFX.
       Carga la pantalla de inicio de sesión y configura la ventana inicial del sistema.
    */
    @Override
    public void start(Stage stage) throws Exception {
        // Permite seleccionar una base de datos si no encuentra la original.
        if (!ConexionSQLite.existeBaseDatos()) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Base de datos");
            alerta.setHeaderText("No se encontró la base de datos.");
            alerta.setContentText("Seleccione el archivo Gestion_Club.db correspondiente al sistema.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
            boolean seleccionada = ConexionSQLite.seleccionarBaseDatos(stage);
            if (!seleccionada) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Base de datos");
                error.setHeaderText("No se pudo iniciar el sistema.");
                error.setContentText("Debe seleccionar una base de datos válida para continuar.");
                Ventana.aplicarEstiloAlerta(error);
                error.showAndWait();
                return;
            }
        }
        // Carga la página de inicio de sesión.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        Parent root = loader.load();
        Scene scene = Ventana.crearScene(root);
        Ventana.configurarStage(stage, Ventana.obtenerNombreSistema());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}