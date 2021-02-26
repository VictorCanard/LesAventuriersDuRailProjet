package ch.epfl.tchu.game;

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

    private Station departureStation;
    private Map<Station, Integer> arrivalStationsAndAssociatedPoints = new HashMap<>();
    private TreeSet<String> departureStationsNames = new TreeSet<>();

    /**
     * Primary Ticket Constructor
     * @param trips : a list of trips provided for the construction of a ticket
     */
    public Ticket(List<Trip> trips){

        for(Trip t : trips){
            departureStationsNames.add(t.from().name());
            arrivalStationsAndAssociatedPoints.put(t.to(), t.points());
        }

        if(departureStationsNames.size() != 1){
            throw new IllegalArgumentException("Empty list or all departures aren't from the same station.");
        }
        else{
            departureStation = trips.get(0).from();
            departure = departureStation.name();

            if(trips.size()>1){
                TEXT = String.format("%s - {%s}", departure,Ticket.computeText(DELIMITER, trips));
            }else {
                TEXT = String.format("%s - %s", departure,Ticket.computeText(DELIMITER, trips));
            }
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
     * @param connectivity : the connectivity of the trip
     * @return the max amount of points to be gained
     * or min to be lost.
     */
    public int points(StationConnectivity connectivity){
        int counterOfConnectedStations = 0;

        int minAmountOfPointsToBeSubstracted = Collections.min(arrivalStationsAndAssociatedPoints.values());
        int maxAmountOfPointsToBeAdded = 0;

        for (Map.Entry<Station,Integer> arrivalStation : arrivalStationsAndAssociatedPoints.entrySet()) {


            if (connectivity.connected(departureStation, arrivalStation.getKey())) { //Stations
                counterOfConnectedStations++;

                maxAmountOfPointsToBeAdded = Math.max(maxAmountOfPointsToBeAdded, arrivalStation.getValue()); //Points
            }
        }

        if(counterOfConnectedStations >0){
            return maxAmountOfPointsToBeAdded;
        }else{
            return -minAmountOfPointsToBeSubstracted;
        }
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
