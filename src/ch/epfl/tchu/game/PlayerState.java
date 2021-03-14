package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> TICKETS;
    private final SortedBag<Card> CARDS;
    private final List<Route> ROUTES;

    PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(), routes);

        this.TICKETS = SortedBag.of(tickets);
        this.CARDS = SortedBag.of(cards);
        this.ROUTES = List.copyOf(routes);

    }
    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size()==4);
        return new PlayerState(SortedBag.of(), initialCards, Collections.emptyList()); //Does emptyList work here ?
    }
    public SortedBag<Ticket> tickets(){
        return this.TICKETS;
    }
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets){
        SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();
        builder.add(this.TICKETS)
                .add(newTickets);

        return new PlayerState(builder.build(),this.CARDS, this.ROUTES);
    }
    public SortedBag<Card> cards(){
        return this.CARDS;
    }
    public PlayerState withAddedCard(Card card){
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(this.CARDS)
                .add(card);
        return new PlayerState(this.TICKETS, builder.build(), this.ROUTES);
    }
    public PlayerState withAddedCards(SortedBag<Card> additionalCards){
        SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
        builder.add(this.CARDS)
                .add(additionalCards);
        return new PlayerState(this.TICKETS, builder.build(), this.ROUTES);
    }
    public boolean canClaimRoute(Route route){
        boolean enoughWagonsLeft = hasEnoughWagonsLeft(route);
        boolean playerHasNeccessaryCards = false;

        for (SortedBag<Card> sortedBag: route.possibleClaimCards()
             ) {
            playerHasNeccessaryCards = (this.CARDS.contains(sortedBag)) ? true : playerHasNeccessaryCards;
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

        SortedBag<Card> playerCardsWithoutInitialCards = this.CARDS.difference(initialCards);
        SortedBag.Builder<Card> usableCards = new SortedBag.Builder<>();

        Card initialCard = initialCards.get(0);

        int numberOfLocomotiveCardsToAdd = playerCardsWithoutInitialCards.countOf(Card.LOCOMOTIVE);
        int numberOfSameColorCardsToAdd = playerCardsWithoutInitialCards.countOf(initialCard);

        usableCards.add(numberOfLocomotiveCardsToAdd, Card.LOCOMOTIVE)
                    .add(numberOfSameColorCardsToAdd, initialCard);

        SortedBag<Card> cardSortedBag = usableCards.build();
        Set<SortedBag<Card>> sortedBagSet =cardSortedBag.subsetsOfSize(2);

        List<SortedBag<Card>> options = new ArrayList<>(sortedBagSet);

        options.sort(
                Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return options;

    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        List<Route> routeList = new ArrayList<>(this.ROUTES);
        routeList.add(route);

        SortedBag<Card> finalSortedBag = this.CARDS.difference(claimCards);

        return new PlayerState(this.TICKETS, finalSortedBag,  routeList );
    }
    public int ticketPoints(){
        int ticketPoints = 0;
        int maxStationId = 0;

        for (Route route:
             this.ROUTES) {
            int maxIdOfStations = Math.max(route.station1().id(), route.station2().id());
            maxStationId = Math.max(maxIdOfStations, maxStationId);
        }
        maxStationId ++;
        StationPartition.Builder builder = new StationPartition.Builder(maxStationId);
        StationPartition stationPartition = builder.build();
        for (Ticket ticket:TICKETS
             ) {
          //  ticket.points(); //Partition

        }
        return 0;
    }
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }
}
