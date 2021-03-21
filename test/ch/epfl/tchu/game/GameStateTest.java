package ch.epfl.tchu.game;

import ch.epfl.ChMapTest;
import ch.epfl.tchu.SortedBag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest implements ChMapTest {

    GameState state1 = GameState.initial(ticketBuilder(), new Random()); //initial state with all the tickets

    static SortedBag<Ticket> ticketBuilder(){
        SortedBag.Builder<Ticket> ticketsB = new SortedBag.Builder<>();
        for (Ticket t : ChMap.tickets()) {
            ticketsB.add(t);
        }
        return ticketsB.build();
    }

    @Test
    void initial() {
    }

    @Test
    void playerState() {
    }

    @Test
    void currentPlayerState() {
    }
//group 1
    @Test
    void topTickets() {
    }

    @Test
    void withoutTopTickets() {
    }

    @Test
    void topCard() {
    }

    @Test
    void withoutTopCard() {
    }

    @Test
    void withMoreDiscardedCards() {
    }

    @Test
    void withCardsDeckRecreatedIfNeeded() {
    }
//group 2
    @Test
    void withInitiallyChosenTickets() {
        SortedBag<Ticket> firstTwoTickets = SortedBag.of(1,ChMapTest.BAL_BER, 1 , ChMapTest.ZUR_COUNTRY);
        SortedBag<Ticket> lastTicket = SortedBag.of(ChMapTest.BER_COI);
        SortedBag<Ticket> chosenTickets = firstTwoTickets.union(lastTicket);
        state1.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);


    }

    @Test
    void withChosenAdditionalTickets() {
    }

    @Test
    void withDrawnFaceUpCard() {
    }

    @Test
    void withBlindlyDrawnCard() {
    }

    @Test
    void withClaimedRoute() {
    }
//group 3
    @Test
    void lastTurnBegins() {
    }

    @Test
    void forNextTurn() {
    }
}