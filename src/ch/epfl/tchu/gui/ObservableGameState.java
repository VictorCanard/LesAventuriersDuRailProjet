package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

import java.util.*;

public final class ObservableGameState {
    private final PlayerId playerId;

    //Group 1 : PublicGameState
    private final ObjectProperty<Integer> ticketsPercentageLeft = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> cardsPercentageLeft = new SimpleObjectProperty<>(0);
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final Map<ObjectProperty<Route>, ObjectProperty<PlayerId>> allRoutesContainedByWhom = new HashMap<>();

    //Group 2 : Both Player's Public Player States

    private final Map<ObjectProperty<PlayerId>, ObjectProperty<Integer>> ticketCount = createEmptyPlayerIdIntegerHashMap();
    private final Map<ObjectProperty<PlayerId>, ObjectProperty<Integer>> cardCount = createEmptyPlayerIdIntegerHashMap();
    private final Map<ObjectProperty<PlayerId>, ObjectProperty<Integer>> carCount = createEmptyPlayerIdIntegerHashMap();
    private final Map<ObjectProperty<PlayerId>, ObjectProperty<Integer>> constructionPoints = createEmptyPlayerIdIntegerHashMap();

    //Group 3 : Complete Player State of this Player
    private final List<ObjectProperty<Ticket>> allPlayerTickets = FXCollections.observableArrayList();
    private final Map<ObjectProperty<Card>, ObjectProperty<Integer>> numberOfEachCard = setNumberOfEachCard();
    private final Map<ObjectProperty<Route>, ObjectProperty<Boolean>> routeCanBeClaimedByThisPlayerOrNot = setRoutesClaimedOrNot();

    public ObservableGameState(PlayerId playerId) {
        createFaceUpCards();
        createRoutes();

        this.playerId = playerId;
    }

    private static Map<ObjectProperty<PlayerId>, ObjectProperty<Integer>> createEmptyPlayerIdIntegerHashMap() {
        Map<ObjectProperty<PlayerId>, ObjectProperty<Integer>> mapToReturn = new HashMap<>();

        for (PlayerId playerId : PlayerId.values()
        ) {
            mapToReturn.put(new SimpleObjectProperty<>(playerId), new SimpleObjectProperty<>(0));
        }
        return mapToReturn;
    }

    private static Map<ObjectProperty<Card>, ObjectProperty<Integer>> setNumberOfEachCard() {
        Map<ObjectProperty<Card>, ObjectProperty<Integer>> mapToReturn = new TreeMap<>(Comparator.comparingInt(op -> op.get().ordinal()));

        for (Card card : Card.values()
        ) {
            mapToReturn.put(new SimpleObjectProperty<>(card), new SimpleObjectProperty<>(0));
        }
        return mapToReturn;
    }

    private static Map<ObjectProperty<Route>, ObjectProperty<Boolean>> setRoutesClaimedOrNot() {
        Map<ObjectProperty<Route>, ObjectProperty<Boolean>> mapToReturn = new HashMap<>();

        for (Route route : ChMap.routes()
        ) {
            mapToReturn.put(new SimpleObjectProperty<>(route), new SimpleObjectProperty<>(false));
        }
        return mapToReturn;
    }

    private void createRoutes() {

        for (int i = 0; i < ChMap.routes().size(); i++) {
            allRoutesContainedByWhom.put(new SimpleObjectProperty<>(null), new SimpleObjectProperty<>(null));
        }
    }

    private void setRoutesPlayerId(PublicGameState newPublicGameState) {

        for (Route route : ChMap.routes()
        ) {
            PlayerId whoHasCurrentRoute;

            if (newPublicGameState.playerState(newPublicGameState.currentPlayerId()).routes().contains(route)) {
                whoHasCurrentRoute = newPublicGameState.currentPlayerId();
            } else if (newPublicGameState.playerState(newPublicGameState.currentPlayerId().next()).routes().contains(route)) {
                whoHasCurrentRoute = newPublicGameState.currentPlayerId().next();
            } else {
                whoHasCurrentRoute = null;
            }
            allRoutesContainedByWhom.get(route).set(whoHasCurrentRoute);
        }

    }

    private void createFaceUpCards() {
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            faceUpCards.add(new SimpleObjectProperty<>(null));
        }
    }

    private void setFaceUpCards(List<Card> newFaceUpCards) {
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            faceUpCards.get(slot).set(newFaceUpCards.get(slot));
        }
    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        //
        ticketsPercentageLeft.set(publicGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentageLeft.set(publicGameState.cardState().deckSize() / Constants.ALL_CARDS.size());
        setFaceUpCards(publicGameState.cardState().faceUpCards());
        setRoutesPlayerId(publicGameState);
        //

        //
    }


    public boolean canDrawTickets() {
        return false;
    }

    public boolean canDrawCards() {
        return false;
    }

    public List<SortedBag<Card>> possibleClaimCards() {
        return null;
    }

    public ReadOnlyBooleanProperty claimable(Route route) {
        return new SimpleBooleanProperty(false);
    }
}
