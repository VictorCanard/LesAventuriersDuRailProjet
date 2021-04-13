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
     * Represents the information contained in the game
     */
    private static class GameData {
        private GameState gameState;
        private final Map<PlayerId, Player> players;
        private final Map<PlayerId, String> playerNames;
        private final Map<PlayerId, Info> infoGenerators;
        private final Random rng;

        private GameData(GameState gameState, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> infoGenerators, Random rng) {
            this.gameState = gameState;
            this.players = players;
            this.playerNames = playerNames;
            this.infoGenerators =infoGenerators;
            this.rng = rng;
        }

        private  void modifyGameState(GameState newGameState){
            this.gameState = newGameState;
        }

        private void forNextTurn(){
            this.gameState = this.gameState.forNextTurn();
        }
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

        GameData gameData = new GameData(gameState, players, playerNames, infoGenerators, rng);

        gameData.modifyGameState(setup(gameData));

        //plays one round first so as to make sure the condition lastTurnBegins() is tested at the right moment
        gameData.modifyGameState(nextTurn(gameData));

        //the actual game starts
        while(!gameData.lastTurnBegins()){
            gameData.forNextTurn();
            gameData.modifyGameState(nextTurn(gameData));
        }
        //Last turn begins returned true thus the end of game is activated
        endOfGame(gameData);
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
     * @param gameData : all of the game's information : all of the game's information
     * returns :
     */
    private static GameState setup(GameData gameData){
        Map<PlayerId, Player> players = gameData.players;
        Map<PlayerId, String> playerNames = gameData.playerNames;

        players.forEach(((playerId, player) ->
                player.initPlayers(playerId, playerNames)));

        receiveInfoForAll(players, gameData.infoGenerators
                                            .get(gameData.gameState.currentPlayerId())
                                            .willPlayFirst());
        gameData.modifyGameState(distributeTickets(gameData));

        return gameData.gameState;
    }

    /**
     *
     * @param gameData : all of the game's information : all of the game's information all of the game's information
     * @return :
     */
    private static GameState distributeTickets(GameData gameData){
        Map<PlayerId, Integer> keptTicketNumber = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Player> players = gameData.players;


        for (Player player : players.values()) {
            player.setInitialTicketChoice(gameData.gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));

            gameData.modifyGameState(gameData.gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT));
        }
        updateAllStates(players, gameData.gameState);

        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            PlayerId playerId = entry.getKey();
            Player player = entry.getValue();

            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets(); //Asks the player to choose tickets from the set of options determined in setInitialTicketChoice

            gameData.modifyGameState(gameData.gameState.withInitiallyChosenTickets(playerId, chosenTickets));

            keptTicketNumber.put(playerId, chosenTickets.size());
        }
        gameData.infoGenerators.forEach((playerId, info) -> receiveInfoForAll(players, info.keptTickets(keptTicketNumber.get(playerId))));

        return gameData.gameState;
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
     * @param gameData : all of the game's information:
     * @return the new gameState at the end of the turn
     */
    private static GameState nextTurn(GameData gameData){
        Map<PlayerId, Player> players = gameData.players;

        updateAllStates(players, gameData.gameState);

        PlayerId currentPlayerId = gameData.gameState.currentPlayerId();
        Player currentPlayer = players.get(currentPlayerId);
        Info currentInfo = gameData.infoGenerators.get(currentPlayerId);

        receiveInfoForAll(players, currentInfo.canPlay());

        Player.TurnKind playerChoice = currentPlayer.nextTurn();

        switch (playerChoice){
            case DRAW_TICKETS:
                gameData.modifyGameState(drawTickets(gameData, currentPlayer, currentInfo));
                break;

            case DRAW_CARDS:
                gameData.modifyGameState(drawCards(gameData, currentPlayer, currentInfo));
                break;

            case CLAIM_ROUTE:
                gameData.modifyGameState(claimRoute(gameData, currentPlayer, currentInfo));
        }
        return gameData.gameState;
    }

    /**
     * Makes the current player draw tickets from the ticket deck's top tickets
     * @param gameData : all of the game's information
     * @param currentPlayer : player whose turn it is currently
     * @param currentInfo : information generator of the current player
     * @return a gameState where the current player has drawn 1 to 3 tickets
     */
    private static GameState drawTickets(GameData gameData, Player currentPlayer, Info currentInfo){
        Map<PlayerId, Player> players = gameData.players;
        receiveInfoForAll(players, currentInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

        SortedBag<Ticket> ticketOptions = gameData.gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);

        SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(ticketOptions);

        receiveInfoForAll(players, currentInfo.keptTickets(keptTickets.size()));

        return gameData.gameState.withChosenAdditionalTickets(ticketOptions, keptTickets);
    }

    /**
     * Makes the current player draw two cards (each of which can be from the deck or the face-up cards)
     * @param gameData : all of the game's information
     * @param currentPlayer : player whose turn it is currently
     * @param currentInfo : information generator of the current player
     * @return a gameState where the player has drawn two additional cards that have been removed from the deck or the face-up cards
     */
    private static GameState drawCards(GameData gameData, Player currentPlayer, Info currentInfo){
        Map<PlayerId, Player> players = gameData.players;

        for (int i = 0; i < 2; i++) {
            gameData.modifyGameState(gameData.gameState.withCardsDeckRecreatedIfNeeded(gameData.rng));

            if(i == 1){
                updateAllStates(players, gameData.gameState);
                //To update all states right before the player chooses a second card to draw
            }
            int drawSlot = currentPlayer.drawSlot();
            //-1 or from 0 to 4

            if(drawSlot == Constants.DECK_SLOT){
                //DeckCard
                gameData.modifyGameState(gameData.gameState.withBlindlyDrawnCard());

                receiveInfoForAll(players, currentInfo.drewBlindCard());
            }
            else{
                //Face-up card
                Card chosenVisibleCard = gameData.gameState.cardState().faceUpCard(drawSlot);

                gameData.modifyGameState(gameData.gameState.withDrawnFaceUpCard(drawSlot));

                receiveInfoForAll(players, currentInfo.drewVisibleCard(chosenVisibleCard));
            }
        }
        return gameData.gameState;

    }

    /**
     * Has the player attempt to claim a certain route. The route's level (UNDERGROUND or OVERGROUND)
     * determines how it will be captured (or attempted to be captured).
     * @param gameData : all of the game's information : all of the game's information
     * @param currentPlayer : player whose turn it is currently
     * @param currentInfo : information generator of the current player
     * @return a gameState where the current player has or hasn't claimed a new route with his initial cards.
     */
    private static GameState claimRoute(GameData gameData, Player currentPlayer, Info currentInfo){
        Route claimedRoute = currentPlayer.claimedRoute();
        SortedBag<Card>  initialClaimCards = currentPlayer.initialClaimCards();

        if(claimedRoute.level() == Route.Level.UNDERGROUND) {
            return claimUnderground(gameData, currentPlayer, currentInfo, claimedRoute, initialClaimCards);
        }
        //Overground route
        return claimOverground(gameData, currentInfo, claimedRoute, initialClaimCards);

    }

    /**
     * Procedure to verify if the player has the additional cards necessary to claim this route.
     * @param gameData : all of the game's information
     * @param currentInfo : information generator of the current player
     * @param claimedRoute : route that the player has decided to claim
     * @param initialClaimCards : initial cards the player has chosen to attempt capturing this route
     * @return a gameState where the player has claimed the route if he had the necessary cards, or where he couldn't/ didn't want to claim it.
     */
    private static GameState claimUnderground(GameData gameData, Player currentPlayer, Info currentInfo, Route claimedRoute, SortedBag<Card> initialClaimCards){
        Map<PlayerId, Player> players = gameData.players;

        receiveInfoForAll(players, currentInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

        //Building the 3 cards drawn from the deck
        SortedBag.Builder<Card> drawCardsBuild = new SortedBag.Builder<>();

        for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
            gameData.modifyGameState(gameData.gameState.withCardsDeckRecreatedIfNeeded(gameData.rng));

            drawCardsBuild.add(gameData.gameState.topCard());
            gameData.modifyGameState(gameData.gameState.withoutTopCard());
        }
        SortedBag<Card> drawnCards = drawCardsBuild.build();

        //Calculating additional cost
        int additionalCost = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

        //Displaying that cost for all players and the drawn cards
        receiveInfoForAll(players, currentInfo.drewAdditionalCards(drawnCards, additionalCost));

        PlayerState playerState = gameData.gameState.currentPlayerState();

        if(additionalCost > 0){
            //Additional cost is between 1 and 3 (both included)

            //Cards the player could play
            List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(additionalCost, initialClaimCards, drawnCards);

            if(possibleAdditionalCards.isEmpty()){ //Player can't play any additional cards
                receiveInfoForAll(players, currentInfo.didNotClaimRoute(claimedRoute));

                return gameData.gameState.withMoreDiscardedCards(drawnCards);

            }else{
                //The player can play additional cards. Asks the player which set of cards he want to play.
                SortedBag<Card> tunnelCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);

                receiveInfoForAll(players, currentInfo.claimedRoute(claimedRoute, initialClaimCards.union(tunnelCards)));

                return gameData.gameState
                        .withMoreDiscardedCards(drawnCards)
                        //Drawn cards are put in the discard
                        .withClaimedRoute(claimedRoute, initialClaimCards.union(tunnelCards));
            }
        }
        else{
            //No additional cost
            gameData.modifyGameState(gameData.gameState.withMoreDiscardedCards(drawnCards));

            return claimOverground(gameData, currentInfo, claimedRoute, initialClaimCards);
            //In this case the procedure is the same as when claiming an overground route
        }

    }

    /**
     * Has the player automatically claim the overground route
     * @param gameData : all of the game's information
     * @param currentInfo : information generator of the current player
     * @param claimedRoute : route that the player has decided to claim
     * @param initialClaimCards : initial cards the player has chosen to attempt capturing this route
     * @return a gameState where the current player has claimed the route with his initial claim cards.
     */
    private static GameState claimOverground(GameData gameData,  Info currentInfo,  Route claimedRoute, SortedBag<Card> initialClaimCards){
        Map<PlayerId, Player> players = gameData.players;

        receiveInfoForAll(players, currentInfo.claimedRoute(claimedRoute, initialClaimCards));

        return gameData.gameState.withClaimedRoute(claimedRoute, initialClaimCards);
    }
    /**
     * Plays the last two turns of tCHu, then calculates who gets the longest trail bonus and who won in the end or if there has been a draw
     * @param gameData : all of the game's information
     */
    private static void endOfGame(GameData gameData){
        Map<PlayerId, Player> players = gameData.players;
        Map<PlayerId, Info> infoGenerators = gameData.infoGenerators;

        receiveInfoForAll(players, infoGenerators
                                    .get(gameData.gameState.currentPlayerId())
                                    .lastTurnBegins(gameData.gameState
                                                    .currentPlayerState()
                                                    .carCount())); //LastTurnBegins

        //One more turn for each player
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            gameData.modifyGameState(gameData.gameState.forNextTurn());
            nextTurn(gameData);
        }

        //Calculate final points
        Map<PlayerId, Integer> associatedPlayerPoints = calculateFinalPoints(gameData);

        //Calculates who won the game or if the two players came to a draw
        determineWinnerOrDraw(associatedPlayerPoints, gameData);
    }

    /**
     *Calculates both players' final points (their personal points and then whether or not they obtained the LongestTrailBonus)
     * @param gameData : all of the game's information
     * @return a map with both player's final points
     */
    private static Map<PlayerId, Integer> calculateFinalPoints(GameData gameData){
        PlayerId currentPlayerId = gameData.gameState.currentPlayerId();
        PlayerId nextPlayerId = currentPlayerId.next();

        Map<PlayerId, Player> players = gameData.players;
        Map<PlayerId, Info> infoGenerators = gameData.infoGenerators;
        Map<PlayerId, Trail> eachPlayerAssociatedTrails = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Integer> associatedPlayerPoints = new EnumMap<>(PlayerId.class);

        players.forEach(((playerId, player) -> {
            //Calculate longest trails
            Trail playerLongestTrail = Trail.longest(gameData.gameState.playerState(playerId).routes());
            eachPlayerAssociatedTrails.put(playerId, playerLongestTrail);

            //Put final Points (Without the longest trail bonus)
            associatedPlayerPoints.put(playerId, gameData.gameState.playerState(playerId).finalPoints());
        }));

        updateAllStates(players, gameData.gameState);

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
     * @param gameData : all of the game's information:
     */
    private static void determineWinnerOrDraw(Map<PlayerId, Integer> associatedPlayerPoints, GameData gameData){
       PlayerId currentPlayerId = gameData.gameState.currentPlayerId();
        Map<PlayerId, Info> infoGenerators = gameData.infoGenerators;

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
                    .draw(new ArrayList<>(gameData.playerNames.values()), currentPlayerPoints);
        }

        receiveInfoForAll(gameData.players, endOfGameMessage);
    }

}
