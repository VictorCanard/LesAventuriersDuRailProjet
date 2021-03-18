package ch.epfl.tchu.game;

import ch.epfl.RouteTestMap;
import ch.epfl.TestMap;
import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.epfl.TestMap.*;
import static org.junit.jupiter.api.Assertions.*;

class PlayerStateTest {
    public final Ticket LAU_STG = new Ticket(LAU, STG, 13);
    public final Ticket LAU_BER = new Ticket(LAU, BER, 2);
    public final Ticket LAU_FR1 = new Ticket(LAU, FR1, 5);

    public SortedBag<Ticket> ticketSortedBag = SortedBag.of(1, LAU_BER, 1, LAU_STG);

    public SortedBag<Card> cardSortedBag = SortedBag.of(1, Card.BLACK, 2, Card.WHITE);

    SortedBag<Card> testBag = SortedBag.of(5, Card.BLUE, 7, Card.GREEN);
    SortedBag<Card> preciseBag = SortedBag.of(Card.ALL);
    SortedBag<Card> initialCards = SortedBag.of(2, Card.BLUE, 2, Card.WHITE);
    SortedBag<Card> cardsPlayedInitially = SortedBag.of(4, Card.BLUE);
    SortedBag<Card> drawnCards = SortedBag.of(2, Card.LOCOMOTIVE, 1, Card.WHITE);

    List<Route> routes = ChMap.routes();

    Route YVE_NEU = TestMap.routes.get(66);
    Route BER_NEU = TestMap.routes.get(18);
    Route BER_LUC = TestMap.routes.get(16);
    Route AT1_STG_1 = TestMap.routes.get(0);
    Route BAD_BAL_1 = TestMap.routes.get(2);
    Route BER_FRI_1 = TestMap.routes.get(13);
    Route FR1_MAR_1 = TestMap.routes.get(41);
    Route LUC_SCZ_1 = TestMap.routes.get(61);


    List<Route> listeRoutes = List.of(YVE_NEU,BER_LUC,BER_NEU, AT1_STG_1, BAD_BAL_1,BER_FRI_1,FR1_MAR_1,LUC_SCZ_1);
    List<Route> listeRoutes2 = List.of(RouteTestMap.route1,RouteTestMap.route2, RouteTestMap.route3, RouteTestMap.route4, RouteTestMap.route5, RouteTestMap.route6, RouteTestMap.route7, RouteTestMap.route8 );

