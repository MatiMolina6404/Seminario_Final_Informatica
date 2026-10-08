package model;

public class Actividad {

    private int idActividad;
    private String nombreActividad;
    private String descripcionActividad;
    private int cantidadParticipantes;

    // Constructores
    public Actividad(){}

    public Actividad(int idActividad, String nombreActividad, String descripcionActividad) {
        this.idActividad = idActividad;
        this.nombreActividad = nombreActividad;
        this.descripcionActividad = descripcionActividad;
    }

    // Getters y Setters
    public int getIdActividad() {
        return idActividad;
    }
    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }
    public int getCantidadParticipantes() {
        return cantidadParticipantes;
    }
    public void setCantidadParticipantes(int cantidadParticipantes) {
        this.cantidadParticipantes = cantidadParticipantes;
    }
    public String getNombreActividad() {
        return nombreActividad;
    }
    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }
    public String getDescripcionActividad() {
        return descripcionActividad;
    }
    public void setDescripcionActividad(String descripcionActividad) {
        this.descripcionActividad = descripcionActividad;
    }

    @Override
    public String toString() {
        return nombreActividad;
    }

}
