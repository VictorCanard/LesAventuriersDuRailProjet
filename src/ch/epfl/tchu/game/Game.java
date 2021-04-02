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
        Map<PlayerId, String> namesOfPlayers = Map.copyOf(playerNames);
        Map<PlayerId, Player> playerMap = Map.copyOf(players);

        setup(playerMap, infoGenerators, namesOfPlayers, ticketDeck, gameState, keptTicketNumber);

//the actual game starts

        do{

           nextTurn(playerMap, infoGenerators, gameState, ticketDeck, rng);

        }
        while(!isLastTurn(gameState));

        endOfGame(playerMap, namesOfPlayers, infoGenerators, gameState, ticketDeck, rng);

    }
    private static void setup(Map<PlayerId, Player> players, Map<PlayerId, Info> infoGenerators, Map<PlayerId, String> playerNames, Deck<Ticket> ticketDeck, GameState gameState, Map<PlayerId, Integer> keptTicketNumber){
    //Initializing players
        players.forEach((playerId, player)->{
            infoGenerators.put(playerId, new Info(playerNames.get(playerId)));
            player.initPlayers(playerId, playerNames);
        });

        receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).willPlayFirst());
    //Distributing Tickets

        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            PlayerId key = entry.getKey();
            Player player = entry.getValue();

            player.setInitialTicketChoice(ticketDeck.topCards(Constants.INITIAL_TICKETS_COUNT));
            ticketDeck = ticketDeck.withoutTopCards(Constants.INITIAL_TICKETS_COUNT);

            player.updateState(gameState, gameState.playerState(key)); //call actual method bc its in the middle of instructions in for each

            SortedBag<Ticket> tickets = player.chooseInitialTickets();
            keptTicketNumber.put(key, tickets.size());
        }

        infoGenerators.forEach((playerId, info) -> receiveInfoForAll(players, info.keptTickets(keptTicketNumber.get(playerId))));
    }

    /**
     * Communicated the given information to all the players in the game
     * @param players : the players in the game, associated with their playerIds
     * @param infoToReceive : the information to be communicated to the players
     */
    private static void receiveInfoForAll(Map<PlayerId, Player> players, String infoToReceive){
        players.forEach((playerId, player) -> player.receiveInfo(infoToReceive));
    }

    /**
     * Informs all the players of the updated state of the game and the state of the players
     * @param players : the players in the game, associated with their playerIds
     * @param gameState : the new state of the game
     */
    private static void updateAllStates(Map<PlayerId, Player> players, GameState gameState){
        players.forEach((playerId, player)-> player.updateState(gameState,  gameState.playerState(playerId)));
    }


    private static void nextTurn(Map<PlayerId, Player> players, Map<PlayerId, Info> infoGenerators, GameState gameState, Deck<Ticket> ticketDeck, Random rng){
        updateAllStates(players, gameState);

        PlayerId currentPlayerId = gameState.currentPlayerId();
        Player currentPlayer = players.get(currentPlayerId);
        Player.TurnKind playerChoice = currentPlayer.nextTurn();

        receiveInfoForAll(players, infoGenerators.get(currentPlayerId).canPlay());

        switch (playerChoice){
            case DRAW_CARDS:

                for (int i = 0; i < 2; i++) {
                    gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                    int drawSlot = currentPlayer.drawSlot(); //-1, 0->4

                    if(drawSlot == Constants.DECK_SLOT){
                        //DeckCard
                        gameState = gameState.withBlindlyDrawnCard();
                        receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).drewBlindCard());

                    }
                    else{
                        Card chosenVisibleCard = gameState.cardState().faceUpCard(drawSlot);

                        gameState = gameState.withDrawnFaceUpCard(drawSlot);
                        receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).drewVisibleCard(chosenVisibleCard));
                    }

                    updateAllStates(players, gameState);
                }
                break;
            case DRAW_TICKETS:
                receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).drewTickets(Constants.IN_GAME_TICKETS_COUNT));

                SortedBag<Ticket> ticketOptions = ticketDeck.topCards(Constants.IN_GAME_TICKETS_COUNT);

                SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(ticketOptions);

                receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).keptTickets(keptTickets.size()));

                break;
            case CLAIM_ROUTE:
                Player player = players.get(currentPlayerId);
                Route claimedRoute = player.claimedRoute();
                SortedBag<Card>  initialClaimCards = player.initialClaimCards();


                if(claimedRoute.level() == Level.UNDERGROUND) {
                    receiveInfoForAll(players, infoGenerators.get(currentPlayerId).attemptsTunnelClaim(claimedRoute, initialClaimCards));

                    SortedBag.Builder<Card> drawCardsBuild = new SortedBag.Builder<>();

                    for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        drawCardsBuild.add(gameState.topCard());
                        gameState = gameState.withoutTopCard();
                    }
                    SortedBag<Card> drawnCards = drawCardsBuild.build();

                    int additionalCost = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);
                    receiveInfoForAll(players, infoGenerators.get(currentPlayerId).drewAdditionalCards(drawnCards, additionalCost));

                    PlayerState playerState = gameState.playerState(currentPlayerId);
                    List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>(playerState.possibleAdditionalCards(claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards), initialClaimCards, drawnCards));

                    if(possibleAdditionalCards.isEmpty()){
                        receiveInfoForAll(players, infoGenerators.get(currentPlayerId).didNotClaimRoute(claimedRoute));
                    }else{
                        SortedBag<Card> tunnelCards = player.chooseAdditionalCards(possibleAdditionalCards);
                        gameState = gameState.withClaimedRoute(claimedRoute, tunnelCards);
                        receiveInfoForAll(players, infoGenerators.get(currentPlayerId).claimedRoute(claimedRoute, tunnelCards));
                    }

                }else{
                    gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
                    receiveInfoForAll(players, infoGenerators.get(currentPlayerId).claimedRoute(claimedRoute, initialClaimCards));
                }

                break;
        }
        gameState = gameState.forNextTurn();

        updateAllStates(players, gameState); //Todo Is this call to updateAllStates() needed ?





    }



    private static boolean isLastTurn(GameState gameState){
        return gameState.lastTurnBegins();

    }
    private static void endOfGame(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> infoGenerators, GameState gameState, Deck<Ticket> ticketDeck, Random rng){
        updateAllStates(players, gameState);

        receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).lastTurnBegins(gameState.currentPlayerState().carCount())); //LastTurnBegins

        //One more turn

        nextTurn(players, infoGenerators, gameState, ticketDeck, rng);
        gameState = gameState.forNextTurn();

        nextTurn(players, infoGenerators, gameState, ticketDeck, rng);
        gameState = gameState.forNextTurn();


        //Calculate final points

        Map<PlayerId, Trail> eachPlayerAssociatedTrails = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Integer> associatedPlayerPoints = new EnumMap<>(PlayerId.class);

        GameState finalGameState = gameState;

        players.forEach(((playerId, player) -> {
            //Calculate longest trails
            Trail playerLongestTrail = Trail.longest(finalGameState.playerState(playerId).routes());
            eachPlayerAssociatedTrails.put(playerId, playerLongestTrail);

            //Put final Points (Without the longest trail bonus)
            associatedPlayerPoints.put(playerId, finalGameState.playerState(playerId).finalPoints());


        }));

        updateAllStates(players, gameState);

        PlayerId currentPlayerId = gameState.currentPlayerId();

        Trail trailCurrentPlayer = eachPlayerAssociatedTrails.get(currentPlayerId);
        Trail trailNextPlayer = eachPlayerAssociatedTrails.get(currentPlayerId.next());

        int bonusComparator = Integer.compare(trailCurrentPlayer.length(), trailNextPlayer.length());


        String longestTrailBonus;

        if(bonusComparator > 0){ //Current Player gets the bonus
            longestTrailBonus = infoGenerators.get(currentPlayerId).getsLongestTrailBonus(trailCurrentPlayer);

            associatedPlayerPoints.put(currentPlayerId, associatedPlayerPoints.get(currentPlayerId) + Constants.LONGEST_TRAIL_BONUS_POINTS);

        }else if(bonusComparator < 0){ //Next Player gets the bonus
            longestTrailBonus = infoGenerators.get(currentPlayerId.next()).getsLongestTrailBonus(trailCurrentPlayer);

            associatedPlayerPoints.put(currentPlayerId.next(), associatedPlayerPoints.get(currentPlayerId.next()) + Constants.LONGEST_TRAIL_BONUS_POINTS);

        }else{ //Both Players get the bonus
            longestTrailBonus = String.format("%s \n%s", infoGenerators.get(currentPlayerId).getsLongestTrailBonus(trailCurrentPlayer),
                    infoGenerators.get(currentPlayerId.next()).getsLongestTrailBonus(trailNextPlayer));

            associatedPlayerPoints.put(currentPlayerId, associatedPlayerPoints.get(currentPlayerId) + Constants.LONGEST_TRAIL_BONUS_POINTS);
            associatedPlayerPoints.put(currentPlayerId.next(), associatedPlayerPoints.get(currentPlayerId.next()) + Constants.LONGEST_TRAIL_BONUS_POINTS);

        }

        receiveInfoForAll(players, longestTrailBonus);

        int currentPlayerPoints = associatedPlayerPoints.get(currentPlayerId);
        int nextPlayerPoints = associatedPlayerPoints.get(currentPlayerId.next());

        int whoWonComparator = Integer.compare(currentPlayerPoints, nextPlayerPoints);

        String endOfGameMessage;

        if(whoWonComparator > 0){ //Current Player won
            endOfGameMessage = infoGenerators.get(currentPlayerId).won(currentPlayerPoints, nextPlayerPoints);

        }else if(whoWonComparator < 0){ //Next Player won
            endOfGameMessage = infoGenerators.get(currentPlayerId).won(nextPlayerPoints, currentPlayerPoints);

        }else{ //Both players came to a draw
            endOfGameMessage = Info.draw(new ArrayList<>(playerNames.values()), currentPlayerPoints);

        }

        receiveInfoForAll(players, endOfGameMessage);


    }




}
