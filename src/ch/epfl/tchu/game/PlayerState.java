package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the player's state at a point in the game
 *
 * @author Victor Canard-Duchêne (326913)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Constructor for the state of the player at a point in the game
     *
     * @param tickets : the tickets that the player possesses
     * @param cards   : the cards that the player possesses
     * @param routes  : the routes the player has claimed so far
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);

        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * The initial state of the player at the beginning of the game (With no tickets and no captured routes)
     *
     * @param initialCards : the initial cards
     * @return the initial PlayerState where the initial cards have been distributed
     * @throws IllegalArgumentException if the number of given cards is not exactly 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);

        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList());
    }

    /**
     * Getter for the tickets the player possesses
     *
     * @return the SortedBag of tickets
     */
    public SortedBag<Ticket> tickets() {
        return this.tickets;
    }

    /**
     * The state of the player after they have drawn tickets
     *
     * @param newTickets : the tickets they have drawn
     * @return a new PlayerState with more tickets in the players possession
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        SortedBag<Ticket> newBagOfTickets = this.tickets.union(newTickets);

        return new PlayerState(newBagOfTickets, this.cards, super.routes());
    }

    /**
     * Getter for the cards the player possesses
     *
     * @return a SortedBag of cards
     */
    public SortedBag<Card> cards() {
        return this.cards;
    }

    /**
     * The state of the player after they have drawn one additional card.
     * (The Builder was used here to create only one additional sorted bag,
     * whereas the union method would have created two, one for the single card and one for the final result with the union method).
     *
     * @param card : the card to give to the player
     * @return a new PlayerState with one additional card in their possession
     */
    public PlayerState withAddedCard(Card card) {
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder
                .add(this.cards)
                .add(card);

        return new PlayerState(this.tickets, builder.build(), super.routes());
    }


    /**
     * Determines if the player can claim a certain route
     *
     * @param route : the route to be claimed
     * @return true if the player is able to, false otherwise
     */
    public boolean canClaimRoute(Route route) {

        if (!hasEnoughWagonsLeft(route)) {
            //If there are not enough wagons left it is futile to test if the player has the necessary cards to claim the route
            return false;
        }

        //Tests to see if there is at least one combination of cards the player can use to capture the route
        return !possibleClaimCards(route).isEmpty();
    }

    /**
     * Returns true if the player has at least as many cars left as the length of the route he wants to claim
     *
     * @param route : route the player would like to claim
     * @return true if the player has enough cars left, false otherwise.
     */
    private boolean hasEnoughWagonsLeft(Route route) {
        return super.carCount() >= route.length();
    }

    /**
     * Determines the lists of cards this player can use to claim a given route
     *
     * @param route : the route to be claimed
     * @return the lists of possible cards to be used to claim the route
     * @throws IllegalArgumentException if the player does not have enough wagons to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(hasEnoughWagonsLeft(route));

        return route
                .possibleClaimCards()
                .stream()
                .filter(this.cards::contains)
                .collect(Collectors.toList());
    }

    /**
     * Determines the lists of groups of cards the player can use to claim a tunnel route
     *
     * @param additionalCardsCount : number of additional cards needed to claim the tunnel
     * @param initialCards         : the cards the player has put down for this tunnel
     * @return a list of all the groups of cards the player can use to claim a tunnel
     * @throws IllegalArgumentException if the number of additional cards is not between 1 and 3, if there are no initial cards or there is not a uniform type
     *                                  of initial cards (excluding locomotives) or if there are not exactly 3 drawn cards.
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards) {
        int minAddCards = 1;
        int maxAddCards = 3;
        int minWagons = 2;
        boolean correctAdditionalCardsCount = minAddCards <= additionalCardsCount && additionalCardsCount <= maxAddCards;
        boolean initialCardsNotNull = !(initialCards.isEmpty());
        boolean initialCardsNotTooManyTypes = initialCards.toSet().size() <= minWagons;

        Preconditions.checkArgument(correctAdditionalCardsCount
                && initialCardsNotNull
                && initialCardsNotTooManyTypes);


        SortedBag<Card> playerCardsWithoutInitialCards = this.cards.difference(initialCards);
        //Player cards without the initially played cards

        SortedBag<Card> cardSortedBag = SortedBag.of(
                playerCardsWithoutInitialCards.stream()
                        .filter(card -> card.equals(Card.LOCOMOTIVE) || card.equals(initialCards.get(0)))
                        //Only keeps locomotive cards and the ones of the same color as the initial card
                        .collect(Collectors.toList()));

        //If the player can play less cards than the additional cards count then he can't play at all
        if (cardSortedBag.size() < additionalCardsCount) {

            return Collections.emptyList();
        }

        //Makes subsets of the size of the additional cards count
        List<SortedBag<Card>> possibleAdditionalCards = new ArrayList<>(cardSortedBag.subsetsOfSize(additionalCardsCount));


        //Sorts the player's options in terms of counts of locomotives
        possibleAdditionalCards.sort(
                Comparator.comparingInt(sortedBag -> sortedBag.countOf(Card.LOCOMOTIVE)));


        return possibleAdditionalCards;
    }

    /**
     * The state of the player after they have claimed a route
     *
     * @param route      : the route to be claimed
     * @param claimCards : the cards the player has used to claim the route
     * @return a new PlayerState including the additional route they have claimed and without the cards they've used
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> routeList = new ArrayList<>(super.routes());
        routeList.add(route);

        SortedBag<Card> finalSortedBag = this.cards.difference(claimCards);

        return new PlayerState(this.tickets, finalSortedBag, routeList);
    }

    /**
     * Determines the number of points the player can earn (or lose) according to the tickets they possess
     *
     * @return the number of points gained (or lost)
     */
    public int ticketPoints() {
        //Finds the maximum id in all of the routes' stations' ids
        int maxStationId = super.routes()
                .stream()
                .map((route -> Math.max(route.station1().id(), route.station2().id())))
                .max((Integer::compareTo))
                .orElse(0);

        //Adds 1 to it
        maxStationId++;


        StationPartition.Builder builder = new StationPartition.Builder(maxStationId);

        //For a given route connects the two stations of that route
        super.routes()
                .forEach((route -> builder.connect(route.station1(), route.station2())));

        StationPartition stationPartition = builder.build();

        return tickets
                .stream()
                .mapToInt((ticket -> ticket.points(stationPartition)))
                .sum();
    }

    /**
     * Determines the total number of points the player gains at the end of the game
     *
     * @return the total number of points
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }
}