package model;

public class Participante {

    private int idParticipante;
    private String nombreParticipante;
    private String apellidoParticipante;
    private String dniParticipante;
    private String telefonoParticipante;
    private String direccionParticipante;
    private String fechaAltaParticipante;

    // Constructores
    public Participante(){}

    public Participante(String nombreParticipante, String apellidoParticipante, String dniParticipante,
                        String telefonoParticipante, String direccionParticipante){
        this.nombreParticipante = nombreParticipante;
        this.apellidoParticipante = apellidoParticipante;
        this.dniParticipante = dniParticipante;
        this.telefonoParticipante = telefonoParticipante;
        this.direccionParticipante = direccionParticipante;
    }

    // Getters y Setters
    public int getIdParticipante() {
        return idParticipante;
    }
    public void setIdParticipante(int idParticipante) {
        this.idParticipante = idParticipante;
    }
    public String getNombreParticipante() {
        return nombreParticipante;
    }
    public void setNombreParticipante(String nombreParticipante) {
        this.nombreParticipante = nombreParticipante;
    }
    public String getApellidoParticipante() {
        return apellidoParticipante;
    }
    public void setApellidoParticipante(String apellidoParticipante) {
        this.apellidoParticipante = apellidoParticipante;
    }
    public String getDniParticipante() {
        return dniParticipante;
    }
    public void setDniParticipante(String dniParticipante) {
        this.dniParticipante = dniParticipante;
    }
    public String getTelefonoParticipante() {
        return telefonoParticipante;
    }
    public void setTelefonoParticipante(String telefonoParticipante) {
        this.telefonoParticipante = telefonoParticipante;
    }
    public String getDireccionParticipante() {
        return direccionParticipante;
    }
    public void setDireccionParticipante(String direccionParticipante) {
        this.direccionParticipante = direccionParticipante;
    }
    public String getFechaAltaParticipante() {
        return fechaAltaParticipante;
    }
    public void setFechaAltaParticipante(String fechaAltaParticipante) {
        this.fechaAltaParticipante = fechaAltaParticipante;
    }

    @Override
    public String toString() {
        return "Participante{" +
                "idParticipante=" + idParticipante +
                ", nombreParticipante='" + nombreParticipante + '\'' +
                ", apellidoParticipante='" + apellidoParticipante + '\'' +
                ", dniParticipante='" + dniParticipante + '\'' +
                ", telefonoParticipante='" + telefonoParticipante + '\'' +
                ", direccionParticipante='" + direccionParticipante + '\'' +
                ", fechaAltaParticipante='" + fechaAltaParticipante + '\'' +
                '}';
    }

}
