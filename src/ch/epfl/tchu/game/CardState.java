package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Random;

public class CardState extends PublicCardState{
    private CardState(){
        super();

    }
    public static CardState of(Deck<Card> deck){}

    public CardState withDrawnFaceUpCard(int slot){}
    public Card topDeckCard(){}
    public CardState withoutTopDeckCard(){}
    public CardState withDeckRecreatedFromDiscards(Random rng){}
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){}
}
