package dao;

import database.ConexionSQLite;
import model.Actividad;
import model.InscripcionActividad;
import model.Participante;
import model.Socio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionActividadDAO {

    // Registra una inscripción a una actividad deportiva.
    public boolean registrarInscripcion(InscripcionActividad inscripcion) {

        String sql = """
                INSERT INTO INSCRIPCION_ACTIVIDAD( fechaInscripcion, estadoInscripcion,
                    idActividad, idSocio, idParticipante)
                VALUES (?, ?, ?, ?, ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, inscripcion.getFechaInscripcion());
            statement.setString(2, inscripcion.getEstadoInscripcion());
            statement.setInt(3, inscripcion.getActividad().getIdActividad());
            // Inscripción de un socio.
            if (inscripcion.getSocio() != null) {
                statement.setInt(4, inscripcion.getSocio().getIdSocio());
            } else {
                statement.setNull(4, Types.INTEGER);
            }
            // Inscripción de un participante.
            if (inscripcion.getParticipante() != null) {
                statement.setInt(5, inscripcion.getParticipante().getIdParticipante());
            } else {
                statement.setNull(5, Types.INTEGER);
            }
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar inscripción.");
            e.printStackTrace();
            return false;
        }
    }

    // Consulta para verificar la existencia de una inscripción a una actividad.
    public boolean existeInscripcion(InscripcionActividad inscripcion) {

        String sql;
        if (inscripcion.getSocio() != null) {
            sql = "SELECT 1 FROM INSCRIPCION_ACTIVIDAD WHERE idActividad = ? AND idSocio = ?";
        } else {
            sql = "SELECT 1 FROM INSCRIPCION_ACTIVIDAD WHERE idActividad = ? AND idParticipante = ?";
        }

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, inscripcion.getActividad().getIdActividad());
            if (inscripcion.getSocio() != null) {
                statement.setInt(2, inscripcion.getSocio().getIdSocio());
            } else {
                statement.setInt(2, inscripcion.getParticipante().getIdParticipante());
            }
            ResultSet resultado = statement.executeQuery();
            return resultado.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar inscripción.");
            e.printStackTrace();
            return false;
        }
    }

    // Consulta las inscripciones correspondientes a una actividad específica.
    public List<InscripcionActividad> consultarPorActividad(int idActividad) {

        List<InscripcionActividad> inscripciones = new ArrayList<>();
        String sql = """
                SELECT IA.idInscripcion,
                       IA.fechaInscripcion,
                       IA.estadoInscripcion,

                       A.idActividad,
                       A.nombreActividad,
                       A.descripcionActividad,

                       S.idSocio,
                       S.nombreSocio,
                       S.apellidoSocio,
                       S.dniSocio,
                       S.telefonoSocio,
                       S.direccionSocio,
                       S.fechaAltaSocio,
                       S.estadoSocio,

                       P.idParticipante,
                       P.nombreParticipante,
                       P.apellidoParticipante,
                       P.dniParticipante,
                       P.telefonoParticipante,
                       P.direccionParticipante,
                       P.fechaAltaParticipante

                FROM INSCRIPCION_ACTIVIDAD IA
                INNER JOIN ACTIVIDAD A ON IA.idActividad = A.idActividad
                LEFT JOIN SOCIO S ON IA.idSocio = S.idSocio
                LEFT JOIN PARTICIPANTE P ON IA.idParticipante = P.idParticipante
                WHERE IA.idActividad = ?
                ORDER BY IA.idInscripcion DESC
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idActividad);
            ResultSet resultado = statement.executeQuery();
            while (resultado.next()) {
                InscripcionActividad inscripcion = armarInscripcion(resultado);
                inscripciones.add(inscripcion);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar inscripciones por actividad.");
            e.printStackTrace();
        }
        return inscripciones;
    }

    // Consulta todas las inscripciones registradas en el sistema.
    public List<InscripcionActividad> consultarInscripciones() {

        List<InscripcionActividad> inscripciones = new ArrayList<>();
        String sql = """
                SELECT IA.idInscripcion,
                       IA.fechaInscripcion,
                       IA.estadoInscripcion,

                       A.idActividad,
                       A.nombreActividad,
                       A.descripcionActividad,

                       S.idSocio,
                       S.nombreSocio,
                       S.apellidoSocio,
                       S.dniSocio,
                       S.telefonoSocio,
                       S.direccionSocio,
                       S.fechaAltaSocio,
                       S.estadoSocio,

                       P.idParticipante,
                       P.nombreParticipante,
                       P.apellidoParticipante,
                       P.dniParticipante,
                       P.telefonoParticipante,
                       P.direccionParticipante,
                       P.fechaAltaParticipante

                FROM INSCRIPCION_ACTIVIDAD IA

                INNER JOIN ACTIVIDAD A ON IA.idActividad = A.idActividad
                LEFT JOIN SOCIO S ON IA.idSocio = S.idSocio
                LEFT JOIN PARTICIPANTE P ON IA.idParticipante = P.idParticipante
                ORDER BY IA.idInscripcion DESC
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                InscripcionActividad inscripcion = armarInscripcion(resultado);
                inscripciones.add(inscripcion);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar inscripciones.");
            e.printStackTrace();
        }
        return inscripciones;
    }

    // Modifica el estado de una inscripción registrada.
    public boolean modificarInscripcion(InscripcionActividad inscripcion) {

        String sql = "UPDATE INSCRIPCION_ACTIVIDAD SET estadoInscripcion = ? WHERE idInscripcion = ?";

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, inscripcion.getEstadoInscripcion());
            statement.setInt(2, inscripcion.getIdInscripcion());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar inscripción.");
            e.printStackTrace();
            return false;
        }
    }

    /* Construye una inscripción completa con su actividad y,
        según corresponda, el socio o participante.
     */
    private InscripcionActividad armarInscripcion(ResultSet resultado) throws SQLException {

        InscripcionActividad inscripcion = new InscripcionActividad();
        inscripcion.setIdInscripcion(resultado.getInt("idInscripcion"));
        inscripcion.setFechaInscripcion(resultado.getString("fechaInscripcion"));
        inscripcion.setEstadoInscripcion(resultado.getString("estadoInscripcion"));

        Actividad actividad = new Actividad();
        actividad.setIdActividad(resultado.getInt("idActividad"));
        actividad.setNombreActividad(resultado.getString("nombreActividad"));
        actividad.setDescripcionActividad(resultado.getString("descripcionActividad"));
        inscripcion.setActividad(actividad);

        // Identifica si la inscripción está asociada a un socio.
        int idSocio = resultado.getInt("idSocio");
        if (!resultado.wasNull()) {
            Socio socio = new Socio();
            socio.setIdSocio(idSocio);
            socio.setNombreSocio(
                    resultado.getString("nombreSocio"));
            socio.setApellidoSocio(
                    resultado.getString("apellidoSocio"));
            socio.setDniSocio(
                    resultado.getString("dniSocio"));
            socio.setTelefonoSocio(
                    resultado.getString("telefonoSocio"));
            socio.setDireccionSocio(
                    resultado.getString("direccionSocio"));
            socio.setFechaAltaSocio(
                    resultado.getString("fechaAltaSocio"));
            socio.setEstadoSocio(
                    resultado.getString("estadoSocio"));
            inscripcion.setSocio(socio);
        }
        // Identifica si la inscripción está asociada a un participante externo.
        int idParticipante = resultado.getInt("idParticipante");
        if (!resultado.wasNull()) {
            Participante participante = new Participante();
            participante.setIdParticipante(idParticipante);
            participante.setNombreParticipante(
                    resultado.getString("nombreParticipante"));
            participante.setApellidoParticipante(
                    resultado.getString("apellidoParticipante"));
            participante.setDniParticipante(
                    resultado.getString("dniParticipante"));
            participante.setTelefonoParticipante(
                    resultado.getString("telefonoParticipante"));
            participante.setDireccionParticipante(
                    resultado.getString("direccionParticipante"));
            participante.setFechaAltaParticipante(
                    resultado.getString("fechaAltaParticipante"));
            inscripcion.setParticipante(participante);
        }
        return inscripcion;
    }

}