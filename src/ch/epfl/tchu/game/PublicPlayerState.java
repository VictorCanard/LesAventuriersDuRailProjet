package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

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
        Preconditions.checkArgument(ticketCount>= 0 && cardCount >= 0);

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

    /**
     * Calculates the number of cars. Subtracts from the initial car count, the sum of each captured route's length.
     * @param routes : routes captured by the player
     * @return the actual car count
     */
    private int calculateCarCount(List<Route> routes){
        int initialCarCount = Constants.INITIAL_CAR_COUNT;
        int usedCarCount = routes
                                .stream()
                                .mapToInt(Route::length)
                                .sum();

        return initialCarCount - usedCarCount;
    }

    /**
     * Calculates the number of claim points. Adds up the sum of each captured route's claim points.
     * @param routes : routes captured by the player
     * @return the actual claim points
     */
    private int calculateClaimPoints(List<Route> routes){
        int claimPoints = routes
                                .stream()
                                .mapToInt(Route::claimPoints)
                                .sum();

        return claimPoints;
    }

}
