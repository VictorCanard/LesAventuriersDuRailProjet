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
            new PlayerState(ticketSortedBag, testBag, listeRoutes);
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

        int actualNumber = playerState.tickets().size();
        SortedBag<Ticket> actual = playerState.withAddedTickets(SortedBag.of(LAU_FR1)).tickets();

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

        int actualNumber = playerState.cards().size();
        PlayerState actualPlayerState = playerState.withAddedCard(Card.BLACK);

        SortedBag<Card> actual = actualPlayerState.cards();

        assertEquals(expectedNumber, actualNumber);
        assertEquals(expected, actual);
    }

    @Test
    void withAddedCards() {
        PlayerState playerState = new PlayerState(ticketSortedBag,initialCards,  listeRoutes);
        int expectedNumber = 7;
        SortedBag<Card> expected = initialCards.union(cardSortedBag);

        int actualNumber = playerState.cards().size();
        PlayerState actualPlayerState = playerState.withAddedCards(cardSortedBag);

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
    }

    @Test
    void possibleAdditionalCards() {
    }

    @Test
    void withClaimedRoute() {
    }

    @Test
    void ticketPoints() {
    }

    @Test
    void finalPoints() {
    }
}