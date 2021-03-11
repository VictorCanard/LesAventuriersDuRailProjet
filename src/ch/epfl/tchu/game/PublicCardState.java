package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import java.util.Objects;


public class PublicCardState {

    private final List<Card> FACEUPCARDS;
    private final int DECKSIZE;
    private final int DISCARDSSIZE;

    /**
     * PublicCardState constructor
     * @param faceUpCards : cards that are shown as visible
     * @param deckSize : size of the deck
     * @param discardsSize : size of the discard pile
     * @throws IllegalArgumentException if there are not exactly 5 cards to be face-up
     * or if the sizes of the draw piles and discard piles are negative
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >=0 && discardsSize >=0);
        this.FACEUPCARDS = List.copyOf(faceUpCards);
        this.DECKSIZE = deckSize;
        this.DISCARDSSIZE = discardsSize;
    }

    /**
     * Getter for the total size of the PublicCardState
     * @return sum of the sizes of the deck pile, discard pile and the amount of faceup cards
     */
    public int totalSize(){
        return DECKSIZE + DISCARDSSIZE + FACEUPCARDS.size();
    }

    /**
     * Getter for the 5 cards that are face-up
     * @return a list of cards
     */
    public List<Card> faceUpCards(){
        return FACEUPCARDS;
    }

    /**
     * Getter for the face-up card at the given slot index
     * @param slot : index of the face-up card to retrieve
     * @throws IndexOutOfBoundsException if the slot isn't in [0;5[
     * @return a specific card at a given index
     */
    public Card faceUpCard(int slot){
        Objects.checkIndex(slot, 5);
        return FACEUPCARDS.get(slot);
    }

    /**
     * Getter for the size of the deck
     * @return an integer
     */
    public int deckSize(){
        return DECKSIZE;
    }

    /**
     * Getter to see if the deck is empty
     * @return true if it is, false if it isn't
     */
    public boolean isDeckEmpty(){
        return DECKSIZE==0;
    }

    /**
     * Getter for the size of the discard pile
     * @return an integer
     */
    public int discardsSize(){
        return DISCARDSSIZE;
    }
}
