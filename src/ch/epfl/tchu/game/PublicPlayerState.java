package ch.epfl.tchu.game;

import java.util.List;

public class PublicPlayerState {
    private final int TICKET_COUNT;
    private final int CARD_COUNT;
    private final List<Route> ROUTES;
    private final int CAR_COUNT;
    private final int CLAIM_POINTS;

    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        this.TICKET_COUNT = ticketCount;
        this.CARD_COUNT = cardCount;
        this.ROUTES = List.copyOf(routes);
        this.CAR_COUNT = calculateCarCount(ROUTES);
        this.CLAIM_POINTS  = calculateClaimPoints(ROUTES);
    }

    public int ticketCount(){
        return TICKET_COUNT;
    }
    public int cardCount(){
        return CARD_COUNT;
    }
    public List<Route> routes(){
        return ROUTES;
    }
    public int carCount(){return CAR_COUNT;}
    public int claimPoints(){return CLAIM_POINTS;}

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
