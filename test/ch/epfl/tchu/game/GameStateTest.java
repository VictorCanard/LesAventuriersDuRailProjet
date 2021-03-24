package ch.epfl.tchu.game;

import ch.epfl.ChMapTest;
import ch.epfl.tchu.SortedBag;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest implements ChMapTest {
    Ticket BAL_BER = new Ticket(BAL, BER, 5);
    Ticket BAL_BRI = new Ticket(BAL, BRI, 10);
    Ticket BAL_STG = new Ticket(BAL, STG, 8);
    Ticket BER_COI = new Ticket(BER, COI, 10);
    Ticket BER_LUG = new Ticket(BER, LUG, 12);
    Ticket BER_SCZ = new Ticket(BER, SCZ, 5) ;
    Ticket BER_ZUR = new Ticket(BER, ZUR, 6) ;
    Ticket FRI_LUC = new Ticket(FRI, LUC, 5) ;
    Ticket GEN_BAL = new Ticket(GEN, BAL, 13);
    Ticket GEN_BER = new Ticket(GEN, BER, 8) ;
    Ticket GEN_SIO = new Ticket(GEN, SIO, 10);
    Ticket GEN_ZUR = new Ticket(GEN, ZUR, 14);
    Ticket INT_WIN = new Ticket(INT, WIN, 7) ;
    Ticket KRE_ZUR = new Ticket(KRE, ZUR, 3) ;
    Ticket LAU_INT = new Ticket(LAU, INT, 7) ;
    Ticket LAU_LUC = new Ticket(LAU, LUC, 8) ;
    Ticket LAU_STG = new Ticket(LAU, STG, 13);
    Ticket LCF_BER = new Ticket(LCF, BER, 3) ;
    Ticket LCF_LUC = new Ticket(LCF, LUC, 7) ;
    Ticket LCF_ZUR = new Ticket(LCF, ZUR, 8) ;
    Ticket LUC_VAD = new Ticket(LUC, VAD, 6) ;
    Ticket LUC_ZUR = new Ticket(LUC, ZUR, 2) ;
    Ticket LUG_COI = new Ticket(LUG, COI, 10);
    Ticket NEU_WIN = new Ticket(NEU, WIN, 9) ;
    Ticket OLT_SCE = new Ticket(OLT, SCE, 5) ;
    Ticket SCE_MAR = new Ticket(SCE, MAR, 15);
    Ticket SCE_STG = new Ticket(SCE, STG, 4) ;
    Ticket SCE_ZOU = new Ticket(SCE, ZOU, 3) ;
    Ticket STG_BRU = new Ticket(STG, BRU, 9) ;
    Ticket WIN_SCZ = new Ticket(WIN, SCZ, 3) ;
    Ticket ZUR_BAL = new Ticket(ZUR, BAL, 4) ;
    Ticket ZUR_BRU = new Ticket(ZUR, BRU, 11);
    Ticket ZUR_LUG = new Ticket(ZUR, LUG, 9) ;
    Ticket ZUR_VAD = new Ticket(ZUR, VAD, 6) ;

    public final static Random NON_RANDOM = new Random(){
        @Override
        public int nextInt(int i){
            return i-1;
        }
    };

    GameState normalState = GameState.initial(ticketBuilder(), new Random()); //initial state with all the tickets

    GameState nonRandomState = GameState.initial(ticketBuilder(), NON_RANDOM);

    GameState stateWithFewTickets = GameState.initial(SortedBag.of(1, BAL_STG,1, BAL_BER), NON_RANDOM);



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
    void topTicketsFailsWithNegativeOrTooLargeCount() {
        assertThrows(IllegalArgumentException.class, ()-> {
            normalState.topTickets(-1);
        });
        assertThrows(IllegalArgumentException.class, ()-> {
            normalState.topTickets(Integer.MAX_VALUE);
        });
    }
    @Test
    void topTickets(){
        SortedBag<Ticket> actual = normalState.topTickets(2); //i think you meant to use the non random state
        SortedBag<Ticket> expected = SortedBag.of(1, new Ticket(BAL, BER, 5), 1,new Ticket(BAL, BRI, 10));

        assertEquals(expected, actual);
    }

    @Test
    void withoutTopTickets() {
        SortedBag<Ticket> actual = stateWithFewTickets.topTickets(2);
        SortedBag<Ticket> expected = SortedBag.of(1, BAL_STG,
                1,BAL_BER);

        assertEquals(expected, actual);
        assertTrue(actual.size()==2);



    }
    @Test
    void withoutTopTicketsFails() {
        assertThrows(IllegalArgumentException.class, ()-> {
            normalState.withoutTopTickets(-1);
        });
        assertThrows(IllegalArgumentException.class, ()-> {
            normalState.withoutTopTickets(Integer.MAX_VALUE);
        });
    }

    @Test
    void topCard() {
        Card topCardActual = nonRandomState.topCard();
        Card expected = Card.LOCOMOTIVE;

        assertEquals(expected, topCardActual);

    }
    @Test
    void topCardFails() {
        assertThrows(IllegalArgumentException.class, ()-> {
            makeDeckEmpty().topCard();
        });

    }
    private GameState makeDeckEmpty(){
        GameState stateWithNoPossibilityToDrawCards = GameState.initial(SortedBag.of(FRI_LUC), new Random());

        do{

            stateWithNoPossibilityToDrawCards = stateWithNoPossibilityToDrawCards.withoutTopCard();

        }while(!stateWithNoPossibilityToDrawCards.cardState().isDeckEmpty());

        return stateWithNoPossibilityToDrawCards;
    }

    @Test
    void withoutTopCard() { //Check that top card returns the first card and works for the whole deck
        CardState expected = CardState.of(Deck.of(Constants.ALL_CARDS, NON_RANDOM));
        GameState gs = nonRandomState;

        for (int i = 0; i < 8; i++) {
            expected = expected.withoutTopDeckCard(); //Remove the top 8 cards to then compare this cs to the actual gs
        }
        for (int i = 0; i < expected.deckSize(); i++) {
            assertEquals(expected.topDeckCard(), gs.topCard());
            expected = expected.withoutTopDeckCard();
            gs = gs.withoutTopCard();
        }


    }
    @Test
    void withoutTopCardFails() {
        assertThrows(IllegalArgumentException.class, ()-> {
            makeDeckEmpty().withoutTopCard();
        });
    }

    @Test
    void withMoreDiscardedCards() { //I just check those in the debugger
        var DiscardCards = SortedBag.of(1, Card.VIOLET, 5, Card.BLUE);
        var actualGS = nonRandomState.withMoreDiscardedCards(DiscardCards);

    }


    @Test
    void withCardsDeckRecreatedIfNeeded() {
        //Deck is empty so it's recreated from discards
        var emptyGS = makeDeckEmpty().withMoreDiscardedCards(SortedBag.of(5, Card.VIOLET)).withCardsDeckRecreatedIfNeeded(NON_RANDOM);
        assertTrue( emptyGS.cardState().isDeckEmpty());

        var newRecreatedDeck = emptyGS.withCardsDeckRecreatedIfNeeded(new Random(1));
        assertTrue(newRecreatedDeck.cardState().deckSize() == 5);

        //Deck is not empty, should return the same gs
        var recreatedDeck = normalState.withCardsDeckRecreatedIfNeeded(NON_RANDOM);
        assertEquals(normalState, recreatedDeck);
    }

//group 2
    @Test
    void withInitiallyChosenTickets() {
        SortedBag<Ticket> firstTwoTickets = SortedBag.of(1,ChMapTest.BAL_BER, 1 , ChMapTest.ZUR_COUNTRY);
        SortedBag<Ticket> lastTicket = SortedBag.of(ChMapTest.BER_COI);
        SortedBag<Ticket> chosenTickets = firstTwoTickets.union(lastTicket);

        var newState = normalState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);



    }
    @Test
    void withInitiallyChosenTicketsFails(){ //As the player has at least one ticket already
        SortedBag<Ticket> firstTwoTickets = SortedBag.of(1,ChMapTest.BAL_BER, 1 , ChMapTest.ZUR_COUNTRY);
        SortedBag<Ticket> lastTicket = SortedBag.of(ChMapTest.BER_COI);
        SortedBag<Ticket> chosenTickets = firstTwoTickets.union(lastTicket);
        GameState stateWithFewTickets = normalState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);

        assertThrows(IllegalArgumentException.class, ()-> {
            stateWithFewTickets.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);
        });

    }

    @Test
    void withChosenAdditionalTickets() {
    }
    @Test
    void withChosenAdditionalTicketsFails() {
        SortedBag<Ticket> drawnTickets = SortedBag.of(1, BAL_BER, 1, BER_COI);
        SortedBag<Ticket> keptTickets = SortedBag.of(BAL_STG);

         assertThrows(IllegalArgumentException.class, ()-> {
             normalState.withChosenAdditionalTickets(drawnTickets, keptTickets);
        });
    }
    @Test
    void withDrawnFaceUpCard() {
    }
    @Test
    void withDrawnFaceUpCardFails() {

        GameState finalStateWithNoPossibilityToDrawCards = makeDeckEmpty();
        assertThrows(IllegalArgumentException.class, ()-> {
            finalStateWithNoPossibilityToDrawCards.withDrawnFaceUpCard(10);
        });

    }

    @Test
    void withBlindlyDrawnCard() {
    }
    @Test
    void withBlindlyDrawnCardFails() {
    }

    @Test
    void withClaimedRoute() {
    }
    @Test
    void withClaimedRouteFails() {
    }






//group 3
    @Test
    void lastTurnBegins() {
    }

    @Test
    void forNextTurn() {
    }
}