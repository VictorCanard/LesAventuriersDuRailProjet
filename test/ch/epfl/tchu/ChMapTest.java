package ch.epfl.tchu;

import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.List;

public interface ChMapTest {

    // Stations - cities
    Station BAD = new Station(0, "Baden");
    Station BAL = new Station(1, "Bâle");
    Station BEL = new Station(2, "Bellinzone");
    Station BER = new Station(3, "Berne");
    Station BRI = new Station(4, "Brigue");
    Station BRU = new Station(5, "Brusio");
    Station COI = new Station(6, "Coire");
    Station DAV = new Station(7, "Davos");
    Station DEL = new Station(8, "Delémont");
    Station FRI = new Station(9, "Fribourg");
    Station GEN = new Station(10, "Genève");
    Station INT = new Station(11, "Interlaken");
    Station KRE = new Station(12, "Kreuzlingen");
    Station LAU = new Station(13, "Lausanne");
    Station LCF = new Station(14, "La Chaux-de-Fonds");
    Station LOC = new Station(15, "Locarno");
    Station LUC = new Station(16, "Lucerne");
    Station LUG = new Station(17, "Lugano");
    Station MAR = new Station(18, "Martigny");
    Station NEU = new Station(19, "Neuchâtel");
    Station OLT = new Station(20, "Olten");
    Station PFA = new Station(21, "Pfäffikon");
    Station SAR = new Station(22, "Sargans");
    Station SCE = new Station(23, "Schaffhouse");
    Station SCZ = new Station(24, "Schwyz");
    Station SIO = new Station(25, "Sion");
    Station SOL = new Station(26, "Soleure");
    Station STG = new Station(27, "Saint-Gall");
    Station VAD = new Station(28, "Vaduz");
    Station WAS = new Station(29, "Wassen");
    Station WIN = new Station(30, "Winterthour");
    Station YVE = new Station(31, "Yverdon");
    Station ZOU = new Station(32, "Zoug");
    Station ZUR = new Station(33, "Zürich");

    // Stations - countries
    Station DE1 = new Station(34, "Allemagne");
    Station DE2 = new Station(35, "Allemagne");
    Station DE3 = new Station(36, "Allemagne");
    Station DE4 = new Station(37, "Allemagne");
    Station DE5 = new Station(38, "Allemagne");
    Station AT1 = new Station(39, "Autriche");
    Station AT2 = new Station(40, "Autriche");
    Station AT3 = new Station(41, "Autriche");
    Station IT1 = new Station(42, "Italie");
    Station IT2 = new Station(43, "Italie");
    Station IT3 = new Station(44, "Italie");
    Station IT4 = new Station(45, "Italie");
    Station IT5 = new Station(46, "Italie");
    Station FR1 = new Station(47, "France");
    Station FR2 = new Station(48, "France");
    Station FR3 = new Station(49, "France");
    Station FR4 = new Station(50, "France");

    // Countries
    List<Station> DE = List.of(DE1, DE2, DE3, DE4, DE5);
    List<Station> AT = List.of(AT1, AT2, AT3);
    List<Station> IT = List.of(IT1, IT2, IT3, IT4, IT5);
    List<Station> FR = List.of(FR1, FR2, FR3, FR4);

    List<Station> ALL_STATIONS = List.of(
            BAD, BAL, BEL, BER, BRI, BRU, COI, DAV, DEL, FRI, GEN, INT, KRE, LAU, LCF, LOC, LUC,
            LUG, MAR, NEU, OLT, PFA, SAR, SCE, SCZ, SIO, SOL, STG, VAD, WAS, WIN, YVE, ZOU, ZUR,
            DE1, DE2, DE3, DE4, DE5, AT1, AT2, AT3, IT1, IT2, IT3, IT4, IT5, FR1, FR2, FR3, FR4);

