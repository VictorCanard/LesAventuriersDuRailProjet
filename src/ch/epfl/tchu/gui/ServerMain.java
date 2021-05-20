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
import java.util.*;

import static ch.epfl.tchu.game.PlayerId.*;

/**
 * Main program of a tCHu server
 *
 * @author Anne-Marie Rusu(296098)
 */
public class ServerMain extends Application {
    private final List<Socket> sockets = new ArrayList<>();
    private final Map<PlayerId, String> defaultNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
    private final Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);

    private final Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);

    private final int port = 5108;

    private final int numberOfLocalPlayersOnTheServerMachine = 1;

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

        for (int i = 0; i < COUNT; i++) {
            PlayerId currentId = ALL.get(i);
            String currentName = (i < parameters.size()) ? parameters.get(i) : defaultNames.get(currentId);

            playerNames.put(currentId, currentName);
        }


        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(port);

                sockets.addAll(Collections.nCopies(COUNT - numberOfLocalPlayersOnTheServerMachine, serverSocket.accept()));

                for (int i = 0; i < COUNT; i++) {
                    Player currentPlayer = (i < numberOfLocalPlayersOnTheServerMachine) ?
                            new GraphicalPlayerAdapter() : new RemotePlayerProxy(sockets.get(i - numberOfLocalPlayersOnTheServerMachine));

                    players.put(ALL.get(i), currentPlayer);
                }

                Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());

                serverSocket.close();
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }).start();

    }

}
