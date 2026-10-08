package service;

import dao.ParticipanteDAO;
import model.Participante;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioParticipante {

    private final ParticipanteDAO participanteDAO;

    // Constructor que inicializa el DAO encargado del acceso a datos de participantes.
    public ServicioParticipante() {
        participanteDAO = new ParticipanteDAO();
    }

    // Valida que el participante tenga cargados los datos obligatorios.
    private boolean validarParticipante(Participante participante) {
        if (participante == null) {
            return false;}
        if (participante.getNombreParticipante() == null ||
                participante.getNombreParticipante().isBlank()) {
            return false;}
        if (participante.getApellidoParticipante() == null ||
                participante.getApellidoParticipante().isBlank()) {
            return false;}
        if (participante.getDniParticipante() == null ||
                participante.getDniParticipante().isBlank()) {
            return false;}
        if (participante.getTelefonoParticipante() == null ||
                participante.getTelefonoParticipante().isBlank()) {
            return false;}
        return participante.getDireccionParticipante() != null &&
                !participante.getDireccionParticipante().isBlank();
    }

    /* Registra un nuevo participante si sus datos son válidos y
       no existe otro participante con el mismo DNI.
    */
    public boolean registrarParticipante(Participante participante) {
        if (!validarParticipante(participante)) {
            return false;
        }
        limpiarDatosParticipante(participante);
        if (participanteDAO.existeParticipante(
                participante.getDniParticipante(), null)) {
            return false;
        }
        participante.setFechaAltaParticipante(LocalDate.now().toString());
        return participanteDAO.registrarParticipante(participante);
    }

    // Consulta participantes según el campo y valor indicados.
    public List<Participante> consultarParticipantes(String campo, String valor) {
        if (campo == null || campo.isBlank()) {
            return new ArrayList<>();
        }
        if (!campo.equals("Todos") && (valor == null || valor.isBlank())) {
            return new ArrayList<>();
        }
        if ("ID".equals(campo) && !valor.matches("\\d+")) {
            return new ArrayList<>();
        }
        return participanteDAO.consultarParticipantes(campo, valor);
    }

    /* Modifica un participante existente si los datos son válidos y
       el DNI no se encuentra registrado en otro participante.
    */
    public boolean modificarParticipante(Participante participante) {
        if (!validarParticipante(participante)) {
            return false;
        }
        limpiarDatosParticipante(participante);
        if (participanteDAO.existeParticipante(participante.getDniParticipante(),
                participante.getIdParticipante())) {
            return false;
        }
        return participanteDAO.modificarParticipante(participante);
    }

    // Devuelve todos los participantes registrados para mostrarlos en pantalla.
    public List<Participante> listarParticipantesPantalla() {
        return participanteDAO.listarParticipantes();
    }

    // Busca un participante por DNI y devuelve el resultado si existe.
    public Optional<Participante> buscarPorDni(String dni) {
        List<Participante> participantes = consultarParticipantes("DNI", dni);
        if (participantes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(participantes.getFirst());
    }

    // Elimina espacios innecesarios al inicio y al final de los datos del participante.
    private void limpiarDatosParticipante(Participante participante) {
        participante.setNombreParticipante(participante.getNombreParticipante().trim());
        participante.setApellidoParticipante(participante.getApellidoParticipante().trim());
        participante.setDniParticipante(participante.getDniParticipante().trim());
        participante.setTelefonoParticipante(participante.getTelefonoParticipante().trim());
        participante.setDireccionParticipante(participante.getDireccionParticipante().trim());
    }

}