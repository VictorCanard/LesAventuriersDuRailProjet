package ch.epfl.tchu.game;


import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    SortedBag<Card> testBag = SortedBag.of(5, Card.BLUE, 7, Card.GREEN);
    SortedBag<Card> preciseBag = SortedBag.of(Card.ALL);

    Deck<Card> deck1 = Deck.of(testBag, new Random());

    public static SortedBag.Builder builder = new SortedBag.Builder();
    public static SortedBag<Card> emptySortedBag = builder.build();

    public final static Random NON_RANDOM = new Random(){
        @Override
        public int nextInt(int i){
            return i-1;
        }
    };

    @Test
    void nonRandomTest(){
        SortedBag<String> cards = SortedBag.of(2, "as de pique", 3, "dame de cœur");
        Deck<String> deck = Deck.of(cards, NON_RANDOM);

        for (int i = 0; i < deck.size(); i++) {
            assertEquals(cards.get(i), deck.topCard());
            deck = deck.withoutTopCard();
        }
    }

    @Test
    void checkIfRandom(){
        Deck<Card> deck1 = Deck.of(preciseBag, new Random());
        Deck<Card> deck2 = Deck.of(preciseBag, new Random());

        int counter =0;

        for (int i = 0; i < preciseBag.size(); i++) {
            Card card1 = deck1.topCard();
            System.out.println(card1);
            deck1 = deck1.withoutTopCard();
            Card card2 = deck2.topCard();
            System.out.println(card2);
            deck2 = deck2.withoutTopCard();
            counter = card1.equals(card2) ? counter+1 : counter;

        }
        assertTrue(counter<3);
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

        Deck<Card> deck2 = Deck.of(emptySortedBag, new Random());
        assertTrue(deck2.size()==0);

        SortedBag<Card> newBag = SortedBag.of(5, Card.BLUE, 20, Card.BLACK);
        Deck<Card> deck3 = Deck.of(newBag, new Random());
        assertTrue(deck3.size()==25);
    }

    @Test
    void isEmpty() {

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

        for (int i = 0; i < 10; i++) {
            Deck<Card> deck1 = Deck.of(testBag, NON_RANDOM);
            for (int j = 0; j < deck1.size(); j++) {
                assertEquals(testBag.get(j), deck1.topCard());
                deck1 = deck1.withoutTopCard();
            }
        }

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