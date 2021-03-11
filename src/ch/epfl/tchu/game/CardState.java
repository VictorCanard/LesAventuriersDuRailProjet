package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.*;

/**
 * @author Anne-Marie Rusu (296098)
 */
public final class CardState extends PublicCardState{

    private final Deck<Card> DRAW_PILE;
    private final SortedBag<Card> DISCARD_PILE;
    private final Card TOP_CARD;

    /**
     * CardState constructor (private so the class has control on the arguments that are passed)
     * @param faceUpCards : cards in the faceUp Pile
     * @param deckSize : size of the Deck (draw pile)
     * @param discardsSize : size of the Discard Pile
     * @param drawPile : cards of the Draw Pile
     * @param discardPile : cards in the Discard Pile
     * @throws IllegalArgumentException if the size of the discard pile is negative
     */
    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> drawPile, SortedBag<Card> discardPile){
        super(faceUpCards, deckSize, discardsSize);
        Preconditions.checkArgument(discardPile.size()>=0);
        DRAW_PILE = drawPile;
        if(!DRAW_PILE.isEmpty()){
            TOP_CARD = DRAW_PILE.topCard();
        }else{
            TOP_CARD = null;
        }
        DISCARD_PILE = discardPile;
    }

    /**
     * Static method to initialize a card state
     * @param deck : Deck of cards to make a new card state
     * Makes a list of cards to be face-up from the deck and creates a draw pile
     * @throws IllegalArgumentException if the size of the deck given as an argument is strictly inferior to 5 cards
     * @return a new card state with no discards, a certain number of face-up Cards and the rest of the deck as a draw pile
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>=Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();
        Deck<Card> newDrawPile = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

        return new CardState(faceUpCards, newDrawPile.size(), 0, newDrawPile, SortedBag.of());
    }

    /**
     *Returns a new set of cards nearly identical to this but where the visible card of index slot has been replaced by the one on top of the draw pile
     * (the one of top of the draw pile is thus removed from the draw pile)
     * @param slot : index of the face up card to be replaced
     * @throws IllegalArgumentException if the draw pile isn't empty
     * @throws IndexOutOfBoundsException if the index slot isn't included in [0;5[
     * @return a new set of cards with different faceUp and draw piles
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!(DRAW_PILE.isEmpty()));
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> newFaceUpCards = new ArrayList<>(this.faceUpCards());
        newFaceUpCards.set(slot, TOP_CARD);

        return new CardState(newFaceUpCards, DRAW_PILE.withoutTopCard().size(), this.discardsSize(), DRAW_PILE.withoutTopCard(), DISCARD_PILE);
    }

    /**
     * Getter for the deck's top card
     * @throws IllegalArgumentException if draw pile is empty
     * @return the card at the top of the deck
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!(DRAW_PILE.isEmpty()));
        return DRAW_PILE.topCard();
    }

    /**
     * Getter for a new deck identical but without the top card
     * @throws IllegalArgumentException if draw pile is empty
     * @return a deck without top card
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!(DRAW_PILE.isEmpty()));
        return new CardState(super.faceUpCards(), deckSize() - 1, super.discardsSize(), DRAW_PILE.withoutTopCard(), DISCARD_PILE);
    }

    /**
     * Getter for a new draw pile recreated from the discard pile
     * (the drawPile is shuffled by the method of() from the class Deck)
     * @param rng : the random number generator to shuffle the draw pile
     * @throws IllegalArgumentException if the draw pile is not empty
     * @return a new CardState with the new draw pile
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(DRAW_PILE.isEmpty());

        Deck<Card> newDrawPile = Deck.of(DISCARD_PILE, rng);

        return new CardState(super.faceUpCards(), newDrawPile.size(), 0, newDrawPile, SortedBag.of());
    }

    /**
     * Getter for a new CardState with additional cards added to the discard pile
     * @param additionalDiscards : cards to add to this CardState's discard pile
     * @return a new CardState with the updated discard pile
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(super.faceUpCards(), super.deckSize(), additionalDiscards.size(), DRAW_PILE, additionalDiscards);
    }
}
