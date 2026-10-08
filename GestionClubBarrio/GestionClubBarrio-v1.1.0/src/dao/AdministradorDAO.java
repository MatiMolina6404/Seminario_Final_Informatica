package dao;

import database.ConexionSQLite;
import model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAO {

    // Busca un usuario registrado.
    public Administrador buscarPorUsuario(String usuario) {

        String sql = """
                SELECT idAdministrador, usuario, nombre, apellido,
                       claveHash, salt, rol, fechaCambioClave
                FROM ADMINISTRADOR
                WHERE usuario = ?
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return null;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, usuario);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                Administrador administrador = new Administrador();
                administrador.setIdAdministrador(resultado.getInt("idAdministrador"));
                administrador.setUsuario(resultado.getString("usuario"));
                administrador.setNombre(resultado.getString("nombre"));
                administrador.setApellido(resultado.getString("apellido"));
                administrador.setClaveHash(resultado.getString("claveHash"));
                administrador.setSalt(resultado.getString("salt"));
                administrador.setRol(resultado.getString("rol"));
                administrador.setFechaCambioClave(resultado.getString("fechaCambioClave"));
                return administrador;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el usuario.");
            e.printStackTrace();
        }
        return null;
    }

    // Lista todos los usuarios registrados.
    public List<Administrador> listarAdministradores() {
        List<Administrador> administradores = new ArrayList<>();

        String sql = """
                SELECT idAdministrador, usuario, nombre, apellido,
                       claveHash, salt, rol, fechaCambioClave
                FROM ADMINISTRADOR
                ORDER BY nombre, apellido
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return new ArrayList<>();
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                administradores.add(cargarAdministrador(resultado));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar los usuarios.");
            e.printStackTrace();
        }
        return administradores;
    }

    // Registra un nuevo usuario.
    public boolean registrarAdministrador(Administrador administrador) {

        String sql = """
                INSERT INTO ADMINISTRADOR
                (usuario, nombre, apellido, claveHash, salt, rol, fechaCambioClave)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, administrador.getUsuario());
            statement.setString(2, administrador.getNombre());
            statement.setString(3, administrador.getApellido());
            statement.setString(4, administrador.getClaveHash());
            statement.setString(5, administrador.getSalt());
            statement.setString(6, administrador.getRol());
            statement.setString(7, administrador.getFechaCambioClave());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario.");
            e.printStackTrace();
        }
        return false;
    }

    // Modifica los datos personales y el rol de un usuario.
    public boolean modificarAdministrador(Administrador administrador) {

        String sql = """
                UPDATE ADMINISTRADOR
                SET nombre = ?, apellido = ?, rol = ?
                WHERE idAdministrador = ?
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, administrador.getNombre());
            statement.setString(2, administrador.getApellido());
            statement.setString(3, administrador.getRol());
            statement.setInt(4, administrador.getIdAdministrador());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar el usuario.");
            e.printStackTrace();
        }
        return false;
    }

    // Modifica los datos personales, el rol y la contraseña de un usuario.
    public boolean modificarAdministradorConClave(Administrador administrador) {

        String sql = """
                UPDATE ADMINISTRADOR
                SET nombre = ?, apellido = ?, rol = ?,
                    claveHash = ?, salt = ?, fechaCambioClave = ?
                WHERE idAdministrador = ?
                """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, administrador.getNombre());
            statement.setString(2, administrador.getApellido());
            statement.setString(3, administrador.getRol());
            statement.setString(4, administrador.getClaveHash());
            statement.setString(5, administrador.getSalt());
            statement.setString(6, administrador.getFechaCambioClave());
            statement.setInt(7, administrador.getIdAdministrador());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar el usuario y la contraseña.");
            e.printStackTrace();
        }
        return false;
    }

    // Elimina un usuario registrado por su identificador.
    public boolean eliminarAdministrador(int idAdministrador) {

        String sql = "DELETE FROM ADMINISTRADOR WHERE idAdministrador = ?";

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idAdministrador);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar el usuario.");
            e.printStackTrace();
        }
        return false;
    }

    // Verifica si existe el usuario indicado en la base de datos.
    public boolean existeUsuario(String usuario) {

        String sql = "SELECT 1 FROM ADMINISTRADOR WHERE usuario = ?";

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, usuario);
            ResultSet resultado = statement.executeQuery();
            return resultado.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar el usuario.");
            e.printStackTrace();
        }
        return false;
    }

    // Carga los datos de un administrador a partir del resultado de la consulta.
    private Administrador cargarAdministrador(ResultSet resultado) throws SQLException {
        Administrador administrador = new Administrador();
        administrador.setIdAdministrador(resultado.getInt("idAdministrador"));
        administrador.setUsuario(resultado.getString("usuario"));
        administrador.setNombre(resultado.getString("nombre"));
        administrador.setApellido(resultado.getString("apellido"));
        administrador.setClaveHash(resultado.getString("claveHash"));
        administrador.setSalt(resultado.getString("salt"));
        administrador.setRol(resultado.getString("rol"));
        administrador.setFechaCambioClave(resultado.getString("fechaCambioClave"));
        return administrador;
    }

}