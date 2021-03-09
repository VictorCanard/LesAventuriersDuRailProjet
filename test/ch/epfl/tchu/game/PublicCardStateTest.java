package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicCardStateTest {
    List<Card> faceUpCards1 = new ArrayList<Card>(List.of(Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.BLUE, Card.RED));
    List<Card> faceUpCardsFAIL = new ArrayList<Card>(List.of(Card.BLUE, Card.RED));
    List<Card> pioche = new ArrayList<>(List.of(Card.BLUE, Card.BLACK, Card.LOCOMOTIVE,Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.BLUE, Card.RED ));
    List<Card> discards = new ArrayList<>(List.of(Card.BLUE));
    List<Card> piocheEmpty = new ArrayList<>();

    int myTotal = 14;
    int myDiscarded = 1;

    PublicCardState cardState = new PublicCardState(faceUpCards1, pioche.size(), discards.size());

    @Test
    void myTest(){
        assertThrows(IllegalArgumentException.class, () ->
        {
            PublicCardState cardStateFAIL = new PublicCardState(faceUpCardsFAIL, pioche.size(), discards.size());

        });

        assertThrows(IllegalArgumentException.class, () ->
        {
            PublicCardState cardStateFAIL = new PublicCardState(faceUpCards1, -2, discards.size());

        });
    }

    @Test
    void totalSize() {
        assertEquals(myTotal, cardState.totalSize());
    }

    @Test
    void faceUpCards() {
        PublicCardState cardState1 = new PublicCardState(faceUpCards1, pioche.size(), discards.size());

        assertEquals(List.of(Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.BLUE, Card.RED), cardState1.faceUpCards());
    }

    @Test
    void faceUpCard() {
        PublicCardState cardState1 = new PublicCardState(faceUpCards1, pioche.size(), discards.size());
        assertEquals(Card.BLACK, cardState1.faceUpCard(0));

        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            cardState1.faceUpCard(7);

        });

    }

    @Test
    void deckSize() {
        assertEquals(8, pioche.size());
    }

    @Test
    void isDeckEmpty() {
        assertEquals(false, pioche.size()==0);
        assertEquals(true, piocheEmpty.size()==0);
    }

    @Test
    void discardsSize() {
        assertEquals(myDiscarded, discards.size());
    }
}