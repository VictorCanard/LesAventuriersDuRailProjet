package ch.epfl.tchu.game;

import java.util.List;

public class PublicCardState {
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize){}

    public int totalSize(){}
    public List<Card> faceUpCards(){}
    public Card faceUpCard(int slot){}
    public int deckSize(){}
    public boolean isDeckEmpty(){}
    public int discardsSize(){}
}
