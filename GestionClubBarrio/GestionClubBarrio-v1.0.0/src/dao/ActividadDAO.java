package dao;

import database.ConexionSQLite;
import model.Actividad;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActividadDAO {

    // Registrar actividad
    public boolean registrarActividad(Actividad actividad) {

        String sql = "INSERT INTO ACTIVIDAD (nombreActividad,descripcionActividad) VALUES (?, ?)";
        Connection conexion = ConexionSQLite.conectar();

        if (conexion == null) {
            return false;
        }
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, actividad.getNombreActividad());
            statement.setString(2, actividad.getDescripcionActividad());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar la actividad.");
            e.printStackTrace();
            return false;
        }
    }

    // Verificar si ya existe una actividad
    public boolean existeActividad(String nombreActividad, Integer idActividadExcluir) {

        String sql;
        if (idActividadExcluir == null) {
            sql = "SELECT 1 FROM ACTIVIDAD WHERE UPPER(nombreActividad) = UPPER(?)";
        } else {
            sql = "SELECT 1 FROM ACTIVIDAD WHERE UPPER(nombreActividad) = UPPER(?) AND idActividad <> ?";
        }

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, nombreActividad);
            if (idActividadExcluir != null) {
                statement.setInt(2, idActividadExcluir);
            }
            ResultSet resultado = statement.executeQuery();
            return resultado.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar la actividad.");
            e.printStackTrace();
            return false;
        }
    }

    // Consultar actividades disponibles
    public List<Actividad> consultarActividades(String campo, String valor) {

        List<Actividad> actividades = new ArrayList<>();
        Map<String, String> camposPermitidos = Map.of(
                "ID", "A.idActividad",
                "Nombre", "A.nombreActividad",
                "Descripción", "A.descripcionActividad"
        );
        String sql;

        if (campo.equals("Todos")) {
            sql = """
                SELECT A.idActividad, A.nombreActividad, A.descripcionActividad,
                       COUNT(IA.idInscripcion) AS cantidadParticipantes 
                FROM ACTIVIDAD A
                LEFT JOIN INSCRIPCION_ACTIVIDAD IA
                    ON A.idActividad = IA.idActividad AND IA.estadoInscripcion = 'Activa'
                GROUP BY A.idActividad, A.nombreActividad, A.descripcionActividad
                ORDER BY A.idActividad ASC
                """;
        } else {
            String columna = camposPermitidos.get(campo);
            if (columna == null) {
                return actividades;
            }
            if ("A.idActividad".equals(columna)) {
                sql = """
                    SELECT A.idActividad, A.nombreActividad, A.descripcionActividad,
                           COUNT(IA.idInscripcion) AS cantidadParticipantes
                    FROM ACTIVIDAD A
                    LEFT JOIN INSCRIPCION_ACTIVIDAD IA
                        ON A.idActividad = IA.idActividad AND IA.estadoInscripcion = 'Activa'
                    WHERE A.idActividad = ?
                    GROUP BY A.idActividad, A.nombreActividad, A.descripcionActividad
                    ORDER BY A.idActividad ASC
                    """;
            } else {
                sql = """
                    SELECT A.idActividad, A.nombreActividad, A.descripcionActividad,
                          COUNT(IA.idInscripcion) AS cantidadParticipantes  
                    FROM ACTIVIDAD A
                    LEFT JOIN INSCRIPCION_ACTIVIDAD IA
                        ON A.idActividad = IA.idActividad AND IA.estadoInscripcion = 'Activa'
                    WHERE """ + columna + """
                    LIKE ?
                    GROUP BY A.idActividad, A.nombreActividad, A.descripcionActividad
                    ORDER BY A.idActividad ASC
                    """;
            }
        }

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            if (!campo.equals("Todos")) {
                String columna = camposPermitidos.get(campo);
                if ("A.idActividad".equals(columna)) {
                    statement.setInt(1, Integer.parseInt(valor));
                } else {
                    statement.setString(1, "%" + valor + "%");
                }
            }
            ResultSet resultado = statement.executeQuery();
            while (resultado.next()) {
                Actividad actividad = new Actividad();
                actividad.setIdActividad(resultado.getInt("idActividad"));
                actividad.setNombreActividad(resultado.getString("nombreActividad"));
                actividad.setDescripcionActividad(resultado.getString("descripcionActividad"));
                actividad.setCantidadParticipantes(resultado.getInt("cantidadParticipantes"));
                actividades.add(actividad);
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar actividades.");
            e.printStackTrace();
        }
        return actividades;
    }

    // Modificar datos de una actividad registrada
    public boolean modificarActividad(Actividad actividad) {

        String sql = """ 
            UPDATE ACTIVIDAD SET nombreActividad = ?, descripcionActividad = ?  
            WHERE idActividad = ?         
            """;

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return false;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, actividad.getNombreActividad());
            statement.setString(2, actividad.getDescripcionActividad());
            statement.setInt(3, actividad.getIdActividad());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al modificar actividad.");
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todas las actividades disponibles
    public List<Actividad> listarActividades() {

        List<Actividad> actividades = new ArrayList<>();
        String sql = """
            SELECT A.idActividad, A.nombreActividad, A.descripcionActividad,
                   COUNT(IA.idInscripcion) AS cantidadParticipantes
            FROM ACTIVIDAD A
            LEFT JOIN INSCRIPCION_ACTIVIDAD IA ON A.idActividad = IA.idActividad
                AND IA.estadoInscripcion = 'Activa'
            GROUP BY A.idActividad, A.nombreActividad, A.descripcionActividad
            ORDER BY A.idActividad ASC
            """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                Actividad actividad = new Actividad();
                actividad.setIdActividad(resultado.getInt("idActividad"));
                actividad.setNombreActividad(resultado.getString("nombreActividad"));
                actividad.setDescripcionActividad(resultado.getString("descripcionActividad"));
                actividad.setCantidadParticipantes(resultado.getInt("cantidadParticipantes"));
                actividades.add(actividad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actividades;
    }

    // Obtener  la cantidad de inscriptos por actividad
    public int contarInscriptos(int idActividad) {

        String sql = """
            SELECT COUNT(*) AS cantidad FROM INSCRIPCION_ACTIVIDAD
            WHERE idActividad = ?
            AND estadoInscripcion = 'Activa'
            """;

        try (Connection conexion = ConexionSQLite.conectar();
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setInt(1, idActividad);
            ResultSet resultado = statement.executeQuery();
            if (resultado.next()) {
                return resultado.getInt("cantidad");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}