/*
package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {
    @Test
    void cardValuesAreDefinedInTheRightOrder() {
        var expectedValues = new Card[]{
                Card.BLACK, Card.VIOLET, Card.BLUE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.RED, Card.WHITE, Card.LOCOMOTIVE
        };
        assertArrayEquals(expectedValues, Card.values());
    }

    @Test
    void cardAllIsDefinedCorrectly() {
        Assertions.assertEquals(List.of(Card.values()), Card.ALL);
    }

    @Test
    void cardCountIsDefinedCorrectly() {
        Assertions.assertEquals(9, Card.COUNT);
    }

    @Test
    void cardOfWorksForAllColors() {
        var allCards = Card.values();
        for (var color : Color.values())
            assertEquals(allCards[color.ordinal()], Card.of(color));
    }

    @Test
    void cardColorWorksForAllColors() {
        var allColors = Color.values();
        for (var card : Card.values()) {
            if (card != Card.LOCOMOTIVE)
                assertEquals(allColors[card.ordinal()], card.color());
        }
    }
}*/
