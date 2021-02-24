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
    private Station from;
    private Station to;
    private int points;

    /**
     * Primary Ticket Constructor
     * @param trips
     */
    Ticket(List<Trip> trips){
        if(trips == null /*or departure not same*/){
            throw new IllegalArgumentException();
        }


    }

    /**
     * Secondary Ticket Constructor
     * @param from
     * @param to
     * @param points
     */
    Ticket(Station from, Station to, int points){
        List.of(new Trip(from, to, points));

    }

    /**
     * Textual representation of the trip ticket
     * @return : text of the ticket
     */
    public String text(){


    }

    private static String computeText(List<Trip> trip){ //list in argument is the list of trips for one departure station

        TreeSet<String> arrivalStations = new TreeSet<>();
        if(trip.size() == 1){
            arrivalStations.add(String.format("%s ( %s )", trip.get(0).to(), trip.get(0).points()));
        }
        for(Trip t : trip){
            arrivalStations.add(String.format("%s ( %s )", t.to(), t.points()));
        }

        return String.join(" ", arrivalStations);

    }

    public int points(StationConnectivity connectivity){

    }

    @Override
    public int compareTo(Ticket that) {
        return 0;
    }


}
