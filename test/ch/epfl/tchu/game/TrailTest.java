package ch.epfl.tchu.game;

import ch.epfl.TestMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest implements TestMap {


    private static
    @Test
    void longest() {
        Route placeholderRoute = TestMap.routes.get(66);
        Route YVE_NEU = new Route(placeholderRoute.id(), placeholderRoute.station2(), placeholderRoute.station1(),placeholderRoute.length() ,placeholderRoute.level(),placeholderRoute.color());
        Route BER_NEU = TestMap.routes.get(18);
        Route BER_LUC = TestMap.routes.get(16);
        Route AT1_STG_1 = TestMap.routes.get(0);
        Route BAD_BAL_1 = TestMap.routes.get(2);
        Route BER_FRI_1 = TestMap.routes.get(13);
        Route FR1_MAR_1 = TestMap.routes.get(41);
        Route LUC_SCZ_1 = TestMap.routes.get(61);


        List<Route> listeRoutes = List.of(YVE_NEU,BER_LUC,BER_NEU, AT1_STG_1, BAD_BAL_1,BER_FRI_1,FR1_MAR_1,LUC_SCZ_1);

        Trail longestTrail = Trail.longest(listeRoutes);

    }

    @Test
    void length() {

    }

    @Test
    void station1() {
    }

    @Test
    void station2() {
    }

    @Test
    void testToString() {
    }
}