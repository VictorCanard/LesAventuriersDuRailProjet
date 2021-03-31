package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.StringsFr;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *A trail formed from a list of routes between two given stations
 * @author Victor Canard-Duchêne (326913)
 */

public final class Trail {

    private final List<Route> routes;
    private final Station station1;
    private final Station station2;

    private Trail(List<Route> routes, Station station1, Station station2){
        this.routes = List.copyOf(routes);
        this.station1 = station1;
        this.station2 = station2;
    }

    /**
     *Determines the longest trail formed from a given list of routes
     * @param routes : a list of routes
     * @return the longest trail
     */
    public static Trail longest(List<Route> routes){
        Trail emptyTrail = new Trail(List.copyOf(routes), null, null);
        ArrayList<Route> routesForImmutable = new ArrayList<>(routes);

        List<Trail> trails = listOfTrailsWithOneRoute(routes);

        //If there are no trails ie because there are no routes in the list, then return emptyTrail
        //Else, return the first trail

        Trail longestTrail = (trails.isEmpty()) ? emptyTrail : trails.get(0);

        while(!(trails.isEmpty())){
            ArrayList<Trail> newTrails = new ArrayList<>();


            for (Trail currentTrail: trails) {
                List<Route> routesToProlong = findRoutesToProlongTrail(currentTrail, routesForImmutable);

                longestTrail = returnLongestTrailBetweenCurrentLongestAndNew(currentTrail, longestTrail);

                routesToProlong.forEach((routeToProlong) ->{
                    List<Route> newListOfRoutes = new ArrayList<>(List.copyOf(currentTrail.routes));
                    newListOfRoutes.add(routeToProlong);
                    Trail newTrail = new Trail(newListOfRoutes, currentTrail.station1(), routeToProlong.stationOpposite(currentTrail.station2()));

                    newTrails.add(newTrail);
                });


            }
            newTrails.add(longestTrail);

            longestTrail = newTrails.stream().max(Comparator.comparingInt(Trail::length)).get();

            trails = newTrails;
        }
        return longestTrail;

    }
    private static Trail returnLongestTrailBetweenCurrentLongestAndNew(Trail newTrail, Trail currentLongestTrail){
        if(newTrailIsLonger(newTrail, currentLongestTrail)){
            return newTrail;
        }
        return currentLongestTrail;
    }
    private static boolean newTrailIsLonger(Trail newTrail, Trail currentLongestTrail){
        return (newTrail.length() > currentLongestTrail.length());
    }

    private static List<Trail> listOfTrailsWithOneRoute(List<Route> routes){
        List<Trail> trailsToReturn = new ArrayList<>();

        routes.forEach((route) -> {   //Initializes two trails one with the route in the right order and one where the stations are inverted
            trailsToReturn.add(new Trail(List.of(route), route.station1(), route.station2()));
            trailsToReturn.add(new Trail(List.of(route), route.station2(), route.station1()));
        });

        return trailsToReturn;
    }

    private static List<Route> findRoutesToProlongTrail(Trail trail, ArrayList<Route> routes){

        Station trailEndStationToWhichRoutesCanBeAdded = trail.station2();

        List<Route> routesToReturn = routes
                                            .stream()
                                            .filter(route -> checkIfNewRouteCanBeAdded(route, trailEndStationToWhichRoutesCanBeAdded))
                                            .filter(route -> !(trail.routes.contains(route)))
                                            .collect(Collectors.toList());


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
        return routes
                .stream()
                .mapToInt(Route::length)
                .sum();
    }

    /**
     *Getter for the first station of the trail
     * @return the first station of the trail
     */
    public Station station1(){
        return  station1;
    }

    /**
     *Getter for the second station of the trail
     * @return the second station of the trail
     */
    public Station station2(){
        return station2;
    }

    /**
     * Redefinition of the toString method for the textual representation of a trail
     * @return the textual representation of the trail determined by its starting and ending stations, and length
     */
    @Override
    public String toString() {
        if(routes.isEmpty()){
            return "";
        }

        StringBuilder text = new StringBuilder()
                .append(station1)
                .append(StringsFr.EN_DASH_SEPARATOR)
                .append(station2);

/*
                Station currentStation = station1;

                text.append(currentStation.name())
                        .append(" - ");

                for (int i = 0; i < routes.size()-1; i++) {
                    currentStation = routes.get(i).stationOpposite(currentStation);
                    text.append(currentStation.name())
                        .append(" - ");
                }
*/
        return String.format("%s (%s)", text, length());
        }
    }


