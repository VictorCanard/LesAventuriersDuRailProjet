package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Represents the state of the playing cards at a point in the game
 *
 * @author Anne-Marie Rusu (296098)
 */
public final class CardState extends PublicCardState {

    private final SortedBag<Card> discardPile;

    private final Deck<Card> drawPile;


    /**
     * Constructs the card state with the given arguments (private so the class has control on the arguments that are passed)
     *
     * @param faceUpCards : cards in the faceUp Pile
     * @param discardPile : cards in the Discard Pile
     * @param drawPile    : cards of the Draw Pile
     */
    private CardState(List<Card> faceUpCards, Deck<Card> drawPile, SortedBag<Card> discardPile) {
        super(faceUpCards, drawPile.size(), discardPile.size());

        this.discardPile = discardPile;
        this.drawPile = drawPile;
    }


    /**
     * Initializes the card state from a given deck of cards to a specific starting arrangement
     *
     * @param deck : Deck of cards to make a new card state
     * @return a new card state with no discards, 5 face-up cards and the rest of the deck as the draw pile
     * @throws IllegalArgumentException if the size of the deck given as an argument is strictly inferior to 5 cards
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = new ArrayList<>();

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }

        return new CardState(faceUpCards, deck, SortedBag.of());
    }

    /**
     * Returns a new set of cards nearly identical to this but where the visible card of index slot has been replaced
     * by the one on top of the draw pile
     * (the one of top of the draw pile is thus removed from the draw pile)
     *
     * @param slot : index of the face up card to be replaced
     * @return a new card state with different faceUp cards and draw piles
     * @throws IllegalArgumentException  if the draw pile isn't empty
     * @throws IndexOutOfBoundsException if the index slot isn't included in [0;5[
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!(drawPile.isEmpty()));
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> newFaceUpCards = new ArrayList<>(super.faceUpCards());
        newFaceUpCards.set(slot, drawPile.topCard());

        return new CardState(newFaceUpCards, drawPile.withoutTopCard(), discardPile);
    }

    /**
     * Getter for the draw pile's top card
     *
     * @return the card at the top of the draw pile
     * @throws IllegalArgumentException if draw pile is empty
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!(drawPile.isEmpty()));

        return drawPile.topCard();
    }

    /**
     * "Removes" a card from the top of the draw pile
     *
     * @return a new card state with the top draw pile card removed
     * @throws IllegalArgumentException if draw pile is empty
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!(drawPile.isEmpty()));

        return new CardState(faceUpCards(), drawPile.withoutTopCard(), discardPile);
    }

    /**
     * "Recreates" a draw pile using the cards from the discard pile
     *
     * @param rng : the random number generator to shuffle the draw pile
     * @return a new CardState where the draw pile is a shuffled discard pile and the discard pile is empty
     * @throws IllegalArgumentException if the draw pile is not empty
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(drawPile.isEmpty());

        Deck<Card> newDrawPile = Deck.of(discardPile, rng);
        return new CardState(faceUpCards(), newDrawPile, SortedBag.of());
    }

    /**
     * "Adds" cards to the discard pile
     *
     * @param additionalDiscards : cards to add to this CardState's discard pile
     * @return a new card state with more cards in the discard pile
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        SortedBag<Card> newDiscards = discardPile.union(additionalDiscards);

        return new CardState(faceUpCards(), drawPile, newDiscards);
    }
}
