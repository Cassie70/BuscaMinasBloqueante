package application;

import Cliente.ClienteMinas;
import LogicaBuscaminas.Tablero;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class GameScene {

    private final int TAM = 40;
    private final int MARGEN = 20;
    private final int ESPACIADO = 5;

    private final Stage stage;
    private Tablero tablero;
    private final int filas;
    private final int columnas;
    private final ClienteMinas cl;

    public GameScene(Stage stage, Tablero tablero, ClienteMinas cl) {
        this.stage = stage;
        this.tablero = tablero;
        filas = tablero.getDificultad().getFilas();
        columnas = tablero.getDificultad().getColumnas();
        this.cl = cl;
    }

    public void mostrar() {
        GridPane grid = new GridPane();
        grid.setHgap(ESPACIADO); // Espaciado horizontal
        grid.setVgap(ESPACIADO); // Espaciado vertical
        Button[][] buttonGrid = new Button[filas][columnas];

        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                Button button = new Button();
                button.setPrefSize(TAM, TAM);
                button.setMinSize(TAM, TAM);
                button.setMaxSize(TAM, TAM);
                buttonGrid[fila][col] = button;

                grid.add(button, col, fila);

                int finalFila = fila;
                int finalCol = col;
                button.setOnMouseClicked(e -> {
                    if (!tablero.isDerrota() && !tablero.isVictoria()) {
                        if (e.getButton() == MouseButton.SECONDARY) {
                            Tablero nuevoTablero = cl.alternarBandera(finalFila, finalCol);
                            if (nuevoTablero != null) {
                                tablero = nuevoTablero;
                            }
                            actualizarBotones(buttonGrid);
                        } else if (e.getButton() == MouseButton.PRIMARY) {
                            Tablero nuevoTablero = cl.revelarCasilla(finalFila, finalCol);
                            if (nuevoTablero != null) {
                                tablero = nuevoTablero;
                            }
                            actualizarBotones(buttonGrid);
                        }

                        if (tablero.isVictoria()) {
                            String tiempo = cl.recibirTiempo();
                            mostrarAlertaVictoria(tiempo);
                        }

                        if (tablero.isDerrota()) {
                            String tiempo = cl.recibirTiempo();
                            mostrarAlertaDerrota(tiempo, buttonGrid[finalFila][finalCol]);
                        }
                    }
                });
            }
        }

        int anchoVentana = columnas * TAM + MARGEN + ESPACIADO * (columnas - 1);
        int altoVentana = filas * TAM + MARGEN + ESPACIADO * (filas - 1);

        Scene scene = new Scene(grid, anchoVentana, altoVentana);
        scene.getStylesheets().add("estilos.css");
        stage.setTitle("Buscaminas");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        grid.setStyle("-fx-padding: " + MARGEN / 2 + "px;");

        actualizarBotones(buttonGrid);
    }


    private void mostrarAlertaVictoria(String tiempo) {
        Stage alertStage = new Stage();
        alertStage.setTitle("¡Victoria!");
        alertStage.initModality(Modality.APPLICATION_MODAL);

        VBox dialogPaneContent = new VBox();
        dialogPaneContent.setSpacing(10);
        dialogPaneContent.setAlignment(Pos.CENTER); // Centrar el contenido verticalmente

        Label mensaje = new Label("¡Felicidades, has ganado!");
        Label tiempoLabel = new Label("Tiempo: " + tiempo);
        Label nombrePrompt = new Label("Ingresa tu nombre:");
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre");

        Button btnOk = new Button("OK");
        btnOk.setOnAction(e -> {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) {
                nombre = "unknown";
            }
            System.out.println("Nombre: " + nombre + ", Tiempo: " + tiempo);
            cl.enviarNombre(nombre);
            alertStage.close();
        });

        dialogPaneContent.getChildren().addAll(mensaje, tiempoLabel, nombrePrompt, nombreField, btnOk);

        HBox buttonContainer = new HBox(btnOk);
        buttonContainer.setAlignment(Pos.CENTER);
        dialogPaneContent.getChildren().add(buttonContainer);
        Scene alertScene = new Scene(dialogPaneContent, 300, 150);
        alertStage.setScene(alertScene);
        alertStage.setResizable(false);

        alertStage.setOnCloseRequest(event -> {
            event.consume();
        });
        alertStage.showAndWait();
    }


    private void mostrarAlertaDerrota(String tiempo, Button buttonPerdido) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("¡Derrota!");
        alert.setHeaderText("Lo siento, has perdido.");
        alert.setContentText("Tiempo: " + tiempo);

        alert.getButtonTypes().setAll(ButtonType.OK);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                buttonPerdido.setStyle("-fx-text-fill: red;");
            }
        });
    }

    private void actualizarBotones(Button[][] buttonGrid) {
        for (int fila = 0; fila < filas; fila++) {
            for (int col = 0; col < columnas; col++) {
                if (tablero.tieneBandera(fila, col)) {
                    buttonGrid[fila][col].setText("🚩");
                    buttonGrid[fila][col].setStyle("-fx-text-fill: crimson;");
                } else if (tablero.esVisible(fila, col)) {
                    buttonGrid[fila][col].setStyle("");
                    int valor = tablero.getValor(fila, col);
                    if (valor == -1) {
                        buttonGrid[fila][col].setText("💣");
                    } else if (valor == 0) {
                        buttonGrid[fila][col].setText("");
                    } else {
                        buttonGrid[fila][col].setText(String.valueOf(valor));
                        switch (valor) {
                            case 1: buttonGrid[fila][col].setStyle("-fx-text-fill: blue;"); break;
                            case 2: buttonGrid[fila][col].setStyle("-fx-text-fill: green;"); break;
                            case 3: buttonGrid[fila][col].setStyle("-fx-text-fill: red;"); break;
                            case 4: buttonGrid[fila][col].setStyle("-fx-text-fill: blueviolet;"); break;
                            case 5: buttonGrid[fila][col].setStyle("-fx-text-fill: maroon;"); break;
                            case 6: buttonGrid[fila][col].setStyle("-fx-text-fill: cadetblue;"); break;
                            case 7: buttonGrid[fila][col].setStyle("-fx-text-fill: darkslategrey;"); break;
                            case 8: buttonGrid[fila][col].setStyle("-fx-text-fill: lightslategray;"); break;
                        }
                    }
                    buttonGrid[fila][col].setDisable(true);
                } else {
                    buttonGrid[fila][col].setText("");
                }
            }
        }
    }
}


