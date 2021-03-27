package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 *Describes the state of the game at a point in time
 * @author Victor Canard-Duchêne (326913)
 * @author Anne-Marie Rusu (296098)
 */
public final class GameState extends PublicGameState{
    private final Map<PlayerId, PlayerState> playerStateMap;
    private final Deck<Ticket> ticketDeck;
    private final CardState cardState;
    private final PlayerId lastPlayer;
    private final PlayerId currentPlayer;
    private final Map<PlayerId, PlayerState> psMap;


    private GameState(Deck<Ticket> ticketDeck,
                      CardState cardState,
                      PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState,
                      PlayerId lastPlayer) {
        super(ticketDeck.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);

        this.cardState = cardState;
        this.ticketDeck = ticketDeck;
        this.playerStateMap = Map.copyOf(playerState); //Immutable collection
        this.currentPlayer = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.psMap = new EnumMap<>(playerStateMap);

    }
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> playerStateMap){
        Map<PlayerId, PublicPlayerState> publicPlayerStateMap = new EnumMap<>(PlayerId.class);
        publicPlayerStateMap.putAll(playerStateMap);
        return publicPlayerStateMap;
    }

    /**
     * Creates the initial state of the game
     * @param tickets : the group of tickets to be used in the game
     * @param rng : an instance of a random number generator, used to "shuffle" the deck of tickets
     * @return a new GameState representing the initial state of the game
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng){
        //Tickets
        Deck<Ticket> ticketDeck = Deck.of(tickets, rng);

        //PlayerStateMap
        Map<PlayerId, PlayerState> playerStateMap = new EnumMap<>(PlayerId.class);

        Deck<Card> initialDeck = Deck.of(Constants.ALL_CARDS, rng); 

        for (PlayerId playerId : PlayerId.values()){ //Initializes each player's deck to the top 4 cards of the deck (and then the 4 next)
            SortedBag<Card> top4Cards = initialDeck.topCards(Constants.INITIAL_CARDS_COUNT);
            initialDeck = initialDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

            PlayerState playerState = PlayerState.initial(top4Cards); //Initializes a new PlayerState with these cards and places it in the map
            playerStateMap.put(playerId, playerState);
        }

        Deck<Card> cardDeck = initialDeck; //Deck without the top 8 cards
        CardState cardState = CardState.of(cardDeck); //Makes a CardState of that initial deck (5 face-up cards, a draw-pile and an empty discard pile)

        int firstPlayerIndex = rng.nextInt(PlayerId.COUNT); //Picks a player at random
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
      * @param count : number of tickets we don't want in this new GameState
     * @throws IllegalFormatCodePointException if the count is negative or strictly superior to the player's number of tickets
     * @return a new game state with count tickets removed
     */
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count>=0 && count<=ticketsCount());
        return new GameState(ticketDeck.withoutTopCards(count), cardState, currentPlayer, playerStateMap, lastPlayer);
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
     * Returns a GameState without the top card
     * @throws IllegalArgumentException if the deck is empty
     * @return a new GameState without the top card
     */
    public GameState withoutTopCard(){
        Preconditions.checkArgument(cardState.deckSize()!=0);
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), currentPlayer, playerStateMap, lastPlayer);
    }

    /**
     * Makes a new GameState with more cards included in the discard pile
     * @param discardedCards : cards to add to card's state discard pile
     * @return a new game state with more discarded cards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(ticketDeck, cardState.withMoreDiscardedCards(discardedCards), currentPlayer, playerStateMap, lastPlayer);
    }

    /**
     * Returns an identical GameState except if the draw pile is empty in which case it creates a new from the discards pile and shuffles it
     * @param rng : Random Number Generator to shuffle the new deck created from the discard pile
     * @return identical game state or with a new draw pile from the discards pile.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(cardState.isDeckEmpty()){
            return new GameState(ticketDeck, cardState.withDeckRecreatedFromDiscards(rng), currentPlayer, playerStateMap, lastPlayer);
        }
        return this;
    }
//Group 2

    /**
     * Returns an identical GameState but with the player's initially chosen tickets added
     * @param playerId : Player 1 or 2, the player that has chosen these tickets
     * @param chosenTickets : tickets chosen at the start of the game
     * @throws IllegalArgumentException if the player has at least one ticket already
     * @return new GameState
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        PlayerState playerStateToModify = playerStateMap.get(playerId);
        Preconditions.checkArgument(playerStateToModify.tickets().isEmpty());

        psMap.put(playerId, playerStateToModify.withAddedTickets(chosenTickets));

        return new GameState(ticketDeck, cardState, currentPlayer, psMap, lastPlayer);
    }

    /**
     * Returns a new GameState with less tickets in the ticket pile as these are now in the player's state
     * @param drawnTickets : all tickets drawn by the player initially
     * @param chosenTickets : tickets kept by the player
     * @throws IllegalArgumentException if drawn tickets does not contain the chosen tickets
     * @return new GameState
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        psMap.put(currentPlayer, currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(ticketDeck.withoutTopCards(drawnTickets.size()), cardState, currentPlayer, psMap, lastPlayer);
    }

    /**
     * Returns a new GameState where the chosen card at index slot was taken from the face-up cards and put into the player's hand
     * And the chosen card in the face up cards was replaced with the one at the top of the deck
     * @param slot : the index of the drawn face-up
     * @throws IllegalArgumentException if it's not possible to draw cards
     * @return a new GameState
     */
    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards());

        Card cardToAdd = cardState.faceUpCard(slot);
        psMap.put(currentPlayer, currentPlayerState().withAddedCard(cardToAdd));
        return new GameState(ticketDeck, cardState.withDrawnFaceUpCard(slot), currentPlayer, psMap, lastPlayer);
    }

    /**
     * Returns a new GameState where the deck's top card has been placed in the current player's hand
     * @throws IllegalArgumentException if it's not possible to draw cards
     * @return a new modified GameState
     */
    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards());

        Card cardOnTopOfTheDeck = cardState.topDeckCard();

        psMap.put(currentPlayer, currentPlayerState().withAddedCard(cardOnTopOfTheDeck));
        return new GameState(ticketDeck, cardState.withoutTopDeckCard(), currentPlayer, psMap, lastPlayer);
    }

    /**
     * Returns a new GameState where the current player has claimed a given route with a given set cards
     * (thus adds the route to the player's collection and removes the cards he used)
     * @param route : the claimed route
     * @param cards : the claim cards
     * @return a new GameState
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        psMap.put(currentPlayer, currentPlayerState().withClaimedRoute(route, cards));
        CardState newState = cardState.withMoreDiscardedCards(cards);

        return new GameState(ticketDeck, newState, currentPlayer, psMap, lastPlayer);
    }

    //Group 3

    /**
     * Determines when the last turn of the game begins
     * @return true if the last player is not already known and if the current player has at most 2 cars left
     */
    public boolean lastTurnBegins(){
        boolean lastPlayerIsUnknown = (lastPlayer == null);

        int currentPlayerNumberOfCars = currentPlayerState().carCount();
        boolean onlyTwoWagonsLeftOrLess = currentPlayerNumberOfCars <= 2;

        return lastPlayerIsUnknown && onlyTwoWagonsLeftOrLess;
    }

    /**
     *The state of the game where it's the next player's turn to play
     * @return a new GameState where its the next player's turn
     */
    public GameState forNextTurn(){
        PlayerId lastPlayer = (lastTurnBegins()) ? currentPlayer : null;
        PlayerId otherPlayer = currentPlayer.next();

        return new GameState(ticketDeck, cardState, otherPlayer, playerStateMap, lastPlayer);
    }

}
