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
    private SortedBag<Card> discardPile;


    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> drawPile, SortedBag<Card> discardPile){
        super(faceUpCards, deckSize, discardsSize);
        Preconditions.checkArgument(discardPile.size()>=0);
        this.discardPile = discardPile;
        this.drawPile = drawPile;
    }

    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>=Constants.FACE_UP_CARDS_COUNT);

       faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();
       drawPile = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

       return new CardState(faceUpCards, drawPile.size(), 0, drawPile, SortedBag.of());
    }

    public CardState withDrawnFaceUpCard(int slot){ //works with the 5 face up cards
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
