package ch.epfl;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Station;

import java.util.List;

public interface RouteTestMap {
    //Stations
    Station BAD = new Station(0, "Baden");
    Station BAL = new Station(1, "Bâle");
    Station BEL = new Station(2, "Bellinzone");
    Station BER = new Station(3, "Berne");
    Station LUC = new Station(16, "Lucerne");
    Station OLT = new Station(20, "Olten");
    Station STG = new Station(27, "Saint-Gall");
    Station WAS = new Station(29, "Wassen");
    Station ZUR = new Station(33, "Zürich");
    Station DE1 = new Station(34, "Allemagne");
    Station AT1 = new Station(39, "Autriche");

    //Routes
    Route route1 = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);
    Route route2 = new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED);
    Route route3 = new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET);
    Route route4 = new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW);
    Route route5 = new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE);
    Route route6 = new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
    Route route7 = new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
    Route route8 = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);


    //for route2
    SortedBag<Card> only3Locomotives = SortedBag.of(3, Card.LOCOMOTIVE);
    SortedBag<Card> only3Red = SortedBag.of(3, Card.RED);

    SortedBag<Card> drawCards1 = SortedBag.of(3, Card.RED);
    SortedBag<Card> drawCards2 = SortedBag.of(1, Card.LOCOMOTIVE, 2, Card.BLUE);
    SortedBag<Card> drawCardsFail = SortedBag.of(2, Card.GREEN);



}




