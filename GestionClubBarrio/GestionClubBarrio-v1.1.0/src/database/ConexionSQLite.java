package database;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {

    private static Path rutaBaseDatos = Paths.get("BDSQLite/Gestion_Club.db");

    // Establece la conexión con la base de datos SQLite.
    public static Connection conectar() {
        if (!Files.exists(rutaBaseDatos)) {
            System.err.println("No se encontró la base de datos: "
                    + rutaBaseDatos.toAbsolutePath());
            return null;
        }
        String url = "jdbc:sqlite:" + rutaBaseDatos.toString();
        try {
            Connection conexion = DriverManager.getConnection(url);
            try (Statement statement = conexion.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
            return conexion;
        } catch (SQLException e) {
            System.err.println("No fue posible establecer la conexión con la base de datos.");
            e.printStackTrace();
            return null;
        }
    }

    // Verifica si existe la base de datos configurada.
    public static boolean existeBaseDatos() {
        return Files.exists(rutaBaseDatos);
    }

    // Permite seleccionar una base de datos SQLite desde el sistema.
    public static boolean seleccionarBaseDatos(Window ventana) {
        FileChooser selector = new FileChooser();
        selector.setTitle("Seleccionar base de datos del sistema");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Base de datos SQLite (*.db)", "*.db"));
        File archivo = selector.showOpenDialog(ventana);
        if (archivo == null) {
            return false;
        }
        if (!validarBaseDatos(archivo.toPath())) {
            return false;
        }
        rutaBaseDatos = archivo.toPath();
        return true;
    }

    // Valida que el archivo seleccionado corresponda a una base de datos del sistema.
    private static boolean validarBaseDatos(Path ruta) {
        if (!Files.exists(ruta)) {
            return false;
        }
        String url = "jdbc:sqlite:" + ruta.toString();
        try (Connection conexion = DriverManager.getConnection(url);
             Statement statement = conexion.createStatement();
             ResultSet resultado = statement.executeQuery(
                     "SELECT name FROM sqlite_master " +
                             "WHERE type='table' AND name='ADMINISTRADOR'")) {
                                return resultado.next();
        } catch (SQLException e) {
            System.err.println("El archivo seleccionado no corresponde a una base de datos válida.");
            return false;
        }
    }

}