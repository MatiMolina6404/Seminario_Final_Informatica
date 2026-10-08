package dao;

import database.ConexionSQLite;
import model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO {

    // Buscar un usuario registrado
    public Administrador buscarPorUsuario(String usuario) {

        String sql = """
                SELECT idAdministrador, usuario, claveHash, salt, rol, fechaCambioClave
                FROM ADMINISTRADOR
                WHERE usuario = ?
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, usuario);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                Administrador administrador = new Administrador();
                administrador.setIdAdministrador(resultado.getInt("idAdministrador"));
                administrador.setUsuario(resultado.getString("usuario"));
                administrador.setClaveHash(resultado.getString("claveHash"));
                administrador.setSalt(resultado.getString("salt"));
                administrador.setRol(resultado.getString("rol"));
                administrador.setFechaCambioClave(resultado.getString
                        ("fechaCambioClave"));
                return administrador;
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar el usuario.");
            e.printStackTrace();
        }
        return null;
    }

    // Registrar un nuevo usuario
    public boolean registrarAdministrador(Administrador administrador) {

        String sql = """
                INSERT INTO ADMINISTRADOR (usuario,claveHash,salt,rol,fechaCambioClave)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, administrador.getUsuario());
            statement.setString(2, administrador.getClaveHash());
            statement.setString(3, administrador.getSalt());
            statement.setString(4, administrador.getRol());
            statement.setString(5, administrador.getFechaCambioClave());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar el usuario.");
            e.printStackTrace();
        }
        return false;
    }

    // Establecer una nueva clave para el usuario
    public boolean cambiarClave(Administrador administrador) {

        String sql = """
                UPDATE ADMINISTRADOR
                SET claveHash = ?, salt = ?, fechaCambioClave = ?
                WHERE usuario = ?
                """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, administrador.getClaveHash());
            statement.setString(2, administrador.getSalt());
            statement.setString(3, administrador.getFechaCambioClave());
            statement.setString(4, administrador.getUsuario());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar la contraseña.");
            e.printStackTrace();
        }
        return false;
    }

    // Verificar si existe el usuario indicado en la base de datos
    public boolean existeUsuario(String usuario) {

        String sql = "SELECT 1 FROM ADMINISTRADOR WHERE usuario = ?";

        try (Connection conexion = ConexionSQLite.conectar();
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

}