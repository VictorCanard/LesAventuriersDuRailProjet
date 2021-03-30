package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

import java.util.*;

public final class GameV2 { //No constructor as the class is only functional; it shouldn't be instantiable

    private static Map<PlayerId, Player> players;
    private static Map<PlayerId, String> playerNames;
    private static Map<PlayerId, Info> infoGenerators;
    private static Map<PlayerId, Integer> keptTicketNumber;

    private static Deck<Ticket> ticketDeck;
    private static GameState gameState;
    private static Random rng;


    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

//before the game starts
        GameV2.rng = rng;
        GameV2.players = Map.copyOf(players);
        GameV2.playerNames = Map.copyOf(playerNames);
        GameV2.ticketDeck = Deck.of(tickets, rng);
        GameV2.infoGenerators = new EnumMap<>(PlayerId.class); //initialized in initializePlayers
        GameV2.gameState = GameState.initial(tickets, rng);


        setup();

//the actual game starts

        do{

            nextTurn();
        }
        while(!isLastTurn()); //This needs to happen n+1 times, length of the game and one more turn.

        endOfGame();

    }
    private static void setup(){
        initializePlayers();

        receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).willPlayFirst());

        distributeInitialTickets();
        //already initialized infoGenerators in initializePlayers()
        infoGenerators.forEach((playerId, info) -> receiveInfoForAll(info.keptTickets(keptTicketNumber.get(playerId))));
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
        updateAllStates(gameState);

        players.forEach((playerId, player) ->{
            receiveInfoForAll(infoGenerators.get(playerId).canPlay());
            //updateStateForAll()
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
            gameState = gameState.forNextTurn();

        });
    }



    private static boolean isLastTurn(){
        return gameState.lastTurnBegins();

    }
    private static void endOfGame(){
        updateAllStates(gameState);

        receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount()));

        //One more turn

        //Calculate final points

        calculateTotal();
        players.forEach(((playerId, player) -> {
            //int totalPoints =


            //Get PlayerInfoGenerator and apply method getsLongestTrailBonus();
            //receiveInfoForAll();
        }));

        //updateStateForAll()
//        receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).won());
//        receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).draw(playerNames.values(), ));


    }


    private static void drawTickets(Player player){

        receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).drewTickets(Constants.IN_GAME_TICKETS_COUNT));
        SortedBag<Ticket> ticketOptions = ticketDeck.topCards(Constants.IN_GAME_TICKETS_COUNT);
        //update state here?

        SortedBag<Ticket> keptTickets = player.chooseTickets(ticketOptions);

        receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).keptTickets(keptTickets.size()));



    }
    private static void drawCards(Player player){
        for (int i = 0; i < 2; i++) {
            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng); //To avoid the deck from being empty
            int drawSlot = player.drawSlot();

            //updateStateForAll()

            if(drawSlot == -1){
                gameState = gameState.withBlindlyDrawnCard();
                receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).drewBlindCard());
            }
            //drawSlot = 0 to 4
            else{
                Card chosenVisibleCard = gameState.cardState().faceUpCard(drawSlot);

                gameState = gameState.withDrawnFaceUpCard(drawSlot);
                receiveInfoForAll(infoGenerators.get(gameState.currentPlayerId()).drewVisibleCard(chosenVisibleCard));
            }


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
            gameState.playerState(gameState.currentPlayerId()).possibleAdditionalCards(0, player.initialClaimCards(), additionalCards);

            //if these 3 cards imply the use of additional cards
            //call chooseAdditionalCards with the options of cards they can use
            //but then they can also back out?????????

        }


    }

    private static int calculateTotal(){
        TreeSet<Trail> longest = longestTrails();

        return 0;
    }

    //Returns a negative int if the second player has a longer trail than the first
    //Returns a positive int if the first player has a longer trail than the second
    //Returns 0 if they both have trails of equal length
    private static TreeSet<Trail> longestTrails(){
        Set<Trail> trailSet = new TreeSet<>();

        players.forEach((playerId, player)->{
            Trail playerLongestTrail = Trail.longest(gameState.playerState(playerId).routes());
            trailSet.add(playerLongestTrail);
        });

        //return trailSet;
        return null;
    }

}
