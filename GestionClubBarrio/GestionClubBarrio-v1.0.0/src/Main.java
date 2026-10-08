import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.Ventana;

public class Main extends Application {

    /* Metodo principal de inicio de la aplicación JavaFX.
       Carga la pantalla de login y configura la ventana inicial del sistema.
    */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        Parent root = loader.load();
        Scene scene = Ventana.crearScene(root);
        Ventana.configurarStage(stage,
                "Sistema de Gestión Administrativa para Clubes de Barrio"
        );
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}