package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A train ticket permitting the player to embark on certain paths in the game
 * @author Anne-Marie Rusu (296098)
 * @author Victor Canard-Duchêne (326913)
 */

public final class Ticket  implements Comparable<Ticket>{

    /**
     * Text of the ticket describing its trips with a certain formatting depending on the number of trips
     */
    private final String text;

    /**
     * List of all trips of this ticket
     */
    private final List<Trip> trips;


    /**
     * Primary Ticket constructor defined by a list of trips
     * Calls the method computeText() to initialize the ticket's text, in a specific format
     * according to the number of Arrival Stations
     * @param trips : a list of trips provided for the construction of a ticket
     * @throws IllegalArgumentException if trips is empty or if all departure stations aren't the same
     */
    public Ticket(List<Trip> trips){
        this.trips = List.copyOf(trips);

        TreeSet<String> departureStationsNames = trips
                .stream()
                .map(((trip -> trip.from().name())))
                .collect(Collectors.toCollection(TreeSet::new));

        Preconditions.checkArgument(departureStationsNames.size() == 1); //Checks all departure stations are the same and that trips isn't empty

        String departure = departureStationsNames.first();
        String computedText = Ticket.computeText(this.trips);

        if(this.trips.size() == 1){ //The first format where there's only one destination
            this.text = String.format("%s - %s", departure, computedText);

        }else { //Multiple Destinations
            this.text = String.format("%s - {%s}", departure, computedText);
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
     * @param trips : list of trips for one departure station
     * @return arrival stations and points associated
     */
    private static String computeText(List<Trip> trips){

        TreeSet<String> arrivalStationNames  = trips
                .stream()
                .map(trip -> {
                    String name = trip.to().name();
                    int points = trip.points();

                    return String.format("%s (%s)", name, points);
                    }
                )
                .collect(Collectors.toCollection(TreeSet::new));


        return String.join(", ", arrivalStationNames);
    }

    /**
     * Calculates the points the player receives or loses according to the stations he connected
     * Takes the max between the current amount of points and the points that can be gained
     * through a connection with a new station. This works as the max amount of points to be added
     * is always larger than the min amount of points to be removed (as that number is negative).
     * @param connectivity : the connectivity of the trip
     * @return the max amount of points to be gained or min to be lost.
     */
    public int points(StationConnectivity connectivity){

        return trips
                .stream()
                .mapToInt(trip -> trip.points(connectivity))
                .max()
                .orElse(0);

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
