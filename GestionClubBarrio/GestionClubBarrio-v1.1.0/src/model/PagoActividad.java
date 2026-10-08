package model;

public class PagoActividad extends Pago {

    private String periodoCuotaAct;
    private InscripcionActividad inscripcionActividad;

    // Constructores
    public PagoActividad() {
    }

    // Getters y Setters
    public String getPeriodoCuotaAct() {
        return periodoCuotaAct;
    }
    public void setPeriodoCuotaAct(String periodoCuotaAct) {
        this.periodoCuotaAct = periodoCuotaAct;
    }
    public InscripcionActividad getInscripcionActividad() {
        return inscripcionActividad;
    }
    public void setInscripcionActividad(InscripcionActividad inscripcionActividad) {
        this.inscripcionActividad = inscripcionActividad;
    }
    public String getNombreActividad() {
        if (inscripcionActividad != null && inscripcionActividad.getActividad() != null) {
            return inscripcionActividad.getActividad().getNombreActividad();
        }
        return "";
    }

}