    // Routes
    Route route1 = new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null);
    Route route2 = new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED);
    Route route3 = new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET);
    Route route4 = new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW);
    Route route5 = new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE);
    Route route6 = new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
    Route route7 = new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null);
    Route route8 = new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null);

    List<Route> routesTo2Cars = List.of(
            new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null),
            new Route("GEN_YVE_1", GEN, YVE, 6, Route.Level.OVERGROUND, null),
            new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null),
            new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null),
            new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null),
            new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED),
            new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null)
            );


    List<Route> ALL_ROUTES = List.of(
            new Route("AT1_STG_1", AT1, STG, 4, Route.Level.UNDERGROUND, null),
            new Route("AT2_VAD_1", AT2, VAD, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_BAL_1", BAD, BAL, 3, Route.Level.UNDERGROUND, Color.RED),
            new Route("BAD_OLT_1", BAD, OLT, 2, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("BAD_ZUR_1", BAD, ZUR, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("BAL_DE1_1", BAL, DE1, 1, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("BAL_DEL_1", BAL, DEL, 2, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("BAL_OLT_1", BAL, OLT, 2, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("BEL_LOC_1", BEL, LOC, 1, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("BEL_LUG_1", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.RED),
            new Route("BEL_LUG_2", BEL, LUG, 1, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("BEL_WAS_1", BEL, WAS, 4, Route.Level.UNDERGROUND, null),
            new Route("BEL_WAS_2", BEL, WAS, 4, Route.Level.UNDERGROUND, null),
            new Route("BER_FRI_1", BER, FRI, 1, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("BER_FRI_2", BER, FRI, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("BER_INT_1", BER, INT, 3, Route.Level.OVERGROUND, Color.BLUE),
            new Route("BER_LUC_1", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("BER_LUC_2", BER, LUC, 4, Route.Level.OVERGROUND, null),
            new Route("BER_NEU_1", BER, NEU, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("BER_SOL_1", BER, SOL, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("BRI_INT_1", BRI, INT, 2, Route.Level.UNDERGROUND, Color.WHITE),
            new Route("BRI_IT5_1", BRI, IT5, 3, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("BRI_LOC_1", BRI, LOC, 6, Route.Level.UNDERGROUND, null),
            new Route("BRI_SIO_1", BRI, SIO, 3, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("BRI_WAS_1", BRI, WAS, 4, Route.Level.UNDERGROUND, Color.RED),
            new Route("BRU_COI_1", BRU, COI, 5, Route.Level.UNDERGROUND, null),
            new Route("BRU_DAV_1", BRU, DAV, 4, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("BRU_IT2_1", BRU, IT2, 2, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("COI_DAV_1", COI, DAV, 2, Route.Level.UNDERGROUND, Color.VIOLET),
            new Route("COI_SAR_1", COI, SAR, 1, Route.Level.UNDERGROUND, Color.WHITE),
            new Route("COI_WAS_1", COI, WAS, 5, Route.Level.UNDERGROUND, null),
            new Route("DAV_AT3_1", DAV, AT3, 3, Route.Level.UNDERGROUND, null),
            new Route("DAV_IT1_1", DAV, IT1, 3, Route.Level.UNDERGROUND, null),
            new Route("DAV_SAR_1", DAV, SAR, 3, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("DE2_SCE_1", DE2, SCE, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("DE3_KRE_1", DE3, KRE, 1, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("DE4_KRE_1", DE4, KRE, 1, Route.Level.OVERGROUND, Color.WHITE),
            new Route("DE5_STG_1", DE5, STG, 2, Route.Level.OVERGROUND, null),
            new Route("DEL_FR4_1", DEL, FR4, 2, Route.Level.UNDERGROUND, Color.BLACK),
            new Route("DEL_LCF_1", DEL, LCF, 3, Route.Level.UNDERGROUND, Color.WHITE),
            new Route("DEL_SOL_1", DEL, SOL, 1, Route.Level.UNDERGROUND, Color.VIOLET),
            new Route("FR1_MAR_1", FR1, MAR, 2, Route.Level.UNDERGROUND, null),
            new Route("FR2_GEN_1", FR2, GEN, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("FR3_LCF_1", FR3, LCF, 2, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("FRI_LAU_1", FRI, LAU, 3, Route.Level.OVERGROUND, Color.RED),
            new Route("FRI_LAU_2", FRI, LAU, 3, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("GEN_LAU_1", GEN, LAU, 4, Route.Level.OVERGROUND, Color.BLUE),
            new Route("GEN_LAU_2", GEN, LAU, 4, Route.Level.OVERGROUND, Color.WHITE),
            new Route("GEN_YVE_1", GEN, YVE, 6, Route.Level.OVERGROUND, null),
            new Route("INT_LUC_1", INT, LUC, 4, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("IT3_LUG_1", IT3, LUG, 2, Route.Level.UNDERGROUND, Color.WHITE),
            new Route("IT4_LOC_1", IT4, LOC, 2, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("KRE_SCE_1", KRE, SCE, 3, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("KRE_STG_1", KRE, STG, 1, Route.Level.OVERGROUND, Color.GREEN),
            new Route("KRE_WIN_1", KRE, WIN, 2, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("LAU_MAR_1", LAU, MAR, 4, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("LAU_NEU_1", LAU, NEU, 4, Route.Level.OVERGROUND, null),
            new Route("LCF_NEU_1", LCF, NEU, 1, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("LCF_YVE_1", LCF, YVE, 3, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("LOC_LUG_1", LOC, LUG, 1, Route.Level.UNDERGROUND, Color.VIOLET),
            new Route("LUC_OLT_1", LUC, OLT, 3, Route.Level.OVERGROUND, Color.GREEN),
            new Route("LUC_SCZ_1", LUC, SCZ, 1, Route.Level.OVERGROUND, Color.BLUE),
            new Route("LUC_ZOU_1", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("LUC_ZOU_2", LUC, ZOU, 1, Route.Level.OVERGROUND, Color.YELLOW),
            new Route("MAR_SIO_1", MAR, SIO, 2, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("NEU_SOL_1", NEU, SOL, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("NEU_YVE_1", NEU, YVE, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("OLT_SOL_1", OLT, SOL, 1, Route.Level.OVERGROUND, Color.BLUE),
            new Route("OLT_ZUR_1", OLT, ZUR, 3, Route.Level.OVERGROUND, Color.WHITE),
            new Route("PFA_SAR_1", PFA, SAR, 3, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("PFA_SCZ_1", PFA, SCZ, 1, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("PFA_STG_1", PFA, STG, 3, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("PFA_ZUR_1", PFA, ZUR, 2, Route.Level.OVERGROUND, Color.BLUE),
            new Route("SAR_VAD_1", SAR, VAD, 1, Route.Level.UNDERGROUND, Color.ORANGE),
            new Route("SCE_WIN_1", SCE, WIN, 1, Route.Level.OVERGROUND, Color.BLACK),
            new Route("SCE_WIN_2", SCE, WIN, 1, Route.Level.OVERGROUND, Color.WHITE),
            new Route("SCE_ZUR_1", SCE, ZUR, 3, Route.Level.OVERGROUND, Color.ORANGE),
            new Route("SCZ_WAS_1", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.GREEN),
            new Route("SCZ_WAS_2", SCZ, WAS, 2, Route.Level.UNDERGROUND, Color.YELLOW),
            new Route("SCZ_ZOU_1", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.BLACK),
            new Route("SCZ_ZOU_2", SCZ, ZOU, 1, Route.Level.OVERGROUND, Color.WHITE),
            new Route("STG_VAD_1", STG, VAD, 2, Route.Level.UNDERGROUND, Color.BLUE),
            new Route("STG_WIN_1", STG, WIN, 3, Route.Level.OVERGROUND, Color.RED),
            new Route("STG_ZUR_1", STG, ZUR, 4, Route.Level.OVERGROUND, Color.BLACK),
            new Route("WIN_ZUR_1", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.BLUE),
            new Route("WIN_ZUR_2", WIN, ZUR, 1, Route.Level.OVERGROUND, Color.VIOLET),
            new Route("ZOU_ZUR_1", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.GREEN),
            new Route("ZOU_ZUR_2", ZOU, ZUR, 1, Route.Level.OVERGROUND, Color.RED));

            // Country to country Tickets
    Ticket deToNeighbors = ticketToNeighbors(DE, 0, 5, 13, 5);
    Ticket atToNeighbors = ticketToNeighbors(AT, 5, 0, 6, 14);
    Ticket itToNeighbors = ticketToNeighbors(IT, 13, 6, 0, 11);
    Ticket frToNeighbors = ticketToNeighbors(FR, 5, 14, 11, 0);


            // City-to-city tickets
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

            // City to country tickets
    Ticket BER_COUNTRY = ticketToNeighbors(List.of(BER), 6, 11, 8, 5);
    Ticket COI_COUNTRY = ticketToNeighbors(List.of(COI), 6, 3, 5, 12);
    Ticket LUG_COUNTRY = ticketToNeighbors(List.of(LUG), 12, 13, 2, 14);
    Ticket ZUR_COUNTRY = ticketToNeighbors(List.of(ZUR), 3, 7, 11, 7);

    static Ticket ticketToNeighbors(List<Station> from, int de, int at, int it, int fr) {
        var trips = new ArrayList<Trip>();
        if (de != 0) trips.addAll(Trip.all(from, DE, de));
        if (at != 0) trips.addAll(Trip.all(from, AT, at));
        if (it != 0) trips.addAll(Trip.all(from, IT, it));
        if (fr != 0) trips.addAll(Trip.all(from, FR, fr));
        return new Ticket(trips);
    }
}
