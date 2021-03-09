package ch.epfl.tchu.game;


import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    SortedBag<Card> testBag = SortedBag.of(5, Card.BLUE, 7, Card.GREEN);
    SortedBag<Card> preciseBag = SortedBag.of(Card.ALL);

    Deck<Card> deck1 = Deck.of(testBag, new Random());



    @Test
    void checkIfRandom(){
        Deck<Card> deck1 = Deck.of(testBag, new Random());
        Deck<Card> deck2 = Deck.of(testBag, new Random());

        for (int i = 0; i < testBag.size(); i++) {
            System.out.println(deck1.topCard());
            deck1 = deck1.withoutTopCard();
            System.out.println(deck2.topCard());
            deck2 = deck2.withoutTopCard();
        }
    }
    @Test
    void checkThrowsExceptions(){
        assertThrows(IllegalArgumentException.class,() ->{
            deck1.topCards(-1);

        });
        assertThrows(IllegalArgumentException.class,() ->{

            deck1.topCards(15);
        });
        assertThrows(IllegalArgumentException.class,() ->{
            deck1.withoutTopCards(-1);

        });
        assertThrows(IllegalArgumentException.class,() ->{
            deck1.withoutTopCards(17);

        });


    }
    @Test
    void of() {
        Deck<Card> deck1 = Deck.of(testBag, new Random());
        assertTrue(!deck1.isEmpty());
        assertTrue(deck1.size()==12);

        SortedBag<Card> allCards = deck1.topCards(12);
        assertTrue(allCards.countOf(Card.BLUE)==5);
        assertTrue(allCards.countOf(Card.GREEN)==7);


    }

    @Test
    void size() {
        Deck<Card> deck1 = Deck.of(testBag, new Random());
        assertTrue(deck1.size()==12);
    }

    @Test
    void isEmpty() {
        SortedBag.Builder builder = new SortedBag.Builder();
        SortedBag<Card> emptySortedBag = builder.build();
        Deck<Card> newDeck = Deck.of(emptySortedBag, new Random());

        assertTrue(newDeck.isEmpty());
        assertTrue(newDeck.size()==0);
        assertThrows(IllegalArgumentException.class,() ->{
            newDeck.topCard();
        });
        assertThrows(IllegalArgumentException.class,() ->{
            newDeck.withoutTopCard();
        });
    }

    @Test
    void topCard() {
        Deck<Card> deck1 = Deck.of(testBag, new Random());
        System.out.println(deck1.topCard());

    }

    @Test
    void withoutTopCard() {
        Deck<Card> deck1 = Deck.of(preciseBag, new Random());
        Card cardOne = deck1.topCard();
        System.out.println(cardOne);
        deck1 = deck1.withoutTopCard();
        Card cardTwo = deck1.topCard();
        System.out.println(cardTwo);

        assertTrue(!cardOne.equals(cardTwo));
    }

    @Test
    void topCards() {
        Deck<Card> deck1 = Deck.of(testBag, new Random());
        SortedBag<Card> bag = deck1.topCards(0);
        for (int i = 1; i <= 12; i++) {
            bag = deck1.topCards(i);
            System.out.println(bag);
        }
        assertEquals(deck1.size(), 12);
        assertEquals(bag.size(), 12);



    }

    @Test
    void withoutTopCards() {
        Deck<Card> emptyDeck = deck1.withoutTopCards(12);

        assertTrue(emptyDeck.isEmpty());

    }
}