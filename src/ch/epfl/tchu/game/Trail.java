package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public final class Trail {

    private final List<Route> ROUTES;
    private final Station STATION1;
    private final Station STATION2;

    private int length;

    private Trail(List<Route> routes, Station station1, Station station2){
        this.STATION1 = station1;
        this.STATION2 = station2;
        this.ROUTES = routes;
    }
    public static Trail longest(List<Route> routes){
        if(routes.size()==0){
            return new Trail(new ArrayList<Route>(){}, null, null);
        }
        else{
            if(routes.contains(null)){

            }

        }

        return null;
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

        for (Route route: ROUTES) {
            text.append(route.station1().name())
                .append(" -");
        }

        String textReturned = String.format(" %s (%s)", STATION2, length);

        return textReturned;
    }
}
