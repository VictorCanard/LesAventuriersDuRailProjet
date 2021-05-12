package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.*;

/**
 * Represents the state of the game at a point in time
 * @author Victor Canard-Duchêne (326913)
 * @author Anne-Marie Rusu (296098)
 */
public final class GameState extends PublicGameState{

    private final Map<PlayerId, PlayerState> playerStateMap;
    private final Deck<Ticket> ticketDeck;
    private final CardState cardState;

    /**
     * Constructs a GameState with the following attributes
     * @param playerStates : map with player ids associated to their player states
     * @param ticketDeck : deck of tickets to be drawn throughout the game
     * @param cardState : state of the cards (includes state of the draw & discards pile as well as the face-up cards)
     * @param currentPlayer : id of the player whose turn it is
     * @param lastPlayer : last player to play (unknown until last turn begins)
     */
    private GameState(Map<PlayerId, PlayerState> playerStates,
                      Deck<Ticket> ticketDeck,
                      CardState cardState,
                      PlayerId currentPlayer,
                      PlayerId lastPlayer) {
        super(ticketDeck.size(), cardState, currentPlayer, Map.copyOf(playerStates), lastPlayer);

        this.playerStateMap = Map.copyOf(playerStates);
        this.ticketDeck = Objects.requireNonNull(ticketDeck);
        this.cardState = Objects.requireNonNull(cardState);
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

        //Initial deck
        Deck<Card> initialDeck = Deck.of(Constants.ALL_CARDS, rng);

        //Initializes each player's deck to the top 4 cards of the deck (and then the 4 next)
        for (PlayerId playerId : PlayerId.values()){
            SortedBag<Card> top4Cards = initialDeck.topCards(Constants.INITIAL_CARDS_COUNT);
            initialDeck = initialDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

            PlayerState playerState = PlayerState.initial(top4Cards);
            //Initializes a new PlayerState with these cards and places it in the map
            playerStateMap.put(playerId, playerState);
        }

        Deck<Card> cardDeck = initialDeck;
        //Deck without the top 8 cards
        CardState cardState = CardState.of(cardDeck);
        //Makes a CardState of that initial deck (5 face-up cards, a draw-pile and an empty discard pile)

        int firstPlayerIndex = rng.nextInt(PlayerId.COUNT);
        //Picks a player at random
        PlayerId firstPlayerId = PlayerId.ALL.get(firstPlayerIndex);

        return new GameState(playerStateMap, ticketDeck, cardState, firstPlayerId, null);
    }

    /**
     * Overrides PublicGameState's method as it returns the private part of the player state
     * @param playerId the player which we want to know the state of
     * @return the private player state associated to the player id given as argument
     */
    @Override
    public PlayerState playerState(PlayerId playerId){return playerStateMap.get(playerId);}

    /**
     * Overrides PublicGameState's method as it returns the private part of the current player state
     * @return the current player's private state
     */
    @Override
    public PlayerState currentPlayerState(){return playerStateMap.get(super.currentPlayerId());}

