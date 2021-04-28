package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.Game.play;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class TestServer {


    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()
        ) {

            Player playerProxy1 = new RemotePlayerProxy(socket);
            //Player playerProxy2 = new RemotePlayerProxy(socket);

            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");

            initPlayersWorksProperly(playerProxy1, playerNames);

            var players = Map.of(PLAYER_1, playerProxy1, PLAYER_2, playerProxy1);


            //SortedBag<Ticket> initialTickets = SortedBag.of(ChMap.tickets().subList(0, 6));
            //playerProxy1.setInitialTicketChoice(initialTickets);


            //playerProxy2.initPlayers(PLAYER_2, playerNames);


            play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random((long) (Math.random() * 2000000000L)));
        }
        System.out.println("Server done!");
    }

    @Test
    static void initPlayersWorksProperly(Player playerProxy, Map<PlayerId, String> playerNames) {
        playerProxy.initPlayers(PLAYER_1, playerNames);
        

    }
}