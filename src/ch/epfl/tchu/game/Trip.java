package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class Trip
 * @author Anne-Marie Rusu (296098)
 * @author Victor Jean Canard-Duchene (326913)
 */
public final class Trip {
    private final Station FROM;
    private final Station TO;
    private final int POINTS;

    /**
     * Constructor for Trip
     * @param from : departure station
     * @param to : arrival station
     * @param points : number of points attributed to the trip
     */
    public Trip(Station from, Station to, int points) {
        this.FROM = Objects.requireNonNull(from);
        this.TO = Objects.requireNonNull(to);

        Preconditions.checkArgument(points>0);

        this.POINTS = points;
    }

    /**
     * @param from : list of departure stations (departure list)
     * @param to : list of arrival stations (arrival list)
     * @param points : number of points attributed to the trip
     * @return : list of all the possible trips from a departure station on the departure
     * list to the arrival station on the arrival list
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        Preconditions.checkArgument(!(from == null || to == null || points <= 0));

        ArrayList<Trip> allTrips = new ArrayList<>();

        for (Station departureStation :from) {
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
     * Determines number of points for the trip depending on whether or not the stations are connected
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
