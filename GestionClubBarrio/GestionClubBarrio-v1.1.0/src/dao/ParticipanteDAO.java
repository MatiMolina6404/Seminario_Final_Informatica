package dao;

import database.ConexionSQLite;
import model.Participante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParticipanteDAO {

    /* Registra un nuevo participante en la base de datos.
       Los participantes pueden registrarse a las actividades deportivas sin ser socios.
    */
    public boolean registrarParticipante(Participante participante) {

        String sql = """
            INSERT INTO PARTICIPANTE (
                nombreParticipante,
                apellidoParticipante,
                dniParticipante,
                telefonoParticipante,
                direccionParticipante,
                fechaAltaParticipante
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, participante.getNombreParticipante());
            statement.setString(2, participante.getApellidoParticipante());
            statement.setString(3, participante.getDniParticipante());
            statement.setString(4, participante.getTelefonoParticipante());
            statement.setString(5, participante.getDireccionParticipante());
            statement.setString(6, participante.getFechaAltaParticipante());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar participante.");
            e.printStackTrace();
            return false;
        }
    }

    // Verifica si ya existe un participante registrado con el mismo DNI.
    public boolean existeParticipante(String dni, Integer idParticipanteExcluir) {

        String sql;
        if (idParticipanteExcluir == null) {
            sql = "SELECT 1 FROM PARTICIPANTE WHERE dniParticipante = ?";
        } else {
            sql = "SELECT 1 FROM PARTICIPANTE WHERE dniParticipante = ? AND idParticipante <> ?";
        }

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, dni);
            if (idParticipanteExcluir != null) {
                statement.setInt(2, idParticipanteExcluir);
            }
            ResultSet resultado = statement.executeQuery();
            return resultado.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar participante.");
            e.printStackTrace();
            return false;
        }
    }

    // Consulta participantes registrados según un campo de búsqueda seleccionado.
    public List<Participante> consultarParticipantes(String campo, String valor) {
        List<Participante> participantes = new ArrayList<>();
        // Se definen los campos permitidos para armar una consulta válida.
        Map<String, String> camposPermitidos = Map.of(
                "ID", "idParticipante",
                "Nombre", "nombreParticipante",
                "Apellido", "apellidoParticipante",
                "DNI", "dniParticipante",
                "Teléfono", "telefonoParticipante",
                "Dirección", "direccionParticipante",
                "Fecha Alta", "fechaAltaParticipante"
        );
        String sql;
        Set<String> busquedaExacta = Set.of("idParticipante",
                "dniParticipante", "telefonoParticipante");
        if (campo.equals("Todos")) {
            sql = "SELECT * FROM PARTICIPANTE";
        } else {
            String columna = camposPermitidos.get(campo);
            if (columna == null) {
                return participantes;
            }
            if (busquedaExacta.contains(columna)) {
                sql = "SELECT * FROM PARTICIPANTE WHERE " + columna + " = ?";
            } else {
                sql = "SELECT * FROM PARTICIPANTE WHERE " + columna + " LIKE ?";
            }
        }

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return participantes;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            if (!campo.equals("Todos")) {
                String columna = camposPermitidos.get(campo);
                if ("idParticipante".equals(columna)) {
                    try {
                        statement.setInt(1, Integer.parseInt(valor));
                    } catch (NumberFormatException e) {
                        return participantes;
                    }
                } else if (busquedaExacta.contains(columna)) {
                    statement.setString(1, valor);
                } else {
                    statement.setString(1, "%" + valor + "%");
                }
            }

            ResultSet resultado = statement.executeQuery();
            while (resultado.next()) {
                Participante participante = new Participante();
                participante.setIdParticipante
                        (resultado.getInt("idParticipante"));
                participante.setNombreParticipante
                        (resultado.getString("nombreParticipante"));
                participante.setApellidoParticipante
                        (resultado.getString("apellidoParticipante"));
                participante.setDniParticipante
                        (resultado.getString("dniParticipante"));
                participante.setTelefonoParticipante
                        (resultado.getString("telefonoParticipante"));
                participante.setDireccionParticipante
                        (resultado.getString("direccionParticipante"));
                participante.setFechaAltaParticipante
                        (resultado.getString("fechaAltaParticipante"));
                participantes.add(participante);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar participantes.");
            e.printStackTrace();
        }
        return participantes;
    }

    // Modifica los datos de un participante existente.
    public boolean modificarParticipante(Participante participante) {

        String sql = """
            UPDATE PARTICIPANTE
            SET nombreParticipante = ?, apellidoParticipante = ?, dniParticipante = ?,
                telefonoParticipante = ?, direccionParticipante = ?
            WHERE idParticipante = ?
            """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, participante.getNombreParticipante());
            statement.setString(2, participante.getApellidoParticipante());
            statement.setString(3, participante.getDniParticipante());
            statement.setString(4, participante.getTelefonoParticipante());
            statement.setString(5, participante.getDireccionParticipante());
            statement.setInt(6, participante.getIdParticipante());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar participante.");
            e.printStackTrace();
            return false;
        }
    }

}
