package util;

import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;

public class Impresion {

    // Muestra un documento de texto en pantalla y permite imprimirlo o guardarlo como PDF.
    public static void mostrarDocumento(String titulo, String contenido, Window ventana) {
        TextArea areaTexto = new TextArea(contenido);
        areaTexto.setEditable(false);
        areaTexto.setWrapText(true);
        areaTexto.setPrefWidth(420);
        areaTexto.setPrefHeight(300);

        Button btnImprimir = new Button("Imprimir / Guardar PDF");
        VBox contenedor = new VBox(10);
        contenedor.setPadding(new Insets(10));
        contenedor.getChildren().addAll(areaTexto, btnImprimir);

        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.getDialogPane().setContent(contenedor);
        Ventana.aplicarEstiloAlerta(alerta);
        btnImprimir.setOnAction(e -> imprimirTexto(
                titulo,
                contenido,
                alerta.getDialogPane().getScene().getWindow()
        ));
        alerta.showAndWait();
    }

    // Envía el documento a impresión o permite guardarlo como PDF mediante impresora virtual.
    private static void imprimirTexto(String titulo, String contenido, Window ventana) {
        Text texto = new Text(contenido);
        texto.setWrappingWidth(500);
        TextFlow documento = new TextFlow(texto);
        documento.setPrefWidth(520);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            mostrarError("No se encontró una impresora disponible.");
            return;
        }

        job.getJobSettings().setJobName(titulo);
        boolean continuar = job.showPrintDialog(ventana);
        if (continuar) {
            boolean impreso = job.printPage(documento);
            if (impreso) {
                job.endJob();
            } else {
                mostrarError("No se pudo imprimir el documento.");
            }
        }
    }

    // Muestra un mensaje de error al usuario.
    private static void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        Ventana.aplicarEstiloAlerta(alerta);
        alerta.showAndWait();
    }

}