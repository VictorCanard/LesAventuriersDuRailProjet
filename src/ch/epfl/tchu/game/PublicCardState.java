package ch.epfl.tchu.game;

import java.util.List;

public class PublicCardState {
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){}

    public int totalSize(){return 0;}
    public List<Card> faceUpCards(){return null;}
    public Card faceUpCard(int slot){return null;}
    public int deckSize(){return 0;}
    public boolean isDeckEmpty(){return false;}
    public int discardsSize(){return 0;}
}
