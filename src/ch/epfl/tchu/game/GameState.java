package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState{
    private final Map<PlayerId, PlayerState> playerStateMap;
    private final Deck<Ticket> ticketDeck;
    private final CardState cardState;
    private final PlayerId lastPlayer;
    private final PlayerId currentPlayer;


    private GameState(Deck<Ticket> ticketDeck,
                      CardState cardState,
                      PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState,
                      PlayerId lastPlayer) {
        super(ticketDeck.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);

        this.cardState = cardState;
        this.ticketDeck = ticketDeck;
        this.playerStateMap = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
        this.currentPlayer = currentPlayerId;

    }
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerStateMap){
        Map<PlayerId, PublicPlayerState> publicPlayerStateMap = new EnumMap<>(PlayerId.class);
        publicPlayerStateMap.putAll(playerStateMap);
        return publicPlayerStateMap;
    }
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        //Tickets
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);

        //PlayerStateMap
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);

        Deck<Card> initialDeck = Deck.of(Constants.ALL_CARDS, rng);

        for (PlayerId playerId : PlayerId.values()){
            SortedBag<Card> top4Cards = initialDeck.topCards(4);
            initialDeck = initialDeck.withoutTopCards(4);

            PlayerState playerState = PlayerState.initial(top4Cards);
            playerStateMap.put(playerId, playerState);
        }

        Deck<Card> cardDeck = initialDeck;
        CardState cardState = CardState.of(cardDeck);

        int firstPlayerIndex = rng.nextInt(2);
        PlayerId firstPlayerId = PlayerId.ALL.get(firstPlayerIndex);

        return new GameState(ticketDeck, cardState,firstPlayerId, playerStateMap, null );
    }

    @Override
    public PlayerState playerState(PlayerId playerId){return playerStateMap.get(playerId);}
    @Override
    public PlayerState currentPlayerState(){return playerStateMap.get(currentPlayerId());}

    //Group 1
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count>=0 && count<=ticketsCount());
        return ticketDeck.topCards(count); }

    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>=0 && count<=ticketsCount());
        return new GameState(ticketDeck.withoutTopCards(count), cardState, this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public Card topCard(){
        Preconditions.checkArgument(cardState.deckSize()!=0);
        return cardState.topDeckCard();
    }

    public GameState withoutTopCard(){
        Preconditions.checkArgument(cardState.deckSize()!=0);
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(ticketDeck, cardState.withMoreDiscardedCards(discardedCards), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        return new GameState(ticketDeck, cardState.withDeckRecreatedFromDiscards(rng), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    //Group 2
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){ // i think???
        playerStateMap.put(playerId, playerStateMap.get(playerId).withAddedTickets(chosenTickets));
        return new GameState(ticketDeck, cardState, this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){ //i think???
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        playerStateMap.put(this.currentPlayerId(), playerStateMap.get(this.currentPlayerId()).withAddedTickets(chosenTickets));
        return new GameState(ticketDeck.withoutTopCards(drawnTickets.size()), cardState, this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards());

        Card card = cardState.faceUpCard(slot);
        playerStateMap.put(this.currentPlayerId(), playerStateMap.get(this.currentPlayerId()).withAddedCard(card));
        return new GameState(ticketDeck, cardState.withDrawnFaceUpCard(slot), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards());

        playerStateMap.put(this.currentPlayerId(), playerStateMap.get(this.currentPlayerId()).withAddedCard(cardState.topDeckCard()));
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){

        playerStateMap.put(this.currentPlayerId(), playerStateMap.get(this.currentPlayerId()).withClaimedRoute(route, cards));
        return new GameState(ticketDeck, cardState, this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    //Group 3
    public boolean lastTurnBegins(){
        boolean lastPlayerIsUnknown = (lastPlayer == null);

        int currentPlayerNumberOfWagons = currentPlayerState().carCount();
        boolean onlyTwoWagonsLeftOrLess = currentPlayerNumberOfWagons <= 2;

        return lastPlayerIsUnknown && onlyTwoWagonsLeftOrLess;
    }
    public GameState forNextTurn(){
        PlayerId lastPlayer = (lastTurnBegins()) ? currentPlayer : null;
        PlayerId otherPlayer = (currentPlayer == PlayerId.PLAYER_1) ? PlayerId.PLAYER_2 : PlayerId.PLAYER_1;

        return new GameState(ticketDeck, cardState, otherPlayer, playerStateMap, lastPlayer);
    }

}
