package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;


public final class Trail {

    private final List<Route> ROUTES;
    private final Station STATION1;
    private final Station STATION2;

    private int length;

    private Trail(List<Route> routes, Station station1, Station station2, int length){
        this.STATION1 = station1;
        this.STATION2 = station2;
        this.ROUTES = routes;
        this.length = length;
    }
    public static Trail longest(List<Route> routes){
        Trail emptyTrail = new Trail(new ArrayList(){}, null, null, 0);

        if(routes.size()==0){
            return emptyTrail;
        }
        else{
            List<Trail> chemins = new ArrayList(routes); //Liste de routes à transformer en liste de chemins à une seule route
            Trail longestTrail = emptyTrail;

            while(!(chemins.isEmpty())){
                List<Trail> nouveauxChemins = new ArrayList<>();

                for (Trail trail: chemins) {
                    List<Route> routesToProlong = findRoutesToProlongTrail(trail, routes);

                    for (Route route: routesToProlong) {
                        List<Route> newListOfRoutes = new ArrayList(List.of(trail.ROUTES,route));

                        int newLength = trail.length()+ route.length();

                        Trail newTrail = new Trail(newListOfRoutes, trail.station1(), trail.station2(), newLength);

                        if(newLength> longestTrail.length()){
                            longestTrail = newTrail;
                        }
                        nouveauxChemins.add(newTrail);
                    }
                }
                chemins = nouveauxChemins;
            }
            return longestTrail;
        }


    }
    private static List<Route> findRoutesToProlongTrail(Trail trail, List<Route> routes){
        List<Route> routesToReturn = new ArrayList<>();
        Station trailEndStationToWhichRoutesCanBeAdded = trail.station2();

        for (Route routeThatCouldBeAdded: routes) {
            if(!(trail.ROUTES.contains(routeThatCouldBeAdded))){

                if(routeThatCouldBeAdded.station1().equals(trailEndStationToWhichRoutesCanBeAdded)){
                    routesToReturn.add(routeThatCouldBeAdded);
                }
                else if(routeThatCouldBeAdded.station2().equals(trailEndStationToWhichRoutesCanBeAdded)){
                    routesToReturn.add(new Route(routeThatCouldBeAdded.id(), routeThatCouldBeAdded.station1(), routeThatCouldBeAdded.station2(),
                            routeThatCouldBeAdded.length(), routeThatCouldBeAdded.level(), routeThatCouldBeAdded.color()));
                }
            }
        }
        return routesToReturn;
    }

    public int length(){
        return this.length;
    }

    public Station station1(){
        return (length == 0) ? null : STATION1;
    }

    public Station station2(){
        return (length == 0) ? null : STATION2;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();

        for (Route route: ROUTES) { //Add station names separated by a " -"
            text.append(route.station1().name())
                .append(" -");
        }

        String textReturned = String.format(" %s (%s)", STATION2, length);

        return textReturned;
    }
}
