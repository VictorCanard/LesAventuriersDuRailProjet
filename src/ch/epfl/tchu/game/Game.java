package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import java.util.*;

/**
 * Represents a game of tCHu
 * @author Victor Jean Canard-Duchene (326913)
 * @author Anne-Marie Rusu (296098)
 */


public final class Game {
    private final static int NUMBER_OF_PLAYERS = 2;
    /**
     * Represents the information contained in the game, and provides utility methods to go to the game's next turn or check if the last turn begins.
     * This class was created to avoid having >5 arguments in each method that is called by play().
     * Mutable class as the function modifyGameState directly changes AllGameData's gameState attributes.
     * (this avoids the allGameData = allGameData.modifyGameState() assignment which quickly takes up space and makes the program lose in clarity).
     */
    private static class AllGameData {
        private GameState gameState;
        private final Map<PlayerId, Player> players;
        private final Map<PlayerId, String> playerNames;
        private final Map<PlayerId, Info> infoGenerators;
        private final Random rng;

        /**
         * Constructs the group of information contained in the game
         * @param gameState : state of the game at the start
         * @param players : the players playing the game
         * @param playerNames : the names of the corresponding players
         * @param rng : an instance of a random number generator
         */
        private AllGameData(GameState gameState, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> infoGenerators, Random rng) {
            this.gameState = gameState;
            this.players = players;
            this.playerNames = playerNames;
            this.infoGenerators =infoGenerators;
            this.rng = rng;
        }

        /**
         * Directly modifies allGameData's gameState
         * @param newGameState : gameState to affect to the old gameState; this updates allGameData's state
         */
        private  void modifyGameState(GameState newGameState){
            this.gameState = newGameState;
        }

        /**
         * Calls gameState's nextTurn method
         */
        private void forNextTurn(){
            this.gameState = this.gameState.forNextTurn();
        }

        /**
         * Checks to see if last turn is beginning
         * @return true if the last turn begins, else otherwise
         */
        private boolean lastTurnBegins(){
            return this.gameState.lastTurnBegins();
        }
    }

    /**
     * Runs a game of tCHu
     * @param players : the players playing the game
     * @param playerNames : the names of the corresponding players
     * @param tickets : the tickets to be used in the game
     * @param rng : an instance of a random number generator
     * @throws IllegalArgumentException if one of the maps (playerNames or players) doesn't have exactly two pairs as there as two players in the game.
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == NUMBER_OF_PLAYERS 
                && playerNames.size() == NUMBER_OF_PLAYERS);

        //before the game starts

        Map<PlayerId, Info> infoGenerators = initializeInfoGenerators(players, playerNames);
        GameState gameState = GameState.initial(tickets, rng);

        AllGameData allGameData = new AllGameData(gameState, players, playerNames, infoGenerators, rng);

        allGameData.modifyGameState(setup(allGameData));

        //plays one round first so as to make sure the condition lastTurnBegins() is tested at the right moment
        allGameData.modifyGameState(nextTurn(allGameData));

        //the actual game starts
        while(!allGameData.lastTurnBegins()){
            allGameData.forNextTurn();
            allGameData.modifyGameState(nextTurn(allGameData));
        }
        //Last turn begins returned true thus the end of game is activated
        endOfGame(allGameData);
    }

    /**
     * Communicates the given information to all the players in the game
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
     * Initializes the player's information generators
     * @param players : the two players in the game
     * @param playerNames : their names
     * @return a map associating each player id to their information generator
     */
    private static Map<PlayerId, Info> initializeInfoGenerators(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames){
        Map<PlayerId, Info> infoGenerators = new EnumMap<>(PlayerId.class);

        players.forEach(((playerId, player) ->
                infoGenerators.put(playerId, new Info(playerNames.get(playerId)))));

        return infoGenerators;
    }

    /**
     * Runs the setup of the game: chooses the first player and distributes the initial tickets and cards
     * @param allGameData : all of the game's information
     * returns : a game state with the game now set up for playing
     */
    private static GameState setup(AllGameData allGameData){
        Map<PlayerId, Player> players = allGameData.players;
        Map<PlayerId, String> playerNames = allGameData.playerNames;

        players.forEach(((playerId, player) ->
                player.initPlayers(playerId, playerNames)));

        receiveInfoForAll(players, allGameData.infoGenerators
                                            .get(allGameData.gameState.currentPlayerId())
                                            .willPlayFirst());
        allGameData.modifyGameState(distributeTickets(allGameData));

        return allGameData.gameState;
    }

