package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the observable state of a game of tCHu
 *
 * @author Victor Canard-Duchêne (326913)
 * @author Anne-Marie Rusu (296098)
 */
public final class ObservableGameState {
    private final PlayerId playerId;

    //Group 1 : PublicGameState
    private final IntegerProperty ticketsPercentageLeft = new SimpleIntegerProperty(0);
    private final IntegerProperty cardsPercentageLeft = new SimpleIntegerProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final Map<Route, ObjectProperty<PlayerId>> allRoutesContainedByWhom = new HashMap<>();

    //Group 2 : Both Player's Public Player States
    private final Map<PlayerId, IntegerProperty> ticketCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> cardCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> carCount = new HashMap<>();
    private final Map<PlayerId, IntegerProperty> constructionPoints = new HashMap<>();

    //Group 3 : Complete Player State of this Player
    private final ObservableList<Ticket> allPlayerTickets = FXCollections.observableArrayList();
    private final Map<Card, IntegerProperty> numberOfEachCard = new HashMap<>();
    private final Map<Route, BooleanProperty> canPlayerClaimRoute = new HashMap<>();
    
    //Other variables for accessing information contained in the ObservableGameState
    private final Set<List<Station>> allPairsOfStationsClaimed = new HashSet<>();
    private PublicGameState publicGameState;
    private PlayerState playerState;

    /**
     * Creates an instance of the observable game state in its initial state
     *
     * @param playerId : the id of the player the observable game state belongs to
     */
    public ObservableGameState(PlayerId playerId) {
        createFaceUpCards();

        createRoutes();
        createEmptyMap(ticketCount);
        createEmptyMap(cardCount);
        createEmptyMap(carCount);
        createEmptyMap(constructionPoints);

        setNumberOfEachCard();
        createRoutesClaimedOrNot();

        this.playerId = playerId;
    }

