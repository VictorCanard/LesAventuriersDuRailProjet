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

    }

    @Test
    void withoutTopTickets() {
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
    void withoutTopCard() {
    }
    @Test
    void withoutTopCardFails() {
        assertThrows(IllegalArgumentException.class, ()-> {
            makeDeckEmpty().withoutTopCard();
        });
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
        normalState.withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);


    }
    @Test
    void withInitiallyChosenTicketsFails(){
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
    void withClaimedRoute() {
    }




    @Test
    void withBlindlyDrawnCardFails() {
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