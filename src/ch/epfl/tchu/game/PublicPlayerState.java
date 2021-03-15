package ch.epfl.tchu.game;

import java.util.List;

public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        this.carCount = calculateCarCount(this.routes);
        this.claimPoints = calculateClaimPoints(this.routes);
    }

    public int ticketCount(){
        return ticketCount;
    }
    public int cardCount(){
        return cardCount;
    }
    public List<Route> routes(){
        return routes;
    }
    public int carCount(){return carCount;}
    public int claimPoints(){return claimPoints;}

    private int calculateCarCount(List<Route> routes){
        int carCount = Constants.INITIAL_CAR_COUNT;
        for (Route route: routes
             ) {
            carCount -= route.length();

        }
        return carCount;
    }
    private int calculateClaimPoints(List<Route> routes){
        int claimPoints = 0;
        for (Route route: routes
        ) {
            claimPoints += route.claimPoints();

        }
        return claimPoints;
    }

}
