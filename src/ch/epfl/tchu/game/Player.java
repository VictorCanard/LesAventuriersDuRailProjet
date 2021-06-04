package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Represents a player in the game
 *
 * @author Anne-Marie Rusu (296098)
 */

public interface Player {
    /**
     * Communicates to the player their id, and all the player names
     *
     * @param ownID       : the id of the player
     * @param playerNames : all the names of the players in the game
     */
    void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames);

    /**
     * Communicates information about the game to the player throughout the game
     *
     * @param info : the information to be communicated to the player
     */
    void receiveInfo(String info);

    /**
     * Informs the player of the new state of the game (version that can be viewed by everyone)
     *
     * @param newState : the new state of the game (visible to everyone)
     * @param ownState : the player's own state
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Informs the player of the 5 tickets they were distributed at the beginning of the game
     *
     * @param tickets : the tickets the player received
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Asks the player which of the 5 tickets they were distributed, they will keep
     *
     * @return the tickets to be kept
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Determines which action the player would like to take during their turn
     *
     * @return the action they will take in the player's turn
     */
    TurnKind nextTurn();

    /**
     * Determines what tickets a player will keep after drawing extra tickets from the ticket draw pile
     *
     * @param options : the tickets the player has picked from the ticket draw pile
     * @return the tickets the player will keep
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Determines from which pile (draw pile or face up cards) the player will draw a card from
     *
     * @return -1 if its from the draw pile, and an integer between 0 and 4 (corresponding to the position of the card)
     * if its from the face up cards
     */
    int drawSlot();

    /**
     * Determines the route a player will attempt to claim
     *
     * @return : the route to be claimed
     */
    Route claimedRoute();

    /**
     * Determines what cards a player will use to claim a route
     *
     * @return the cards the player will use to claim a route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Determines what cards a player will use to claim a tunnel
     *
     * @param options : the possible cards the player can use
     * @return the cards the player will use to claim a tunnel
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Determines the 3 additional cards when claiming a tunnel route
     * @param cards : the three additional cards
     */
    void tunnelDrawnCards(SortedBag<Card> cards);

    /**
     * The additional cost as a result of attempting to claim a tunnel route
     * @param additionalCost : the additional cost
     */
    void additionalCost(int additionalCost);



    /**
     * Describes the three kinds of actions a player can do during their turn
     */
    enum TurnKind {
        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * List of all the actions a player can do on their turn
         */
        public final static List<TurnKind> ALL = List.of(TurnKind.values());
    }
}
