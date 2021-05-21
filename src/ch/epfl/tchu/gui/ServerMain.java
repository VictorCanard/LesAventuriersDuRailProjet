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
 * @author Anne-Marie Rusu(296098)
 */
public class ServerMain extends Application {


    private final Map<PlayerId, String> defaultNames = Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles", PLAYER_3, "Jacob");

    private final Map<PlayerId, Player> players = new HashMap<>();

    private final List<Socket> sockets = new ArrayList<>();
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
    public void start(Stage primaryStage){
        System.out.println("I was started");
        List<String> parameters = getParameters().getRaw();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (int i = 0; i < Menu.number_of_players; i++) {
            PlayerId currentId = ALL.get(i);
            if(i< parameters.size()){
                playerNames.put(currentId, parameters.get(i));
            }else{
                playerNames.put(currentId, defaultNames.get(currentId));
            }
        }


        new Thread(() -> {
            try{
                ServerSocket serverSocket = new ServerSocket(5108);

                for (int i = 0; i < Menu.number_of_players-1; i++) {
                    sockets.add(serverSocket.accept());
                }

                GraphicalPlayerAdapter graphicalPlayerAdapter = new GraphicalPlayerAdapter();

                players.put(PLAYER_1, graphicalPlayerAdapter);

                for (int i = 1; i < Menu.number_of_players; i++) {
                    players.put(ALL.get(i), new RemotePlayerProxy(sockets.get(i-1)));
                }

                Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());
                serverSocket.close();
            } catch (IOException ioException) {
                throw new UncheckedIOException(ioException);
            }
        }).start();

    }

}
