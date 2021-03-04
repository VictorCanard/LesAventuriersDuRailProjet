package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A trip between two stations
 * @author Anne-Marie Rusu (296098)
 * @author Victor Jean Canard-Duchene (326913)
 */
public final class Trip {
    private final Station FROM;
    private final Station TO;
    private final int POINTS;

    /**
     * Constructs a trip with the given stations and number of points attributed to it
     * checks if the arguments aren't empty before attributing them
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points attributed to the trip
     * @throws IllegalArgumentException if the points are negative
     * @throws NullPointerException if the stations are null
     */
    public Trip(Station from, Station to, int points) {
        this.FROM = Objects.requireNonNull(from);
        this.TO = Objects.requireNonNull(to);

        Preconditions.checkArgument(points>0);

        this.POINTS = points;
    }

    /**
     * Returns a list of all the possible trips from a departure station on the departure
     * list to an arrival station on the arrival list
     * @param from : list of departure stations (departure list)
     * @param to : list of arrival stations (arrival list)
     * @param points : number of points attributed to the trip
     * @throws IllegalArgumentException if the departure station or arrival stations are null
     * or if the number of points is negative
     * @return : all possible trips.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(from != null && to != null && points > 0);

        ArrayList<Trip> allTrips = new ArrayList<>();

        for (Station departureStation : from) {
            for(Station arrivalStation : to){
                allTrips.add(new Trip(departureStation,arrivalStation,points));
            }
        }
        return allTrips;
    }

    /**
     * Getter for departure station
     * @return : departure station
     */
    public Station from(){
        return FROM;
    }

    /**
     * Getter for arrival station
     * @return : arrival station
     */
    public Station to(){
        return TO;
    }

    /**
     * Getter for points attributed to the trip
     * @return : points attributed to the trip
     */
    public int points(){
        return POINTS;
    }

    /**
     * Determines the number of points for the trip depending on whether or not the stations are connected
     * @param connectivity : connectivity of trip
     * @return : positive points for a connection, negative points otherwise
     */
    public int points(StationConnectivity connectivity){
        if(connectivity.connected(FROM, TO)){
            return POINTS;
        }
        return -POINTS;
    }
}
