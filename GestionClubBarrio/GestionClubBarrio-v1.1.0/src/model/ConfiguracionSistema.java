package model;

public class ConfiguracionSistema {

    private int id;
    private String nombreClub;
    private String nombreSistema;
    private String rutaEscudo;
    private String colorFondo;
    private String colorMenu;
    private String colorPrincipal;

    public ConfiguracionSistema() {
    }

    public ConfiguracionSistema(int id, String nombreClub, String nombreSistema,
                                String rutaEscudo, String colorFondo,
                                String colorMenu, String colorPrincipal) {
        this.id = id;
        this.nombreClub = nombreClub;
        this.nombreSistema = nombreSistema;
        this.rutaEscudo = rutaEscudo;
        this.colorFondo = colorFondo;
        this.colorMenu = colorMenu;
        this.colorPrincipal = colorPrincipal;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombreClub() {
        return nombreClub;
    }
    public void setNombreClub(String nombreClub) {
        this.nombreClub = nombreClub;
    }
    public String getNombreSistema() {
        return nombreSistema;
    }
    public void setNombreSistema(String nombreSistema) {
        this.nombreSistema = nombreSistema;
    }
    public String getRutaEscudo() {
        return rutaEscudo;
    }
    public void setRutaEscudo(String rutaEscudo) {
        this.rutaEscudo = rutaEscudo;
    }
    public String getColorFondo() {
        return colorFondo;
    }
    public void setColorFondo(String colorFondo) {
        this.colorFondo = colorFondo;
    }
    public String getColorMenu() {
        return colorMenu;
    }
    public void setColorMenu(String colorMenu) {
        this.colorMenu = colorMenu;
    }
    public String getColorPrincipal() {
        return colorPrincipal;
    }
    public void setColorPrincipal(String colorPrincipal) {
        this.colorPrincipal = colorPrincipal;
    }

}