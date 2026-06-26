package dao;
import database.ConexionSQLite;
import model.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SocioDAO {

    /* Registrar un nuevo Socio en la base de datos con sus datos personales
       y su estado administrativo.
     */
    public boolean registrarSocio(Socio socio) {

        String sql = """
                INSERT INTO SOCIO (nombreSocio, apellidoSocio, dniSocio,
                                   telefonoSocio, direccionSocio, fechaAltaSocio, estadoSocio)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
        Connection conexion = ConexionSQLite.conectar();

        if (conexion == null) {
            return false;
        }
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, socio.getNombreSocio());
            statement.setString(2, socio.getApellidoSocio());
            statement.setString(3, socio.getDniSocio());
            statement.setString(4, socio.getTelefonoSocio());
            statement.setString(5, socio.getDireccionSocio());
            statement.setString(6, socio.getFechaAltaSocio());
            statement.setString(7, socio.getEstadoSocio());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar el socio.");
            e.printStackTrace();
            return false;
        }
    }

    // Verifica si ya existe un socio registrado con el mismo DNI.
    public boolean existeSocio(String dni, Integer idSocioExcluir) {

        String sql;
        if (idSocioExcluir == null) {
            sql = "SELECT 1 FROM SOCIO WHERE dniSocio = ?";
        } else {
            sql = "SELECT 1 FROM SOCIO WHERE dniSocio = ? AND idSocio <> ?";
        }

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, dni);
            if (idSocioExcluir != null) {
                statement.setInt(2, idSocioExcluir);
            }
            ResultSet resultado = statement.executeQuery();
            return resultado.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia del socio.");
            e.printStackTrace();
            return false;
        }
    }

    // Consulta socios según el campo seleccionado.
    public List<Socio> consultarSocios(String campo, String valor) {

        List<Socio> socios = new ArrayList<>();
        Map<String, String> camposPermitidos = Map.of(
                "ID", "idSocio",
                "Nombre", "nombreSocio",
                "Apellido", "apellidoSocio",
                "DNI", "dniSocio",
                "Teléfono", "telefonoSocio",
                "Dirección", "direccionSocio",
                "Fecha Alta", "fechaAltaSocio",
                "Estado", "estadoSocio"
        );

        String sql;
        Set<String> busquedaExacta = Set.of("idSocio", "dniSocio", "telefonoSocio", "estadoSocio");
            if (campo.equals("Todos")) {
                sql = "SELECT * FROM SOCIO ORDER BY idSocio DESC";
            } else {
            String columna = camposPermitidos.get(campo);
            if (columna == null) {
                return socios;
            }
            if (busquedaExacta.contains(columna)) {
                sql = "SELECT * FROM SOCIO WHERE " + columna + " = ?";
            } else {
                sql = "SELECT * FROM SOCIO WHERE " + columna + " LIKE ?";
            }
        }

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            if (!campo.equals("Todos")) {
                String columna = camposPermitidos.get(campo);
                if ("idSocio".equals(columna)) {
                    statement.setInt(1, Integer.parseInt(valor));
                } else if (busquedaExacta.contains(columna)) {
                    statement.setString(1, valor);
                } else {
                    statement.setString(1, "%" + valor + "%");
                }
            }
            ResultSet resultado = statement.executeQuery();
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
                socios.add(socio);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar socios.");
            e.printStackTrace();
        }
        return socios;
    }

    // Modifica los datos de un socio existente.
    public boolean modificarSocio(Socio socio) {

        String sql = """
            UPDATE SOCIO
            SET nombreSocio = ?, apellidoSocio = ?, dniSocio = ?, telefonoSocio = ?,
                direccionSocio = ?, estadoSocio = ?
            WHERE idSocio = ?
            """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, socio.getNombreSocio());
            statement.setString(2, socio.getApellidoSocio());
            statement.setString(3, socio.getDniSocio());
            statement.setString(4, socio.getTelefonoSocio());
            statement.setString(5, socio.getDireccionSocio());
            statement.setString(6, socio.getEstadoSocio());
            statement.setInt(7, socio.getIdSocio());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar el socio");
            e.printStackTrace();
            return false;
        }
    }

    // Lista todos los socios registrados, ordenados desde el más reciente al más antiguo.
    public List<Socio> listarSocios() {

        List<Socio> socios = new ArrayList<>();
        String sql = "SELECT * FROM SOCIO ORDER BY idSocio DESC";

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
                socios.add(socio);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar socios");
            e.printStackTrace();
        }
        return socios;
    }

}