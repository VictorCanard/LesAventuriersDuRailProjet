package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Node;
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

import java.util.List;


public class Menu extends Application {
    public static int number_of_players = 2;
    public static List<PlayerId> activePlayers = PlayerId.ALL.subList(0, number_of_players);


    public static void main(String[] args) {
       launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        BorderPane mainPane = getBorderPane();

        mainPane.getStylesheets().add("menu.css");

        Scene scene = new Scene(mainPane);


        primaryStage.setResizable(false);
        primaryStage.setTitle("tCHu");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Platform.runLater(()-> ServerMain.main(new String[0]));
    }

    private BorderPane getBorderPane() {
        Text title = new Text("tCHu");
        title.setId("title");

        Button startGame = new Button("Commencer");
        startGame.setId("start-button");

        Node playerNames = makePlayerNamesNode();
        playerNames.setId("player-names");

        HBox center = new HBox();
        Label numberOfPlayers = new Label("Nombre de Joueurs: ");
        ChoiceBox<Integer> choiceBox = new ChoiceBox<>(FXCollections.observableList(List.of(2, 3)));

        center.getChildren().addAll(numberOfPlayers, choiceBox);

        BorderPane mainPane =
                new BorderPane(center, title, startGame, playerNames, null);

        mainPane.setPrefSize(800, 600);
        return mainPane;
    }

    private Node makePlayerNamesNode() {
        VBox vBox = new VBox();

        for (int i = 1; i <= 3; i++) {
            HBox hBox = new HBox();
            Label label = new Label("Joueur "+ i+ ":");
            TextField textField = new TextField();

            hBox.getChildren().addAll(label, textField);
            vBox.getChildren().add(hBox);
        }

        return vBox;
    }
}
