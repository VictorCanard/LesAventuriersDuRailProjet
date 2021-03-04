package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The route between two stations on the map
 * @author Anne-Marie Rusu (296098)
 */

public final class Route {
    private final String ID;
    private final Station STATION1;
    private final Station STATION2;
    private final int LENGTH;
    private final Level LEVEL;
    private final Color COLOR;

    /** Constructs a route based on specific given parameters as seen below
     * @param id : Route's unique identifier
     * @param station1 : Route's first station
     * @param station2 : Route's second station
     * @param length : Length (from 1 to 6)
     * @param level : Overground route or tunnel route
     * @param color : Unique color or null for neutral
     * @throws NullPointerException if the values given in argument are null
     * @throws IllegalArgumentException if stations are the same or if the length isn't realistic according to the game's rules.
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {

        Preconditions.checkArgument(!(station1.equals(station2)|| length<Constants.MIN_ROUTE_LENGTH || length > Constants.MAX_ROUTE_LENGTH));

        this.ID = Objects.requireNonNull(id);
        this.STATION1 = Objects.requireNonNull(station1);
        this.STATION2 = Objects.requireNonNull(station2);
        this.LEVEL = Objects.requireNonNull(level);
        this.COLOR = color;
        this.LENGTH = length;
    }

    /**
     * Enum type defining the two different levels of routes:
     * overground and underground
     */
    public enum Level{
        OVERGROUND,
        UNDERGROUND
    }

    /**
     * Getter for the route's id
     * @return id
     */
    public String id() {
        return ID;
    }

    /**
     * Getter for the first station
     * @return Station 1
     */
    public Station station1() {
        return STATION1;
    }

    /**
     * Getter for the second station
     * @return Station 2
     */
    public Station station2() {
        return STATION2;
    }

    /** Getter for the route's length
     * @return length of route from 1 to 6
     */
    public int length() {
        return LENGTH;
    }

    /**
     * Getter for the route's level
     * @return overground or underground
     */
    public Level level() {
        return LEVEL;
    }

    /**
     * Getter for the route's color
     * @return a specific color or null for neutral
     */
    public Color color() {
        return COLOR;
    }

    /**
     * Getter for a list of specified stations of a route
     * @return List with both stations
     */
    public List<Station> stations(){
        return List.of(STATION1, STATION2);
    }

    /**- Gets the opposite station to the one given as an argument
     * Throws an exception if the station doesn't belong to the route
     * @param station : one of the possible stations of the given route
     * @return oppositeStation
     */
    public Station stationOpposite(Station station){

        Preconditions.checkArgument(stations().contains(station));

        if(station.equals(STATION1)){
            return STATION2;
        }
        else{
            return STATION1;
        }
    }

    /** Determines all the possible combinations of cards which can be played to capture this route using in particular the level, color and length of the route
     *  If the route is underground (tunnel):
     *      If the route has a specific color:
     *          Creates a list of all possible combinations of that color card and locomotive cards to be played
     *      Else: (route is neutral)
     *          Creates a list of all possible combinations of each color card and locomotive cards
     *  Else if the route is neutral (and thus overground):
     *          Creates a list of groups of cards of all colors (no locomotives)
     *  Else (overground and specific color):
     *          Creates a list of one element that is the group of cards of that specific color.
     * @return : List of Sorted Bags of cards, each sorted bag is an accepted combination to claim this route
     */
    public List<SortedBag<Card>> possibleClaimCards(){
        List<SortedBag<Card>> possibleCards = new ArrayList<>();

        if(LEVEL == Level.UNDERGROUND) {
            if (COLOR != null) {  // Underground route with a specific non-neutral color
                for (int i = 0; i <= LENGTH; i++) {
                    possibleCards.add(SortedBag.of(LENGTH - i, Card.of(COLOR), i, Card.LOCOMOTIVE));
                }
            } else {  //Underground route with neutral color

                for (int i = 0; i < LENGTH; i++) {
                    for (Card c : Card.CARS) {
                        possibleCards.add(SortedBag.of(LENGTH - i, c, i, Card.LOCOMOTIVE));
                    }
                }
                possibleCards.add(SortedBag.of(LENGTH, Card.LOCOMOTIVE));
            }
        }else if(COLOR == null){ // Overground route with neutral color
            for(Card c : Card.CARS) {
                possibleCards.add(SortedBag.of(LENGTH, c));
            }
        }else {  //Overground route with a specific non-neutral color
            possibleCards.add(SortedBag.of(LENGTH, Card.of(COLOR)));
        }
        return possibleCards;
    }

    /** Determines the number of additional cards the player must play to capture the tunnel route
     * If the player is only playing locomotive cards then returns the number of locomotive cards in the drawnCards,
     * else returns the number of locomotive cards plus the number of cards of the player's color in the drawnCards.
     * @param claimCards : cards from the player's deck that were played to try capturing the route
     * @param drawnCards : exactly 3 cards from the deck which will constrain the player to play 0, 1, 2 or 3 more cards, if he still wants to capture the route
     * @throws IllegalArgumentException if this method is used on a overground route or if there is not the right amount of drawn cards.
     * @return an int from 0 to 3 (both extremities included)
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){

        int aCCC;
        Preconditions.checkArgument(LEVEL == Level.UNDERGROUND && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        if(claimCards.equals(SortedBag.of(LENGTH, Card.LOCOMOTIVE))){
            aCCC = drawnCards.countOf(Card.LOCOMOTIVE);
        }else {
            aCCC = drawnCards.countOf(claimCards.get(0)) + drawnCards.countOf(Card.LOCOMOTIVE);
        }
        return aCCC;
    }

    /** Getter for the route's points.
     * @return number of points gained from taking this route
     * (depends on the route's length)
     */
    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(LENGTH);
    }


}
