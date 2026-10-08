package dao;

import database.ConexionSQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReporteDAO {

    // Cuenta la cantidad de socios registrados dentro de un período determinado.
    public int contarSocios(String desde, String hasta) {

        String sql = """
                SELECT COUNT(*) AS total FROM SOCIO
                WHERE fechaAltaSocio BETWEEN ? AND ?
                """;

        return consultaEntera(sql, desde, hasta);
    }

    /* Cuenta la cantidad de socios según su estado administrativo,
       dentro de un período determinado.
    */
    public int contarSociosPorEstado(String estado, String desde, String hasta) {

        String sql = """
                SELECT COUNT(*) AS total FROM SOCIO
                WHERE estadoSocio = ?
                AND fechaAltaSocio BETWEEN ? AND ?
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, estado);
            statement.setString(2, desde);
            statement.setString(3, hasta);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar socios por estado.");
            e.printStackTrace();
        }
        return 0;
    }

    /* Cuenta la cantidad de pagos de cuotas societarias registrados
       dentro de un período determinado.
    */
    public int contarPagosCuotas(String desde, String hasta) {

        String sql = """
                SELECT COUNT(*) AS total FROM PAGO P
                INNER JOIN PAGO_CUOTA_SOCIETARIA PCS ON P.idPago = PCS.idPago
                WHERE P.fechaPago BETWEEN ? AND ?
                """;
        return consultaEntera(sql, desde, hasta);
    }

    /* Suma el monto total recaudado por cuotas societarias
       dentro del período seleccionado.
    */
    public double sumarCuotas(String desde, String hasta) {

        String sql = """
                SELECT COALESCE(SUM(P.montoPago), 0) AS total FROM PAGO P
                INNER JOIN PAGO_CUOTA_SOCIETARIA PCS ON P.idPago = PCS.idPago
                WHERE P.fechaPago BETWEEN ? AND ?
                """;
        return consultaDecimal(sql, desde, hasta);
    }

    /* Suma el monto recaudado por cuotas societarias,
       filtrando según el tipo de pago utilizado.
    */
    public double sumarCuotasPorTipoPago(String desde, String hasta, String tipoPago) {

        String sql = """
                SELECT COALESCE(SUM(P.montoPago), 0) AS total FROM PAGO P
                INNER JOIN PAGO_CUOTA_SOCIETARIA PCS ON P.idPago = PCS.idPago
                WHERE P.fechaPago BETWEEN ? AND ?
                AND P.tipoPago = ?
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, desde);
            statement.setString(2, hasta);
            statement.setString(3, tipoPago);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al sumar cuotas por tipo de pago.");
            e.printStackTrace();
        }
        return 0;
    }

    /* Cuenta la cantidad de pagos de actividades deportivas registrados
       en un período determinado.
     */
    public int contarPagosActividades(String desde, String hasta, Integer idActividad) {

        String sql = """
                SELECT COUNT(*) AS total
                FROM PAGO P
                INNER JOIN PAGO_ACTIVIDAD PA ON P.idPago = PA.idPago
                INNER JOIN INSCRIPCION_ACTIVIDAD IA ON PA.idInscripcion = IA.idInscripcion
                INNER JOIN ACTIVIDAD A ON IA.idActividad = A.idActividad
                WHERE P.fechaPago BETWEEN ? AND ?
                AND (? IS NULL OR A.idActividad = ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, desde);
            statement.setString(2, hasta);
            statement.setObject(3, idActividad);
            statement.setObject(4, idActividad);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al contar pagos de actividades.");
            e.printStackTrace();
        }
        return 0;
    }

    // Suma el monto total recaudado por actividades deportivas.
    public double sumarActividades(String desde, String hasta, Integer idActividad) {

        String sql = """
                SELECT COALESCE(SUM(P.montoPago), 0) AS total FROM PAGO P
                INNER JOIN PAGO_ACTIVIDAD PA ON P.idPago = PA.idPago
                INNER JOIN INSCRIPCION_ACTIVIDAD IA ON PA.idInscripcion = IA.idInscripcion
                INNER JOIN ACTIVIDAD A ON IA.idActividad = A.idActividad
                WHERE P.fechaPago BETWEEN ? AND ?
                AND (? IS NULL OR A.idActividad = ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, desde);
            statement.setString(2, hasta);
            statement.setObject(3, idActividad);
            statement.setObject(4, idActividad);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al sumar pagos de actividades.");
            e.printStackTrace();
        }
        return 0;
    }

    /* Suma el monto recaudado por actividades deportivas,
       filtrado por tipo de pago y/o por actividad.
     */
    public double sumarActividadesPorTipoPago(String desde, String hasta,
                                              String tipoPago, Integer idActividad) {

        String sql = """
                SELECT COALESCE(SUM(P.montoPago), 0) AS total FROM PAGO P
                INNER JOIN PAGO_ACTIVIDAD PA ON P.idPago = PA.idPago
                INNER JOIN INSCRIPCION_ACTIVIDAD IA ON PA.idInscripcion = IA.idInscripcion
                INNER JOIN ACTIVIDAD A ON IA.idActividad = A.idActividad
                WHERE P.fechaPago BETWEEN ? AND ?
                AND P.tipoPago = ?
                AND (? IS NULL OR A.idActividad = ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, desde);
            statement.setString(2, hasta);
            statement.setString(3, tipoPago);
            statement.setObject(4, idActividad);
            statement.setObject(5, idActividad);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al sumar actividades por tipo de pago.");
            e.printStackTrace();
        }
        return 0;
    }

    /* Cuenta la cantidad de inscripciones a actividades deportivas realizadas
       dento del período seleccionado.
     */
    public int contarInscripciones(String desde, String hasta) {

        String sql = """
                SELECT COUNT(*) AS total
                FROM INSCRIPCION_ACTIVIDAD
                WHERE fechaInscripcion BETWEEN ? AND ?
                """;

        return consultaEntera(sql, desde, hasta);
    }

    /* Registra en la base de datos que se generó un reporte.
       Se guarda el tipo de reporte, la fecha actual y el período consultado.
     */
    public void registrarReporteGenerado(String tipoReporte, String periodoReporte) {

        String sql = """
                INSERT INTO REPORTE ( tipoReporte, fechaReporte, periodoReporte)
                VALUES (?, ?, ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, tipoReporte);
            statement.setString(2, LocalDate.now().toString());
            statement.setString(3, periodoReporte);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar reporte generado.");
            e.printStackTrace();
        }
    }

    /* Ejecuta consultas que devuelven un valor entero,
       como el conteo de socios, pagos o inscripciones.
     */
    private int consultaEntera(String sql, String desde, String hasta) {
        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, desde);
            statement.setString(2, hasta);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta entera.");
            e.printStackTrace();
        }
        return 0;
    }

    /* Ejecuta consultas que devuelven un valor decimal,
       principalmente las sumas de los montos recaudados.
    */
    private double consultaDecimal(String sql, String desde, String hasta) {
        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return 0;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, desde);
            statement.setString(2, hasta);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta decimal.");
            e.printStackTrace();
        }
        return 0;
    }

}