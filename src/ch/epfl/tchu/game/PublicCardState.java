package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.List;
import java.util.Objects;


public class PublicCardState {

    private final List<Card> FACEUPCARDS;
    private final int DECKSIZE;
    private final int DISCARDSSIZE;

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT && deckSize >=0 && discardsSize >=0);
        this.FACEUPCARDS = List.copyOf(faceUpCards);
        this.DECKSIZE = deckSize;
        this.DISCARDSSIZE = discardsSize;
    }

    public int totalSize(){
        return DECKSIZE + DISCARDSSIZE + FACEUPCARDS.size();
    }

    public List<Card> faceUpCards(){
        return FACEUPCARDS;
    }

    public Card faceUpCard(int slot){
        Objects.checkIndex(slot, 5);
        return FACEUPCARDS.get(slot);
    }
    public int deckSize(){
        return DECKSIZE;
    }

    public boolean isDeckEmpty(){
        return DECKSIZE==0;
    }

    public int discardsSize(){
        return DISCARDSSIZE;
    }
}
