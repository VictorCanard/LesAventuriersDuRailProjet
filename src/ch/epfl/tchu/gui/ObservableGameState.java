package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public final class ObservableGameState {
    //Group 1
    private final ObjectProperty<Integer> ticketsPercentageLeft = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> cardsPercentageLeft = new SimpleObjectProperty<>(0);
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<Route>> allRoutes;

    //Group 2

    private final ObjectProperty<Integer> ticketCount = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> cardCount = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> carCount = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> constructionPoints = new SimpleObjectProperty<>(0);
    //Group 3
    private final List<ObjectProperty<Ticket>> allPlayerTickets = new SimpleObjectProperty<>(0);
    private final List<ObjectProperty<Integer>> numberOfEachCard = new SimpleObjectProperty<>(0);

    public ObservableGameState(PlayerId playerId) {
        this.faceUpCards = new observableArrayList();
        this.allRoutes =;
    }

    public Integer getTicketsPercentageLeft() {
        return ticketsPercentageLeft.get();
    }

    public void setTicketsPercentageLeft(Integer ticketsPercentageLeft) {
        this.ticketsPercentageLeft.set(ticketsPercentageLeft);
    }

    public ObjectProperty<Integer> ticketsPercentageLeftProperty() {
        return ticketsPercentageLeft;
    }

    public Integer getCardsPercentageLeft() {
        return cardsPercentageLeft.get();
    }

    public void setCardsPercentageLeft(Integer cardsPercentageLeft) {
        this.cardsPercentageLeft.set(cardsPercentageLeft);
    }

    public ObjectProperty<Integer> cardsPercentageLeftProperty() {
        return cardsPercentageLeft;
    }

    public List<ObjectProperty<Card>> getFaceUpCards() {
        return faceUpCards;
    }

    public List<ObjectProperty<Route>> getAllRoutes() {
        return allRoutes;
    }

    public Integer getTicketCount() {
        return ticketCount.get();
    }

    public void setTicketCount(Integer ticketCount) {
        this.ticketCount.set(ticketCount);
    }

    public ObjectProperty<Integer> ticketCountProperty() {
        return ticketCount;
    }

    public Integer getCardCount() {
        return cardCount.get();
    }

    public void setCardCount(Integer cardCount) {
        this.cardCount.set(cardCount);
    }

    public ObjectProperty<Integer> cardCountProperty() {
        return cardCount;
    }

    public Integer getCarCount() {
        return carCount.get();
    }

    public void setCarCount(Integer carCount) {
        this.carCount.set(carCount);
    }

    public ObjectProperty<Integer> carCountProperty() {
        return carCount;
    }

    public Integer getConstructionPoints() {
        return constructionPoints.get();
    }

    public void setConstructionPoints(Integer constructionPoints) {
        this.constructionPoints.set(constructionPoints);
    }

    public ObjectProperty<Integer> constructionPointsProperty() {
        return constructionPoints;
    }

    public List<ObjectProperty<Ticket>> getAllPlayerTickets() {
        return allPlayerTickets;
    }

    public List<ObjectProperty<Integer>> getNumberOfEachCard() {
        return numberOfEachCard;
    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {
    }


    public boolean canDrawTickets() {
        return false;
    }

    public boolean canDrawCards() {
        return false;
    }

    public List<SortedBag<Card>> possibleClaimCards() {
        return;
    }

}
