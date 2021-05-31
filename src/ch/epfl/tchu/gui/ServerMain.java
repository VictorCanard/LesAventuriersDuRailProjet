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
 * @author Victor Canard-Duchêne (326913)
 */
public class ServerMain extends Application {
    private final List<Socket> sockets = new ArrayList<>();
    private final Map<PlayerId, String> defaultNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
    private final Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);

    private final int localPlayerNumber = 1;
    private final Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);

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
        new Thread(() -> {
            try {
                ServerSocket serverSocket = createSockets();

                createPlayerNames();

                createPlayers();

                Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());

                close(serverSocket);
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }).start();
    }

    private ServerSocket createSockets() throws IOException {
        ServerSocket serverSocket = new ServerSocket(GuiUtils.PORT);
        for (int i = localPlayerNumber; i < Menu.numberOfPlayers; i++) {
            sockets.add(serverSocket.accept());
        }
        return serverSocket;
    }

    private void createPlayerNames() {
        List<String> parameters = getParameters().getRaw();

        for (int i = 0; i < Menu.numberOfPlayers; i++) {
            PlayerId currentId = ALL.get(i);
            if (i < parameters.size()) {
                playerNames.put(currentId, parameters.get(i));
            } else {
                playerNames.put(currentId, defaultNames.get(currentId));
            }
        }
    }

    private void createPlayers() {
        players.put(PLAYER_1, new GraphicalPlayerAdapter());

        for (int i = localPlayerNumber; i < Menu.numberOfPlayers; i++) {
            players.put(ALL.get(i), new RemotePlayerProxy(sockets.get(i - localPlayerNumber)));
        }
    }


    private void close(ServerSocket serverSocket) throws IOException {
        for (Socket socket : sockets
        ) {
            socket.close();
        }
        serverSocket.close();
    }

}