    @Test
    void initial() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState.initial(testBag); // Too many initial cards

        });



    }

    @Test
    void tickets() {
        PlayerState playerState = new PlayerState(ticketSortedBag,initialCards,  listeRoutes);
        int expectedNumber = 2;
        SortedBag<Ticket> expected = ticketSortedBag;

        int actualNumber = playerState.tickets().size();
        SortedBag<Ticket> actual = playerState.tickets();

        assertEquals(expectedNumber, actualNumber);
        assertEquals(expected, actual);
    }

    @Test
    void withAddedTickets() {
        PlayerState playerState = new PlayerState(ticketSortedBag,initialCards,  listeRoutes);
        int expectedNumber = 3;
        SortedBag<Ticket> expected = ticketSortedBag.union(SortedBag.of(LAU_FR1));


        playerState = playerState.withAddedTickets(SortedBag.of(LAU_FR1));
        SortedBag<Ticket> actual = playerState.tickets();
        int actualNumber = playerState.tickets().size();

        assertEquals(expectedNumber, actualNumber);
        assertEquals(expected, actual);
    }

    @Test
    void cards() {
        PlayerState playerState = new PlayerState(ticketSortedBag,initialCards,  listeRoutes);
        int expectedNumber = 4;
        SortedBag<Card> expected = initialCards;

        int actualNumber = playerState.cards().size();
        SortedBag<Card> actual = playerState.cards();

        assertEquals(expectedNumber, actualNumber);
        assertEquals(expected, actual);
    }

    @Test
    void withAddedCard() {
        PlayerState playerState = new PlayerState(ticketSortedBag,initialCards,  listeRoutes);
        int expectedNumber = 5;
        SortedBag<Card> expected = initialCards.union(SortedBag.of(Card.BLACK));


        PlayerState actualPlayerState = playerState.withAddedCard(Card.BLACK);
        int actualNumber = actualPlayerState.cards().size();
        SortedBag<Card> actual = actualPlayerState.cards();

        assertEquals(expectedNumber, actualNumber);
        assertEquals(expected, actual);
    }

    @Test
    void withAddedCards() {
        PlayerState playerState = new PlayerState(ticketSortedBag,initialCards,  listeRoutes);
        int expectedNumber = 7;
        SortedBag<Card> expected = initialCards.union(cardSortedBag);


        PlayerState actualPlayerState = playerState.withAddedCards(cardSortedBag);
        int actualNumber = actualPlayerState.cards().size();
        SortedBag<Card> actual = actualPlayerState.cards();

        assertEquals(expectedNumber, actualNumber);
        assertEquals(expected, actual);
    }

    @Test
    void canClaimRoute() {
        Route testRoute = AT1_STG_1;
        PlayerState testPlayerState = new PlayerState(ticketSortedBag, initialCards, listeRoutes);
        boolean actual = testPlayerState.canClaimRoute(testRoute);

        assertTrue(!actual);

        Route testRoute2 = AT1_STG_1;
        SortedBag<Card> requiredCards = SortedBag.of(4, Card.LOCOMOTIVE);
        PlayerState testPlayerState2 = new PlayerState(ticketSortedBag, requiredCards, listeRoutes);
        boolean canClaimRoute = testPlayerState2.canClaimRoute(testRoute2);

        assertTrue(canClaimRoute);

    }

    @Test
    void possibleClaimCards() {
        Route testRoute1 = AT1_STG_1;
        SortedBag<Card> requiredCards = SortedBag.of(4, Card.LOCOMOTIVE);
        PlayerState testPlayerState2 = new PlayerState(ticketSortedBag, requiredCards, listeRoutes);

        List<SortedBag<Card>> sortedBagList = testPlayerState2.possibleClaimCards(testRoute1);

        System.out.println(sortedBagList);
        //Correct because Route's possible claim cards is correct

    }

    @Test
    void possibleAdditionalCards() {
        SortedBag<Card> initialPlayerCards = SortedBag.of(4, Card.LOCOMOTIVE, 4, Card.BLUE);
        PlayerState testPlayerState2 = new PlayerState(ticketSortedBag, initialPlayerCards,  listeRoutes);

        assertThrows(IllegalArgumentException.class, () -> {
            testPlayerState2.possibleAdditionalCards(4, initialCards, drawnCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            testPlayerState2.possibleAdditionalCards(0, initialCards, drawnCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            SortedBag<Card> cardSortedBagWithMoreThanTwoTypes = SortedBag.of(1, Card.BLUE, 1, Card.WHITE);
            SortedBag<Card> newSortedBag = cardSortedBagWithMoreThanTwoTypes.union(SortedBag.of(Card.LOCOMOTIVE));
            testPlayerState2.possibleAdditionalCards(3, newSortedBag, drawnCards);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            SortedBag<Card> drawnCardsWithOnlyOneCard = SortedBag.of(1, Card.BLUE);
            testPlayerState2.possibleAdditionalCards(3, initialCards, drawnCardsWithOnlyOneCard);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            SortedBag<Card> emptySortedBag = SortedBag.of();
            testPlayerState2.possibleAdditionalCards(3, emptySortedBag, drawnCards);
        });

        // CardsPlayedInitially: 4x BLUE
        // Drawn Cards: 2x Locomotive, 1x White
        // PLayerCards: 4x Locomotive, 4xBlue

        List<SortedBag<Card>> sortedBagList = testPlayerState2.possibleAdditionalCards(3, cardsPlayedInitially, drawnCards);

        System.out.println(sortedBagList);

        //playerCards: 8xBlue, 6xLocomotive, 3xYellow
        SortedBag<Card> initialPlayerCards2 = SortedBag.of(6, Card.LOCOMOTIVE, 8, Card.BLUE);
        initialPlayerCards2 = initialPlayerCards2.union(SortedBag.of(3, Card.YELLOW));
        PlayerState testPlayerState3 = new PlayerState(ticketSortedBag, initialPlayerCards2,  listeRoutes);

        List<SortedBag<Card>> sortedBagList2 = testPlayerState3.possibleAdditionalCards(3, cardsPlayedInitially, drawnCards);

        System.out.println(sortedBagList2);

        //playerCards: 18xBlue, 2xLocomotive, 3xYellow, 4xRed, 1x Black
        SortedBag<Card> initialPlayerCards3 = SortedBag.of(2, Card.LOCOMOTIVE, 18, Card.BLUE);
        initialPlayerCards3 = initialPlayerCards3.union(SortedBag.of(3, Card.YELLOW, 4, Card.RED));
        initialPlayerCards3 = initialPlayerCards3.union(SortedBag.of(1, Card.BLACK));
        PlayerState testPlayerState4 = new PlayerState(ticketSortedBag, initialPlayerCards3,  listeRoutes);

        List<SortedBag<Card>> sortedBagList3 = testPlayerState4.possibleAdditionalCards(3, cardsPlayedInitially, drawnCards);

        System.out.println(sortedBagList3);

        //playerCards: 18xBlue, 0xLocomotive
        SortedBag<Card> initialPlayerCards4 = SortedBag.of(18, Card.BLUE);

        PlayerState testPlayerState5 = new PlayerState(ticketSortedBag, initialPlayerCards4,  listeRoutes);

        List<SortedBag<Card>> sortedBagList4 = testPlayerState5.possibleAdditionalCards(3, cardsPlayedInitially, drawnCards);

        System.out.println(sortedBagList4);


    }

    @Test
    void withClaimedRoute() {
        PlayerState testPlayerState = new PlayerState(ticketSortedBag, initialCards, listeRoutes);
        Route routeThatWasClaimed = routes.get(3);
        SortedBag<Card> cardsThatWereUsed = SortedBag.of(2, Card.WHITE);

        testPlayerState = testPlayerState.withClaimedRoute(routeThatWasClaimed,cardsThatWereUsed );

        assertEquals(initialCards.difference(cardsThatWereUsed), testPlayerState.cards());
        assertTrue(testPlayerState.cards().size() == initialCards.size() - cardsThatWereUsed.size());

        assertTrue(testPlayerState.routes().contains(routeThatWasClaimed));
    }

    @Test
    void ticketPoints() {


    }

    @Test
    void finalPoints() {
    }
}