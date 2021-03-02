package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class Trail {

    private final List<Route> ROUTES;
    private final Station STATION1;
    private final Station STATION2;


    private Trail(List<Route> routes, Station station1, Station station2){
        this.STATION1 = station1;
        this.STATION2 = station2;
        this.ROUTES = List.copyOf(routes);
    }
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
        return (newTrail.length() > currentLongestTrail.length()) ? true : false;
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
    public int length(){
        int length = 0;
        for (Route route:ROUTES
             ) {
            length += route.length();
        }
        return length;
    }

    public Station station1(){
        return (length() == 0) ? null : STATION1;
    }

    public Station station2(){
        return (length() == 0) ? null : STATION2;
    }

    @Override
    public String toString() {
        if(ROUTES.isEmpty()){
            return "";
        }
        else{
            StringBuilder text = new StringBuilder();

            Station currentStation = ROUTES.get(0).station1();
            text.append(currentStation.name())
                    .append(" -");

            for (int i = 0; i < ROUTES.size(); i++) {
                currentStation = ROUTES.get(i).stationOpposite(currentStation);
                text.append(currentStation.name())
                        .append(" -");
            }

            String textReturned = String.format("%s (%s)", text, length());

            return textReturned;
        }
    }

}
