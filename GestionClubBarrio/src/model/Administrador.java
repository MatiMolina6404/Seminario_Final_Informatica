package model;

public class Administrador {

    private int idAdministrador;
    private String usuario;
    private String claveHash;
    private String salt;
    private String rol;
    private String fechaCambioClave;

    // Constructores
    public Administrador() {
    }

    public Administrador(int idAdministrador,
                         String usuario,
                         String claveHash,
                         String salt,
                         String rol,
                         String fechaCambioClave) {
        this.idAdministrador = idAdministrador;
        this.usuario = usuario;
        this.claveHash = claveHash;
        this.salt = salt;
        this.rol = rol;
        this.fechaCambioClave = fechaCambioClave;
    }

    //Getters y Setters
    public int getIdAdministrador() {
        return idAdministrador;
    }
    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getClaveHash() {
        return claveHash;
    }
    public void setClaveHash(String claveHash) {
        this.claveHash = claveHash;
    }
    public String getSalt() {
        return salt;
    }
    public void setSalt(String salt) {
        this.salt = salt;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public String getFechaCambioClave() {
        return fechaCambioClave;
    }
    public void setFechaCambioClave(String fechaCambioClave) {
        this.fechaCambioClave = fechaCambioClave;
    }

}