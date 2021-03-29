package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game { //No constructor as the class is only functional; it shouldn't be instantiable

    private static Map<PlayerId, Player> players;
    private static Map<PlayerId, String> playerNames;
    private static Map<PlayerId, Info> infoGenerators;
    private static Map<PlayerId, Integer> keptTicketNumber;
    private static PlayerId currentPlayerId;
    private static Deck<Ticket> ticketDeck;
    private static GameState gameState;
    private static Random rng;


    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

//before the game starts
        Game.rng = rng;
        Game.players = Map.copyOf(players);
        Game.playerNames = Map.copyOf(playerNames);
        Game.ticketDeck = Deck.of(tickets, rng);
        Game.infoGenerators = new EnumMap<>(PlayerId.class); //initialized in initializePlayers


        initializePlayers();

        Game.gameState = GameState.initial(tickets, rng);
        Game.currentPlayerId = gameState.currentPlayerId();


        receiveInfoForAll(infoGenerators.get(currentPlayerId).willPlayFirst());

        distributeInitialTickets();
        //already initialized infoGenerators in initializePlayers()
        infoGenerators.forEach((playerId, info) -> receiveInfoForAll(info.keptTickets(keptTicketNumber.get(playerId))));

//the actual game starts



        while(!isLastTurn()) {





            updateAllStates(gameState);
            nextTurn();

        }
        updateAllStates(gameState);
        endOfGame();

    }

    private static void receiveInfoForAll(String infoToReceive){
        players.forEach((playerId, player) -> player.receiveInfo(infoToReceive));
    }

    private static void updateAllStates(PublicGameState newState){
        players.forEach((playerId, player)-> player.updateState(newState,  gameState.playerState(playerId)));
    }


    private static void initializePlayers(){
        players.forEach((playerId, player)->{
            infoGenerators.put(playerId, new Info(playerNames.get(playerId)));
            player.initPlayers(playerId, playerNames);
        });
    }

    private static void distributeInitialTickets() {
        players.forEach((playerId, player)->{
            player.setInitialTicketChoice(ticketDeck.topCards(Constants.INITIAL_TICKETS_COUNT));
            ticketDeck.withoutTopCards(Constants.INITIAL_TICKETS_COUNT);

            player.updateState(gameState, gameState.playerState(playerId)); //call actual method bc its in the middle of instructions in for each

            player.chooseInitialTickets();
            keptTicketNumber.put(playerId, player.chooseInitialTickets().size());
        });
    }

    private static void nextTurn(){

        for (Player player: players.values()){
            Player.TurnKind playerChoice = player.nextTurn();

            switch (playerChoice){
                case DRAW_CARDS:
                    gameState.withCardsDeckRecreatedIfNeeded(rng);
                    drawCards(player);
                    break;
                case DRAW_TICKETS:
                    drawTickets(player);
                    break;
                case CLAIM_ROUTE:
                    gameState.withCardsDeckRecreatedIfNeeded(rng);
                    claimRoute(player);
                    break;
            }
            gameState = gameState.forNextTurn();
            currentPlayerId.next(); //To call the method updateState for playerTwo as well
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
            //receiveInfoForAll();

        }

    }


    private static void drawTickets(Player player){
        SortedBag<Ticket> ticketOptions = ticketDeck.topCards(Constants.IN_GAME_TICKETS_COUNT);
        //update state here?
        player.chooseTickets(ticketOptions);

    }
    private static void drawCards(Player player){
        for (int i = 0; i < 2; i++) {
            //update
            player.drawSlot();
        }


    }

    private static void claimRoute(Player player){
        player.claimedRoute();
        player.initialClaimCards();

        if(player.claimedRoute().level() == Level.UNDERGROUND) {
            //idk if im supposed to do this here
            SortedBag.Builder<Card> additionalCardsBuild = new SortedBag.Builder<>();

            for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                gameState.withCardsDeckRecreatedIfNeeded(new Random());
                additionalCardsBuild.add(gameState.topCard());
                gameState.withoutTopCard();
            }
            SortedBag<Card> additionalCards = additionalCardsBuild.build();



            //player.chooseAdditionalCards(/*list of sorted bags of cards*/);

            //this returns a list of cards but what to put in the first argument
            gameState.playerState(currentPlayerId).possibleAdditionalCards(0, player.initialClaimCards(), additionalCards);

            //if these 3 cards imply the use of additional cards
            //call chooseAdditionalCards with the options of cards they can use
            //but then they can also back out?????????

        }


    }

    private static int calculateTotal(Player player){
//        PlayerState currentPlayerState = gameState.playerState();
//
//
//        return currentPlayerState.finalPoints();
        return 0;

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
