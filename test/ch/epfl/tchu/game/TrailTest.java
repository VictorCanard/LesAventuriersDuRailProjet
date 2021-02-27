package ch.epfl.tchu.game;

import ch.epfl.TestMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest implements TestMap {

    @Test
    void longest() {
        Route NEU_YVE = new Route(routes.get(66));
        Route BER_NEU = new Route(routes.get(18));
        Route BER_LUC = new Route(routes.get(16));

        List<Route> listeRoutes = List.of(NEU_YVE,BER_LUC,BER_NEU);

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