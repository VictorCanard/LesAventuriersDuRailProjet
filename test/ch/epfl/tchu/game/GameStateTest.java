package ch.epfl.tchu.game;

import ch.epfl.tchu.ChMapTest;

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

    PlayerState playerState1 = new PlayerState(SortedBag.of(1, ChMapTest.ZUR_VAD, 1, ChMapTest.ZUR_COUNTRY), SortedBag.of(3, Card.BLUE, 1, Card.ORANGE), List.of(ChMapTest.route1, ChMapTest.route8, ChMapTest.route6));
    PlayerState playerState2 = new PlayerState(SortedBag.of(1, ChMapTest.BAL_BER, 1, ChMapTest.ZUR_COUNTRY), SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.RED), List.of(ChMapTest.route3, ChMapTest.route4));



    @Test
    void initial() {
        GameState initialState = GameState.initial(ticketBuilder(), new Random()); //initial state with all the tickets
        //debug to see inside i guess
        System.out.println("initialState first player is : " + initialState.currentPlayerId());
        assertEquals(ChMap.tickets().size(), initialState.ticketsCount());
        assertEquals(Constants.ALL_CARDS.size()-8-5, initialState.cardState().deckSize());
        //"la pioche des cartes contient les cartes de Constants.ALL_CARDS, sans les 8 (2×4) du dessus,
        // distribuées aux joueurs " (mais cardState enleve aussi 5 cartes) ?

        GameState emptyTicketsState = GameState.initial(SortedBag.of(), new Random());
        assertEquals(0, emptyTicketsState.ticketsCount());
    }

    @Test
    void playerState() {
        PlayerState ps = normalState.playerState(PlayerId.PLAYER_1); //should be same as initial see debug
    }

    @Test
    void currentPlayerState() {
        PlayerState ps = normalState.currentPlayerState();
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

        SortedBag<Ticket> actual = nonRandomState.topTickets(2);
        SortedBag<Ticket> expected =SortedBag.of(ticketBuilder().toList().subList(0, 2));

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
        Card expected = Card.VIOLET;

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
        var emptyGS = makeDeckEmpty();
        assertTrue( emptyGS.cardState().isDeckEmpty());

        var newRecreatedDeck = emptyGS.withMoreDiscardedCards(SortedBag.of(5, Card.VIOLET)).withCardsDeckRecreatedIfNeeded(new Random(1));
        assertTrue(newRecreatedDeck.cardState().deckSize() == 5);

        //Deck is not empty, should return the same gs
        var recreatedDeck = normalState.withCardsDeckRecreatedIfNeeded(NON_RANDOM);
        assertEquals(normalState, recreatedDeck);

        //test with empty discard pile
        var deckWithEmptyDP = makeDeckEmpty().withCardsDeckRecreatedIfNeeded(NON_RANDOM);
    }

//group 2
    @Test
    void withInitiallyChosenTickets() {
        SortedBag<Ticket> firstTwoTickets = SortedBag.of(1,ChMapTest.BAL_BER, 1 , ChMapTest.ZUR_COUNTRY);
        SortedBag<Ticket> lastTicket = SortedBag.of(ChMapTest.BER_COI);
        SortedBag<Ticket> chosenTickets = firstTwoTickets.union(lastTicket);

        var newState = GameState.initial(SortedBag.of(), NON_RANDOM).withInitiallyChosenTickets(PlayerId.PLAYER_1, chosenTickets);

        var iterator = chosenTickets.iterator();

        for (Ticket ticket: newState.topTickets(newState.ticketsCount())
             ) {
            assertEquals(iterator.next(), ticket);
        }



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
    void withChosenAdditionalTickets() { // TO test with more cases
        var drawnT = SortedBag.of(1, BAL_BER, 1, BER_COI);
        var keptT = SortedBag.of(1, BAL_BER);

        var ticketDeck = drawnT.union(keptT);

        var stateWithMoreTickets = GameState.initial(ticketDeck, NON_RANDOM).withChosenAdditionalTickets(drawnT, keptT);

        for (Ticket ticket: stateWithMoreTickets.topTickets(stateWithMoreTickets.ticketsCount())
             ) {
            assertEquals(keptT.get(0), ticket);
        }
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
    void withDrawnFaceUpCard() { //Use a for loop here to test more cases
        var gs = GameState.initial(SortedBag.of(), new Random(1)); // 8 Black, 8 Violet....(Shuffled)

        var faceUpCards = gs.cardState().faceUpCards();
        var topFUCard = faceUpCards.get(0);
        var topDeckCard = gs.topCard();

        var gsWithDFUC = gs.withDrawnFaceUpCard(0);
        var newFUCard = gsWithDFUC.cardState().faceUpCard(0);
        var topPlayerCard = gsWithDFUC.currentPlayerState().cards().get(gsWithDFUC.currentPlayerState().cardCount()-1);

        assertEquals(topDeckCard, newFUCard);
        assertEquals(topFUCard, topPlayerCard);



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
        var gs = nonRandomState;


        var initialDS = gs.cardState().deckSize();

        var newGsWithBDC = gs;

        for (int i = 1; i < initialDS-4; i++) {
            newGsWithBDC = newGsWithBDC.withBlindlyDrawnCard();

            assertEquals(initialDS-i,newGsWithBDC.cardState().deckSize()); //A card was removed from the draw pile
            assertEquals(4+i, newGsWithBDC.currentPlayerState().cards().size()); //It was placed in the current player's hand


        }

    }
    @Test
    void withBlindlyDrawnCardFails() {
        GameState finalStateWithNoPossibilityToDrawCards = makeDeckEmpty();
        assertThrows(IllegalArgumentException.class, ()-> {
            finalStateWithNoPossibilityToDrawCards.withDrawnFaceUpCard(10);
        });

    }

    @Test
    void withClaimedRoute() {
        Route route1 = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);
        var cards1 = SortedBag.of(4, Card.BLACK);

        var gs1 = nonRandomState.withClaimedRoute(route1, cards1);

        assertTrue(gs1.currentPlayerState().cardCount() == nonRandomState.currentPlayerState().cardCount() - cards1.size());
        assertTrue(gs1.currentPlayerState().routes().contains(route1));

        Route route2 = new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED);
        var cards2 = SortedBag.of(3, Card.RED);

        //car number goes down but not cards of player
    }
    @Test
    void withClaimedRouteFails() {


        var gs2 = normalState;

        for (int i = 0; i < 40; i++) { //Simulate the player drawing card so he can capture the above route
            gs2 =  gs2.withBlindlyDrawnCard();
        }
        var cards2 = SortedBag.of(3, Card.RED);
         gs2 = gs2.withClaimedRoute(route2, cards2);


        int playerCardCount = gs2.currentPlayerState().cardCount();
        int cardsInitially =  normalState.currentPlayerState().cardCount();
        int cardsWithOperations = cardsInitially - cards2.size() + 40;

        assertEquals(cardsWithOperations, playerCardCount);
        assertTrue(gs2.currentPlayerState().routes().contains(route2));
    }


