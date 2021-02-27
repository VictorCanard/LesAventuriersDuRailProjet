package ch.epfl.tchu.game;

import java.util.List;

public final class Trail {
   // private final String TEXT;
    private static List<Route> routes;

    public static Trail longest(List<Route> routes){
        if(routes.size()==0){
            return new Trail();
        }
        for (Route route: routes
             ) {


        }
        return null;

    }
    public int length(){
        return 0;

    }
    public Station station1(){return null;}
    public Station station2(){return null; }

    @Override
    public String toString() {
        return null;
    }
}
