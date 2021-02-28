package ch.epfl;

import ch.epfl.tchu.game.*;

import java.util.ArrayList;
import java.util.List;

public interface TestMap {

        // Stations - cities
        public final Station BER = new Station(0, "Berne");
        public final Station LAU = new Station(1, "Lausanne");
        public final Station STG = new Station(2, "Saint-Gall");

        // Stations - countries
        public final Station DE1 = new Station(3, "Allemagne");
        public final Station DE2 = new Station(4, "Allemagne");
        public final Station DE3 = new Station(5, "Allemagne");
        public final Station AT1 = new Station(6, "Autriche");
        public final Station AT2 = new Station(7, "Autriche");
        public final Station IT1 = new Station(8, "Italie");
        public final Station IT2 = new Station(9, "Italie");
        public final Station IT3 = new Station(10, "Italie");
        public final Station FR1 = new Station(11, "France");
        public final Station FR2 = new Station(12, "France");

        // Countries
        public final List<Station> DE = List.of(DE1, DE2, DE3);
        public final List<Station> AT = List.of(AT1, AT2);
        public final List<Station> IT = List.of(IT1, IT2, IT3);
        public final List<Station> FR = List.of(FR1, FR2);

        public final Ticket LAU_STG = new Ticket(LAU, STG, 13);
        public final Ticket LAU_BER = new Ticket(LAU, BER, 2);
        public final Ticket BER_NEIGHBORS = ticketToNeighbors(List.of(BER), 6, 11, 8, 5);
        public final Ticket FR_NEIGHBORS = ticketToNeighbors(FR, 5, 14, 11, 0);

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

}