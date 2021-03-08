package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Deck;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class CardState extends PublicCardState{

    private static List<Card> faceUpCards;
    private static Deck<Card> drawPile;
    private static Card topCard;


    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize){
        super(faceUpCards, deckSize, discardsSize);
    }

    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>=5);

       faceUpCards = deck.topCards(5).toList();
       drawPile = deck.withoutTopCards(5);

       return new CardState(faceUpCards, drawPile.size(), 0);
    }

    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument();
        Objects.checkIndex(slot, 5);
        topCard = drawPile.topCard();
        drawPile = drawPile.withoutTopCard();
        faceUpCards = faceUpCards.set(slot, topCard); //???????????/


        return new CardState(faceUpCards, drawPile.size(), this.discardsSize());

    }
    public Card topDeckCard(){
        return this.faceUpCard(0);
    }
    public CardState withoutTopDeckCard(){

    }
    public CardState withDeckRecreatedFromDiscards(Random rng){
        return
    }
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){

    }
}
