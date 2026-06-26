package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQLite {

    private static final String URL = "jdbc:sqlite:BDSQLite/Gestion_Club.db";

    // Establece la conexión con la base de datos SQLite.
    public static Connection conectar() {
        try {
            Connection conexion = DriverManager.getConnection(URL);
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
}