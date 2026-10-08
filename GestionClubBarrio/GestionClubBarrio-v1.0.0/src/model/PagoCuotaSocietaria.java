package model;

public class PagoCuotaSocietaria extends Pago {

    private String periodoCuotaSoc;
    private Socio socio;

    // Constructores
    public PagoCuotaSocietaria() {
    }

    // Getters y Setters
    public String getPeriodoCuotaSoc() {
        return periodoCuotaSoc;
    }
    public void setPeriodoCuotaSoc(String periodoCuotaSoc) {
        this.periodoCuotaSoc = periodoCuotaSoc;
    }
    public Socio getSocio() {
        return socio;
    }
    public void setSocio(Socio socio) {
        this.socio = socio;
    }
    public String getNombreSocio() {
        if (socio != null) {
            return socio.getNombreSocio();
        }
        return "";
    }
    public String getApellidoSocio() {
        if (socio != null) {
            return socio.getApellidoSocio();
        }
        return "";
    }

}