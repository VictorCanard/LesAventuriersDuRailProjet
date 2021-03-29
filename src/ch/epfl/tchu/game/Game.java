package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

import java.util.*;

public final class Game { //No constructor as the class is only functional; it shouldn't be instantiable

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
        Game.rng = rng;
        Game.players = Map.copyOf(players);
        Game.playerNames = Map.copyOf(playerNames);
        Game.ticketDeck = Deck.of(tickets, rng);
        Game.infoGenerators = new EnumMap<>(PlayerId.class); //initialized in initializePlayers
        Game.gameState = GameState.initial(tickets, rng);


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
            //receiveInfoForAll();
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

        //receiveInfoForAll(); It's last turn
        //One more turn

        //Calculate final points
        players.forEach(((playerId, player) -> {
            int totalPoints = calculateTotal(player);

            //Get PlayerInfoGenerator and apply method getsLongestTrailBonus();
            //receiveInfoForAll();
        }));

        //updateStateForAll()
        //receiveInfoForAll(); The winner or both ex aequo

    }


    private static void drawTickets(Player player){
        //receiveInfoForAll();
        SortedBag<Ticket> ticketOptions = ticketDeck.topCards(Constants.IN_GAME_TICKETS_COUNT);
        //update state here?
        SortedBag<Ticket> keptTickets = player.chooseTickets(ticketOptions);
        //receiveInfoForAll();



    }
    private static void drawCards(Player player){
        for (int i = 0; i < 2; i++) {
            //withCardsDeckRecreatedIfNeeded
            int drawSlot = player.drawSlot();
            if(drawSlot == -1){
                //DeckCard
                gameState = gameState.withBlindlyDrawnCard();
            }
            else{
                gameState = gameState.withDrawnFaceUpCard(drawSlot);
            }

            //receiveInfoForAll(); The player has chosen a card from the deck (-1) or from the FU Cards (0-4)
            //updateStateForAll()
        }


    }

    private static void claimRoute(Player player){
        Route claimedRoute = player.claimedRoute();
        SortedBag<Card>  initialClaimCards = player.initialClaimCards();

        if(player.claimedRoute().level() == Level.UNDERGROUND) {
            //idk if im supposed to do this here
            SortedBag.Builder<Card> additionalCardsBuild = new SortedBag.Builder<>();

            for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                gameState.withCardsDeckRecreatedIfNeeded(new Random());
                additionalCardsBuild.add(gameState.topCard());
                gameState.withoutTopCard();
            }
            SortedBag<Card> additionalCards = additionalCardsBuild.build();


            claimedRoute.additionalClaimCardsCount();
            claimedRoute.possibleClaimCards();
            //player.chooseAdditionalCards(/*list of sorted bags of cards*/);

            //this returns a list of cards but what to put in the first argument
            gameState.playerState(gameState.currentPlayerId()).possibleAdditionalCards(0, player.initialClaimCards(), additionalCards);

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

        players.forEach((playerId, player) -> {
            PlayerState currentPlayerState = gameState.playerState(playerId);
            int length = Trail.longest(currentPlayerState.routes()).length();
            playerIdIntegerMap.put(playerId, length);

        });


        return Integer.compare(playerIdIntegerMap.get(PlayerId.PLAYER_1), playerIdIntegerMap.get(PlayerId.PLAYER_2));
    }

}
