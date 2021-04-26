package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class TestServer {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);
            //playerProxy.nextTurn();
        }
        System.out.println("Server done!");
    }
}
//todo: problems:

/* the message construction is sometimes good, sometimes switched for the playernames...
issue with r.readLine() in RemotePlayerClient being null :(
i made the try catch in proxy normal bc it was causing problems
 */