package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents the state of the game at a point in time, visible to everyone
 *
 * @author Victor Canard-Duchêne (326913)
 */
public class PublicGameState {

    private final int ticketDeckSize;
    private final PublicCardState publicCardState;
    private final Map<PlayerId, PublicPlayerState> playerStates;
    private final PlayerId currentPlayerId;
    private final PlayerId lastPlayer;
    private final int minCardsAllowed = 5;

    /**
     * Constructs the "public" state of the game
     *
     * @param ticketsCount    : the number of tickets in the ticket draw pile
     * @param cardState       : the "public" state of the cards at the corresponding point in the game
     * @param currentPlayerId : the player who's turn it is
     * @param playerState     : the "public" state of the players at the corresponding point of the game
     * @param lastPlayer      : when it is known, the last player to have a turn at the end of the game
     * @throws IllegalArgumentException if the ticket count is negative or if there isn't the right number of pairs in the player state map.
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        boolean positiveTicketCount = ticketsCount >= 0;
        boolean rightNumberOfPairs = playerState.size() == PlayerId.COUNT;

        Preconditions.checkArgument(positiveTicketCount);
        Preconditions.checkArgument(rightNumberOfPairs);

        this.ticketDeckSize = ticketsCount;
        this.publicCardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerStates = Objects.requireNonNull(Map.copyOf(playerState));
        this.lastPlayer = lastPlayer;
    }

    /**
     * Getter for the size of the ticket draw pile
     *
     * @return the size of the ticket draw pile
     */
    public int ticketsCount() {
        return ticketDeckSize;
    }

    /**
     * Getter for if the player can draw tickets or not
     *
     * @return true if the ticket draw pile isn't empty, false otherwise
     */
    public boolean canDrawTickets() {
        return ticketDeckSize != 0;
    }

    /**
     * Getter for the "public" cardState at the corresponding point in the game
     *
     * @return the visible version of the card state
     */
    public PublicCardState cardState() {
        return publicCardState;
    }

    /**
     * Determines if a player can draw cards from the card draw pile or not
     *
     * @return true if the number of cards in the discard and card draw pile is at least 5
     */
    public boolean canDrawCards() {
        int numberOfCardsInDrawPile = publicCardState.deckSize();
        int numberOfCardsInDiscardPile = publicCardState.discardsSize();

        return (numberOfCardsInDiscardPile + numberOfCardsInDrawPile) >= minCardsAllowed;
    }

    /**
     * Getter for the id of the current player
     *
     * @return the current player's id
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Getter for the last player to play
     *
     * @return the id of the last player
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }

    /**
     * Getter for the given player's state
     *
     * @param playerId the player which we want to know the state of
     * @return the given player's visible player state
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerStates.get(playerId);
    }

    /**
     * Getter for the current player's state
     *
     * @return the current player's visible player state
     */
    public PublicPlayerState currentPlayerState() {
        return playerStates.get(currentPlayerId);
    }

    /**
     * Getter for all the claimed routes in the game
     *
     * @return a list of all the routes claimed by both the players
     */
    public List<Route> claimedRoutes() {
        return playerStates.values()
                .stream()
                .flatMap((publicPlayerState -> publicPlayerState.routes().stream()))
                .collect(Collectors.toList());
    }
}
