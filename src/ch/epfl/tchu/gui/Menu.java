package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;


public class Menu extends Application {
    public static int numberOfPlayers = 2;
    public static List<PlayerId> activePlayers = PlayerId.ALL.subList(0, numberOfPlayers);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainPane = getBorderPane(primaryStage);

        mainPane.getStylesheets().add("menu.css");

        Scene scene = new Scene(mainPane);

        primaryStage.setResizable(false);
        primaryStage.setTitle("tCHu");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private BorderPane getBorderPane(Stage primaryStage) {
        Text title = new Text("tCHu");
        title.setId("title");

        HBox center = new HBox();
        Label numberOfPlayers = new Label("Nombre de Joueurs: ");
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>(FXCollections.observableList(List.of(2, 3)));


        //TextField and Box
        VBox vBox = new VBox();
        List<TextField> textFields = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            HBox hBox = new HBox();
            Label label = new Label("Joueur " + i + ":");
            TextField textField = new TextField();
            textField.setDisable(true);

            textFields.add(textField);
            hBox.getChildren().addAll(label, textField);
            vBox.getChildren().add(hBox);
        }

        choiceBox.getSelectionModel().selectedIndexProperty().addListener((p, o, n) -> {
            for (int i = 0; i <= 2; i++) {
                textFields.get(i).setDisable(i - 1 > n.intValue());
            }
        });

        vBox.setId("player-names");

        //
        Button startGame = new Button("Commencer");
        startGame.setId("start-button");
        startGame.disableProperty().bind(Bindings.isNull(choiceBox.valueProperty()));
        startGame.setOnAction(e -> {

            HBox waiting = new HBox();
            Label serverIsStarting = new Label();
            serverIsStarting.setText("Le serveur démarre.\nEn attente de la connexion des autres joueurs.");
            waiting.getChildren().add(serverIsStarting);

            primaryStage.getScene().setRoot(waiting);
            Menu.numberOfPlayers = choiceBox.getValue();
            Menu.activePlayers = PlayerId.ALL.subList(0, Menu.numberOfPlayers);

            ServerMain server = new ServerMain();
            List<String> playerNames = new ArrayList<>();
            for (int i = 0; i < Menu.numberOfPlayers; i++) {
                playerNames.add(textFields.get(i).getText());
            }
            server.setParameters(playerNames);
            server.start(primaryStage);
        });


        center.getChildren().addAll(numberOfPlayers, choiceBox);

        BorderPane mainPane =
                new BorderPane(center, title, startGame, vBox, null);

        mainPane.setPrefSize(800, 600);
        return mainPane;
    }

}
