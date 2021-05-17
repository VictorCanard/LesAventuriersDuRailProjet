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

import static ch.epfl.tchu.game.PlayerId.*;

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

        switch (parameters.size()) {
            case 0:
                playerNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles", PLAYER_3, "Jacob");
                break;
            case 1:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, "Charles",PLAYER_3, "Jacob");
                break;
            case 2:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, parameters.get(1), PLAYER_3, "Jacob");
                break;
            default:
                playerNames = Map.of(PLAYER_1, parameters.get(0), PLAYER_2, parameters.get(1), PLAYER_3, parameters.get(2));
        }

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(5108);
                 Socket socket1 = serverSocket.accept();
            Socket socket2 = serverSocket.accept()) {

                GraphicalPlayerAdapter graphicalPlayerAdapter = new GraphicalPlayerAdapter();

                RemotePlayerProxy playerProxy1 = new RemotePlayerProxy(socket1);
                RemotePlayerProxy playerProxy2 = new RemotePlayerProxy(socket2);

                Map<PlayerId, Player> players = Map.of(PLAYER_1, graphicalPlayerAdapter, PLAYER_2, playerProxy1, PLAYER_3, playerProxy2);

                Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }).start();

    }

}
