package application;

import Cliente.ClienteMinas;
import LogicaBuscaminas.Dificultad;
import LogicaBuscaminas.Tablero;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class ConnectionFormScene {

    private final Stage stage;

    public ConnectionFormScene(Stage stage){
        this.stage = stage;
    }

    public void mostrar() {

        Label ipLabel = new Label("Dirección IP:");
        TextField ipField = new TextField();
        ipField.setPromptText("Ej. 127.0.0.1");

        Label puertoLabel = new Label("Puerto:");
        TextField puertoField = new TextField();
        puertoField.setPromptText("Ej. 8080");

        Label dificultadLabel = new Label("Dificultad:");
        ComboBox<Dificultad> dificultadBox = new ComboBox<>();
        dificultadBox.getItems().addAll(Dificultad.PRINCIPIANTE,Dificultad.INTERMEDIO,Dificultad.EXPERTO);
        dificultadBox.setValue(Dificultad.PRINCIPIANTE);

        Button enviarBtn = new Button("Enviar");

        enviarBtn.setOnAction(event -> {
            String ip = ipField.getText();
            String puerto = puertoField.getText();
            Dificultad dificultad = dificultadBox.getValue();

            System.out.println("IP: " + ip);
            System.out.println("Puerto: " + puerto);
            System.out.println("Dificultad: " + dificultad);

            ClienteMinas cl = new ClienteMinas();
                if(puerto.matches("\\d+")) {
                    Tablero tablero = cl.conectar(ip, Integer.parseInt(puerto), dificultad);
                    if(tablero != null) {
                        GameScene gameScene = new GameScene(stage, tablero, cl);
                        gameScene.mostrar();
                    }
                }else{
                    System.err.println("El puerto no es un número");
                }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(ipLabel, 0, 0);
        grid.add(ipField, 1, 0);
        grid.add(puertoLabel, 0, 1);
        grid.add(puertoField, 1, 1);
        grid.add(dificultadLabel, 0, 2);
        grid.add(dificultadBox, 1, 2);
        grid.add(enviarBtn, 1, 3);

        Scene scene = new Scene(grid, 300, 200);
        stage.setTitle("Formulario de Conexión");
        stage.setScene(scene);
        stage.show();
    }
}

