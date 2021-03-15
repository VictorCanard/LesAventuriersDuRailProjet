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
    SortedBag.Builder ticketBuilder = new SortedBag.Builder<>();


    SortedBag<Card> testBag = SortedBag.of(5, Card.BLUE, 7, Card.GREEN);
    SortedBag<Card> preciseBag = SortedBag.of(Card.ALL);

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
            new PlayerState(ticketList, testBag, listeRoutes);
        })
    }

    @Test
    void tickets() {
    }

    @Test
    void withAddedTickets() {
    }

    @Test
    void cards() {
    }

    @Test
    void withAddedCard() {
    }

    @Test
    void withAddedCards() {
    }

    @Test
    void canClaimRoute() {
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