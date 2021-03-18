package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Describes the player's situation at a point in the game
 * @author Victor Canard-Duchêne (326913)
 */
public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    /**
     * Constructor for the state of the player at a point in the game
     * @param tickets : the tickets that the player possesses
     * @param cards : the cards that the player possesses
     * @param routes : the routes the player has claimed so far
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(), routes);

        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);
        this.routes = List.copyOf(routes);
    }

    /**
     * The initial state of the player at the beginning of the game
     * @param initialCards : the initial cards
     * @return the initial PlayerState where the initial cards have been distributed
     * @throws IllegalArgumentException if the number of given cards is not exactly 4
     */
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size()==4);
        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList()); //Does emptyList work here ?
    }

    /**
     * Getter for the tickets the player possesses
     * @return the SortedBag of tickets
     */
    public SortedBag<Ticket> tickets(){
        return this.tickets;
    }

    /**
     * The state of the player after they have drawn tickets
     * @param newTickets : the tickets they have drawn
     * @return a new PlayerState with more tickets in the players possession
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();
        builder.add(this.tickets)
                .add(newTickets);
        return new PlayerState(builder.build(),this.cards, this.routes);
    }

    /**
     * Getter for the cards the player possesses
     * @return a SortedBag of cards
     */
    public SortedBag<Card> cards(){
        return this.cards;
    }

    /**
     * The state of the player after they have drawn one additional card
     * @param card : the card to give to the player
     * @return a new PlayerState with one additional card in their possession
     */
    public PlayerState withAddedCard(Card card){
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(this.cards)
                .add(card);
        return new PlayerState(this.tickets, builder.build(), this.routes);
    }

    /**
     * The state of the player after they have drawn a number of additional card
     * @param additionalCards : the group of cards to be given to the player
     * @return a new PlayerState with the new cards added to the player's possession
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        SortedBag<Card> newBagOfCards = this.cards.union(additionalCards);
        return new PlayerState(this.tickets, newBagOfCards, this.routes);
    }

    /**
     * Determines if the player can claim a certain route
     * @param route : the route to be claimed
     * @return true if the player is able to, false otherwise
     */
    public boolean canClaimRoute(Route route){
        boolean enoughWagonsLeft = hasEnoughWagonsLeft(route);
        boolean playerHasNecessaryCards = false;

        for (SortedBag<Card> sortedBag: route.possibleClaimCards()
             ) {
            playerHasNecessaryCards = (this.cards.contains(sortedBag)) ? true : playerHasNecessaryCards;
        }
        return enoughWagonsLeft && playerHasNecessaryCards;
    }

    private boolean hasEnoughWagonsLeft(Route route){
        return super.carCount() >= route.length();
    }

    /**
     * Determines the lists of cards the player can use to claim a given route
     * @param route : the route to be claimed
     * @return the lists of possible cards to be used to claim the route
     * @throws IllegalArgumentException if the player does not have enough wagons to claim the route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(hasEnoughWagonsLeft(route));
        return route.possibleClaimCards();
    }

    /**
     * Determines the lists of groups of cards the player can use to claim a tunnel route
     * @param additionalCardsCount : number of additional cards needed to claim the tunnel
     * @param initialCards : the cards the player has put down for this tunnel
     * @param drawnCards : the three top cards from the draw pile
     * @return a list of all the groups of cards the player can use to claim a tunnel
     * @throws IllegalArgumentException if the number of additional cards is not between 1 and 3, if there are no initial cards or there is not a uniform type
     * of initial cards (excluding locomotives) or if there are not exactly 3 drawn cards.
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards){
        boolean correctAdditionalCardsCount = 1<= additionalCardsCount && additionalCardsCount <=3;
        boolean initialCardsNotNull = !(initialCards.isEmpty());
        boolean initialCardsNotTooManyTypes = initialCards.toSet().size() <= 2;
        boolean rightNumberOfDrawnCards = drawnCards.size() == 3;

        Preconditions.checkArgument(correctAdditionalCardsCount && initialCardsNotNull && initialCardsNotTooManyTypes && rightNumberOfDrawnCards);

        SortedBag<Card> playerCardsWithoutInitialCards = this.cards.difference(initialCards);
        SortedBag.Builder<Card> usableCards = new SortedBag.Builder<>();

        Card initialCard = initialCards.get(0);

        int numberOfLocomotiveCardsToAdd = playerCardsWithoutInitialCards.countOf(Card.LOCOMOTIVE);
        int numberOfSameColorCardsToAdd = playerCardsWithoutInitialCards.countOf(initialCard);

        usableCards.add(numberOfLocomotiveCardsToAdd, Card.LOCOMOTIVE)
                    .add(numberOfSameColorCardsToAdd, initialCard);

        SortedBag<Card> cardSortedBag = usableCards.build();
        Set<SortedBag<Card>> sortedBagSet =cardSortedBag.subsetsOfSize(additionalCardsCount);

        List<SortedBag<Card>> options = new ArrayList<>(sortedBagSet);

        options.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));
        return options;
    }

    /**
     *The state of the player after they have claimed a route
     * @param route : the route to be claimed
     * @param claimCards : the cards the player has used to claim the route
     * @return a new PlayerState including the additional route they have claimed
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routeList = new ArrayList<>(this.routes);
        routeList.add(route);

        SortedBag<Card> finalSortedBag = this.cards.difference(claimCards);
        return new PlayerState(this.tickets, finalSortedBag,  routeList );
    }

    /**
     * Determines the number of points the player can earn (or lose) according to the tickets they possess
     * @return the number of points gained (or lost)
     */
    public int ticketPoints(){
        int ticketPoints = 0;
        int maxStationId = 0;

        for (Route route:
             this.routes) {
            int maxIdOfStations = Math.max(route.station1().id(), route.station2().id());
            maxStationId = Math.max(maxIdOfStations, maxStationId);
        }
        maxStationId ++;
        StationPartition.Builder builder = new StationPartition.Builder(maxStationId);

        for (Route route: routes
             ) {
            builder.connect(route.station1(), route.station2());
        }

        StationPartition stationPartition = builder.build();

        for (Ticket ticket: tickets
             ) {
            ticketPoints += ticket.points(stationPartition);
        }

        return ticketPoints;
    }

    /**
     * Determines the total number of points the player gains at the end of the game
     * @return the total number of points
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }
}
