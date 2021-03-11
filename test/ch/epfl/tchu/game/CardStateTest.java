package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CardStateTest {

    Deck<Card> drawPile =  Deck.of(SortedBag.of(5, Card.BLUE, 7, Card.GREEN), new Random());
    Deck<Card> drawPiletestfortopcard =  Deck.of(SortedBag.of(1, Card.BLACK, 6, Card.LOCOMOTIVE), new Random());
    CardState state1 =  CardState.of(drawPile);
    Deck<Card> drawPile2 = CardStateTest.deckBuilder();
    SortedBag<Card> discards = CardStateTest.discardBuilder();

    static Deck<Card> deckBuilder(){
        SortedBag.Builder<Card> cardsBuilder1 = new SortedBag.Builder<>();
        for (Card w : List.of(Card.LOCOMOTIVE, Card.BLUE, Card.BLACK, Card.GREEN, Card.YELLOW, Card.LOCOMOTIVE, Card.BLUE)) {
            cardsBuilder1.add(w);
        }
        SortedBag<Card> cardsA = cardsBuilder1.build();

        return Deck.of(cardsA, new Random());
    }

    static SortedBag<Card> discardBuilder(){
        SortedBag.Builder<Card> cardsBuilder1 = new SortedBag.Builder<>();
        for (Card w : List.of(Card.LOCOMOTIVE, Card.GREEN, Card.YELLOW, Card.LOCOMOTIVE, Card.BLUE, Card.LOCOMOTIVE, Card.GREEN, Card.YELLOW, Card.LOCOMOTIVE, Card.BLUE)) {
            cardsBuilder1.add(w);
        }
        SortedBag<Card> cardsA = cardsBuilder1.build();
        return cardsA;
    }

    /////////////////////////////////////////////////////////////////////
    @Test
    void myTest(){



    }

    @Test
    void of() {

        assertThrows(IllegalArgumentException.class, () ->
        {
            PublicCardState cardStateFAIL = CardState.of(Deck.of(SortedBag.of(Card.GREEN), new Random()));
        });

        System.out.println("top card: " + drawPile.topCard());
        System.out.println("top 5 cards: " + drawPile.topCards(5));
        System.out.println("face up cards: " + state1.faceUpCards());
        System.out.println("----------end state1----------");

        assertEquals(0, state1.discardsSize());
        assertEquals(7, state1.deckSize()); //because top 5 cards are gone


        System.out.println("top 5 cards : " + drawPile2.topCards(5));

        CardState state2 = CardState.of(drawPile2);
        System.out.println("face up cards: " + state2.faceUpCards());
        System.out.println("face up card slot 0: " +state2.faceUpCard(0));
        assertEquals(2, state2.deckSize());

        System.out.println("----------end state2----------"); //all in enum order? not very shuffled....

        System.out.println("top 5 cards: " + drawPiletestfortopcard.topCards(5));
        System.out.println("top card: " + drawPiletestfortopcard.topCard());
        CardState stateTopTest = CardState.of(drawPiletestfortopcard);
        System.out.println("Faceup cards: " + stateTopTest.faceUpCards());
        System.out.println("faceUP card slot 0: "+ stateTopTest.faceUpCard(0));
        System.out.println("faceUP card slot 1: "+ stateTopTest.faceUpCard(1));
        System.out.println("----------end stateTopTest----------");
    }

    @Test
    void withDrawnFaceUpCard() {
        SortedBag<Card> empty = SortedBag.of();
        assertThrows(IllegalArgumentException.class, () ->
        {
            PublicCardState cardStateFAIL = CardState.of(Deck.of(empty, new Random()));
        });
        Deck<Card> lastCardDeck = Deck.of(SortedBag.of(Card.ORANGE), new Random());
        System.out.println("top deck card: "+ lastCardDeck.topCard());
        System.out.println("---------------");
        CardState state3 = CardState.of(drawPile2);
        System.out.println("deck size: " + state3.deckSize());
        System.out.println("face up cards: "+ state3.faceUpCards());
        System.out.println("top deck card: " + state3.topDeckCard());
        System.out.println("replacing slot 2 of face up cards with top deck card" + state3.withDrawnFaceUpCard(2).faceUpCards());
        System.out.println("new deck size: " + state3.deckSize());
        System.out.println("----------end withDFUP_1----------");


       // System.out.println("Deck size without top card: " +state3.withoutTopDeckCard().deckSize());

        CardState finalState = state3;
        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            finalState.withDrawnFaceUpCard(5);
        });


        state3 =  state3.withoutTopDeckCard();
        assertEquals(1, state3.deckSize());
        System.out.println("top deck card (last card): " + state3.topDeckCard());
        System.out.println("State3 face up cards: " + state3.faceUpCards());
        System.out.println("replacing last card with " + state3.topDeckCard() + ": " + state3.withDrawnFaceUpCard(4).faceUpCards());
        state3 = state3.withDrawnFaceUpCard(4);
        assertEquals(0, state3.deckSize());
        System.out.println("----------end withDFUP_2----------");

        CardState finalState1 = state3;
        assertThrows(IllegalArgumentException.class, () ->
        {
            finalState1.withDrawnFaceUpCard(2);
        });
    }

    @Test
    void topDeckCard() {
        Deck<Card> smallDeck = drawPile2.withoutTopCards(5);
        System.out.println(smallDeck.topCard());
        CardState state4 = CardState.of(drawPile2);
        System.out.println(state4.topDeckCard());
        assertEquals(smallDeck.topCard(), state4.topDeckCard());
    }

    @Test
    void withoutTopDeckCard() {
        Deck<Card> smallDeck = drawPile2.withoutTopCards(6);
        CardState state4 = CardState.of(drawPile2);
        CardState state5 = state4.withoutTopDeckCard();
        assertEquals(1, state5.deckSize());
        assertEquals(smallDeck.topCard(), state5.topDeckCard());

        CardState state6 = state5.withoutTopDeckCard();
        assertEquals(0, state6.deckSize());

    }

    @Test
    void withDeckRecreatedFromDiscards() {
        CardState state6 = CardState.of(drawPile2); //no discards in this pile

        assertThrows(IllegalArgumentException.class, () ->
        {
            state6.withDeckRecreatedFromDiscards(new Random()); //because pioche not empty
        });

       CardState state7 =  state6.withMoreDiscardedCards(discards);
        System.out.println("discard cards: " +discards);
        System.out.println("discards: " + state7.discardsSize());
        System.out.println("face up cards are: " + state7.faceUpCards());

        CardState state81 = state7.withoutTopDeckCard();
        System.out.println("draw pile number is: "+ state81.deckSize());

        CardState state82 = state81.withoutTopDeckCard();
        System.out.println("draw pile number is: "+ state82.deckSize());

        CardState newState =  state82.withDeckRecreatedFromDiscards(new Random());
        System.out.println("face up cards of new state: " +newState.faceUpCards());
        assertEquals(10, newState.deckSize());
        System.out.println("----------end of newState----------");
       //-----no discards----------
            CardState stateLonely = state6.withMoreDiscardedCards(SortedBag.of());
        stateLonely = stateLonely.withoutTopDeckCard();
        stateLonely = stateLonely.withoutTopDeckCard();
        assertEquals(0, stateLonely.deckSize());

        CardState emptyState = stateLonely.withDeckRecreatedFromDiscards(new Random());
        assertEquals(0, emptyState.deckSize());
        System.out.println("empty state face up cards: " + emptyState.faceUpCards());
        //aaaa
    }

    @Test
    void withMoreDiscardedCards() {
        CardState state6 = CardState.of(drawPile2);
        SortedBag empty = SortedBag.of();
        assertEquals(10, state6.withMoreDiscardedCards(discards).discardsSize());
        assertEquals(0, state6.withMoreDiscardedCards(empty).discardsSize());
        assertEquals(2, state6.withMoreDiscardedCards(empty).deckSize());
        assertEquals(2, state6.withMoreDiscardedCards(discards).deckSize());
    }
}