package ch.epfl.tchu.game;


import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    private static class DeckMap{
        private static Deck<Card> testDeck = Deck.of(SortedBag.of(Card.CARS), new Random());

    }

    @Test
    void of() {

    }

    @Test
    void size() {
    }

    @Test
    void isEmpty() {
    }

    @Test
    void topCard() {
    }

    @Test
    void withoutTopCard() {
    }

    @Test
    void topCards() {
    }

    @Test
    void withoutTopCards() {
    }
}