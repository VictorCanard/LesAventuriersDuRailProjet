package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Class Ticket
 * @author Anne-Marie Rusu (296098)
 * @author Victor Canard-Duchêne (326913)
 */

public final class Ticket  implements Comparable<Ticket>{

    private final String TEXT;
    private final String DELIMITER = ", ";
    private String departure;

    private List<Trip> trips;
    private TreeSet<String> departureStationsNames = new TreeSet<>();


    /**
     * Primary Ticket Constructor
     * @param trips : a list of trips provided for the construction of a ticket
     */
    public Ticket(List<Trip> trips){

        this.trips = List.copyOf(trips);

        for(Trip t : this.trips){
            departureStationsNames.add(t.from().name());
        }

        Preconditions.checkArgument(departureStationsNames.size() == 1);

        departure = this.trips.get(0).from().name();

        if(this.trips.size()>1){ //The first format where there's only one destination
            TEXT = String.format("%s - {%s}", departure,Ticket.computeText(DELIMITER, this.trips));
        }else {
            TEXT = String.format("%s - %s", departure,Ticket.computeText(DELIMITER, this.trips));
        }

    }

    /**
     * Secondary Ticket Constructor
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points allocated for the corresponding trip
     */
    public Ticket(Station from, Station to, int points){

        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Textual representation of the trip ticket
     * @return : text of the ticket
     */
    public String text(){
        return TEXT;
    }

    /**
     * Formats the ticket's text
     * @param delimiter : separator for the display of arrival stations' names
     * @param trip : list of trips for one departure station
     * @return arrival stations and points associated
     */
    private static String computeText(String delimiter, List<Trip> trip){

        TreeSet<String> arrivalStationsNames = new TreeSet<>();

        for(Trip t : trip){
            arrivalStationsNames.add(String.format("%s (%s)", t.to().name(), t.points()));
        }

        return String.join(delimiter, arrivalStationsNames);
    }

    /**
     * Calculates the points the player receives or loses according to the stations he connected
     * Take the max between the current amount of points and the points that can be gained
     * through a connection with a new station. This works as the max amount of points to be added
     * is always larger than the min amount of points to be removed (as that number is negative).
     * @param connectivity : the connectivity of the trip
     * @return the max amount of points to be gained
     * or min to be lost.
     */
    public int points(StationConnectivity connectivity){
        int pointsToBeReturned = -100;

        for (Trip trip: trips) {
            pointsToBeReturned = Math.max(pointsToBeReturned, trip.points(connectivity));
        }
        return pointsToBeReturned;
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
