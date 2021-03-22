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

    /**
     * Return the count number of tickets from the top of the pile
     * @param count : number of tickets
     * @throws IllegalArgumentException if the count is negative or strictly superior to the player's number of tickets
     * @return the count tickets
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count>=0 && count<=ticketsCount());
        return ticketDeck.topCards(count);
    }

    /**
     * Return a new game state without the count number of tickets
      * @param count : number of tickets we don't want in this new gamestate
     * @throws IllegalFormatCodePointException if the count is negative or strictly superior to the player's number of tickets
     * @return a new game state with count tickets removed
     */
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>=0 && count<=ticketsCount());
        return new GameState(ticketDeck.withoutTopCards(count), cardState, this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    /**
     * Returns the top card of the cardState
     * @throws IllegalArgumentException if the deck is empty
     * @return the card at the top
     */
    public Card topCard(){
        Preconditions.checkArgument(cardState.deckSize()!=0);
        return cardState.topDeckCard();
    }

    /**
     * Returns a gameState without the top card
     * @throws IllegalArgumentException if the deck is empty
     * @return a new Gamestate without the top card
     */
    public GameState withoutTopCard(){
        Preconditions.checkArgument(cardState.deckSize()!=0);
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    /**
     * Makes a new gamestate with more cards included in the discard pile
     * @param discardedCards : cards to add to card's state discard pile
     * @return a new game state with more discarded cards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(ticketDeck, cardState.withMoreDiscardedCards(discardedCards), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }

    /**
     * Returns an identical gamestate except if the draw pile is empty in which case it creates a new from the discards pile and shuffles it
     * @param rng : Random Number Generator to shuffle the new deck created from the discard pile
     * @return identical game state or with a new draw pile from the discards pile.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        return new GameState(ticketDeck, cardState.withDeckRecreatedFromDiscards(rng), this.currentPlayerId(), playerStateMap, this.lastPlayer());
    }
//Group 2

    /**
     * Returns an identical gamestate but with the player's initially chosen tickets added
     * @param playerId : Player 1 or 2, the player that has chosen these tickets
     * @param chosenTickets : tickets chosen at the start of the game
     * @throws IllegalArgumentException if the player has at least one ticket already
     * @return new GameState
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        PlayerState playerStateToModify = playerStateMap.get(playerId);
        Preconditions.checkArgument(playerStateToModify.tickets().size()== 0);

        Map<PlayerId, PlayerState> psMap = new TreeMap<>(playerStateMap);

        psMap.put(playerId, playerStateToModify.withAddedTickets(chosenTickets));
        return new GameState(ticketDeck, cardState, this.currentPlayerId(), psMap, this.lastPlayer());
    }

    /**
     * Returns a new gamestate with less tickets in the ticket pile as these are now in the player's state
     * @param drawnTickets : all tickets drawn by the player initially
     * @param chosenTickets : tickets kept by the player
     * @throws IllegalArgumentException if drawn tickets does not contain the chosen tickets
     * @return new GameState
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){ //i think???
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> psMap = new TreeMap<>(playerStateMap);
        psMap.put(currentPlayer, currentPlayerState().withAddedTickets(chosenTickets));
        return new GameState(ticketDeck.withoutTopCards(drawnTickets.size()), cardState, currentPlayer, psMap, lastPlayer);
    }

    /**
     * Returns a new gamestate where the chosen card at index slot was taken from the drawn pile and put into the player's hand
     * @param slot : the index of the drawn face-up
     * @throws IllegalArgumentException if it's not possible to draw cards
     * @return a new GameState
     */
    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards());

        Card card = cardState.faceUpCard(slot);
        Map<PlayerId, PlayerState> psMap = new TreeMap<>(playerStateMap);
        psMap.put(currentPlayer, playerStateMap.get(this.currentPlayerId()).withAddedCard(card));
        return new GameState(ticketDeck, cardState.withDrawnFaceUpCard(slot), currentPlayer, psMap, lastPlayer);
    }

    /**
     * Returns a new gamestate where the deck's top card has been placed in the current player's hand
     * @throws IllegalArgumentException if it's not possible to draw cards
     * @return a new modified gamestate
     */
    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards());
        Map<PlayerId, PlayerState> psMap = new TreeMap<>(playerStateMap);
        psMap.put(this.currentPlayerId(), playerStateMap.get(this.currentPlayerId()).withAddedCard(cardState.topDeckCard()));
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), this.currentPlayerId(), psMap, this.lastPlayer());
    }

    /**
     * Returns a new gameState where the current player has claimed a given route with a given set cards
     * (thus adds the route to the player's collection and removes the cards he used)
     * @param route : the claimed route
     * @param cards : the claim cards
     * @return a new gamestate
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        Map<PlayerId, PlayerState> psMap = new TreeMap<>(playerStateMap);
        psMap.put(this.currentPlayerId(), playerStateMap.get(this.currentPlayerId()).withClaimedRoute(route, cards));
        return new GameState(ticketDeck, cardState, this.currentPlayerId(), psMap, this.lastPlayer());
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
