package util;

import dao.ConfiguracionSistemaDAO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.ConfiguracionSistema;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;

public class Ventana {

    private static final String rutaCSS = "/css/estilos.css";
    private static final String rutaEscudoPorDefecto = "/images/escudo.png";
    private static ConfiguracionSistema configuracionCache;

    // Crea una escena JavaFX y le aplica la hoja de estilos CSS y los colores configurados.
    public static Scene crearScene(Parent root) {
        aplicarColores(root);
        Scene scene = new Scene(root);
        URL cssUrl = Ventana.class.getResource(rutaCSS);
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("No se encontró el CSS: " + rutaCSS);
        }
        return scene;
    }

    // Crea una nueva ventana con título, escena, estilos e ícono configurados.
    public static Stage crearVentana(Parent root, String titulo) {
        Stage stage = new Stage();
        Scene scene = crearScene(root);
        configurarStage(stage, titulo);
        stage.setScene(scene);
        stage.setResizable(false);
        return stage;
    }

    // Configura el título y el ícono (escudo del club) de una ventana.
    public static void configurarStage(Stage stage, String titulo) {
        stage.setTitle(titulo);
        Image escudo = cargarEscudo();
        if (escudo != null) {
            stage.getIcons().setAll(escudo);
        }
    }

    // Aplica el estilo CSS, los colores configurados y el escudo del club a las alertas.
    public static void aplicarEstiloAlerta(Dialog<?> alerta) {
        aplicarColores(alerta.getDialogPane());
        URL cssUrl = Ventana.class.getResource(rutaCSS);
        if (cssUrl != null) {
            alerta.getDialogPane().getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("No se encontró el CSS: " + rutaCSS);
        }
        Image escudo = cargarEscudo();
        if (escudo != null) {
            alerta.setOnShown(event -> {
                Stage stage = (Stage) alerta.getDialogPane().getScene().getWindow();
                stage.getIcons().setAll(escudo);
            });
        }
    }

    /* Carga el escudo configurado en el sistema.
       Si la ruta empieza con "/" se lee desde los recursos del proyecto;
       si no, se lee como archivo (por ejemplo "escudo/escudo.png").
       Si no se puede cargar, devuelve el escudo por defecto.
    */
    public static Image cargarEscudo() {
        ConfiguracionSistema configuracion = cargarConfiguracion();
        if (configuracion != null) {
            String ruta = configuracion.getRutaEscudo();
            if (ruta != null && !ruta.isBlank()) {
                Image imagen = null;
                if (ruta.startsWith("/")) {
                    URL url = Ventana.class.getResource(ruta);
                    if (url != null) {
                        imagen = new Image(url.toExternalForm());
                    }
                } else {
                    File archivo = new File(ruta);
                    if (archivo.exists()) {
                        imagen = new Image(archivo.toURI().toString());
                    }
                }
                if (imagen != null && !imagen.isError()) {
                    return imagen;
                }
            }
        }
        URL urlPorDefecto = Ventana.class.getResource(rutaEscudoPorDefecto);
        if (urlPorDefecto == null) {
            System.err.println("No se encontró el escudo: " + rutaEscudoPorDefecto);
            return null;
        }
        return new Image(urlPorDefecto.toExternalForm());
    }

    // Obtiene la configuración actual del sistema.
    public static ConfiguracionSistema cargarConfiguracion() {
        if (configuracionCache == null) {
            configuracionCache = new ConfiguracionSistemaDAO().obtenerConfiguracion();
        }
        return configuracionCache;
    }

    // Obtiene el nombre del sistema configurado.
    public static String obtenerNombreSistema() {
        ConfiguracionSistema configuracion = cargarConfiguracion();
        if (configuracion != null &&
                configuracion.getNombreSistema() != null &&
                !configuracion.getNombreSistema().isBlank()) {
            return configuracion.getNombreSistema();
        }
        return "Sistema de Gestión Administrativa para Clubes de Barrio";
    }

    /* Configura un selector de fecha para que solo permita elegir desde el calendario.
       Bloquea las fechas posteriores a hoy y, si se indica, las anteriores a la fecha "desde".
    */
    public static void limitarFechas(DatePicker selector, LocalDate desde) {
        selector.setEditable(false);
        selector.setDayCellFactory(calendario -> new DateCell() {
            @Override
            public void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setDisable(vacia || fecha.isAfter(LocalDate.now())
                        || (desde != null && fecha.isBefore(desde)));
            }
        });
    }

    // Bloquea las fechas posteriores a hoy.
    public static void bloquearFechasFuturas(DatePicker selector) {
        limitarFechas(selector, null);
    }

    // Configura el selector de fecha utilizado para los registros.
    public static void configurarSelectorFechaRegistro(DatePicker selector) {
        bloquearFechasFuturas(selector);
        selector.setValue(LocalDate.now());
    }

    /* Lee los tres colores configurados y los aplica como variables CSS al nodo indicado.
       Principal: botones y encabezados. Secundario: menú, tablas y campos. Terciario: fondo.
       Si no se puede leer la configuración, se usan los colores por defecto del CSS.
    */
    private static void aplicarColores(Parent nodo) {
        ConfiguracionSistema configuracion = cargarConfiguracion();
        if (configuracion == null) {
            return;
        }
        try {
            String principal = configuracion.getColorPrincipal().replace("0x", "#");
            String secundario = configuracion.getColorMenu().replace("0x", "#");
            String terciario = configuracion.getColorFondo().replace("0x", "#");
            nodo.setStyle(
                    "-color-principal: " + principal + ";"
                            + "-color-secundario: " + secundario + ";"
                            + "-color-terciario: " + terciario + ";"
                            + "-texto-principal: " + colorTexto(principal) + ";"
                            + "-texto-secundario: " + colorTexto(secundario) + ";"
                            + "-texto-terciario: " + colorTexto(terciario) + ";");
        } catch (IllegalArgumentException | NullPointerException e) {
            System.err.println("Los colores guardados no son válidos: " + e.getMessage());
        }
    }

    // Devuelve negro o blanco según qué color de texto se lee mejor sobre el fondo indicado.
    private static String colorTexto(String colorFondo) {
        Color c = Color.web(colorFondo);
        double luminancia = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
        return luminancia > 0.6 ? "black" : "white";
    }

    // Descarta la configuración guardada en memoria para que se vuelva a leer de la base.
    public static void invalidarConfiguracionCache() {
        configuracionCache = null;
    }

}