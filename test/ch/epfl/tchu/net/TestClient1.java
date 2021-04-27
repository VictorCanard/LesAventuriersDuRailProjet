package ch.epfl.tchu.net;


import ch.epfl.tchu.TestPlayer;
import ch.epfl.tchu.game.ChMap;

public final class TestClient1 {


    public static void main(String[] args) {
        System.out.println("Starting client1!");
        RemotePlayerClient playerClient1 =
                new RemotePlayerClient(new TestPlayer((long) (Math.random() * 10000L), ChMap.routes()),
                        "localhost",
                        5108);
        playerClient1.run();
        System.out.println("Client1 done!");
    }


}