    /**
     * Distributes the initial tickets to the players
     * @param allGameData : all of the game's information
     * @return : a game state with the distributed and chosen tickets
     */
    private static GameState distributeTickets(AllGameData allGameData){
        Map<PlayerId, Integer> keptTicketNumber = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Player> players = allGameData.players;


        for (Player player : players.values()) {
            player.setInitialTicketChoice(allGameData.gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));

            allGameData.modifyGameState(allGameData.gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT));
        }
        updateAllStates(players, allGameData.gameState);

        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            PlayerId playerId = entry.getKey();
            Player player = entry.getValue();

            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets(); //Asks the player to choose tickets from the set of options determined in setInitialTicketChoice

            allGameData.modifyGameState(allGameData.gameState.withInitiallyChosenTickets(playerId, chosenTickets));

            keptTicketNumber.put(playerId, chosenTickets.size());
        }
        allGameData.infoGenerators.forEach((playerId, info) -> receiveInfoForAll(players, info.keptTickets(keptTicketNumber.get(playerId))));

        return allGameData.gameState;
    }

    /**
     * Runs the next turn of the game
     * @param allGameData : all of the game's information
     * @return a game state representing the state at the end of the turn
     */
    private static GameState nextTurn(AllGameData allGameData){
        Map<PlayerId, Player> players = allGameData.players;

        updateAllStates(players, allGameData.gameState);

        PlayerId currentPlayerId = allGameData.gameState.currentPlayerId();
        Player currentPlayer = players.get(currentPlayerId);
        Info currentInfo = allGameData.infoGenerators.get(currentPlayerId);

        receiveInfoForAll(players, currentInfo.canPlay());

        Player.TurnKind playerChoice = currentPlayer.nextTurn();

        switch (playerChoice){
            case DRAW_TICKETS:
                allGameData.modifyGameState(drawTickets(allGameData, currentPlayer, currentInfo));
                break;

            case DRAW_CARDS:
                allGameData.modifyGameState(drawCards(allGameData, currentPlayer, currentInfo));
                break;

            case CLAIM_ROUTE:
                allGameData.modifyGameState(claimRoute(allGameData, currentPlayer, currentInfo));
        }
        return allGameData.gameState;
    }

    /**
     * Makes the current player draw tickets from the ticket deck's top tickets
     * @param allGameData : all of the game's information
     * @param currentPlayer : player whose turn it is currently
     * @param currentInfo : information generator of the current player
     * @return a gameState where the current player has drawn 1 to 3 tickets
     */
    private static GameState drawTickets(AllGameData allGameData, Player currentPlayer, Info currentInfo){
        Map<PlayerId, Player> players = allGameData.players;
        receiveInfoForAll(players, currentInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

        SortedBag<Ticket> ticketOptions = allGameData.gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);

        SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(ticketOptions);

        receiveInfoForAll(players, currentInfo.keptTickets(keptTickets.size()));

        return allGameData.gameState.withChosenAdditionalTickets(ticketOptions, keptTickets);
    }

    /**
     * Makes the current player draw two cards (each of which can be from the deck or the face-up cards)
     * @param allGameData : all of the game's information
     * @param currentPlayer : player whose turn it is currently
     * @param currentInfo : information generator of the current player
     * @return a gameState where the player has drawn two additional cards that have been removed from the deck or the face-up cards
     */
    private static GameState drawCards(AllGameData allGameData, Player currentPlayer, Info currentInfo){
        Map<PlayerId, Player> players = allGameData.players;

        for (int i = 0; i < 2; i++) {
            allGameData.modifyGameState(allGameData.gameState.withCardsDeckRecreatedIfNeeded(allGameData.rng));

            if(i == 1){
                updateAllStates(players, allGameData.gameState);
                //To update all states right before the player chooses a second card to draw
            }
            int drawSlot = currentPlayer.drawSlot();
            //-1 or from 0 to 4

            if(drawSlot == Constants.DECK_SLOT){
                //DeckCard
                allGameData.modifyGameState(allGameData.gameState.withBlindlyDrawnCard());

                receiveInfoForAll(players, currentInfo.drewBlindCard());
            }
            else{
                //Face-up card
                Card chosenVisibleCard = allGameData.gameState.cardState().faceUpCard(drawSlot);

                allGameData.modifyGameState(allGameData.gameState.withDrawnFaceUpCard(drawSlot));

                receiveInfoForAll(players, currentInfo.drewVisibleCard(chosenVisibleCard));
            }
        }
        return allGameData.gameState;
    }

    /**
     * Has the player attempt to claim a certain route. The route's level (UNDERGROUND or OVERGROUND)
     * determines how it will be captured (or attempted to be captured).
     * @param allGameData : all of the game's information
     * @param currentPlayer : player whose turn it is currently
     * @param currentInfo : information generator of the current player
     * @return a game state where the current player has or hasn't claimed a new route with his initial cards.
     */
    private static GameState claimRoute(AllGameData allGameData, Player currentPlayer, Info currentInfo){
        Route claimedRoute = currentPlayer.claimedRoute();
        SortedBag<Card>  initialClaimCards = currentPlayer.initialClaimCards();

        if(claimedRoute.level() == Route.Level.UNDERGROUND) {
            return claimUnderground(allGameData, currentPlayer, currentInfo, claimedRoute, initialClaimCards);
        }
        return claimOverground(allGameData, currentInfo, claimedRoute, initialClaimCards);
    }

    /**
     * Procedure to verify if the player has the additional cards necessary to claim this route.
     * @param allGameData : all of the game's information
     * @param currentInfo : information generator of the current player
     * @param claimedRoute : route that the player has decided to claim
     * @param initialClaimCards : initial cards the player has chosen to attempt capturing this route
     * @return a game state where the player has claimed the route if he had the necessary cards, or where he couldn't/ didn't want to claim it.
     */
    private static GameState claimUnderground(AllGameData allGameData, Player currentPlayer, Info currentInfo, Route claimedRoute, SortedBag<Card> initialClaimCards){
        Map<PlayerId, Player> players = allGameData.players;

        receiveInfoForAll(players, currentInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

        //Building the 3 cards drawn from the deck
        SortedBag.Builder<Card> drawCardsBuild = new SortedBag.Builder<>();

        for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
            allGameData.modifyGameState(allGameData.gameState.withCardsDeckRecreatedIfNeeded(allGameData.rng));

            drawCardsBuild.add(allGameData.gameState.topCard());
            allGameData.modifyGameState(allGameData.gameState.withoutTopCard());
        }
        SortedBag<Card> drawnCards = drawCardsBuild.build();

        //Calculating additional cost
        int additionalCost = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

        //Displaying that cost for all players and the drawn cards
        receiveInfoForAll(players, currentInfo.drewAdditionalCards(drawnCards, additionalCost));

        PlayerState playerState = allGameData.gameState.currentPlayerState();

        if(additionalCost > 0){
            //Additional cost is between 1 and 3 (both included)

            //Cards the player could play
            List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(additionalCost, initialClaimCards, drawnCards);

            if(possibleAdditionalCards.isEmpty()){ //Player can't play any additional cards
                receiveInfoForAll(players, currentInfo.didNotClaimRoute(claimedRoute));

                return allGameData.gameState.withMoreDiscardedCards(drawnCards);

            }else{
                //The player can play additional cards. Asks the player which set of cards he want to play.
                SortedBag<Card> tunnelCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);

                receiveInfoForAll(players, currentInfo.claimedRoute(claimedRoute, initialClaimCards.union(tunnelCards)));

                return allGameData.gameState
                        //Drawn cards are put in the discard
                        .withMoreDiscardedCards(drawnCards)
                        .withClaimedRoute(claimedRoute, initialClaimCards.union(tunnelCards));
            }
        }
        else{
            //No additional cost
            allGameData.modifyGameState(allGameData.gameState.withMoreDiscardedCards(drawnCards));

            return claimOverground(allGameData, currentInfo, claimedRoute, initialClaimCards);
            //In this case the procedure is the same as when claiming an overground route
        }
    }

    /**
     * Has the player claim the overground route
     * @param allGameData : all of the game's information
     * @param currentInfo : information generator of the current player
     * @param claimedRoute : route that the player has decided to claim
     * @param initialClaimCards : initial cards the player has chosen to attempt capturing this route
     * @return a game state where the current player has claimed the route with his initial claim cards.
     */
    private static GameState claimOverground(AllGameData allGameData, Info currentInfo, Route claimedRoute, SortedBag<Card> initialClaimCards){
        Map<PlayerId, Player> players = allGameData.players;

        receiveInfoForAll(players, currentInfo.claimedRoute(claimedRoute, initialClaimCards));

        return allGameData.gameState.withClaimedRoute(claimedRoute, initialClaimCards);
    }
    /**
     * Runs the end of the game:
     * Plays the last two turns of tCHu, then calculates who gets the longest trail bonus and who won
     * in the end or if there has been a draw
     * @param allGameData : all of the game's information
     */
    private static void endOfGame(AllGameData allGameData){
        Map<PlayerId, Player> players = allGameData.players;
        Map<PlayerId, Info> infoGenerators = allGameData.infoGenerators;

        receiveInfoForAll(players, infoGenerators
                                    .get(allGameData.gameState.currentPlayerId())
                                    .lastTurnBegins(allGameData.gameState
                                                    .currentPlayerState()
                                                    .carCount())); //LastTurnBegins

        //One more turn for each player
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            allGameData.modifyGameState(allGameData.gameState.forNextTurn());
            nextTurn(allGameData);
        }

        //Calculate final points
        Map<PlayerId, Integer> associatedPlayerPoints = calculateFinalPoints(allGameData);

        //Calculates who won the game or if the two players came to a draw
        determineWinnerOrDraw(associatedPlayerPoints, allGameData);
    }

    /**
     * Calculates both players' final points (their personal points and then whether or not they obtained the LongestTrailBonus)
     * @param allGameData : all of the game's information
     * @return a map with both player's final points
     */
    private static Map<PlayerId, Integer> calculateFinalPoints(AllGameData allGameData){
        PlayerId currentPlayerId = allGameData.gameState.currentPlayerId();
        PlayerId nextPlayerId = currentPlayerId.next();

        Map<PlayerId, Player> players = allGameData.players;
        Map<PlayerId, Info> infoGenerators = allGameData.infoGenerators;
        Map<PlayerId, Trail> eachPlayerAssociatedTrails = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Integer> associatedPlayerPoints = new EnumMap<>(PlayerId.class);

        players.forEach(((playerId, player) -> {
            //Calculate longest trails
            Trail playerLongestTrail = Trail.longest(allGameData.gameState.playerState(playerId).routes());
            eachPlayerAssociatedTrails.put(playerId, playerLongestTrail);

            //Put final Points (Without the longest trail bonus)
            associatedPlayerPoints.put(playerId, allGameData.gameState.playerState(playerId).finalPoints());
        }));

        updateAllStates(players, allGameData.gameState);

        Trail trailCurrentPlayer = eachPlayerAssociatedTrails.get(currentPlayerId);
        Trail trailNextPlayer = eachPlayerAssociatedTrails.get(currentPlayerId.next());

        int bonusComparator = Integer.compare(trailCurrentPlayer.length(), trailNextPlayer.length());

        String longestTrailBonus;

        if(bonusComparator > 0){ //Current Player gets the bonus
            longestTrailBonus = infoGenerators.get(currentPlayerId).getsLongestTrailBonus(trailCurrentPlayer);

            associatedPlayerPoints.put(currentPlayerId, associatedPlayerPoints.get(currentPlayerId) + Constants.LONGEST_TRAIL_BONUS_POINTS);

        }else if(bonusComparator < 0){ //Next Player gets the bonus
            longestTrailBonus = infoGenerators.get(nextPlayerId).getsLongestTrailBonus(trailCurrentPlayer);

            associatedPlayerPoints.put(nextPlayerId, associatedPlayerPoints.get(nextPlayerId) + Constants.LONGEST_TRAIL_BONUS_POINTS);

        }else{ //Both Players get the bonus
            longestTrailBonus = String.format("%s%s", infoGenerators.get(currentPlayerId).getsLongestTrailBonus(trailCurrentPlayer),
                    infoGenerators.get(nextPlayerId).getsLongestTrailBonus(trailNextPlayer));

            for (PlayerId playerId: PlayerId.values()) {
                associatedPlayerPoints.merge(playerId, Constants.LONGEST_TRAIL_BONUS_POINTS, Integer::sum);  //Adds 10 points to each player's count
            }
        }
        receiveInfoForAll(players, longestTrailBonus);

        return associatedPlayerPoints;
    }

    /**
     * Determines who won the game or if there has been a draw, according to each player's point total.
     * @param allGameData : all of the game's information:
     */
    private static void determineWinnerOrDraw(Map<PlayerId, Integer> associatedPlayerPoints, AllGameData allGameData){
       PlayerId currentPlayerId = allGameData.gameState.currentPlayerId();
        Map<PlayerId, Info> infoGenerators = allGameData.infoGenerators;

        int currentPlayerPoints = associatedPlayerPoints.get(currentPlayerId);
        int nextPlayerPoints = associatedPlayerPoints.get(currentPlayerId.next());

        int whoWonComparator = Integer.compare(currentPlayerPoints, nextPlayerPoints);

        String endOfGameMessage;

        if(whoWonComparator > 0){
            //Current Player won
            endOfGameMessage = infoGenerators.get(currentPlayerId)
                    .won(currentPlayerPoints, nextPlayerPoints);

        }else if(whoWonComparator < 0){
            //Next Player won
            endOfGameMessage = infoGenerators.get(currentPlayerId)
                    .won(nextPlayerPoints, currentPlayerPoints);

        }else{
            //Both players came to a draw
            endOfGameMessage = Info
                    .draw(new ArrayList<>(allGameData.playerNames.values()), currentPlayerPoints);
        }
        receiveInfoForAll(allGameData.players, endOfGameMessage);
    }
}