//group 3
    @Test
    void lastTurnBegins() {
       assertFalse(normalState.lastTurnBegins());

       GameState s1 = normalState.withClaimedRoute(ChMapTest.routesTo2Cars.get(0), SortedBag.of(6, Card.LOCOMOTIVE));
       GameState s2 = s1.withClaimedRoute(ChMapTest.routesTo2Cars.get(1), SortedBag.of(6, Card.LOCOMOTIVE));
       GameState s3= s2.withClaimedRoute(ChMapTest.routesTo2Cars.get(2), SortedBag.of(5, Card.LOCOMOTIVE));
       GameState s4 = s3.withClaimedRoute(ChMapTest.routesTo2Cars.get(3), SortedBag.of(5, Card.LOCOMOTIVE));
       GameState s5 = s4.withClaimedRoute(ChMapTest.routesTo2Cars.get(4), SortedBag.of(4, Card.LOCOMOTIVE));
       GameState s6 = s5.withClaimedRoute(ChMapTest.routesTo2Cars.get(5), SortedBag.of(4, Card.LOCOMOTIVE));
       GameState s7 = s6.withClaimedRoute(ChMapTest.routesTo2Cars.get(6), SortedBag.of(1, Card.RED));
       GameState s8 = s7.withClaimedRoute(ChMapTest.routesTo2Cars.get(7), SortedBag.of(3, Card.RED));
       GameState s9 = s8.withClaimedRoute(ChMapTest.routesTo2Cars.get(8), SortedBag.of(4, Card.LOCOMOTIVE));
    assertTrue(s9.lastTurnBegins());
    }

    @Test
    void forNextTurn() {
        GameState s1 = normalState.withClaimedRoute(ChMapTest.routesTo2Cars.get(0), SortedBag.of(6, Card.LOCOMOTIVE));
        GameState s2 = s1.withClaimedRoute(ChMapTest.routesTo2Cars.get(1), SortedBag.of(6, Card.LOCOMOTIVE));
        GameState s3= s2.withClaimedRoute(ChMapTest.routesTo2Cars.get(2), SortedBag.of(5, Card.LOCOMOTIVE));
        GameState s4 = s3.withClaimedRoute(ChMapTest.routesTo2Cars.get(3), SortedBag.of(5, Card.LOCOMOTIVE));
        GameState s5 = s4.withClaimedRoute(ChMapTest.routesTo2Cars.get(4), SortedBag.of(4, Card.LOCOMOTIVE));
        GameState s6 = s5.withClaimedRoute(ChMapTest.routesTo2Cars.get(5), SortedBag.of(4, Card.LOCOMOTIVE));
        GameState s7 = s6.withClaimedRoute(ChMapTest.routesTo2Cars.get(6), SortedBag.of(1, Card.RED));
        GameState s8 = s7.withClaimedRoute(ChMapTest.routesTo2Cars.get(7), SortedBag.of(3, Card.RED));
        GameState s9 = s8.withClaimedRoute(ChMapTest.routesTo2Cars.get(8), SortedBag.of(4, Card.LOCOMOTIVE));
        GameState s10 = s9.forNextTurn();


    }
}