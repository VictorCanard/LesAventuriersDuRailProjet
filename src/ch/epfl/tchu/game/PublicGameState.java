package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PublicGameState {
    private final int ticketListSize;
    private final PublicCardState publicCardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){
        boolean positiveTicketCount = ticketsCount >=0;
        boolean exactlyTwoPairs = playerState.size() == 2;

        Preconditions.checkArgument(positiveTicketCount && exactlyTwoPairs);

        this.ticketListSize = ticketsCount;
        this.publicCardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Objects.requireNonNull(playerState);
        this.lastPlayer = lastPlayer;
    }

    public int ticketsCount(){
        return ticketListSize;
    }

    public boolean canDrawTickets(){
        return !publicCardState.isDeckEmpty();
    }

    public PublicCardState cardState(){
        return publicCardState;
    }

    public boolean canDrawCards(){
        int numberOfCardsInDrawPile = publicCardState.deckSize();
        int numberOfCardsInDiscardPile = publicCardState.discardsSize();

        return (numberOfCardsInDiscardPile + numberOfCardsInDrawPile) >= 5;
    }
    public PlayerId currentPlayerId(){return currentPlayerId;}

    public PublicPlayerState playerState(PlayerId playerId){return playerState.get(playerId);}

    public PublicPlayerState currentPlayerState(){return playerState.get(currentPlayerId);}

    public List<Route> claimedRoutes(){
        List<Route> playerOneRoutes = playerState(PlayerId.PLAYER_1).routes();
        List<Route> playerTwoRoutes = playerState(PlayerId.PLAYER_2).routes();

        playerOneRoutes.addAll(playerTwoRoutes);

        return playerOneRoutes; // Now total list of routes
    }
    public PlayerId lastPlayer(){
        return lastPlayer;
    }
}
