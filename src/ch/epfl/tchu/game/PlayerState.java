package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;
    private final List<Route> routes;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(), routes);

        this.tickets = SortedBag.of(tickets);
        this.cards = SortedBag.of(cards);
        this.routes = List.copyOf(routes);

    }
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size()==4);
        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList()); //Does emptyList work here ?
    }
    public SortedBag<Ticket> tickets(){
        return this.tickets;
    }
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();
        builder.add(this.tickets)
                .add(newTickets);

        return new PlayerState(builder.build(),this.cards, this.routes);
    }
    public SortedBag<Card> cards(){
        return this.cards;
    }
    public PlayerState withAddedCard(Card card){
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(this.cards)
                .add(card);
        return new PlayerState(this.tickets, builder.build(), this.routes);
    }
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        SortedBag<Card> newBagOfCards = this.cards.union(additionalCards);

        return new PlayerState(this.tickets, newBagOfCards, this.routes);
    }
    public boolean canClaimRoute(Route route){
        boolean enoughWagonsLeft = hasEnoughWagonsLeft(route);
        boolean playerHasNeccessaryCards = false;

        for (SortedBag<Card> sortedBag: route.possibleClaimCards()
             ) {
            playerHasNeccessaryCards = (this.cards.contains(sortedBag)) ? true : playerHasNeccessaryCards;
        }

        return enoughWagonsLeft && playerHasNeccessaryCards;
    }
    private boolean hasEnoughWagonsLeft(Route route){

        return super.carCount() >= route.length();
    }
    public List<SortedBag<Card>> possibleClaimCards(Route route){
        Preconditions.checkArgument(hasEnoughWagonsLeft(route));

        return route.possibleClaimCards();


    }
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

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routeList = new ArrayList<>(this.routes);
        routeList.add(route);

        SortedBag<Card> finalSortedBag = this.cards.difference(claimCards);

        return new PlayerState(this.tickets, finalSortedBag,  routeList );
    }
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
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }
}
