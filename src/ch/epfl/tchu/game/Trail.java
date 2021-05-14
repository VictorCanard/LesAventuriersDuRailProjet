package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.StringsFr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a trail formed from a list of routes between two given stations
 *
 * @author Victor Canard-Duchêne (326913)
 */

public final class Trail {

    private final static Trail EMPTY_TRAIL = new Trail(List.of(), null, null);
    private final List<Route> routes;
    private final Station station1;
    private final Station station2;
    private final int length;

    /**
     * Private constructor for a trail
     *
     * @param routes   : that make up the trail
     * @param station1 : departure station
     * @param station2 : arrival station
     */
    private Trail(List<Route> routes, Station station1, Station station2) {
        this.routes = routes;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length();
    }

    /**
     * Determines the longest trail formed from a given list of routes
     *
     * @param routes : a list of routes
     * @return the longest trail of these routes
     */
    public static Trail longest(List<Route> routes) {
        List<Trail> trails = listOfTrailsWithOneRoute(routes);

        Trail longestTrail = trails
                .stream()
                .max(Comparator.comparingInt(trail -> trail.length))
                .orElse(EMPTY_TRAIL);

        while (!(trails.isEmpty())) {
            ArrayList<Trail> newTrails = new ArrayList<>();

            for (Trail currentTrail : trails) {
                List<Route> routesToProlong = findRoutesToProlongTrail(currentTrail, routes);

                routesToProlong.forEach((routeToProlong) -> {
                    List<Route> newListOfRoutes = new ArrayList<>(currentTrail.routes);
                    newListOfRoutes.add(routeToProlong);

                    Trail newTrail = new Trail(newListOfRoutes, currentTrail.station1(), routeToProlong.stationOpposite(currentTrail.station2()));

                    newTrails.add(newTrail);
                });
            }

            newTrails.add(longestTrail);

            longestTrail = newTrails
                    .stream()
                    .max(Comparator.comparingInt(trail -> trail.length))
                    .orElseThrow();

            newTrails.remove(longestTrail);
            trails = newTrails;


        }
        return longestTrail;
    }

    /**
     * Creates a new list of trails where each trail is composed of one route
     * (first in the correct order then with the stations reversed to be sure to find the correct longest trail).
     *
     * @param routes : routes we want to split up each into a trail composed of one route
     * @return trails with one route
     */
    private static List<Trail> listOfTrailsWithOneRoute(List<Route> routes) {

        List<Trail> trailsToReturn = new ArrayList<>();

        routes.forEach((route) -> {
            //Initializes two trails one with the route in the right order and one where the stations are inverted
            trailsToReturn.add(new Trail(List.of(route), route.station1(), route.station2()));
            trailsToReturn.add(new Trail(List.of(route), route.station2(), route.station1()));
        });

        return trailsToReturn;
    }

    /**
     * Finds all the routes that can prolong the given trail,
     * ie the ones that aren't already in the trail and that can be added to the current trail's end station.
     *
     * @param trail  : given trail we want to prolong
     * @param routes : that could be added to this trail
     * @return a new list of routes that can prolong the trail
     */
    private static List<Route> findRoutesToProlongTrail(Trail trail, List<Route> routes) {

        Station trailEndStationToWhichRoutesCanBeAdded = trail.station2();

        return routes
                .stream()
                .filter(route -> checkIfNewRouteCanBeAdded(route, trailEndStationToWhichRoutesCanBeAdded)
                        && !(trail.routes.contains(route)))
                .collect(Collectors.toList());

    }

    /**
     * Checks if a given route can be added. Tests if one of the route's stations is the same as the trail end station we want to attach routes to.
     *
     * @param routeThatCouldProlongTrail : route that could prolong
     * @param trailEndStation            : station to which we add routes
     * @return true if the route can be added, false otherwise.
     */
    private static boolean checkIfNewRouteCanBeAdded(Route routeThatCouldProlongTrail, Station trailEndStation) {
        return (routeThatCouldProlongTrail.stations().contains(trailEndStation));
    }


    /**
     * Getter for the length of a trail as the sum of the lengths of its routes
     *
     * @return length of the trail
     */
    public int length() {
        return routes
                .stream()
                .mapToInt(Route::length)
                .sum();
    }

    /**
     * Getter for the first station of the trail
     *
     * @return the first station of the trail
     */
    public Station station1() {
        return station1;
    }

    /**
     * Getter for the second station of the trail
     *
     * @return the second station of the trail
     */
    public Station station2() {
        return station2;
    }

    /**
     * Redefinition of the toString method for the textual representation of a trail
     *
     * @return the textual representation of the trail determined by its starting and ending stations, and by its length
     */
    @Override
    public String toString() {
        if (routes.isEmpty()) {
            return "";
        }

        String text = station1 + StringsFr.EN_DASH_SEPARATOR + station2;

        return String.format("%s (%s)", text, length());
    }
}


