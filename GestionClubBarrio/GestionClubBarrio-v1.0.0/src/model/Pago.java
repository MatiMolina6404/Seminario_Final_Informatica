package model;

public class Pago {

    private int idPago;
    private String fechaPago;
    private double montoPago;
    private String tipoPago;

    // Constructores
    public Pago() {
    }
    /* Constructor utilizado para crear un pago sin id asignado.
       Se usa antes de registrar el pago en la base de datos.
     */
    public Pago(String fechaPago, double montoPago, String tipoPago) {
        this.fechaPago = fechaPago;
        this.montoPago = montoPago;
        this.tipoPago = tipoPago;
    }
    /* Constructor utilizado para crear un pago con id asignado.
       Se usa cuando el pago ya fue recuperado desde la base de datos.
     */
    public Pago(int idPago, String fechaPago, double montoPago, String tipoPago) {
        this.idPago = idPago;
        this.fechaPago = fechaPago;
        this.montoPago = montoPago;
        this.tipoPago = tipoPago;
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
    public double getMontoPago() {
        return montoPago;
    }
    public void setMontoPago(double montoPago) {
        this.montoPago = montoPago;
    }
    public String getTipoPago() {
        return tipoPago;
    }
    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    @Override
    public String toString() {
        return "Pago{" +
                "idPago=" + idPago +
                ", fechaPago='" + fechaPago + '\'' +
                ", montoPago=" + montoPago +
                ", tipoPago='" + tipoPago + '\'' +
                '}';
    }

}