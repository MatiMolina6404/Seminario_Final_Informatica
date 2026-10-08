package model.Auxiliares;

    /*
    Clase auxiliar utilizada para representar los datos del historial de pagos.
    Reúne información proveniente de distintos tipos de pagos en una estructura común.
    Su finalidad principal es facilitar la visualización del historial en tablas,
    reportes o consultas del sistema.
    */

public class PagoHistorial {

    private int idPago;
    private String fechaPago;
    private String nombre;
    private String apellido;
    private String concepto;
    private String periodo;
    private String formaPago;
    private double monto;

    // Constructores
    public PagoHistorial() {
    }

    public PagoHistorial(int idPago, String fechaPago, String nombre,
                         String apellido, String concepto, String periodo,
                         String formaPago, double monto) {
        this.idPago = idPago;
        this.fechaPago = fechaPago;
        this.nombre = nombre;
        this.apellido = apellido;
        this.concepto = concepto;
        this.periodo = periodo;
        this.formaPago = formaPago;
        this.monto = monto;
    }

    // Getters y Setters
    public int getIdPago() {
        return idPago;
    }
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }
    public String getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getConcepto() {
        return concepto;
    }
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    public String getPeriodo() {
        return periodo;
    }
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    public String getFormaPago() {
        return formaPago;
    }
    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }

}