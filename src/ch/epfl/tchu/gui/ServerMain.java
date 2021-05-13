package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class ServerMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        Map<PlayerId, String> playerNames;

        switch (parameters.size()) {
            case 0:
                playerNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
                break;
            case 1:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, "Charles");
                break;
            default:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, parameters.get(1));
                break;
        }

        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {

            GraphicalPlayerAdapter graphicalPlayerAdapter = new GraphicalPlayerAdapter();
            RemotePlayerProxy playerProxy = new RemotePlayerProxy(socket);

            Map<PlayerId, Player> players = Map.of(PLAYER_1, graphicalPlayerAdapter, PLAYER_2, playerProxy);

            new Thread(() -> Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random())).start();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

}
