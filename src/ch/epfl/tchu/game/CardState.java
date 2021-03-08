package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Random;

public class CardState extends PublicCardState{
    private CardState(){
        super(null,0,0);

    }
    public static CardState of(Deck<Card> deck){return null;}

    public CardState withDrawnFaceUpCard(int slot){return null;}
    public Card topDeckCard(){return null;}
    public CardState withoutTopDeckCard(){return null;}
    public CardState withDeckRecreatedFromDiscards(Random rng){return null;}
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){return null;}
}
