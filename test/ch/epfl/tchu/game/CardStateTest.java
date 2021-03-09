package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CardStateTest {


    Deck<Card> drawPile =  Deck.of(SortedBag.of(5, Card.BLUE, 7, Card.GREEN), new Random());
    CardState state1 =  CardState.of(drawPile);

    Deck<Card> drawPile2 = CardStateTest.deckBuilder();

    static Deck<Card> deckBuilder(){
        SortedBag.Builder<Card> cardsBuilder1 = new SortedBag.Builder<>();
        for (Card w : List.of(Card.LOCOMOTIVE, Card.BLUE, Card.BLACK, Card.GREEN, Card.YELLOW, Card.LOCOMOTIVE, Card.BLUE)) {
            cardsBuilder1.add(w);
        }
        SortedBag<Card> cardsA = cardsBuilder1.build();

        return Deck.of(cardsA, new Random());

    }

    @Test
    void of() {
        System.out.println("top card: " + drawPile.topCard());
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
    }

    @Test
    void withDrawnFaceUpCard() {
        CardState state3 = CardState.of(drawPile2);
        System.out.println("face up cards: "+ state3.faceUpCards());
        System.out.println("top deck card: " + state3.topDeckCard());
        System.out.println("replacing slot 2 of face up cards with top deck card" + state3.withDrawnFaceUpCard(2).faceUpCards());
        System.out.println("end withDFUP");


    }

    @Test
    void topDeckCard() {
    }

    @Test
    void withoutTopDeckCard() {
    }

    @Test
    void withDeckRecreatedFromDiscards() {
    }

    @Test
    void withMoreDiscardedCards() {
    }
}