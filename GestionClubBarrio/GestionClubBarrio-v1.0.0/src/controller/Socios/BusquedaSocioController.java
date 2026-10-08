package controller.Socios;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Socio;
import service.ServicioSocio;
import util.Ventana;

import java.io.IOException;
import java.util.List;

public class BusquedaSocioController {

    private ServicioSocio servicioSocio;
    @FXML
    private ComboBox<String> cmbCampo;
    @FXML
    private TextField txtValor;
    @FXML
    private TableView<Socio> tablaResultados;
    @FXML
    private TableColumn<Socio,Integer> colId;
    @FXML
    private TableColumn<Socio,String> colNombre;
    @FXML
    private TableColumn<Socio,String> colApellido;
    @FXML
    private TableColumn<Socio,String> colDni;
    @FXML
    private TableColumn<Socio,String> colTelefono;
    @FXML
    private TableColumn<Socio,String> colDireccion;
    @FXML
    private TableColumn<Socio,String> colFechaAlta;
    @FXML
    private TableColumn<Socio,String> colEstado;

    /* Inicializa el servicio de socios, carga los campos de búsqueda y
       vincula las columnas de la tabla con los atributos de Socio.
    */
    @FXML
    public void initialize() {
        servicioSocio = new ServicioSocio();
        cmbCampo.getItems().addAll("Todos", "ID", "Nombre", "Apellido", "DNI",
                "Teléfono", "Dirección", "Fecha Alta", "Estado");
        cmbCampo.setValue("Todos");
        colId.setCellValueFactory(new PropertyValueFactory<>("idSocio"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreSocio"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellidoSocio"));
        colDni.setCellValueFactory(new PropertyValueFactory<>("dniSocio"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefonoSocio"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccionSocio"));
        colFechaAlta.setCellValueFactory(new PropertyValueFactory<>("fechaAltaSocio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoSocio"));
        tablaResultados.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // Busca socios según el campo y valor ingresados, y muestra los resultados en la tabla.
    @FXML
    private void buscarSocios() {
        String campo = cmbCampo.getValue();
        String valor = txtValor.getText();
        List<Socio> resultados = servicioSocio.consultarSocios(campo, valor);
        tablaResultados.getItems().setAll(resultados);
        tablaResultados.refresh();
    }

    /* Abre la ventana de modificación para el socio seleccionado.
       Al cerrar la ventana, actualiza la tabla de la búsqueda de socios.
     */
    @FXML
    private void abrirModificar() {
        Socio socioSeleccionado = tablaResultados.getSelectionModel().getSelectedItem();
        if (socioSeleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Atención");
            alerta.setHeaderText(null);
            alerta.setContentText("Seleccione un socio.");
            Ventana.aplicarEstiloAlerta(alerta);
            alerta.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/view/Socios/ModificarSocios.fxml"
            ));
            Parent root = loader.load();
            ModificarSocioController controller = loader.getController();
            controller.cargarSocio(socioSeleccionado);
            Stage stage = Ventana.crearVentana(root, "Modificar Socio");
            stage.showAndWait();
            buscarSocios();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
