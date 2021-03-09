package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.*;

public final class CardState extends PublicCardState{

    private final Deck<Card> DRAW_PILE;
    private final SortedBag<Card> DISCARD_PILE;
    private final Card TOP_CARD;

    private CardState(List<Card> faceUpCards, int deckSize, int discardsSize, Deck<Card> drawPile, SortedBag<Card> discardPile){
        super(faceUpCards, deckSize, discardsSize);
        Preconditions.checkArgument(discardPile.size()>=0);
        DRAW_PILE = drawPile;
        TOP_CARD = DRAW_PILE.topCard();
        DISCARD_PILE = discardPile;
    }

    public static CardState of(Deck<Card> deck){
        Preconditions.checkArgument(deck.size()>=Constants.FACE_UP_CARDS_COUNT);

        List<Card> faceUpCards = deck.topCards(Constants.FACE_UP_CARDS_COUNT).toList();
        Deck<Card> newDrawPile = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

        return new CardState(faceUpCards, newDrawPile.size(), 0, newDrawPile, SortedBag.of());
    }

    public CardState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(!(DRAW_PILE.isEmpty()));
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);

        List<Card> newFaceUpCards = new ArrayList<>();
        newFaceUpCards.addAll(this.faceUpCards());
        newFaceUpCards.set(slot, TOP_CARD);

        return new CardState(newFaceUpCards, DRAW_PILE.withoutTopCard().size(), this.discardsSize(), DRAW_PILE.withoutTopCard(), DISCARD_PILE);
    }
    public Card topDeckCard(){
        Preconditions.checkArgument(!(DRAW_PILE.isEmpty()));
        return DRAW_PILE.topCard();
    }
    public CardState withoutTopDeckCard(){
        Preconditions.checkArgument(!(DRAW_PILE.isEmpty()));
            return new CardState(faceUpCards(), deckSize() - 1, discardsSize(), DRAW_PILE.withoutTopCard(), DISCARD_PILE);
    }
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(DRAW_PILE.isEmpty());

        Deck<Card> newDrawPile = Deck.of(DISCARD_PILE, rng);

        return new CardState(faceUpCards(), newDrawPile.size(), 0, newDrawPile, SortedBag.of());
    }
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards){
        return new CardState(super.faceUpCards(), super.deckSize(), additionalDiscards.size(), DRAW_PILE, additionalDiscards);
    }
}
