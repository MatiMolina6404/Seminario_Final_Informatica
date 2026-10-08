package model.Auxiliares;

    /*
    Clase auxiliar utilizada para configurar los períodos de tolerancia
    que determinan los cambios de estado de socios e inscripciones
    según su situación de pago.

    Estos valores permiten establecer, por ejemplo, después de cuántos días
    un socio pasa a estado deudor o inactivo, y cuándo una inscripción
    a actividad debe considerarse inactiva.
    */

public class ConfiguracionEstados {

    private int diasSocioDeudor;
    private int diasSocioInactivo;
    private int diasInscripcionInactiva;

    // Constructores
    public ConfiguracionEstados() {
    }

    public ConfiguracionEstados(int diasSocioDeudor, int diasSocioInactivo,
                                int diasInscripcionInactiva) {
        this.diasSocioDeudor = diasSocioDeudor;
        this.diasSocioInactivo = diasSocioInactivo;
        this.diasInscripcionInactiva = diasInscripcionInactiva;
    }

    // Getters
    public int getDiasSocioDeudor() {
        return diasSocioDeudor;
    }
    public int getDiasSocioInactivo() {
        return diasSocioInactivo;
    }
    public int getDiasInscripcionInactiva() {
        return diasInscripcionInactiva;
    }

}