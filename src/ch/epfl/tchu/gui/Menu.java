package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;

public class Menu extends Application {
    public static int number_of_players = 2;
    public static List<PlayerId> activePlayers = PlayerId.ALL.subList(0, number_of_players);

    public static void main(String[] args) {
        Platform.runLater(()-> ServerMain.main(new String[0]));

        Platform.runLater(()->ClientMain.main(new String[0]));
    }

    @Override
    public void start(Stage primaryStage) {
        new Thread(()->  new ServerMain().start(primaryStage)).start();
        for (int i = 1; i < number_of_players; i++) {
            new Thread(()-> new ClientMain().start(primaryStage)).start();
        }
    }
}
