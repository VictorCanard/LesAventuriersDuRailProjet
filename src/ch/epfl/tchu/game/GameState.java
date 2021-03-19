package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState{
    private Map<PlayerId, PlayerState> playerStateMap; //?
    private final Deck<Ticket> ticketDeck;

    private GameState(Deck<Ticket> ticketDeck, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(ticketDeck.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);
        this.ticketDeck = ticketDeck;
        this.playerStateMap = playerState;
    }
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerStateMap){
        Map<PlayerId, PublicPlayerState> publicPlayerStateMap = new EnumMap<>(PlayerId.class);
        publicPlayerStateMap.putAll(playerStateMap);
        return publicPlayerStateMap;
    }
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);

        Deck<Card> cardDeck = Deck.of(Constants.ALL_CARDS, rng).withoutTopCards(8);

        int firstPlayerIndex = rng.nextInt(2);
        PlayerId firstPlayerId = PlayerId.ALL.get(firstPlayerIndex);

        return new GameState(tickets.size(), ticketDeck, );
    }

    @Override
    public PlayerState playerState(PlayerId playerId){return playerStateMap.get(playerId);}
    @Override
    public PlayerState currentPlayerState(){return playerStateMap.get(currentPlayerId());}

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
