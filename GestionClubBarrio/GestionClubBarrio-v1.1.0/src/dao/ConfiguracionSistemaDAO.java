package dao;

import database.ConexionSQLite;
import model.ConfiguracionSistema;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionSistemaDAO {

    // Obtiene la configuración actual del sistema.
    public ConfiguracionSistema obtenerConfiguracion() {

        String sql = "SELECT * FROM CONFIGURACION_SISTEMA WHERE id = 1";

        try (Connection conexion = ConexionSQLite.conectar()) {
            if (conexion == null) {
                return null;
            }
            try (PreparedStatement statement = conexion.prepareStatement(sql);
                 ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return new ConfiguracionSistema(
                            resultado.getInt("id"),
                            resultado.getString("nombreClub"),
                            resultado.getString("nombreSistema"),
                            resultado.getString("rutaEscudo"),
                            resultado.getString("colorFondo"),
                            resultado.getString("colorMenu"),
                            resultado.getString("colorPrincipal")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la configuración del sistema: " + e.getMessage());
        }
        return null;
    }

    // Modifica la configuración actual del sistema.
    public boolean modificarConfiguracion(ConfiguracionSistema configuracion) {

        String sql = """
                UPDATE CONFIGURACION_SISTEMA
                SET nombreClub = ?,
                    nombreSistema = ?,
                    rutaEscudo = ?,
                    colorFondo = ?,
                    colorMenu = ?,
                    colorPrincipal = ?
                WHERE id = 1
                """;

        try (Connection conexion = ConexionSQLite.conectar()) {
            if (conexion == null) {
                return false;
            }
            try (PreparedStatement statement = conexion.prepareStatement(sql)) {
                statement.setString(1, configuracion.getNombreClub());
                statement.setString(2, configuracion.getNombreSistema());
                statement.setString(3, configuracion.getRutaEscudo());
                statement.setString(4, configuracion.getColorFondo());
                statement.setString(5, configuracion.getColorMenu());
                statement.setString(6, configuracion.getColorPrincipal());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al modificar la configuración del sistema: " + e.getMessage());
            return false;
        }
    }

}