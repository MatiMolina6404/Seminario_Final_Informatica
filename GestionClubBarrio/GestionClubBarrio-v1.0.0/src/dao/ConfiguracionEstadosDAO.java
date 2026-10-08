package dao;

import database.ConexionSQLite;
import model.Auxiliares.ConfiguracionEstados;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Auxiliares.UltimoPago;

public class ConfiguracionEstadosDAO {

    /* Obtiene la configuración actual de los días que marcan los diferentes estados
       de los socios, para actualizarlos automáticamente.
    */
    public ConfiguracionEstados obtenerConfiguracion() {

        String sql = """
                SELECT diasSocioDeudor,
                       diasSocioInactivo,
                       diasInscripcionInactiva
                FROM CONFIGURACION_ESTADOS
                WHERE idConfiguracion = 1
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            if (resultado.next()) {
                return new ConfiguracionEstados(
                        resultado.getInt("diasSocioDeudor"),
                        resultado.getInt("diasSocioInactivo"),
                        resultado.getInt("diasInscripcionInactiva")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración de estados.");
            e.printStackTrace();
        }
        return new ConfiguracionEstados(30, 60, 30);
    }

    // Actualiza el estado administrativo de un socio.
    public void actualizarEstadoSocio(int idSocio, String estado) {

        String sql = "UPDATE SOCIO SET estadoSocio = ? WHERE idSocio = ?";

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setInt(2, idSocio);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del socio.");
            e.printStackTrace();
        }
    }

    // Actualiza el estado de la inscripción a una actividad deportiva.
    public void actualizarEstadoInscripcion(int idInscripcion, String estado) {

        String sql = """
                UPDATE INSCRIPCION_ACTIVIDAD
                SET estadoInscripcion = ? WHERE idInscripcion = ?
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setInt(2, idInscripcion);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de inscripción.");
            e.printStackTrace();
        }
    }

    // Consulta la fecha del último pago de cuota de cada socio.
    public List<UltimoPago> consultarUltimoPagoSocios() {

        List<UltimoPago> lista = new ArrayList<>();
        String sql = """
            SELECT S.idSocio,
                   COALESCE(MAX(P.fechaPago), S.fechaAltaSocio) AS ultimaFechaPago
            FROM SOCIO S
            LEFT JOIN PAGO_CUOTA_SOCIETARIA PCS ON S.idSocio = PCS.idSocio
            LEFT JOIN PAGO P ON PCS.idPago = P.idPago
            GROUP BY S.idSocio
            """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                lista.add(new UltimoPago(
                        resultado.getInt("idSocio"),
                        resultado.getString("ultimaFechaPago")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar último pago de socios.");
            e.printStackTrace();
        }
        return lista;
    }

    // Consulta la fecha del último pago asociado a cada inscripción a actividad deportiva
    public List<UltimoPago> consultarUltimoPagoInscripciones() {

        List<UltimoPago> lista = new ArrayList<>();
        String sql = """
            SELECT IA.idInscripcion,
                   COALESCE(MAX(P.fechaPago), IA.fechaInscripcion) AS ultimaFechaPago
            FROM INSCRIPCION_ACTIVIDAD IA
            LEFT JOIN PAGO_ACTIVIDAD PA ON IA.idInscripcion = PA.idInscripcion
            LEFT JOIN PAGO P ON PA.idPago = P.idPago      
            GROUP BY IA.idInscripcion
            """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                lista.add(new UltimoPago(
                        resultado.getInt("idInscripcion"),
                        resultado.getString("ultimaFechaPago")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar último pago de inscripciones.");
            e.printStackTrace();
        }
        return lista;
    }

}