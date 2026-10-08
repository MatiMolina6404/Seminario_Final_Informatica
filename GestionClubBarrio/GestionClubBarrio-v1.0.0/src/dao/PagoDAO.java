package dao;

import database.ConexionSQLite;
import model.Actividad;
import model.InscripcionActividad;
import model.PagoActividad;
import model.PagoCuotaSocietaria;
import model.Participante;
import model.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    /* Registra los datos generales de un pago en la tabla PAGO.
       Devuelve el id generado para poder asociarlo luego con una cuota o actividad.
    */
    private int registrarPago(Connection conexion, String fechaPago, double montoPago,
                              String tipoPago) throws SQLException {

        String sql = "INSERT INTO PAGO (fechaPago,montoPago,tipoPago) VALUES (?, ?, ?)";

        try (PreparedStatement statement = conexion.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fechaPago);
            statement.setDouble(2, montoPago);
            statement.setString(3, tipoPago);
            int filas = statement.executeUpdate();
            if (filas == 0) {
                return 0;
            }
            ResultSet resultado = statement.getGeneratedKeys();
            if (resultado.next()) {
                return resultado.getInt(1);
            }
            return 0;
        }
    }

    // Registra un pago correspondiente a una cuota societaria.
    public boolean registrarPagoCuotaSocietaria(PagoCuotaSocietaria pago) {

        String sqlCuota = """
                INSERT INTO PAGO_CUOTA_SOCIETARIA (
                    idPago,
                    periodoCuotaSoc,
                    idSocio
                ) VALUES (?, ?, ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion) {
            conexion.setAutoCommit(false);
            int idPagoGenerado = registrarPago(conexion,
                    pago.getFechaPago(),
                    pago.getMontoPago(),
                    pago.getTipoPago());
            if (idPagoGenerado <= 0) {
                conexion.rollback();
                return false;
            }
            try (PreparedStatement statement = conexion.prepareStatement(sqlCuota)) {
                statement.setInt(1, idPagoGenerado);
                statement.setString(2, pago.getPeriodoCuotaSoc());
                statement.setInt(3, pago.getSocio().getIdSocio());
                int filas = statement.executeUpdate();
                if (filas > 0) {
                    conexion.commit();
                    pago.setIdPago(idPagoGenerado);
                    return true;
                } else {
                    conexion.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar pago de cuota societaria.");
            e.printStackTrace();
            return false;
        }
    }

    // Registra un pago asociado a una actividad deportiva.
    public boolean registrarPagoActividad(PagoActividad pago) {
        String sqlActividad = """
                INSERT INTO PAGO_ACTIVIDAD (
                    idPago,
                    periodoCuotaAct,
                    idInscripcion
                ) VALUES (?, ?, ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion) {
            conexion.setAutoCommit(false);
            int idPagoGenerado = registrarPago(conexion,
                    pago.getFechaPago(),
                    pago.getMontoPago(),
                    pago.getTipoPago());
            if (idPagoGenerado <= 0) {
                conexion.rollback();
                return false;
            }
            try (PreparedStatement statement = conexion.prepareStatement(sqlActividad)) {
                statement.setInt(1, idPagoGenerado);
                statement.setString(2, pago.getPeriodoCuotaAct());
                statement.setInt(3, pago.getInscripcionActividad().getIdInscripcion());
                int filas = statement.executeUpdate();
                if (filas > 0) {
                    conexion.commit();
                    pago.setIdPago(idPagoGenerado);
                    return true;
                } else {
                    conexion.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar pago de actividad.");
            e.printStackTrace();
            return false;
        }
    }

    /* Consulta todos los pagos de cuotas societarias registrados y
       los datos del socio vinculado a cada pago.
    */
    public List<PagoCuotaSocietaria> consultarPagosCuotaSocietaria() {

        List<PagoCuotaSocietaria> pagos = new ArrayList<>();
        String sql = """
                SELECT P.idPago,
                       P.fechaPago,
                       P.montoPago,
                       P.tipoPago,

                       PCS.periodoCuotaSoc,

                       S.idSocio,
                       S.nombreSocio,
                       S.apellidoSocio,
                       S.dniSocio,
                       S.telefonoSocio,
                       S.direccionSocio,
                       S.fechaAltaSocio,
                       S.estadoSocio

                FROM PAGO P
                INNER JOIN PAGO_CUOTA_SOCIETARIA PCS ON P.idPago = PCS.idPago
                INNER JOIN SOCIO S ON PCS.idSocio = S.idSocio
                ORDER BY P.idPago DESC
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                Socio socio = new Socio();
                socio.setIdSocio(resultado.getInt("idSocio"));
                socio.setNombreSocio(resultado.getString("nombreSocio"));
                socio.setApellidoSocio(resultado.getString("apellidoSocio"));
                socio.setDniSocio(resultado.getString("dniSocio"));
                socio.setTelefonoSocio(resultado.getString("telefonoSocio"));
                socio.setDireccionSocio(resultado.getString("direccionSocio"));
                socio.setFechaAltaSocio(resultado.getString("fechaAltaSocio"));
                socio.setEstadoSocio(resultado.getString("estadoSocio"));

                PagoCuotaSocietaria pago = new PagoCuotaSocietaria();
                pago.setIdPago(resultado.getInt("idPago"));
                pago.setFechaPago(resultado.getString("fechaPago"));
                pago.setMontoPago(resultado.getDouble("montoPago"));
                pago.setTipoPago(resultado.getString("tipoPago"));
                pago.setPeriodoCuotaSoc(resultado.getString("periodoCuotaSoc"));
                pago.setSocio(socio);
                pagos.add(pago);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar pagos de cuota societaria.");
            e.printStackTrace();
        }
        return pagos;
    }

    /* Consulta los pagos asociados a actividades deportivas.
       Recupera datos del pago, la inscripción, la actividad y la persona inscripta.
    */
    public List<PagoActividad> consultarPagosActividad() {

        List<PagoActividad> pagos = new ArrayList<>();
        String sql = """
                SELECT P.idPago,
                       P.fechaPago,
                       P.montoPago,
                       P.tipoPago,

                       PA.periodoCuotaAct,

                       IA.idInscripcion,
                       IA.fechaInscripcion,
                       IA.estadoInscripcion,

                       A.idActividad,
                       A.nombreActividad,
                       A.descripcionActividad,

                       S.idSocio,
                       S.nombreSocio,
                       S.apellidoSocio,
                       S.dniSocio,

                       PART.idParticipante,
                       PART.nombreParticipante,
                       PART.apellidoParticipante,
                       PART.dniParticipante

                FROM PAGO P
                INNER JOIN PAGO_ACTIVIDAD PA ON P.idPago = PA.idPago
                INNER JOIN INSCRIPCION_ACTIVIDAD IA ON PA.idInscripcion = IA.idInscripcion 
                INNER JOIN ACTIVIDAD A ON IA.idActividad = A.idActividad
                LEFT JOIN SOCIO S ON IA.idSocio = S.idSocio  
                LEFT JOIN PARTICIPANTE PART ON IA.idParticipante = PART.idParticipante
                ORDER BY P.idPago DESC
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                // Actividad asociada a la inscripción.
                Actividad actividad = new Actividad();
                actividad.setIdActividad(resultado.getInt("idActividad"));
                actividad.setNombreActividad(resultado.getString("nombreActividad"));
                actividad.setDescripcionActividad(resultado.getString("descripcionActividad"));
                // Inscripción relacionada con el pago.
                InscripcionActividad inscripcion = new InscripcionActividad();
                inscripcion.setIdInscripcion(resultado.getInt("idInscripcion"));
                inscripcion.setFechaInscripcion(resultado.getString("fechaInscripcion"));
                inscripcion.setEstadoInscripcion(resultado.getString("estadoInscripcion"));
                inscripcion.setActividad(actividad);
                // Verifica si la inscripción corresponde a un socio o a un participante no socio.
                int idSocio = resultado.getInt("idSocio");
                if (!resultado.wasNull()) {
                    Socio socio = new Socio();
                    socio.setIdSocio(idSocio);
                    socio.setNombreSocio(resultado.getString("nombreSocio"));
                    socio.setApellidoSocio(resultado.getString("apellidoSocio"));
                    socio.setDniSocio(resultado.getString("dniSocio"));
                    inscripcion.setSocio(socio);
                } else {
                    Participante participante = new Participante();
                    participante.setIdParticipante(resultado.getInt("idParticipante"));
                    participante.setNombreParticipante(resultado.getString("nombreParticipante"));
                    participante.setApellidoParticipante(resultado.getString("apellidoParticipante"));
                    participante.setDniParticipante(resultado.getString("dniParticipante"));
                    inscripcion.setParticipante(participante);
                }
                // Se crea el pago de la actividad y se lo vincula con la inscripción correspondiente.
                PagoActividad pago = new PagoActividad();
                pago.setIdPago(resultado.getInt("idPago"));
                pago.setFechaPago(resultado.getString("fechaPago"));
                pago.setMontoPago(resultado.getDouble("montoPago"));
                pago.setTipoPago(resultado.getString("tipoPago"));
                pago.setPeriodoCuotaAct(resultado.getString("periodoCuotaAct"));
                pago.setInscripcionActividad(inscripcion);
                pagos.add(pago);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar pagos de actividades.");
            e.printStackTrace();
        }
        return pagos;
    }

}