    /**
     * Returns the specified number of tickets from the top of the pile
     * @param count : number of tickets
     * @throws IllegalArgumentException if the count is negative or strictly superior to the player's number of tickets
     * @return the count number of top tickets
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());

        return ticketDeck.topCards(count);
    }

    /**
     * "Removes" the specified number of tickets from the top of the ticket draw pile
     * @param count : number of tickets we don't want in this new GameState
     * @throws IllegalFormatCodePointException if the count is negative or strictly superior to the player's number of tickets
     * @return a new game state with count tickets removed
     */
    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());

        return new GameState(playerStateMap, ticketDeck.withoutTopCards(count), cardState, super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * Returns the top card of the cardState
     * @throws IllegalArgumentException if the deck is empty
     * @return the card at the top
     */
    public Card topCard(){
        Preconditions.checkArgument(!cardState.isDeckEmpty());

        return cardState.topDeckCard();
    }

    /**
     * "Removes" the top card from the draw pile
     * @throws IllegalArgumentException if the deck is empty
     * @return a new GameState without the top card in the draw pile
     */
    public GameState withoutTopCard(){
        Preconditions.checkArgument(!cardState.isDeckEmpty());

        return new GameState(playerStateMap, ticketDeck, cardState.withoutTopDeckCard(), super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * "Adds" cards to the discard pile
     * @param discardedCards : cards to add to card's state discard pile
     * @return a new game state with more discarded cards
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        return new GameState(playerStateMap, ticketDeck, cardState.withMoreDiscardedCards(discardedCards), super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * "Recreates" the draw pile with the discarded cards, if the draw pile becomes empty
     * @param rng : Random Number Generator to shuffle the new deck created from the discard pile
     * @return identical game state or with a new draw pile made from the discards pile.
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        if(cardState.isDeckEmpty()){
            return new GameState(playerStateMap, ticketDeck, cardState.withDeckRecreatedFromDiscards(rng), super.currentPlayerId(), super.lastPlayer());
        }
        return this;
    }

    /**
     * "Adds" the specified tickets to the player, chosen from the 5 initially distributed ones
     * @param playerId : Player 1 or 2, the player that has chosen these tickets
     * @param chosenTickets : tickets chosen at the start of the game
     * @throws IllegalArgumentException if the player has at least one ticket already
     * @return new GameState with added tickets for the player state associated to the param player id
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Map<PlayerId, PlayerState> psMap = new EnumMap<>(playerStateMap);
        PlayerState playerStateToModify = playerStateMap.get(playerId);
        Preconditions.checkArgument(playerStateToModify.tickets().isEmpty());

        psMap.put(playerId, playerStateToModify.withAddedTickets(chosenTickets));

        return new GameState(psMap, ticketDeck, cardState, super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * "Adds" the specified tickets to the player, from the one's drawn throughout the game
     * @param drawnTickets : all tickets drawn by the player initially
     * @param chosenTickets : tickets kept by the player
     * @throws IllegalArgumentException if drawn tickets does not contain the chosen tickets
     * @return new GameState with the current player's chosen additional tickets, and less tickets in the ticket draw pile
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        Map<PlayerId, PlayerState> psMap = new EnumMap<>(playerStateMap);

        psMap.put(super.currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(psMap, ticketDeck.withoutTopCards(drawnTickets.size()), cardState, super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * Draws a card from the chosen card at index slot from the face-up cards and put into the player's hand and replaces
     * it with one from the draw pile
     * @param slot : the index of the drawn face-up card
     * @return a new GameState with the drawn card removed from the face up cards, and a card added in that slot from the draw pile
     */
    public GameState withDrawnFaceUpCard(int slot){
        Map<PlayerId, PlayerState> psMap = new EnumMap<>(playerStateMap);

        Card cardToAdd = cardState.faceUpCard(slot);
        psMap.put(super.currentPlayerId(), currentPlayerState().withAddedCard(cardToAdd));

        return new GameState(psMap, ticketDeck, cardState.withDrawnFaceUpCard(slot), super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * Draws the top card from the draw pile and place it in the player's hand
     * @return a new GameState with a blindly drawn card
     */
    public GameState withBlindlyDrawnCard(){
        Map<PlayerId, PlayerState> psMap = new EnumMap<>(playerStateMap);
        Card cardOnTopOfTheDeck = cardState.topDeckCard();

        psMap.put(super.currentPlayerId(), currentPlayerState().withAddedCard(cardOnTopOfTheDeck));

        return new GameState(psMap, ticketDeck, cardState.withoutTopDeckCard(), super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * Claims a route with a given set of cards
     * (thus adds the route to the player's collection and removes the cards he used)
     * @param route : the claimed route
     * @param cards : the claim cards
     * @return a new GameState with a new route added and less cards for the current player
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        Map<PlayerId, PlayerState> psMap = new EnumMap<>(playerStateMap);
        psMap.put(super.currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));
        CardState newState = cardState.withMoreDiscardedCards(cards);

        return new GameState(psMap, ticketDeck, newState, super.currentPlayerId(), super.lastPlayer());
    }

    /**
     * Determines when the last turn of the game begins
     * @return true if the last player is not already known and if the current player has at most 2 cars left
     */
    public boolean lastTurnBegins(){
        boolean lastPlayerIsUnknown = (super.lastPlayer() == null);

        int numberOfCars = super.currentPlayerState().carCount();
        int minWagons = 2;

        boolean onlyTwoWagonsLeftOrLess = numberOfCars <= minWagons;

        return lastPlayerIsUnknown && onlyTwoWagonsLeftOrLess;
    }

    /**
     * The state of the game where it's the next player's turn to play.
     * Changes the last player's id if the last turn begins.
     * @return a new GameState where it's the next player's turn
     */
    public GameState forNextTurn(){
        PlayerId lastPlayer = (lastTurnBegins()) ? super.currentPlayerId() : super.lastPlayer();
        PlayerId otherPlayer = super.currentPlayerId().next();

        return new GameState(playerStateMap, ticketDeck, cardState, otherPlayer, lastPlayer);
    }
}
