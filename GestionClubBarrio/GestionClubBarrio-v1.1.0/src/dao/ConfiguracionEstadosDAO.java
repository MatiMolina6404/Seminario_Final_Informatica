package dao;

import database.ConexionSQLite;
import model.Auxiliares.ConfiguracionEstados;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return null;
        }
        try (conexion;
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

    // Actualiza el estado administrativo de los socios.
    public void actualizarEstadoSocio(Map<Integer, String> estados) {
        if (estados == null || estados.isEmpty()) {
            return;
        }

        String sql = "UPDATE SOCIO SET estadoSocio = ? WHERE idSocio = ?";

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return;
        }
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            conexion.setAutoCommit(false);
            for (Map.Entry<Integer, String> entrada : estados.entrySet()) {
                statement.setString(1, entrada.getValue());
                statement.setInt(2, entrada.getKey());
                statement.addBatch();
            }
            statement.executeBatch();
            conexion.commit();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estados de socios.");
            e.printStackTrace();
            try {
                conexion.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    // Actualiza el estado de las inscripciones a actividades deportivas.
    public void actualizarEstadoInscripcion(Map<Integer, String> estados) {
        if (estados == null || estados.isEmpty()) {
            return;
        }

        String sql = """
            UPDATE INSCRIPCION_ACTIVIDAD
            SET estadoInscripcion = ? WHERE idInscripcion = ?
            """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return;
        }
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            conexion.setAutoCommit(false);
            for (Map.Entry<Integer, String> entrada : estados.entrySet()) {
                statement.setString(1, entrada.getValue());
                statement.setInt(2, entrada.getKey());
                statement.addBatch();
            }
            statement.executeBatch();
            conexion.commit();
        } catch (SQLException e) {
            System.err.println("Error al actualizar estados de inscripciones.");
            e.printStackTrace();
            try {
                conexion.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    // Consulta la fecha del último pago de cuota de cada socio.
    public List<UltimoPago> consultarUltimoPagoSocios() {
        List<UltimoPago> lista = new ArrayList<>();

        String sql = """
            SELECT S.idSocio, MAX(P.fechaPago) AS ultimaFechaPago
            FROM SOCIO S
            LEFT JOIN PAGO_CUOTA_SOCIETARIA PCS ON S.idSocio = PCS.idSocio
            LEFT JOIN PAGO P ON PCS.idPago = P.idPago
            GROUP BY S.idSocio
        """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return new ArrayList<>();
        }
        try (conexion;
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

    // Consulta la fecha del último pago asociado a cada inscripción a actividad deportiva.
    public List<UltimoPago> consultarUltimoPagoInscripciones() {
        List<UltimoPago> lista = new ArrayList<>();

        String sql = """
            SELECT IA.idInscripcion, MAX(P.fechaPago) AS ultimaFechaPago
            FROM INSCRIPCION_ACTIVIDAD IA
            LEFT JOIN PAGO_ACTIVIDAD PA ON IA.idInscripcion = PA.idInscripcion
            LEFT JOIN PAGO P ON PA.idPago = P.idPago
            GROUP BY IA.idInscripcion
        """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return new ArrayList<>();
        }
        try (conexion;
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

    // Guarda los días que determinan el vencimiento de las cuotas de socios e inscripciones.
    public boolean modificarConfiguracion(ConfiguracionEstados configuracion) {

        String sql = """
            INSERT OR REPLACE INTO CONFIGURACION_ESTADOS
                (idConfiguracion, diasSocioDeudor, diasSocioInactivo, diasInscripcionInactiva)
            VALUES (1, ?, ?, ?)
            """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, configuracion.getDiasSocioDeudor());
            statement.setInt(2, configuracion.getDiasSocioInactivo());
            statement.setInt(3, configuracion.getDiasInscripcionInactiva());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar la configuración de estados.");
            e.printStackTrace();
            return false;
        }
    }

}