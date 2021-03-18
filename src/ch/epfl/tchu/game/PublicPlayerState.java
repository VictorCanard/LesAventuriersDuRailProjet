package ch.epfl.tchu.game;

import java.util.List;

/**
 * Describes the players situation at a point in the game, visible to everyone in the game
 * @author Victor Canard-Duchêne (326913)
 */
public class PublicPlayerState {
    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * Constructor for the players public state at a point in the game
     * @param ticketCount : the number of tickets the player possesses
     * @param cardCount : the number of cards the player possesses
     * @param routes : the list of routes the player has claimed so far
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes){
        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);
        this.carCount = calculateCarCount(this.routes);
        this.claimPoints = calculateClaimPoints(this.routes);
    }

    /**
     * Getter for the number of tickets the player possesses
     * @return the number of tickets
     */
    public int ticketCount(){
        return ticketCount;
    }

    /**
     * Getter for the number of cards the player possesses
     * @return the number of cards
     */
    public int cardCount(){
        return cardCount;
    }

    /**
     * Getter for the list of routes the player has claimed
     * @return the list of routes
     */
    public List<Route> routes(){
        return routes;
    }

    /**
     * Getter for the number of cars the player has
     * @return number of cars
     */
    public int carCount(){return carCount;}

    /**
     * Getter for the number of construction points the player obtained
     * @return the number of construction points
     */
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