    /**
     * Creates the 5 properties of the 5 face up cards to be used in the game
     */
    private void createFaceUpCards() {
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(new SimpleObjectProperty<>(null));
        }
    }

    private void createRoutes() {

        ChMap.routes().forEach(route -> allRoutesContainedByWhom.put(route, new SimpleObjectProperty<>(null)));
    }

    private void createEmptyMap(Map<PlayerId, IntegerProperty> currentMap) {

        Arrays.stream(PlayerId.values()).forEach(playerId -> currentMap.put(playerId, new SimpleIntegerProperty(0)));
    }

    private void setNumberOfEachCard() {
        Arrays.stream(Card.values()).forEach(card -> numberOfEachCard.put(card, new SimpleIntegerProperty(0)));
    }
    
    private void createRoutesClaimedOrNot() {

        ChMap.routes().forEach(route -> canPlayerClaimRoute.put(route, new SimpleBooleanProperty(false)));
    }


    /**
     * Sets the state of the observable game state. Uses the auxiliary methods down below to set each of the properties
     * contained in this ObservableGameState to the values contained in the publicGameState and playerState passed as arguments.
     *
     * @param publicGameState : the public game state at this point in the game
     * @param playerState : the player state of the player the observable game state belongs to
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        Preconditions.checkArgument(publicGameState != null);
        Preconditions.checkArgument(playerState != null);

        final int percentageConvert = 100;

        ticketsPercentageLeft.set((publicGameState.ticketsCount() * percentageConvert / ChMap.tickets().size()));
        cardsPercentageLeft.set((publicGameState.cardState().deckSize() * percentageConvert / Constants.TOTAL_CARDS_COUNT));
        setFaceUpCards(publicGameState.cardState().faceUpCards());
        setRoutesPlayerId(publicGameState);

        setEachPlayerCountAttributesCount(publicGameState);

        setPlayerTickets(playerState);
        setPlayerCards(playerState);
        setPlayerCanClaimRouteOrNot(publicGameState, playerState);

        this.publicGameState = publicGameState;
        this.playerState = playerState;
    }
    //Group 1
    private void setFaceUpCards(List<Card> newFaceUpCards) {
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newFaceUpCards.get(slot);
            faceUpCards.get(slot).set(newCard);
        }
    }

    private void setRoutesPlayerId(PublicGameState newPublicGameState) {

        ChMap.routes().forEach(route -> PlayerId.ALL.forEach(playerId -> {
            if (newPublicGameState.playerState(playerId).routes().contains(route)) {
                allRoutesContainedByWhom.get(route).set(playerId);
                allPairsOfStationsClaimed.add(route.stations());
            }
        }));
    }
    //Group 2

    private void setEachPlayerCountAttributesCount(PublicGameState publicGameState) {

        PlayerId.ALL.forEach(playerId -> {
            ticketCount.get(playerId).set(publicGameState.playerState(playerId).ticketCount());
            cardCount.get(playerId).set(publicGameState.playerState(playerId).cardCount());
            carCount.get(playerId).set(publicGameState.playerState(playerId).carCount());
            constructionPoints.get(playerId).set(publicGameState.playerState(playerId).claimPoints());

        });
    }

    //Group 3
    private void setPlayerTickets(PlayerState playerState) {
        allPlayerTickets.addAll(playerState
                .tickets()
                .stream()
                .filter(ticket -> !allPlayerTickets.contains(ticket))
                .collect(Collectors.toList()));
    }

    private void setPlayerCards(PlayerState playerState) {

        Card.ALL.forEach(card -> numberOfEachCard.get(card).set(playerState.cards().countOf(card)));
    }

    private void setPlayerCanClaimRouteOrNot(PublicGameState publicGameState, PlayerState playerState) {
        canPlayerClaimRoute.forEach((route, booleanObjectProperty) -> booleanObjectProperty.set(
                publicGameState.currentPlayerId().equals(playerId)
                        && playerState.canClaimRoute(route)
                        && !publicGameState.claimedRoutes().contains(route)
                        && !allPairsOfStationsClaimed.contains(route.stations())));
    }

    /**
     * Getter for the property corresponding to the percentage of tickets left in the ticket draw pile
     *
     * @return the (read-only) integer property corresponding to the percentage of tickets left
     */
    public ReadOnlyIntegerProperty ticketsPercentageLeftProperty() {
        return ticketsPercentageLeft;
    }

    /**
     * Getter for the property corresponding to the percentage of cards left in the card draw pile
     *
     * @return the (read-only) integer property corresponding to the percentage of card left
     */
    public ReadOnlyIntegerProperty cardsPercentageLeftProperty() {
        return cardsPercentageLeft;
    }

    /**
     * Getter for the property of the given face up card
     *
     * @param slot : the index of the card to retrieve
     * @return a list of the face up card properties
     */
    public ReadOnlyObjectProperty<Card> getFaceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * Getter for the playerId property corresponding to the player which has claimed the given route
     *
     * @param route : the route claimed by a player
     * @return the playerId property of the player who has claimed the given route
     */
    public ReadOnlyObjectProperty<PlayerId> getPlayerIdClaimingRoute(Route route) {
        return allRoutesContainedByWhom.get(route);
    }

    /**
     * Getter for the property of the number of tickets the given player has
     *
     * @return the ticket count property of the given player
     */
    public ReadOnlyIntegerProperty getTicketCount(PlayerId playerId) {
        return ticketCount.get(playerId);
    }

    /**
     * Getter for the property of the number of cards the given player has
     *
     * @return the card count property of the given player
     */
    public ReadOnlyIntegerProperty getCardCount(PlayerId playerId) {
        return cardCount.get(playerId);
    }

    /**
     * Getter for the property of the number of cars the given player has
     *
     * @return the car count property of the given player
     */
    public ReadOnlyIntegerProperty getCarCount(PlayerId playerId) {
        return carCount.get(playerId);
    }

    /**
     * Getter for the property of the construction points of the given player
     *
     * @return the construction point property of the given player
     */
    public ReadOnlyIntegerProperty getConstructionPoints(PlayerId playerId) {
        return constructionPoints.get(playerId);
    }

    /**
     * Getter for all the players tickets in the game
     *
     * @return an observable list of all the players tickets
     */
    public ObservableList<Ticket> getAllPlayerTickets() {
        return FXCollections.unmodifiableObservableList(allPlayerTickets);
    }

    /**
     * Getter for the property of the number of the given card
     *
     * @return the integer property of the number of the given card
     */
    public ReadOnlyIntegerProperty getNumberOfCard(Card card) {
        return numberOfEachCard.get(card);
    }

    /**
     * Getter for the boolean property corresponding to if the route can be claimed by the player
     *
     * @return a map containing the routes and their boolean properties
     */
    public Map<Route, ReadOnlyBooleanProperty> getCanPlayerClaimRoute() {

        return new HashMap<>(canPlayerClaimRoute);
    }

    /**
     * Determines if the given route is claimable or not
     *
     * @param route : the route to be claimed
     * @return a true property if the route can be claimed, a false property otherwise
     */
    public ReadOnlyBooleanProperty claimable(Route route) {
        return getCanPlayerClaimRoute().get(route);
    }

    //Methods that call the respective method (with the same name) on the publicGameState or playerState contained in this ObservableGameState
    /**
     * Determines if the player can draw tickets
     *
     * @return true if the player is able to draw tickets based on the current state of the game, false otherwise
     */
    public boolean canDrawTickets() {
        return publicGameState.canDrawTickets();
    }

    /**
     * Determines if the player can draw cards
     *
     * @return true if the player is able to draw cards based on the current state of the game, false otherwise
     */
    public boolean canDrawCards() {
        return publicGameState.canDrawCards();
    }

    /**
     * Determines all the possible combinations of cards a player can use to claim a route
     *
     * @param route : the route to be claimed
     * @return a list of all the possible card combinations for this route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState.possibleClaimCards(route);
    }


}
