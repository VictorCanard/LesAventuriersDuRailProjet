package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

public final class ObservableGameState {
    private final PlayerId playerId;

    //Group 1 : PublicGameState
    private final IntegerProperty ticketsPercentageLeft = new SimpleIntegerProperty(0);
    private final ObjectProperty<Integer> cardsPercentageLeft = new SimpleObjectProperty<>(0);
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();

    //Group 2 : Both Player's Public Player States
    private final Map<Route, ObjectProperty<PlayerId>> allRoutesContainedByWhom = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> ticketCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> cardCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> carCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> constructionPoints = new HashMap<>();

    //Group 3 : Complete Player State of this Player
    private final ObservableList<Ticket> allPlayerTickets = FXCollections.observableArrayList();
    private final Map<Card, IntegerProperty> numberOfEachCard = new HashMap<>();

    private final Map<Route, BooleanProperty> routeCanBeClaimedByThisPlayerOrNot = new HashMap<>();
    //
    private final Set<List<Station>> allPairsOfStationsClaimed = new HashSet<>();
    private PublicGameState publicGameState;
    private PlayerState playerState;

    public ObservableGameState(PlayerId playerId) {
        createFaceUpCards();
        createRoutes();
        //
        createEmptyPlayerIdIntegerHashMap(ticketCount);
        createEmptyPlayerIdIntegerHashMap(cardCount);
        createEmptyPlayerIdIntegerHashMap(carCount);
        createEmptyPlayerIdIntegerHashMap(constructionPoints);
        //
        setNumberOfEachCard();
        createRoutesClaimedOrNot();
        //
        this.playerId = playerId;
    }


    public ReadOnlyIntegerProperty ticketsPercentageLeftProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsPercentageLeft);
    }


    public ReadOnlyIntegerProperty cardsPercentageLeftProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsPercentageLeft);
    }

    public List<ObjectProperty<Card>> getFaceUpCards() {
        return Collections.unmodifiableList(faceUpCards);
    }

    private void setFaceUpCards(List<Card> newFaceUpCards) {
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.get(slot).set(newFaceUpCards.get(slot));
        }
    }

    public Map<Route, ObjectProperty<PlayerId>> getAllRoutesContainedByWhom() {
        return Collections.unmodifiableMap(allRoutesContainedByWhom);
    }


    //Todo: Getters à modifier/ finir
//    public Map<PlayerId, ObjectProperty<Integer>> getTicketCount() {
//        return Collections.unmodifiableMap(ticketCount);
//    }
//
//    public Map<PlayerId, ObjectProperty<Integer>> getCardCount() {
//        return cardCount;
//    }
//
//    public Map<PlayerId, ObjectProperty<Integer>> getCarCount() {
//        return carCount;
//    }
//
//    public Map<PlayerId, ObjectProperty<Integer>> getConstructionPoints() {
//        return constructionPoints;
//    }

    public ObservableList<Ticket> getAllPlayerTickets() {
        return FXCollections.unmodifiableObservableList(allPlayerTickets);
    }

    public Map<Card, ReadOnlyIntegerProperty> getNumberOfEachCard() {
        Map<Card, ReadOnlyIntegerProperty> newMap = new HashMap<>();

        numberOfEachCard.forEach(newMap::put);
        return newMap;
    }

//    public Map<Route, ObjectProperty<Boolean>> getRouteCanBeClaimedByThisPlayerOrNot() {
//        return routeCanBeClaimedByThisPlayerOrNot;
//    }
    //End of getters

    private void setNumberOfEachCard() {
        Arrays.stream(Card.values()).forEach(card -> numberOfEachCard.put(card, new SimpleIntegerProperty(0)));
    }

    private void createRoutesClaimedOrNot() {

        ChMap.routes().forEach(route -> routeCanBeClaimedByThisPlayerOrNot.put(route, new SimpleBooleanProperty(false)));
    }

    private void createEmptyPlayerIdIntegerHashMap(Map<PlayerId, IntegerProperty> currentMap) {

        Arrays.stream(PlayerId.values()).forEach(playerId -> currentMap.put(playerId, new SimpleIntegerProperty(0)));
    }

    private void createRoutes() {

        ChMap.routes().forEach(route -> allRoutesContainedByWhom.put(route, new SimpleObjectProperty<>(null)));

    }

    private void setRoutesPlayerId(PublicGameState newPublicGameState) {

        ChMap.routes().forEach(route -> {
            PlayerId whoHasCurrentRoute;

            if (newPublicGameState.playerState(newPublicGameState.currentPlayerId()).routes().contains(route)) {
                whoHasCurrentRoute = newPublicGameState.currentPlayerId();
                allPairsOfStationsClaimed.add(route.stations());
            } else if (newPublicGameState.playerState(newPublicGameState.currentPlayerId().next()).routes().contains(route)) {
                whoHasCurrentRoute = newPublicGameState.currentPlayerId().next();
                allPairsOfStationsClaimed.add(route.stations());
            } else {
                whoHasCurrentRoute = null;
            }
            allRoutesContainedByWhom.get(route).set(whoHasCurrentRoute);

        });

    }

    private void createFaceUpCards() {
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(new SimpleObjectProperty<>(null));
        }
    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        //
        ticketsPercentageLeft.set((publicGameState.ticketsCount() * 100 / ChMap.tickets().size()));
        cardsPercentageLeft.set((publicGameState.cardState().deckSize() * 100 / Constants.ALL_CARDS.size()));
        setFaceUpCards(publicGameState.cardState().faceUpCards());
        setRoutesPlayerId(publicGameState);
        //
        setEachPlayerCountAttributesCount(publicGameState);
        //
        setPlayerTickets(playerState);
        setPlayerCards(playerState);
        setPlayerCanClaimRouteOrNot(publicGameState, playerState);

        //
        this.publicGameState = publicGameState;
        this.playerState = playerState;
    }

    private void setPlayerTickets(PlayerState playerState) {

        allPlayerTickets.addAll(playerState
                .tickets()
                .stream()
                .filter(ticket -> !allPlayerTickets.contains(ticket))
                .collect(Collectors.toList()));
    }

    private void setPlayerCards(PlayerState playerState) {


        playerState.cards().forEach(card -> numberOfEachCard.merge(card, new SimpleIntegerProperty(1), (integerObjectProperty, one) -> {
            int sum = integerObjectProperty.get() + one.get();
            integerObjectProperty.set(sum);
            return integerObjectProperty;
        }));


    }

    private void setPlayerCanClaimRouteOrNot(PublicGameState publicGameState, PlayerState playerState) {
        routeCanBeClaimedByThisPlayerOrNot.forEach((route, booleanObjectProperty) -> {


            if (publicGameState.currentPlayerId().equals(playerId)
                    && playerState.canClaimRoute(route)
                    && !allPairsOfStationsClaimed.contains(route.stations())) {

                booleanObjectProperty.set(true);

            }
        });
    }


    private void setEachPlayerCountAttributesCount(PublicGameState publicGameState) {
        PlayerId.ALL.forEach(playerId -> {
            ticketCount.get(playerId).set(publicGameState.playerState(playerId).ticketCount());
            cardCount.get(playerId).set(publicGameState.playerState(playerId).cardCount());
            carCount.get(playerId).set(publicGameState.playerState(playerId).carCount());
            constructionPoints.get(playerId).set(publicGameState.playerState(playerId).claimPoints());

        });
    }


    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }

    public ReadOnlyBooleanProperty claimable(Route route) {

        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(routeCanBeClaimedByThisPlayerOrNot.get(route));
    }
}
