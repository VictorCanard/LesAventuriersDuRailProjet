package ch.epfl.tchu.game;

import ch.epfl.tchu.RouteTestMap;
import ch.epfl.tchu.TestMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PublicPlayerStateTest {
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
    void ticketCount() {
        int ticketCount = 10;
        int cardCount = 100;
        PublicPlayerState testState = new PublicPlayerState(ticketCount, cardCount, listeRoutes);

        int expectedTicketCount = 10;
        int actualTicketCount = testState.ticketCount();

        assertEquals(expectedTicketCount, actualTicketCount);

    }

    @Test
    void cardCount() {
        int ticketCount = 10;
        int cardCount = 100;
        PublicPlayerState testState = new PublicPlayerState(ticketCount, cardCount, listeRoutes);

        int expectedCardCount = 100;
        int actualCardCount = testState.cardCount();

        assertEquals(expectedCardCount, actualCardCount);
    }

    @Test
    void routes() {
        int ticketCount = 10;
        int cardCount = 100;
        PublicPlayerState testState = new PublicPlayerState(ticketCount, cardCount, listeRoutes);

        List<Route> expectedRoutesCount = listeRoutes;
        List<Route> actualRoutes = testState.routes();

        for (int i = 0; i < expectedRoutesCount.size(); i++) {
            assertEquals(expectedRoutesCount.get(i), actualRoutes.get(i));
        }

    }

    @Test
    void carCount() {
        int ticketCount = 10;
        int cardCount = 100;
        int carCount = 17;
        PublicPlayerState testState = new PublicPlayerState(ticketCount, cardCount, listeRoutes2);

        int actualCarCount = testState.carCount();

        assertEquals(carCount, actualCarCount);
    }

    @Test
    void claimPoints() {
        int ticketCount = 10;
        int cardCount = 100;
        PublicPlayerState testState = new PublicPlayerState(ticketCount, cardCount, listeRoutes2);

        int expectedClaimPoints = 36;
        int actualClaimPoints = testState.claimPoints();

        assertEquals(expectedClaimPoints, actualClaimPoints);
    }
}