package ch.epfl.tchu.game;

import java.util.List;

public final class Trail {
    private final String TEXT;
    private static List<Route> routes;

    public static Trail longest(List<Route> routes){
        if(routes.size()==0){
            return new Trail();
        }
        for (Route route: routes
             ) {


        }

    }
    public int length(){

    }
    public Station station1(){}
    public Station station2(){}

    @Override
    public String toString() {
        return TEXT;
    }
}
