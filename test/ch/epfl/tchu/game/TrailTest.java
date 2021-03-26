package ch.epfl.tchu.game;

import ch.epfl.tchu.TestMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrailTest implements TestMap {

    @Test

    void checkStationReturnsNullIfListEmpty(){
        List<Route> emptyList = new ArrayList<>();
        Trail actualTrail = Trail.longest(emptyList);
        assertEquals(null, actualTrail.station1());
        assertEquals(null, actualTrail.station2());
        assertTrue(actualTrail.length()==0);
    }

    @Test
    void longest() {

        Trail actualLongestTrail = Trail.longest(listeRoutes);
        List<Route> expectedListRoutes = List.of(YVE_NEU, BER_NEU, BER_LUC, LUC_SCZ_1);

        assertTrue(actualLongestTrail.station1().equals(YVE));
        assertTrue(actualLongestTrail.station2().equals(SCZ));
        assertTrue(actualLongestTrail.length() == 9);


    }

    @Test
    void length() {


        int expectedLength = 8;
        int actualLength = testTrail1.length();

        assertEquals(expectedLength,actualLength);
    }

    @Test
    void station1() {
        Station expectedStation = ChMap.stations().get(31);
        Station actualStation = testTrail1.station1();
        assertTrue(expectedStation.equals(actualStation));
    }

    @Test
    void station2() {
        Station expectedStation = SCZ;
        Station actualStation = Trail.longest(listeRoutes).station2();
        assertTrue(expectedStation.equals(actualStation));
    }

    @Test
    void testToString() {
        Trail longestTrail = Trail.longest(listeRoutes);
        Trail longestTrail2 = Trail.longest(listeRoutes2);


        String expectedString = "Yverdon - Neuchâtel - Berne - Lucerne - Schwyz (9)";
        String actualString = longestTrail.toString();

        assertEquals(expectedString,actualString);

        String expectedString2 = "Berne - Fribourg (1)";
        String actualString2 = longestTrail2.toString();

       assertEquals(expectedString2, actualString2);
    }
}