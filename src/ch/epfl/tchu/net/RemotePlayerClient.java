package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;

public class RemotePlayerClient {
    private final Player player;
    private final String name;
    private final int port;

    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;
        this.name = name;
        this.port = port;
    }

    public void run(){

    }
}
