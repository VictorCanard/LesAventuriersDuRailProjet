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
    private static GameState gameState;



    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        Game.players = Map.copyOf(players);
        Game.playerNames = Map.copyOf(playerNames);
        Game.ticketOptions = tickets;
        Game.infoGenerators = new EnumMap<>(PlayerId.class);
        Game.gameState = GameState.initial(tickets, rng);
        Game.firstPlayer = gameState.currentPlayerId();

        initial();

        do {
            nextTurn();

        }while(! isLastTurn());

        endOfGame();

    }
    private static void initial(){
        for (Map.Entry<PlayerId, Player> playerMapEntry: players.entrySet() // Call initPlayers() for both players and initializes their Info generators
        ) {
            PlayerId currentPlayerIdInTheLoop = playerMapEntry.getKey();
            Player currentPlayer  = playerMapEntry.getValue();

            infoGenerators.put(currentPlayerIdInTheLoop, new Info(playerNames.get(currentPlayerIdInTheLoop)));
            currentPlayer.initPlayers(currentPlayerIdInTheLoop, playerNames);

        }
    }
    private static void nextTurn(){
        PlayerId playerOne = firstPlayer;

        for (Player player: players.values()
        ) {
            Player.TurnKind playerChoice = player.nextTurn();

            switch (playerChoice){
                case DRAW_CARDS:
                    drawCards(player);
                    break;
                case DRAW_TICKETS:
                    drawTickets(player);
                    break;
                case CLAIM_ROUTE:
                    claimRoute(player);
                    break;

            }
            playerOne = playerOne.next(); //To call the method updateState for playerTwo as well
        }


    }
    private static boolean isLastTurn(){
        return gameState.lastTurnBegins();

    }
    private static void endOfGame(){
        //One more turn

        //Calculate final points
        for (Player player : players.values()
             ) {
            int totalPoints = calculateTotal(player);
            //Get PlayerInfoGenerator and apply method getsLongestTrailBonus();
            receiveInfoForAll();

        }

    }


    private static void receiveInfoForAll(String infoToReceive){
        for (Player player: players.values()
             ) {
            player.receiveInfo(infoToReceive);
        }
    }
    private static void updateAllStates(PublicGameState newState, Map<PlayerId, PlayerState> bothPlayersOwnStates){
        PlayerId playerOne = firstPlayer;

        for (Player player: players.values()
        ) {
            player.updateState(newState, bothPlayersOwnStates.get(playerOne));
            playerOne = playerOne.next(); //To call the method updateState for playerTwo as well
        }

    }

    private static void drawTickets(Player player){
        player.chooseTickets(ticketOptions);
    }
    private static void drawCards(Player player){
        for (int i = 0; i < 2; i++) {
            player.drawSlot();
        }


    }
    //Anne-Marie
    private static void claimRoute(Player player){

    }
    
    private static int calculateTotal(Player player){
        PlayerState currentPlayerState = gameState.playerState();


        return currentPlayerState.finalPoints();

    }

    //Returns a negative int if the second player has a longer trail than the first
    //Returns a positive int if the first player has a longer trail than the second
    //Returns 0 if they both have trails of equal length
    private static int longestTrail(){
        Map<PlayerId, Integer> playerIdIntegerMap = new EnumMap<>(PlayerId.class);

        for (Map.Entry<PlayerId, Player> playerMapEntry : players.entrySet()) {
            PlayerId currentPlayerId = playerMapEntry.getKey();

            PlayerState currentPlayerState = gameState.playerState(currentPlayerId);
            int length = Trail.longest(currentPlayerState.routes()).length();

            playerIdIntegerMap.put(currentPlayerId, length);
        }

        return Integer.compare(playerIdIntegerMap.get(PlayerId.PLAYER_1), playerIdIntegerMap.get(PlayerId.PLAYER_2));
    }

}
