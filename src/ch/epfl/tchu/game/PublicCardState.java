package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import java.util.Objects;

/**
 * Represents the state of the playing cards at a point in the game, viewable by everyone in the game
 * @author Anne-Marie Rusu (296098)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructs the public card state from the given arguments
     * @param faceUpCards : cards that are shown as visible
     * @param deckSize : size of the deck
     * @param discardsSize : size of the discard pile
     * @throws IllegalArgumentException if there are not exactly 5 cards to be face-up
     * or if the sizes of the draw piles and discard piles are negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >=0 && discardsSize >=0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Getter for the 5 cards that are face-up
     * @return a list of cards
     */
    public List<Card> faceUpCards(){
        return faceUpCards;
    }

    /**
     * Getter for the face-up card at the given slot index
     * @param slot : index of the face-up card to retrieve
     * @throws IndexOutOfBoundsException if the slot isn't in [0;5[
     * @return a specific card at a given index
     */
    public Card faceUpCard(int slot){
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        return faceUpCards.get(slot);
    }

    /**
     * Getter for the size of the deck
     * @return an integer
     */
    public int deckSize(){
        return deckSize;
    }

    /**
     * Getter to see if the deck is empty
     * @return true if it is, false if it isn't
     */
    public boolean isDeckEmpty(){
        return deckSize == 0;
    }

    /**
     * Getter for the size of the discard pile
     * @return an integer
     */
    public int discardsSize(){
        return discardsSize;
    }
}
