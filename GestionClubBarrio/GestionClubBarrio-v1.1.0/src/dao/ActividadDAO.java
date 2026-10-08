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

    // Registra una actividad.
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

    // Verifica si ya existe una actividad.
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
        try (conexion; PreparedStatement statement = conexion.prepareStatement(sql)) {
            statement.setString(1, nombreActividad);
            if (idActividadExcluir != null) {
                statement.setInt(2, idActividadExcluir);
            }
            try (ResultSet resultado = statement.executeQuery()) {
                return resultado.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar la actividad.");
            e.printStackTrace();
            return false;
        }
    }

    // Consulta las actividades disponibles.
    public List<Actividad> consultarActividades(String campo, String valor) {
        List<Actividad> actividades = new ArrayList<>();
        Map<String, String> camposPermitidos = Map.of(
                "ID", "A.idActividad",
                "Nombre", "A.nombreActividad",
                "Descripción", "A.descripcionActividad");
        String sql;

        if ("Todos".equals(campo)) {
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
                            ON A.idActividad = IA.idActividad
                            AND IA.estadoInscripcion = 'Activa'
                        WHERE %s LIKE ?
                        GROUP BY A.idActividad, A.nombreActividad, A.descripcionActividad
                        ORDER BY A.idActividad ASC
                        """.formatted(columna);
            }
        }

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return actividades;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql)) {
            if (!campo.equals("Todos")) {
                String columna = camposPermitidos.get(campo);
                if ("A.idActividad".equals(columna)) {
                    try {
                        statement.setInt(1, Integer.parseInt(valor));
                    } catch (NumberFormatException e) {
                        return actividades;
                    }
                } else {
                    statement.setString(1, "%" + valor + "%");
                }
            }
            ResultSet resultado = statement.executeQuery();
            while (resultado.next()) {
                actividades.add(mapearActividad(resultado));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar actividades.");
            e.printStackTrace();
        }
        return actividades;
    }

    // Modifica los datos de una actividad registrada.
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

    // Obtiene todas las actividades disponibles.
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

        Connection conexion = ConexionSQLite.conectar();
        if (conexion == null) {
            return actividades;
        }
        try (conexion;
             PreparedStatement statement = conexion.prepareStatement(sql);
             ResultSet resultado = statement.executeQuery()) {
            while (resultado.next()) {
                actividades.add(mapearActividad(resultado));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar actividades.");
            e.printStackTrace();
        }
        return actividades;
    }

    private Actividad mapearActividad(ResultSet resultado) throws SQLException {
        Actividad actividad = new Actividad();
        actividad.setIdActividad(resultado.getInt("idActividad"));
        actividad.setNombreActividad(resultado.getString("nombreActividad"));
        actividad.setDescripcionActividad(resultado.getString("descripcionActividad"));
        actividad.setCantidadParticipantes(resultado.getInt("cantidadParticipantes"));
        return actividad;
    }

}