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


//todo: reassigned local variable gameState.
public final class Game {

    private static class AllGameData{
        private GameState gameState;
        private Map<PlayerId, Player> players;
        private Map<PlayerId, String> playerNames;


        private Map<PlayerId, Info> infoGenerators;
        private Random rng;


        private AllGameData(GameState gameState, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> infoGenerators, Random rng) {
            this.gameState = gameState;
            this.players = players;
            this.playerNames = playerNames;
            this.infoGenerators =infoGenerators;
            this.rng = rng;
        }

        private void modifyGameState(GameState newGameState){
            this.gameState = newGameState;
        }
        private void nextTurn(){
            this.gameState = this.gameState.forNextTurn();
        }

        private boolean lastTurnBegins(){
            return this.gameState.lastTurnBegins();
        }




    }
    private final static int constantNumberOfPlayers = 2;
    /**
     * Runs a game of tCHu
     * @param players : the players playing the game
     * @param playerNames : the names of the corresponding players
     * @param tickets : the tickets to be used in the game
     * @param rng : an instance of a random number generator
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == constantNumberOfPlayers
                                            && playerNames.size() == constantNumberOfPlayers);

        //before the game starts
        GameState gameState = GameState.initial(tickets, rng);

        Map<PlayerId, Integer> keptTicketNumber = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> namesOfPlayers = Map.copyOf(playerNames);
        Map<PlayerId, Player> playerMap = Map.copyOf(players);

        Map<PlayerId, Info> infoGenerators = initializeInfoGenerators(players, playerNames);


        gameState = setup(playerMap, namesOfPlayers, infoGenerators,  gameState, keptTicketNumber);

        //plays one round first so as to make sure the condition lastTurnBegins() is tested at the right moment
        gameState = nextTurn(playerMap, infoGenerators, gameState, rng);

        //the actual game starts
        while(!gameState.lastTurnBegins()){ //Checks if last turn is beginning in the next turn

            gameState = gameState.forNextTurn(); //Takes the GameState's next turn (swaps the current player and the other player)
            gameState = nextTurn(playerMap, infoGenerators, gameState,  rng);
        }

        //Last turn begins returned true thus the end of game is activated
        endOfGame(playerMap, namesOfPlayers, infoGenerators, gameState, rng);
    }

    /**
     * Runs the setup of the game: chooses the first player and distributes the initial tickets and cards
     * @param players : the players playing the game
     * @param playerNames : the names of the corresponding players
     * @param currentInfo : the information to be communicated to the players
     * @param gameState : the current state of the game
     * @param keptTicketNumber : the number of kept tickets the player has chosen
     */
    private static GameState setup(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,Map<PlayerId, Info> currentInfo, GameState gameState, Map<PlayerId, Integer> keptTicketNumber){
        initializePlayers(players, playerNames);

        receiveInfoForAll(players, currentInfo.get(gameState.currentPlayerId()).willPlayFirst());

        gameState = distributeTickets(players, gameState, keptTicketNumber);

        currentInfo.forEach((playerId, info) ->
                receiveInfoForAll(players, info.keptTickets(keptTicketNumber.get(playerId))));

        return gameState;
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
     *
     * @param players
     * @param playerNames
     */
    private static void initializePlayers(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames){
        players.forEach(((playerId, player) ->
                player.initPlayers(playerId, playerNames)));
    }

    /**
     *
     * @param players
     * @param gameState
     * @param keptTicketNumber
     * @return
     */
    private static GameState distributeTickets(Map<PlayerId, Player> players,  GameState gameState, Map<PlayerId, Integer> keptTicketNumber){

        for (Player player : players.values()) {
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));

            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);

        }
        updateAllStates(players, gameState);

        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            PlayerId playerId = entry.getKey();
            Player player = entry.getValue();

            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets(); //Asks the player to choose tickets from the set of options determined in setInitialTicketChoice

            gameState = gameState.withInitiallyChosenTickets(playerId, chosenTickets);

            keptTicketNumber.put(playerId, chosenTickets.size());
        }

        return gameState;

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
     * @param rng : an instance of a random number
     * @return the new gameState at the end of the turn
     */
    private static GameState nextTurn(Map<PlayerId, Player> players, Map<PlayerId, Info> infoGenerators, GameState gameState, Random rng){
        updateAllStates(players, gameState);

        PlayerId currentPlayerId = gameState.currentPlayerId();
        Player currentPlayer = players.get(currentPlayerId);
        Info currentInfo = infoGenerators.get(currentPlayerId);

        receiveInfoForAll(players, currentInfo.canPlay());

        Player.TurnKind playerChoice = currentPlayer.nextTurn();

        switch (playerChoice){
            case DRAW_TICKETS:
                gameState = drawTickets(gameState, players, currentPlayer, currentInfo);
                break;

            case DRAW_CARDS:

                gameState = drawCards(gameState, players, currentPlayer, currentInfo, rng);
                break;

            case CLAIM_ROUTE:
                gameState = claimRoute(gameState, players, currentPlayer, currentInfo, rng);
        }
        return gameState;
    }

    /**
     *
     * @param gameState
     * @param players
     * @param currentPlayer
     * @param currentInfo
     * @return
     */
    private static GameState drawTickets(GameState gameState, Map<PlayerId, Player> players, Player currentPlayer, Info currentInfo){
        receiveInfoForAll(players, currentInfo.drewTickets(Constants.IN_GAME_TICKETS_COUNT));

        SortedBag<Ticket> ticketOptions = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);

        SortedBag<Ticket> keptTickets = currentPlayer.chooseTickets(ticketOptions);

        receiveInfoForAll(players, currentInfo.keptTickets(keptTickets.size()));

        return gameState.withChosenAdditionalTickets(ticketOptions, keptTickets);
    }

    /**
     *
     * @param gameState
     * @param players
     * @param currentPlayer
     * @param currentInfo
     * @param rng
     * @return
     */
    private static GameState drawCards(GameState gameState, Map<PlayerId, Player> players, Player currentPlayer, Info currentInfo, Random rng){
        for (int i = 0; i < 2; i++) {
            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

            if(i == 1){
                updateAllStates(players, gameState); //To update all states right before the player chooses a second card to draw
            }
            int drawSlot = currentPlayer.drawSlot(); //-1, 0->4

            if(drawSlot == Constants.DECK_SLOT){
                //DeckCard
                gameState = gameState.withBlindlyDrawnCard();

                receiveInfoForAll(players, currentInfo.drewBlindCard());
            }
            else{
                Card chosenVisibleCard = gameState.cardState().faceUpCard(drawSlot);

                gameState = gameState.withDrawnFaceUpCard(drawSlot);

                receiveInfoForAll(players, currentInfo.drewVisibleCard(chosenVisibleCard));
            }
        }
        return gameState;

    }

    /**
     *
     * @param gameState
     * @param players
     * @param currentPlayer
     * @param currentInfo
     * @param rng
     * @return
     */
    private static GameState claimRoute(GameState gameState, Map<PlayerId, Player> players, Player currentPlayer, Info currentInfo, Random rng){

        Route claimedRoute = currentPlayer.claimedRoute();
        SortedBag<Card>  initialClaimCards = currentPlayer.initialClaimCards();

        if(claimedRoute.level() == Route.Level.UNDERGROUND) {
            return claimUnderground(gameState, players, currentPlayer, currentInfo, rng, claimedRoute, initialClaimCards);
        }
        //Overground route
        return claimOverground(gameState, players, currentInfo, claimedRoute, initialClaimCards);

    }

    /**
     *
     * @param gameState
     * @param players
     * @param currentPlayer
     * @param currentInfo
     * @param rng
     * @param claimedRoute
     * @param initialClaimCards
     * @return
     */
    private static GameState claimUnderground(GameState gameState, Map<PlayerId, Player> players, Player currentPlayer, Info currentInfo, Random rng, Route claimedRoute, SortedBag<Card> initialClaimCards){
        receiveInfoForAll(players, currentInfo.attemptsTunnelClaim(claimedRoute, initialClaimCards));

        //Building the 3 cards drawn from the deck
        SortedBag.Builder<Card> drawCardsBuild = new SortedBag.Builder<>();

        for(int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

            drawCardsBuild.add(gameState.topCard());
            gameState = gameState.withoutTopCard();
        }
        SortedBag<Card> drawnCards = drawCardsBuild.build();

        //Calculating additional cost
        int additionalCost = claimedRoute.additionalClaimCardsCount(initialClaimCards, drawnCards);

        //Displaying that cost for all players and the drawn cards
        receiveInfoForAll(players, currentInfo.drewAdditionalCards(drawnCards, additionalCost));

        PlayerState playerState = gameState.currentPlayerState();

        if(additionalCost > 0){
            //Additional cost is between 1 and 3 (both included)

            //Cards the player could play
            List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(additionalCost, initialClaimCards, drawnCards);

            if(possibleAdditionalCards.isEmpty()){ //Player can't play any additional cards
                receiveInfoForAll(players, currentInfo.didNotClaimRoute(claimedRoute));

                return gameState.withMoreDiscardedCards(drawnCards);

            }else{ //The player can play additional cards. Asks the player which set of cards he want to play.
                SortedBag<Card> tunnelCards = currentPlayer.chooseAdditionalCards(possibleAdditionalCards);

                receiveInfoForAll(players, currentInfo.claimedRoute(claimedRoute, initialClaimCards.union(tunnelCards)));

                return gameState
                        .withMoreDiscardedCards(drawnCards)               //Drawn cards are put in the discard
                        .withClaimedRoute(claimedRoute, initialClaimCards.union(tunnelCards)); //Claimed route

            }

        }
        else{ //No additional cost
            gameState = gameState.withMoreDiscardedCards(drawnCards);

            return claimOverground(gameState, players, currentInfo, claimedRoute, initialClaimCards); //In this case the procedure is the same as when claiming an overground route
        }

    }

    /**
     *
     * @param gameState
     * @param players
     * @param currentInfo
     * @param claimedRoute
     * @param initialClaimCards
     * @return
     */
    private static GameState claimOverground(GameState gameState, Map<PlayerId, Player> players,  Info currentInfo,  Route claimedRoute, SortedBag<Card> initialClaimCards){
        receiveInfoForAll(players, currentInfo.claimedRoute(claimedRoute, initialClaimCards));

        return gameState.withClaimedRoute(claimedRoute, initialClaimCards);
    }
    /**
     * Plays the last two turns of tCHu, then calculates who gets the longest trail bonus and who won in the end or if there has been a draw
     * @param players : the two players in the game
     * @param playerNames : their names
     * @param infoGenerators : their information displays
     * @param gameState : the state of the game (before the last two turns)
     * @param rng : random number generator for recreating decks
     */
    private static void endOfGame(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, Map<PlayerId, Info> infoGenerators, GameState gameState, Random rng){
        receiveInfoForAll(players,
                infoGenerators
                .get(gameState.currentPlayerId())
                .lastTurnBegins(
                        gameState
                        .currentPlayerState()
                        .carCount())); //LastTurnBegins

        //One more turn for each player
        for (int i = 0; i < 2; i++) {
            gameState = gameState.forNextTurn();
            nextTurn(players, infoGenerators, gameState,rng);

        }

        //Calculate final points
        PlayerId currentPlayerId = gameState.currentPlayerId();

        Map<PlayerId, Integer> associatedPlayerPoints = calculateFinalPoints(gameState, players, currentPlayerId, infoGenerators);

        //Calculates who won the game or if the two players came to a draw
        determineWinnerOrDraw(players, associatedPlayerPoints, currentPlayerId, infoGenerators, playerNames);

    }

    /**
     *
     * @param gameState
     * @param players
     * @param currentPlayerId
     * @param infoGenerators
     * @return
     */
    private static Map<PlayerId, Integer> calculateFinalPoints(GameState gameState, Map<PlayerId, Player> players, PlayerId currentPlayerId, Map<PlayerId, Info> infoGenerators){
        Map<PlayerId, Trail> eachPlayerAssociatedTrails = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Integer> associatedPlayerPoints = new EnumMap<>(PlayerId.class);

        players.forEach(((playerId, player) -> {
            //Calculate longest trails
            Trail playerLongestTrail = Trail.longest(gameState.playerState(playerId).routes());
            eachPlayerAssociatedTrails.put(playerId, playerLongestTrail);

            //Put final Points (Without the longest trail bonus)
            associatedPlayerPoints.put(playerId, gameState.playerState(playerId).finalPoints());
        }));

        updateAllStates(players, gameState);

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
            longestTrailBonus = String.format("%s%s", infoGenerators.get(currentPlayerId).getsLongestTrailBonus(trailCurrentPlayer),
                    infoGenerators.get(currentPlayerId.next()).getsLongestTrailBonus(trailNextPlayer));


            for (PlayerId playerId: PlayerId.values()
            ) {
                associatedPlayerPoints
                        .merge(playerId, Constants.LONGEST_TRAIL_BONUS_POINTS, Integer::sum);  //Adds 10 points to each player's count
            }
        }

        receiveInfoForAll(players, longestTrailBonus);

        return associatedPlayerPoints;
    }

    /**
     *
     * @param players
     * @param associatedPlayerPoints
     * @param currentPlayerId
     * @param infoGenerators
     * @param playerNames
     */
    private static void determineWinnerOrDraw(Map<PlayerId, Player> players, Map<PlayerId, Integer> associatedPlayerPoints, PlayerId currentPlayerId, Map<PlayerId, Info> infoGenerators, Map<PlayerId, String> playerNames){
        int currentPlayerPoints = associatedPlayerPoints.get(currentPlayerId);
        int nextPlayerPoints = associatedPlayerPoints.get(currentPlayerId.next());

        int whoWonComparator = Integer.compare(currentPlayerPoints, nextPlayerPoints);

        String endOfGameMessage;

        if(whoWonComparator > 0){ //Current Player won
            endOfGameMessage = infoGenerators.get(currentPlayerId)
                    .won(currentPlayerPoints, nextPlayerPoints);

        }else if(whoWonComparator < 0){ //Next Player won
            endOfGameMessage = infoGenerators.get(currentPlayerId)
                    .won(nextPlayerPoints, currentPlayerPoints);

        }else{ //Both players came to a draw
            endOfGameMessage = Info
                    .draw(new ArrayList<>(playerNames.values()), currentPlayerPoints);
        }

        receiveInfoForAll(players, endOfGameMessage);
    }

}
