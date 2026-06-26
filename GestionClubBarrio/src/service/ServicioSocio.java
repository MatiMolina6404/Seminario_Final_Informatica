package service;

import dao.SocioDAO;
import model.Socio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioSocio {

    private final SocioDAO socioDAO;

    // Constructor que inicializa el DAO encargado del acceso a datos de socios.
    public ServicioSocio() {
        socioDAO = new SocioDAO();
    }

    // Valida que el socio tenga cargados los datos obligatorios.
    private boolean validarSocio(Socio socio) {
        if (socio == null) {
            return false;}
        if (socio.getNombreSocio() == null || socio.getNombreSocio().isBlank()) {
            return false;}
        if (socio.getApellidoSocio() == null || socio.getApellidoSocio().isBlank()) {
            return false;}
        if (socio.getDniSocio() == null || socio.getDniSocio().isBlank()) {
            return false;}
        if (socio.getTelefonoSocio() == null || socio.getTelefonoSocio().isBlank()) {
            return false;}
        return socio.getDireccionSocio() != null && !socio.getDireccionSocio().isBlank();
    }

    // Registra un nuevo socio si sus datos son válidos y no existe otro socio con el mismo DNI.
    public boolean registrarSocio(Socio socio) {
        if (!validarSocio(socio)) {
            return false;
        }
        limpiarDatosSocio(socio);
        if (socioDAO.existeSocio(socio.getDniSocio(), null)) {
            return false;
        }
        socio.setFechaAltaSocio(LocalDate.now().toString());
        socio.setEstadoSocio("Inactivo");
        return socioDAO.registrarSocio(socio);
    }

    // Consulta socios según el campo y valor indicados.
    public List<Socio> consultarSocios(String campo, String valor) {
        if (campo == null || campo.isBlank()) {
            return new ArrayList<>();
        }
        if (!campo.equals("Todos") && (valor == null || valor.isBlank())) {
            return new ArrayList<>();
        }
        if ("ID".equals(campo) && !valor.matches("\\d+")) {
            return new ArrayList<>();
        }
        return socioDAO.consultarSocios(campo, valor);
    }

    // Busca un socio por DNI y devuelve el resultado si existe.
    public Optional<Socio> buscarPorDni(String dni) {
        List<Socio> socios = consultarSocios("DNI", dni);
        if (socios.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(socios.getFirst());
    }

    /* Modifica un socio existente si los datos son válidos y
       el DNI no se encuentra registrado en otro socio.
    */
    public boolean modificarSocio(Socio socio) {
        if (!validarSocio(socio)) {
            return false;
        }
        limpiarDatosSocio(socio);
        if (socioDAO.existeSocio(socio.getDniSocio(), socio.getIdSocio())) {
            return false;
        }
        return socioDAO.modificarSocio(socio);
    }

    // Devuelve todos los socios registrados para mostrarlos en pantalla.
    public List<Socio> listarSociosPantalla() {
        return socioDAO.listarSocios();
    }

    // Elimina espacios innecesarios al inicio y al final de los datos del socio.
    private void limpiarDatosSocio(Socio socio) {
        socio.setNombreSocio(socio.getNombreSocio().trim());
        socio.setApellidoSocio(socio.getApellidoSocio().trim());
        socio.setDniSocio(socio.getDniSocio().trim());
        socio.setTelefonoSocio(socio.getTelefonoSocio().trim());
        socio.setDireccionSocio(socio.getDireccionSocio().trim());
    }

}
