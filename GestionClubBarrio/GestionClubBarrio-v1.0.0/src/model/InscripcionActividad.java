package model;

public class InscripcionActividad {

    private int idInscripcion;
    private String fechaInscripcion;
    private String estadoInscripcion;
    private Actividad actividad;
    private Socio socio;
    private Participante participante;

    // Constructores
    public InscripcionActividad() {
    }

    public InscripcionActividad(int idInscripcion, String fechaInscripcion,
                                String estadoInscripcion, Actividad actividad) {
        this.idInscripcion = idInscripcion;
        this.fechaInscripcion = fechaInscripcion;
        this.estadoInscripcion = estadoInscripcion;
        this.actividad = actividad;
    }

    // Se devuelven los datos del inscripto, ya sea socio o participante.
    public String getNombre() {
        if (socio != null) {return socio.getNombreSocio();}
        if (participante != null) {return participante.getNombreParticipante();}
        return "";
    }
    public String getApellido() {
        if (socio != null) {return socio.getApellidoSocio();}
        if (participante != null) {return participante.getApellidoParticipante();}
        return "";
    }
    public String getDni() {
        if (socio != null) {return socio.getDniSocio();}
        if (participante != null) {return participante.getDniParticipante();}
        return "";
    }
    public String getTipo() {
        if (socio != null) {
            return "Socio";
        }
        if (participante != null) {
            return "Participante";
        }
        return "";
    }

    // Getters y Setters
    public String getNombreActividad() {
        if (actividad != null) {return actividad.getNombreActividad();}
        return "";
    }
    public int getIdInscripcion() {
        return idInscripcion;
    }
    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }
    public String getFechaInscripcion() {
        return fechaInscripcion;
    }
    public void setFechaInscripcion(String fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }
    public String getEstadoInscripcion() {
        return estadoInscripcion;
    }
    public void setEstadoInscripcion(String estadoInscripcion) {
        this.estadoInscripcion = estadoInscripcion;
    }
    public Actividad getActividad() {
        return actividad;
    }
    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }
    public Socio getSocio() {
        return socio;
    }
    public void setSocio(Socio socio) {
        this.socio = socio;
    }
    public Participante getParticipante() {
        return participante;
    }
    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    @Override
    public String toString() {
        String actividadTexto = "";
        if (actividad != null) {
            actividadTexto = actividad.getNombreActividad();
        }
        String personaTexto = "";
        if (socio != null) {
            personaTexto = socio.getNombreSocio() + " " + socio.getApellidoSocio();
        } else if (participante != null) {
            personaTexto = participante.getNombreParticipante() + " "
                    + participante.getApellidoParticipante();
        }
        return "Inscripción " + idInscripcion + " - " + actividadTexto + " - " + personaTexto;
    }

}
