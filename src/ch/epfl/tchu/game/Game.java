package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import ch.epfl.tchu.game.Route.Level;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Represents a game of tCHu
 * @author Victor Jean Canard-Duchene (326913)
 * @author Anne-Marie Rusu (296098)
 */

public final class Game {
    /**
     * Runs a game of tCHu
     * @param players : the players playing the game
     * @param playerNames : the names of the corresponding players
     * @param tickets : the tickets to be used in the game
     * @param rng : an instance of a random number generator
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

//before the game starts
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);
        Map<PlayerId, Info> infoGenerators = new EnumMap<>(PlayerId.class); //initialized in initializePlayers
        GameState gameState = GameState.initial(tickets, rng);
        Map<PlayerId, Integer> keptTicketNumber = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> namesOfPlayers = Map.copyOf(playerNames);
        Map<PlayerId, Player> playerMap = Map.copyOf(players);

        setup(playerMap, namesOfPlayers, infoGenerators, ticketDeck, gameState, keptTicketNumber);

        //Plays one round first so as to make sure the condition lastTurnBegins() is tested at the right moment
        gameState = nextTurn(playerMap, infoGenerators, gameState, ticketDeck, rng);

        //the actual game starts
        while(!isLastTurn(gameState)){
            gameState = gameState.forNextTurn();
            gameState = nextTurn(playerMap, infoGenerators, gameState, ticketDeck, rng);

        }

        //Last turn begins returned true thus the end of game is activated
        endOfGame(playerMap, namesOfPlayers, infoGenerators, gameState, ticketDeck, rng);

    }

    /**
     * Runs the setup of the game: chooses the first player and distributes the initial tickets and cards
     * @param players : the players playing the game
     * @param playerNames : the names of the corresponding players
     * @param infoGenerators : the information to be communicated to the players
     * @param gameState : the current state of the game
     * @param ticketDeck : the tickets available in the ticket draw pile
     * @param keptTicketNumber : the number of kept tickets the player has chosen
     */
    private static void setup(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,Map<PlayerId, Info> infoGenerators, Deck<Ticket> ticketDeck, GameState gameState, Map<PlayerId, Integer> keptTicketNumber){
    //Initializing players
        players.forEach((playerId, player)->{
            infoGenerators.put(playerId, new Info(playerNames.get(playerId)));
            player.initPlayers(playerId, playerNames);
        });

        receiveInfoForAll(players, infoGenerators.get(gameState.currentPlayerId()).willPlayFirst());
    //Distributing Tickets

        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            PlayerId playerId = entry.getKey();
            Player player = entry.getValue();

            player.setInitialTicketChoice(ticketDeck.topCards(Constants.INITIAL_TICKETS_COUNT));
            ticketDeck = ticketDeck.withoutTopCards(Constants.INITIAL_TICKETS_COUNT);
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            player.updateState(gameState, gameState.playerState(playerId)); //call actual method bc its in the middle of instructions in for each

            SortedBag<Ticket> tickets = player.chooseInitialTickets();
            keptTicketNumber.put(playerId, tickets.size());
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

    /**
     * Runs the next turn of the game
     * @param players : the players playing the game
     * @param infoGenerators : the information to be communicated to the players
     * @param gameState : the current state of the game
     * @param ticketDeck : the tickets available in the ticket draw pile
     * @param rng : an instance of a random number
     * @return the new gameState at the end of the turn
     */
    private static GameState nextTurn(Map<PlayerId, Player> players, Map<PlayerId, Info> infoGenerators, GameState gameState, Deck<Ticket> ticketDeck, Random rng){
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

                    //Building the 3 cards drawn from the deck
                    SortedBag.Builder<Card> drawCardsBuild = new SortedBag.Builder<>();

                    for(int i = 0; i<Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        drawCardsBuild.add(gameState.topCard());
                        gameState = gameState.withoutTopCard();
                    }
                    SortedBag<Card> drawnCards = drawCardsBuild.build();

                    //Calculating additional cost
                    int additionalCost = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

                    //Displaying that cost for all players and the drawn cards
                    receiveInfoForAll(players, infoGenerators.get(currentPlayerId).drewAdditionalCards(drawnCards, additionalCost));

                    PlayerState playerState = gameState.playerState(currentPlayerId);
                    List<SortedBag<Card>> possibleAdditionalCards;

                    if(additionalCost > 0){ //Additional cost is between 1 and 3 (both included)
                        //Cards the player could play
                       possibleAdditionalCards = playerState.possibleAdditionalCards(additionalCost, initialClaimCards, drawnCards);

                        if(possibleAdditionalCards.isEmpty()){ //Player can't play any additional cards
                            receiveInfoForAll(players, infoGenerators.get(currentPlayerId).didNotClaimRoute(claimedRoute));

                        }else{ //The player can play additional cards. Asks the player which set of cards he want to play.
                            SortedBag<Card> tunnelCards = player.chooseAdditionalCards(possibleAdditionalCards);

                            gameState = putInDiscard(gameState, initialClaimCards.union(tunnelCards).union(drawnCards)); //Cards the player played, drawn cards and the additional cards he played
                            gameState = gameState.withClaimedRoute(claimedRoute, tunnelCards); //Claimed route

                            receiveInfoForAll(players, infoGenerators.get(currentPlayerId).claimedRoute(claimedRoute, tunnelCards));
                        }
                    }
                    else{ //No additional cost
                        gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
                        gameState = putInDiscard(gameState, initialClaimCards);
                        receiveInfoForAll(players, infoGenerators.get(currentPlayerId).claimedRoute(claimedRoute, initialClaimCards));
                    }

                }else{
                    gameState = gameState.withClaimedRoute(claimedRoute, initialClaimCards);
                    gameState = putInDiscard(gameState, initialClaimCards);
                    receiveInfoForAll(players, infoGenerators.get(currentPlayerId).claimedRoute(claimedRoute, initialClaimCards));
                }

                break;
        }

        //updateAllStates(players, gameState); //Todo Is this call to updateAllStates() needed ?

        return gameState;

    }

    private static GameState putInDiscard(GameState gameState, SortedBag<Card> discardCards){
        return gameState.withMoreDiscardedCards(discardCards);
    }


    private static boolean isLastTurn(GameState gameState){
        return gameState.lastTurnBegins();

    }

    /**
     * Runs the end of the game : completes the last two turns of the game, then calculates the winner
     * @param players : the players playing the game
     * @param playerNames : the names of the corresponding players
     * @param infoGenerators : the information to be communicated to the players
     * @param gameState : the current state of the game
     * @param ticketDeck : the tickets available in the ticket draw pile
     * @param rng : an instance of a random number
     */
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
