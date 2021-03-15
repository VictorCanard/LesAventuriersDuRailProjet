package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * A train ticket permitting the player to embark on certain paths in the game
 * @author Anne-Marie Rusu (296098)
 * @author Victor Canard-Duchêne (326913)
 */

public final class Ticket  implements Comparable<Ticket>{

    private final String text;
    private final String delimiter = ", ";
    private String departure;

    private List<Trip> trips;
    private TreeSet<String> departureStationsNames = new TreeSet<>();


    /**
     * Primary Ticket constructor defined by a list of trips
     * Calls the method computeText() to initialize the ticket's text, in a specific format
     * according to the number of Arrival Stations
     * @param trips : a list of trips provided for the construction of a ticket
     * @throws IllegalArgumentException if trips is empty or if all departure stations aren't the same
     */
    public Ticket(List<Trip> trips){

        this.trips = List.copyOf(trips);

        for(Trip t : this.trips){
            departureStationsNames.add(t.from().name());
        }

        Preconditions.checkArgument(departureStationsNames.size() == 1);

        departure = this.trips.get(0).from().name();

        if(this.trips.size()>1){ //The first format where there's only one destination
            text = String.format("%s - {%s}", departure,Ticket.computeText(delimiter, this.trips));
        }else {
            text = String.format("%s - %s", departure,Ticket.computeText(delimiter, this.trips));
        }

    }

    /**
     * Secondary Ticket constructor defined by a single trip
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points allocated for the corresponding trip
     */
    public Ticket(Station from, Station to, int points){

        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Textual representation of the trip's ticket
     * @return : text of the ticket
     */
    public String text(){
        return text;
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
     * Takes the max between the current amount of points and the points that can be gained
     * through a connection with a new station. This works as the max amount of points to be added
     * is always larger than the min amount of points to be removed (as that number is negative).
     * @param connectivity : the connectivity of the trip
     * @return the max amount of points to be gained
     * or min to be lost.
     */
    public int points(StationConnectivity connectivity){
        int pointsToBeReturned = Integer.MIN_VALUE;

        for (Trip trip: trips) {
            pointsToBeReturned = Math.max(pointsToBeReturned, trip.points(connectivity));
        }
        return pointsToBeReturned;
    }

    /**
     * Compares this ticket with a specified ticket according to alphabetical order.
     * @param that : the ticket to compare this ticket to
     * @return : a negative integer, zero, or a positive integer according to if this ticket's text
     * is less than, equal to, or greater than the specified ticket's text, alphabetically.
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text().compareTo(that.text());
    }
}
