package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class TestServer {

    @Test //Todo: Make it so multiple methods can be called as RemotePlayerClient always stops after 1 method call (Don't know if this is intended behavior or not)
    void multipleCallsSucceed(){

    }
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {

            Player playerProxy1 = new RemotePlayerProxy(socket);

            initPlayersWorksProperly(socket, playerProxy1);


            //Player playerProxy2 = new RemotePlayerProxy(socket);

            //var players = Map.of(PLAYER_1, playerProxy1, PLAYER_2, playerProxy2);


            //SortedBag<Ticket> initialTickets = SortedBag.of(ChMap.tickets().subList(0, 6));
            //playerProxy1.setInitialTicketChoice(initialTickets);

            playerProxy1.receiveInfo("This is working correctly as it should !");

            //playerProxy2.initPlayers(PLAYER_2, playerNames);

            //Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random((long) (Math.random()* 1000000L)));
        }
        System.out.println("Server done!");
    }

    @Test
    static void initPlayersWorksProperly(Socket socket, Player playerProxy){
        var playerNames = Map.of(PLAYER_1, "Ada",
            PLAYER_2, "Charles");
        playerProxy.initPlayers(PLAYER_1, playerNames);
    }
}