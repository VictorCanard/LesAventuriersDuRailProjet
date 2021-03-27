package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game { //No constructor as the class is only functional; it shouldn't be instantiable
    private static Map<PlayerId, Player> players;
    private static PlayerId firstPlayer;
    private static Map<PlayerId, String> playerNames;
    private static SortedBag<Ticket> ticketOptions;
    private static Map<PlayerId, Info> infoGenerators;


    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        players = Map.copyOf(players);
        playerNames = Map.copyOf(playerNames);
        ticketOptions = tickets;
        infoGenerators = new EnumMap<>(PlayerId.class);

        for (Map.Entry<PlayerId, Player> playerMapEntry: players.entrySet() // Call initPlayers() for both players and initializes their Info generators
             ) {
            PlayerId currentPlayerIdInTheLoop = playerMapEntry.getKey();
            Player currentPlayer  = playerMapEntry.getValue();

            currentPlayer.initPlayers(currentPlayerIdInTheLoop, playerNames);
            infoGenerators.put(currentPlayerIdInTheLoop, new Info(playerNames.get(currentPlayerIdInTheLoop)));
        }
        //ReceiveInfo

        //Do While (!lastTurn())
            //TurnKind
            //switch()
        // CurrentPlayer = GameState.initial().currentPlayerState();

        //Fin du jeu

    }


    private void receiveInfoForAll(String infoToReceive){
        for (Player player: players.values()
             ) {
            player.receiveInfo(infoToReceive);
        }
    }
    private void updateAllStates(PublicGameState newState, Map<PlayerId, PlayerState> bothPlayersOwnStates){
        PlayerId playerOne = firstPlayer;

        for (Player player: players.values()
        ) {
            player.updateState(newState, bothPlayersOwnStates.get(playerOne));
            playerOne = playerOne.next(); //To call the method updateState for playerTwo as well
        }

    }

    private void drawTickets(){
        chooseTickets(ticketOptions);
    }
    private void drawCards(){
        drawSlot();
        drawSlot();

    }
    //Anne-Marie
    private void claimRoute(){

    }

}
