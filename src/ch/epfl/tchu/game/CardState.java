package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.*;

/**
 * @author Anne-Marie Rusu (296098)
 */
public final class CardState extends PublicCardState{

    private final SortedBag<Card> discardPile;

    private final Deck<Card> drawPile;



    /**
     * CardState constructor (private so the class has control on the arguments that are passed)
     * @param faceUpCards : cards in the faceUp Pile
     * @param discardPile : cards in the Discard Pile
     * @param drawPile : cards of the Draw Pile
     */
    private CardState(List<Card> faceUpCards, Deck<Card> drawPile, SortedBag<Card> discardPile){
        super(faceUpCards, drawPile.size(), discardPile.size());

        this.discardPile = discardPile;
        this.drawPile = drawPile;
    }


    /**
     * Static method to initialize a card state
     * @param deck : Deck of cards to make a new card state
     * Makes a list of cards to be face-up from the deck and creates a draw pile
     * @throws IllegalArgumentException if the size of the deck given as an argument is strictly inferior to 5 cards
     * @return a new card state with no discards, a certain number of face-up Cards and the rest of the deck as a draw pile
     */
    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size() >= 5);

        List<Card> faceUpCards = new ArrayList<>();

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }

        return new CardState(faceUpCards,  deck, SortedBag.of());
    }

    /**
     *Returns a new set of cards nearly identical to this but where the visible card of index slot has been replaced
     * by the one on top of the draw pile
     * (the one of top of the draw pile is thus removed from the draw pile)
     * @param slot : index of the face up card to be replaced
     * @throws IllegalArgumentException if the draw pile isn't empty
     * @throws IndexOutOfBoundsException if the index slot isn't included in [0;5[
     * @return a new set of cards with different faceUp cards and draw piles
     */
    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!(drawPile.isEmpty()));
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> newFaceUpCards = new ArrayList<>(super.faceUpCards());
        newFaceUpCards.set(slot, drawPile.topCard());

        return new CardState(newFaceUpCards, drawPile.withoutTopCard(), discardPile);
    }

    /**
     * Getter for the deck's top card
     * @throws IllegalArgumentException if draw pile is empty
     * @return the card at the top of the deck
     */
    public Card topDeckCard(){
        Preconditions.checkArgument(!(drawPile.isEmpty()));

        return drawPile.topCard();
    }

    /**
     * Getter for a new deck identical but without the top card
     * @throws IllegalArgumentException if draw pile is empty
     * @return a deck without its top card
     */
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!(drawPile.isEmpty()));

        return new CardState(faceUpCards(),  drawPile.withoutTopCard(), discardPile);
    }

    /**
     * Getter for a new draw pile recreated from the discard pile
     * (the drawPile is shuffled by the method of() from the class Deck)
     * @param rng : the random number generator to shuffle the draw pile
     * @throws IllegalArgumentException if the draw pile is not empty
     * @return a new CardState where the draw pile is a shuffled discard pile and the discard pile is thus empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(drawPile.isEmpty());

        Deck<Card> newDrawPile = Deck.of(discardPile, rng);
        return new CardState(faceUpCards(), newDrawPile, SortedBag.of());
    }

    /**
     * Getter for a new CardState with additional cards added to the discard pile
     * @param additionalDiscards : cards to add to this CardState's discard pile
     * @return a new CardState with the updated discard pile
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        SortedBag<Card> newDiscards = discardPile.union(additionalDiscards);

        return new CardState(super.faceUpCards(), drawPile, newDiscards);
    }
}
