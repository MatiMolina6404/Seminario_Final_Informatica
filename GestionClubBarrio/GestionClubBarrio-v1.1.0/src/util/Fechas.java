package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class Fechas {

    // Constructor privado
    private Fechas() {
    }

    /* Normaliza la fecha de un registro. Si no se indicó ninguna, devuelve la fecha de hoy.
       Si se indicó una, devuelve la misma fecha en formato aaaa-mm-dd.
       Devuelve null si es una fecha inválida o si es posterior a hoy.
    */
    public static String normalizarFechaRegistro(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return LocalDate.now().toString();
        }
        try {
            LocalDate fechaIngresada = LocalDate.parse(fecha.trim());
            return fechaIngresada.isAfter(LocalDate.now()) ? null : fechaIngresada.toString();
        } catch (DateTimeParseException e) {
            return null;
        }
    }

}