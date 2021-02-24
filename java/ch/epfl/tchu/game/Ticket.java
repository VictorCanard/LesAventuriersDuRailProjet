package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Class Ticket
 * @author Anne-Marie Rusu (296098)
 */
public final class Ticket  implements Comparable<Ticket>{

    private final String TEXT;
    private static boolean biggerThan1; //default false
    private int i;
    private Station departure;

    /**
     * Primary Ticket Constructor
     * @param trips : a list of trips provided for the construction of a ticket
     */
    Ticket(List<Trip> trips){

        TreeSet<Station> departureStations = new TreeSet<>();

        for(Trip t : trips){departureStations.add(t.from());}

        if(departureStations.size() != 1){
            throw new IllegalArgumentException();
        }else{
            departure = departureStations.first();

            if(biggerThan1){
                TEXT = departure + " - {" + Ticket.computeText(trips) + "}";
            }else {
                TEXT = departure + " - " + Ticket.computeText(trips);
            }
        }
    }

    /**
     * Secondary Ticket Constructor
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points allocated for the corresponding trip
     */
    Ticket(Station from, Station to, int points){
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Textual representation of the trip ticket
     * @return : text of the ticket
     */
    public String text(){
        return TEXT;
    }

    private static String computeText(List<Trip> trip){ //list in argument is the list of trips for one departure station

        TreeSet<String> arrivalStations = new TreeSet<>();

        for(Trip t : trip){
            arrivalStations.add(String.format("%s (%s)", t.to(), t.points()));
        }
        if(arrivalStations.size()>1){ biggerThan1 = true; }
        biggerThan1 = false;

        return String.join(", ", arrivalStations);
    }

    /**
     * @param connectivity : the connectivity of the trip
     * @return
     */
    public int points(StationConnectivity connectivity){
       // if(connectivity.connected(departure, ))
        return 0;

    }

    /**
     * Compares this ticket with a specified ticket for alphabetical order.
     * @param that : the ticket to be compared.
     * @return : a negative integer, zero, or a positive integer as this ticket
     * is less than, equal to, or greater than the specified ticket, alphabetically.
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }
}
