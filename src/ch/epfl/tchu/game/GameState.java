package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState{
    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
    }
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){return null;}

    @Override
    public PlayerState playerState(PlayerId playerId){return null;}
    @Override
    public PlayerState currentPlayerState(){return null;}

    //Group 1
    public SortedBag<Ticket> topTickets(int count){return null;}
    public GameState withoutTopTickets(int count){return null;}
    public Card topCard(){return null;}
    public GameState withoutTopCard(){return null;}
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){return null;}
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){return null;}

    //Group 2
    GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){return null;}
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){return null;}
    public GameState withDrawnFaceUpCard(int slot){return null;}
    public GameState withBlindlyDrawnCard(){return null;}
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){return null;}

    //Group 3
    public boolean lastTurnBegins(){return false;}
    public GameState forNextTurn(){return null;}

}
