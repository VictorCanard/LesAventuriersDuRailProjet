package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *A trail formed from a list of routes between two given stations
 * @author Victor Canard-Duchêne (326913)
 */

public final class Trail {

    private final List<Route> ROUTES;
    private final Station STATION1;
    private final Station STATION2;

    private Trail(List<Route> routes, Station station1, Station station2){
        this.STATION1 = station1;
        this.STATION2 = station2;
        this.ROUTES = List.copyOf(routes);
    }

    /**
     *Determines the longest trail formed from a given list of routes
     * @param routes : a list of routes
     * @return the longest trail
     */
    public static Trail longest(List<Route> routes){
        Trail emptyTrail = new Trail(new ArrayList(){}, null, null);

        if(routes.size()==0){
            return emptyTrail;
        }
        else{
            List<Trail> trails = listOfTrailsWithOneRoute(routes);

            Trail longestTrail = emptyTrail;

            while(!(trails.isEmpty())){
                List<Trail> newTrails = new ArrayList<>();

                for (Trail currentTrail: trails) {
                    List<Route> routesToProlong = findRoutesToProlongTrail(currentTrail, routes);

                    for (Route route: routesToProlong) {
                        List<Route> newListOfRoutes = new ArrayList<>(List.copyOf(currentTrail.ROUTES));
                        newListOfRoutes.add(route);

                        Trail newTrail = new Trail(newListOfRoutes, currentTrail.station1(), route.stationOpposite(currentTrail.station2()));

                        if(newTrailIsLonger(newTrail, longestTrail)){
                            longestTrail = newTrail;
                        }
                        newTrails.add(newTrail);
                    }
                }
                trails = newTrails;
            }
            return longestTrail;
        }
    }
    private static boolean newTrailIsLonger(Trail newTrail, Trail currentLongestTrail){
        return (newTrail.length() > currentLongestTrail.length());
    }

    private static List<Trail> listOfTrailsWithOneRoute(List<Route> routes){
        List<Trail> trailsToReturn = new ArrayList<>();

        for(Route route: routes){
            trailsToReturn.add(new Trail(new ArrayList<>(Collections.singleton(route)), route.station1(), route.station2()));
        }
        return trailsToReturn;
    }

    private static List<Route> findRoutesToProlongTrail(Trail trail, List<Route> routes){
        List<Route> routesToReturn = new ArrayList<>();
        Station trailEndStationToWhichRoutesCanBeAdded = trail.station2();

        for (Route routeThatCouldBeAdded: routes) {
            if(!(trail.ROUTES.contains(routeThatCouldBeAdded))){

                if(checkIfNewRouteCanBeAdded(routeThatCouldBeAdded, trailEndStationToWhichRoutesCanBeAdded)) {
                    routesToReturn.add(routeThatCouldBeAdded);
                }
            }
        }
        return routesToReturn;
    }

    private static boolean checkIfNewRouteCanBeAdded(Route newRoute, Station trailEndStation){
        return (newRoute.stations().contains(trailEndStation));
    }

    /**
     *Getter for length of a trail as the sum of its routes
     * @return length of the trail
     */
    public int length(){
        int length = 0;

        for (Route route:ROUTES) {
            length += route.length();
        }
        return length;
    }

    /**
     *Getter for the first station of the trail
     * @return the first station of the trail
     */
    public Station station1(){
        return (length() == 0) ? null : STATION1;
    }

    /**
     *Getter for the second station of the trail
     * @return the second station of the trail
     */
    public Station station2(){
        return (length() == 0) ? null : STATION2;
    }

    /**
     * Redefinition of the toString method for the textual representation of a trail
     * @return the textual representation of the trail determined by its starting and ending stations, and length
     */
    @Override
    public String toString() {
        if(ROUTES.isEmpty()){
            return "";
        }
        else{
            StringBuilder text = new StringBuilder();

            Station currentStation = ROUTES.get(0).station1();

            text.append(currentStation.name())
                    .append(" - ");

            for (int i = 0; i < ROUTES.size()-1; i++) {
                currentStation = ROUTES.get(i).stationOpposite(currentStation);
                text.append(currentStation.name())
                        .append(" - ");
            }
            return String.format("%s%s (%s)", text, station2(), length());
        }
    }

}
