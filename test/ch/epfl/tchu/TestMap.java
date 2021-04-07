package ch.epfl.tchu;

import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.List;

public interface TestMap {

        // Stations - cities
        Station BER = new Station(0, "Berne");
        Station LAU = new Station(1, "Lausanne");
        Station STG = new Station(2, "Saint-Gall");

        Station YVE = ChMap.stations().get(31);
        Station SCZ = ChMap.stations().get(24);

        // Stations - countries
        Station DE1 = new Station(3, "Allemagne");
        Station DE2 = new Station(4, "Allemagne");
        Station DE3 = new Station(5, "Allemagne");
        Station AT1 = new Station(6, "Autriche");
        Station AT2 = new Station(7, "Autriche");
        Station IT1 = new Station(8, "Italie");
        Station IT2 = new Station(9, "Italie");
        Station IT3 = new Station(10, "Italie");
        Station FR1 = new Station(11, "France");
        Station FR2 = new Station(12, "France");

        // Countries
        List<Station> DE = List.of(DE1, DE2, DE3);
        List<Station> AT = List.of(AT1, AT2);
        List<Station> IT = List.of(IT1, IT2, IT3);
        List<Station> FR = List.of(FR1, FR2);

        Ticket LAU_STG = new Ticket(LAU, STG, 13);
        Ticket LAU_BER = new Ticket(LAU, BER, 2);
        Ticket BER_NEIGHBORS = ticketToNeighbors(List.of(BER), 6, 11, 8, 5);
        Ticket FR_NEIGHBORS = ticketToNeighbors(FR, 5, 14, 11, 0);

        private static Ticket ticketToNeighbors(List<Station> from, int de, int at, int it, int fr) {
            var trips = new ArrayList<Trip>();
            if (de != 0) trips.addAll(Trip.all(from, DE, de));
            if (at != 0) trips.addAll(Trip.all(from, AT, at));
            if (it != 0) trips.addAll(Trip.all(from, IT, it));
            if (fr != 0) trips.addAll(Trip.all(from, FR, fr));
            return new Ticket(trips);
        }

        //Routes

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
        List<Route> listeRoutes2 = List.of(BER_FRI_1);
        //Trails

        Trail testTrail1 = Trail.longest(listeRoutes.subList(0, 4));
        Trail testTrail2 = Trail.longest(listeRoutes.subList(0, 3));

}
