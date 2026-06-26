package model.Auxiliares;

    /*
    Clase auxiliar utilizada para almacenar la última fecha de pago
    asociada a un socio o a una inscripción.
    */

public class UltimoPago {

    private int id;
    private String ultimaFechaPago;

    // Constructores
    public UltimoPago() {
    }
    public UltimoPago(int id, String ultimaFechaPago) {
        this.id = id;
        this.ultimaFechaPago = ultimaFechaPago;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getUltimaFechaPago() {
        return ultimaFechaPago;
    }

}
