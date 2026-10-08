package model;

public class Socio {

    private int idSocio;
    private String nombreSocio;
    private String apellidoSocio;
    private String dniSocio;
    private String telefonoSocio;
    private String direccionSocio;
    private String fechaAltaSocio;
    private String estadoSocio;
    private String ultimaFechaPago;

    // Constructores
    public Socio(){}

    public Socio(String nombreSocio, String apellidoSocio, String dniSocio,
                 String telefonoSocio, String direccionSocio) {
        this.nombreSocio = nombreSocio;
        this.apellidoSocio = apellidoSocio;
        this.dniSocio = dniSocio;
        this.telefonoSocio = telefonoSocio;
        this.direccionSocio = direccionSocio;
    }

    // Getters y Setters
    public int getIdSocio() {
        return idSocio;
    }
    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }
    public String getNombreSocio() {
        return nombreSocio;
    }
    public void setNombreSocio(String nombreSocio) {
        this.nombreSocio = nombreSocio;
    }
    public String getApellidoSocio() {
        return apellidoSocio;
    }
    public void setApellidoSocio(String apellidoSocio) {
        this.apellidoSocio = apellidoSocio;
    }
    public String getDniSocio() {
        return dniSocio;
    }
    public void setDniSocio(String dniSocio) {
        this.dniSocio = dniSocio;
    }
    public String getTelefonoSocio() {
        return telefonoSocio;
    }
    public void setTelefonoSocio(String telefonoSocio) {
        this.telefonoSocio = telefonoSocio;
    }
    public String getDireccionSocio() {
        return direccionSocio;
    }
    public void setDireccionSocio(String direccionSocio) {
        this.direccionSocio = direccionSocio;
    }
    public String getFechaAltaSocio() {
        return fechaAltaSocio;
    }
    public void setFechaAltaSocio(String fechaAltaSocio) {
        this.fechaAltaSocio = fechaAltaSocio;
    }
    public String getEstadoSocio() {
        return estadoSocio;
    }
    public void setEstadoSocio(String estadoSocio) {
        this.estadoSocio = estadoSocio;
    }
    public String getUltimaFechaPago() {
        return ultimaFechaPago;
    }
    public void setUltimaFechaPago(String ultimaFechaPago) {
        this.ultimaFechaPago = ultimaFechaPago;
    }

    @Override
    public String toString() {
        return "Socio{" +
                "idSocio=" + idSocio +
                ", nombre='" + nombreSocio + '\'' +
                ", apellido='" + apellidoSocio + '\'' +
                ", dni='" + dniSocio + '\'' +
                ", telefono='" + telefonoSocio + '\'' +
                ", direccion='" + direccionSocio + '\'' +
                ", fechaAlta='" + fechaAltaSocio + '\'' +
                ", estado='" + estadoSocio + '\'' +
                '}';
    }

}
