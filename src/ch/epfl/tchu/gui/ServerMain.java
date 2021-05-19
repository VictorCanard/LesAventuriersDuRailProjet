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

/**
 * Main program of a tCHu server
 * @author Anne-Marie Rusu(296098)
 */
public class ServerMain extends Application {

    /**
     * Launches the application with the given args
     *
     * @param args : args to pass to the launch method
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Determines the names of the players, then creates a new Server Socket.
     * There it blocks execution with .accept() until a client connects to the server.
     * Finally, it creates the players, the first being a Graphical Player and the second a proxy for the Second Player, playing on another machine,
     * before launching the game on a new execution thread.
     *
     * @param primaryStage : unused parameter
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> parameters = getParameters().getRaw();
        Map<PlayerId, String> playerNames;

        String defaultP1 = "Ada";
        String defaultP2 = "Charles";
        int port = 5108;

        switch (parameters.size()) {
            case 0:
                playerNames = Map.of(PLAYER_1, defaultP1, PLAYER_2, defaultP2);
                break;
            case 1:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, defaultP2);
                break;
            default:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, parameters.get(1));
                break;
        }

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port);
                 Socket socket = serverSocket.accept()) {

                GraphicalPlayerAdapter graphicalPlayerAdapter = new GraphicalPlayerAdapter();
                RemotePlayerProxy playerProxy = new RemotePlayerProxy(socket);

                Map<PlayerId, Player> players = Map.of(PLAYER_1, graphicalPlayerAdapter, PLAYER_2, playerProxy);

                Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }).start();

    }

}
