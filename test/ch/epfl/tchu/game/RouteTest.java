package ch.epfl.tchu.game;

import ch.epfl.tchu.RouteTestMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest implements RouteTestMap {

    @Test
    void id() {
        assertEquals("AT1_STG_1", route1.id());
    }

    @Test
    void station1() {
        assertEquals("Baden", route2.station1().name());
        assertEquals(0, route2.station1().id());

        assertEquals(BAD, route2.station1());

    }

    @Test
    void station2() {
        assertEquals("Bâle", route2.station2().name());
        assertEquals(1, route2.station2().id());
    }

    @Test
    void length() {
        assertEquals(2, route3.length());
    }

    @Test
    void level() {
        assertEquals(Route.Level.OVERGROUND, route4.level());
    }

    @Test
    void color() {
        assertEquals(Color.BLUE, route5.color());
        assertEquals(null, route6.color());
    }

    @Test
    void stations() {
        assertEquals("Bellinzone", route7.stations().get(0).name());
        assertEquals(2, route7.stations().get(0).id());

        assertEquals("Wassen", route7.stations().get(1).name());
        assertEquals(29, route7.stations().get(1).id());

        assertEquals(2, route7.stations().size());

    }

    @Test
    void stationOpposite() {

        assertEquals(LUC, route8.stationOpposite(BER));
        assertEquals(BER, route8.stationOpposite(LUC));
    }

    @Test
    void possibleClaimCards() {
        System.out.println(route1.possibleClaimCards());
        System.out.println(route2.possibleClaimCards());
        System.out.println(route3.possibleClaimCards());
        System.out.println(route4.possibleClaimCards());
        System.out.println(route5.possibleClaimCards());
        System.out.println(route6.possibleClaimCards());
        System.out.println(route7.possibleClaimCards());
        System.out.println(route8.possibleClaimCards());
    }

    @Test
    void additionalClaimCardsCount() {
        assertEquals(0, route2.additionalClaimCardsCount(only3Locomotives, drawCards1));
        assertEquals(1, route2.additionalClaimCardsCount(only3Red, drawCards2));

        assertFalse(drawCardsFail.size() == 3);


        assertThrows(IllegalArgumentException.class, () ->

        {
            route2.additionalClaimCardsCount(only3Red, drawCardsFail);
        });

        assertThrows(IllegalArgumentException.class, () ->

        {
            route3.additionalClaimCardsCount(only3Red, drawCards2);
        });
    }

    @Test
    void claimPoints() {

        assertEquals(7, route1.claimPoints());
        assertEquals(1, route4.claimPoints());


    }
}