package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicCardStateTest {
    List<Card> faceUpCards1 = new ArrayList<Card>(List.of(Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.BLUE, Card.RED));
    List<Card> faceUpCardsFAIL = new ArrayList<Card>(List.of(Card.BLUE, Card.RED));
    List<Card> pioche = new ArrayList<>(10);
    List<Card> discards = new ArrayList<>(3);
    List<Card> piocheEmpty = new ArrayList<>();

    int myTotal = 10;
    int myDiscarded = 3;

    PublicCardState cardState = new PublicCardState(faceUpCards1, pioche.size(), discards.size());

    @Test
    void myTest(){
        assertThrows(IllegalArgumentException.class, () ->
        {
            PublicCardState cardStateFAIL = new PublicCardState(faceUpCardsFAIL, pioche.size(), discards.size());
        });

    }

    @Test
    void totalSize() {
        assertEquals(18, cardState.totalSize());
    }

    @Test
    void faceUpCards() {
    }

    @Test
    void faceUpCard() {
    }

    @Test
    void deckSize() {
        assertEquals(10, pioche.size());
    }

    @Test
    void isDeckEmpty() {
        assertEquals(false, pioche.size());
        assertEquals(true, piocheEmpty.size());
    }

    @Test
    void discardsSize() {
        assertEquals(3, discards.size());
    }
}