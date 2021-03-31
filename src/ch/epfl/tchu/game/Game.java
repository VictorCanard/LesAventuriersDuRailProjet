package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

import java.util.*;

public final class Game { //No constructor as the class is only functional; it shouldn't be instantiable

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

//before the game starts
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);
        Map<PlayerId, Info> infoGenerators = new EnumMap<>(PlayerId.class); //initialized in initializePlayers
        GameState gameState = GameState.initial(tickets, rng);
        Map<PlayerId, Integer> keptTicketNumber = new EnumMap<>(PlayerId.class);

        setup(players, infoGenerators, playerNames, ticketDeck, gameState, keptTicketNumber);

//the actual game starts

        do{

            nextTurn(players, infoGenerators, gameState, ticketDeck);
        }
        while(!isLastTurn(gameState)); //This needs to happen n+1 times, length of the game and one more turn.

        endOfGame(players, gameState);

    }
    private static void setup(Map<PlayerId, Player> players, Map<PlayerId, Info> infoGenerators, Map<PlayerId, String> playerNames, Deck<Ticket> ticketDeck, GameState gameState, Map<PlayerId, Integer> keptTicketNumber){
        initializePlayers(players, playerNames, infoGenerators);

        receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).willPlayFirst());

        distributeInitialTickets(players, ticketDeck, gameState, keptTicketNumber);
        //already initialized infoGenerators in initializePlayers()
        infoGenerators.forEach((playerId, info) -> receiveInfoForAll(players, info.keptTickets(keptTicketNumber.get(playerId))));
    }

    private static void receiveInfoForAll(Map<PlayerId, Player> players, String infoToReceive){
        players.forEach((playerId, player) -> player.receiveInfo(infoToReceive));
    }

    private static void updateAllStates(Map<PlayerId, Player> players, GameState gameState){
        players.forEach((playerId, player)-> player.updateState(gameState,  gameState.playerState(playerId)));
    }



    private static void initializePlayers(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> infoGenerators){
        players.forEach((playerId, player)->{
            infoGenerators.put(playerId, new Info(playerNames.get(playerId)));
            player.initPlayers(playerId, playerNames);
        });
    }

    private static void distributeInitialTickets(Map<PlayerId, Player> players, Deck<Ticket> ticketDeck, GameState gameState, Map<PlayerId, Integer> keptTicketNumber) {
        players.forEach((playerId, player)->{
            player.setInitialTicketChoice(ticketDeck.topCards(Constants.INITIAL_TICKETS_COUNT));
            ticketDeck.withoutTopCards(Constants.INITIAL_TICKETS_COUNT);

            player.updateState(gameState, gameState.playerState(playerId)); //call actual method bc its in the middle of instructions in for each

            player.chooseInitialTickets();
            keptTicketNumber.put(playerId, player.chooseInitialTickets().size());
        });
    }

    private static void nextTurn(Map<PlayerId, Player> players, Map<PlayerId, Info> infoGenerators, GameState gameState, Deck<Ticket> ticketDeck){
        updateAllStates(players, gameState);

        players.forEach((playerId, player) ->{
            //receiveInfoForAll();
            //updateStateForAll()
            Player.TurnKind playerChoice = player.nextTurn();

            switch (playerChoice){
                case DRAW_CARDS:
                    drawCards(player, gameState);
                    break;
                case DRAW_TICKETS:
                    drawTickets(player, ticketDeck);
                    break;
                case CLAIM_ROUTE:
                    claimRoute(players, playerId, gameState, infoGenerators);
                    break;

            }
            gameState.forNextTurn();

        });



    }



    private static boolean isLastTurn(GameState gameState){
        return gameState.lastTurnBegins();

    }
    private static void endOfGame(Map<PlayerId, Player> players, GameState gameState){
        updateAllStates(players, gameState);

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


    private static void drawTickets(Player player, Deck<Ticket> ticketDeck){
        //receiveInfoForAll();
        SortedBag<Ticket> ticketOptions = ticketDeck.topCards(Constants.IN_GAME_TICKETS_COUNT);
        //update state here?
        SortedBag<Ticket> keptTickets = player.chooseTickets(ticketOptions);
        //receiveInfoForAll();
    }
    private static void drawCards(Player player, GameState gameState){
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

    private static void claimRoute(Map<PlayerId, Player> players, PlayerId playerId, GameState gameState, Map<PlayerId, Info> infoGenerators){
        //additional cost,
        Player player = players.get(playerId);
        Route claimedRoute = player.claimedRoute();
        SortedBag<Card>  initialClaimCards = player.initialClaimCards();

        PlayerState playerState = gameState.playerState(playerId);
        // List<SortedBag<Card>> possibleClaimCards = playerState.possibleClaimCards(claimedRoute);


        if(claimedRoute.level() == Level.UNDERGROUND) {

            infoGenerators.get(playerId).attemptsTunnelClaim(claimedRoute, initialClaimCards);

            SortedBag.Builder<Card> drawCardsBuild = new SortedBag.Builder<>();

            for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                gameState = gameState.withCardsDeckRecreatedIfNeeded(new Random());
                drawCardsBuild.add(gameState.topCard());
                gameState = gameState.withoutTopCard();
            }
            SortedBag<Card> drawnCards = drawCardsBuild.build();

            List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards), initialClaimCards, drawnCards);
            //verify if it returns an empty list or not
            player.chooseAdditionalCards(possibleAdditionalCards); //distinction here
        }

        //when you claim a route you:
        //for tunnel: draw cards out of the pioche and into the discard pile
        //take cards from the player and put them in the discard pile
        //add route to players player state and game state





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
    private static int longestTrail(Map<PlayerId, Player> players, GameState gameState){
        Map<PlayerId, Integer> playerIdIntegerMap = new EnumMap<>(PlayerId.class);

        players.forEach((playerId, player) -> {
            PlayerState currentPlayerState = gameState.playerState(playerId);
            int length = Trail.longest(currentPlayerState.routes()).length();
            playerIdIntegerMap.put(playerId, length);

        });


        return Integer.compare(playerIdIntegerMap.get(PlayerId.PLAYER_1), playerIdIntegerMap.get(PlayerId.PLAYER_2));
    }

}
