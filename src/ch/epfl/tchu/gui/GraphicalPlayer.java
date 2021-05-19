package ch.epfl.tchu.gui;


import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static javafx.application.Platform.isFxApplicationThread;

/**
 * Represents the graphical interface of a player of the game
 *
 * @author Victor Jean Canard-Duchene (326913)
 * @author Anne-Marie Rusu (296098)
 */
public final class GraphicalPlayer {

    private final PlayerId thisPlayer;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState observableGameState;
    private final ObservableList<Text> messages = FXCollections.observableArrayList();
    private final Stage primaryStage;

    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP = new SimpleObjectProperty<>(null);

    /**
     * Creates the graphical interface of the perspective of the given player
     *
     * @param thisPlayer  : the player the graphical interface belongs to
     * @param playerNames : the names of the players in the game
     * @throws NullPointerException if the playerId or the player names map is null
     * @throws IllegalArgumentException if there aren't the right number of pairs in the player names map
     */
    public GraphicalPlayer(PlayerId thisPlayer, Map<PlayerId, String> playerNames) {
        Preconditions.checkArgument(playerNames.size() == Menu.number_of_players);

        this.thisPlayer = Objects.requireNonNull(thisPlayer);
        this.playerNames = Map.copyOf(Objects.requireNonNull(playerNames));
        this.observableGameState = new ObservableGameState(thisPlayer);
        this.primaryStage = new Stage();

        setSceneGraph();
    }

    private void setSceneGraph() {
        Node mapView = MapViewCreator
                .createMapView(observableGameState, claimRouteHP, this::chooseClaimCards);

        Node cardsView = DecksViewCreator
                .createCardsView(observableGameState, drawTicketsHP, drawCardsHP);

        Node handView = DecksViewCreator
                .createHandView(observableGameState);

        Node infoView = InfoViewCreator
                .createInfoView(thisPlayer, playerNames, observableGameState, messages);

        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

        Scene scene = new Scene(mainPane);

        primaryStage.setTitle("tCHu" + " \u2014 " + playerNames.get(thisPlayer));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets the state of the game on the javafx thread
     *
     * @param publicGameState : the public game state at the point in the game
     * @param playerState     : the player state at the point in the game
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    /**
     * Adds the given message to the information view of the graphical interface
     *
     * @param messageToAdd : the information to be displayed to the player
     */
    public void receiveInfo(String messageToAdd) {
        assert isFxApplicationThread();

        if (messages.size() > 4) {
            messages.remove(0);
        }
        messages.add(new Text(messageToAdd));
    }

    /**
     * Starts the player's turn on the javafx thread, where they can initially do 3 actions
     *
     * @param drawTicketsHandler : the action handler corresponding to the player drawing tickets
     * @param drawCardHandler    : the action handler corresponding to the player drawing cards
     * @param claimRouteHandler  : the action handler corresponding to the player claiming a route
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler, ActionHandlers.DrawCardHandler drawCardHandler, ActionHandlers.ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

        if (observableGameState.canDrawTickets()) {
            drawTicketsHP.set(() -> {
                drawTicketsHandler.onDrawTickets();
                disableAllTurnActions();
            });
        }

        if (observableGameState.canDrawCards()) {
            drawCardsHP.set((slot) -> {
                drawCardHandler.onDrawCards(slot);
                disableAllTurnActions();

            });
        }

        claimRouteHP.set((route, cards) -> {
            claimRouteHandler.onClaimRoute(route, cards);
            disableAllTurnActions();
        });
    }

    /**
     * Disables all the actions for the player
     */
    private void disableAllTurnActions() {
        drawCardsHP.set(null);
        drawTicketsHP.set(null);
        claimRouteHP.set(null);
    }

    /**
     * Allows the player to draw cards, through the 5 face up cards or the cards button
     *
     * @param drawCardHandler : the action handler corresponding to the player drawing cards
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(drawCardHandler != null);

        disableAllTurnActions();

        drawCardsHP.set((slot) -> {
            drawCardHandler.onDrawCards(slot);
            drawCardsHP.set(null);
        });
    }

    /**
     * Allows the player to choose tickets through the ticket button by displaying a pop up window
     *
     * @param ticketsToChooseFrom  : the tickets the player must choose at least 1 of
     * @param chooseTicketsHandler : the action handler corresponding to the player choosing ticket(s)
     */
    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!ticketsToChooseFrom.isEmpty());
        Preconditions.checkArgument(chooseTicketsHandler != null);

        int ticketChooseSize = ticketsToChooseFrom.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        createChoiceWindow(StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)),
                ticketsToChooseFrom.toList(),
                SelectionMode.MULTIPLE,
                Object::toString,
                ticketChooseSize,
                selectedItems -> chooseTicketsHandler.onChooseTickets(SortedBag.of(selectedItems)));
    }

    /**
     * Allows the player to choose the initial claim cards they want to use when attempting to claim a route
     *
     * @param possibleClaimCards : the list of groups of claim cards the player can use for the route
     * @param chooseCardsHandler : the action handler corresponding to the player choosing cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!possibleClaimCards.isEmpty());
        Preconditions.checkArgument(chooseCardsHandler != null);

        createChoiceWindow(StringsFr.CARDS_CHOICE,
                StringsFr.CHOOSE_CARDS,
                List.copyOf(possibleClaimCards),
                SelectionMode.SINGLE,
                Info::cardNames,
                1,
                items -> chooseCardsHandler.onChooseCards(items.get(0)));
    }

    /**
     * Allows the player to choose additional cards when it is necessary when attempting to claim a tunnel route.
     * The player also has an option to abandon the route by not selecting cards and clicking the choose button
     *
     * @param possibleAdditionalCards : the possible additional cards the player can play to claim the route
     * @param chooseCardsHandler      : the action handler corresponding to the player choosing cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!possibleAdditionalCards.isEmpty());
        Preconditions.checkArgument(chooseCardsHandler != null);

        createChoiceWindow(StringsFr.CARDS_CHOICE,
                StringsFr.CHOOSE_ADDITIONAL_CARDS,
                List.copyOf(possibleAdditionalCards),
                SelectionMode.SINGLE,
                Info::cardNames,
                0,
                items -> {
                    if (items.isEmpty()) {
                        chooseCardsHandler.onChooseCards(SortedBag.of());
                    } else {
                        chooseCardsHandler.onChooseCards(items.get(0));
                    }
                });
    }


    private <E> void createChoiceWindow(String title, String displayText, List<E> list, SelectionMode selectionMode, Function<E, String> objectToStringFunction, int minToSelect, Consumer<ObservableList<E>> consumer) {
        VBox vbox = new VBox();
        //
        Scene scene = new Scene(vbox);
        scene.getStylesheets().add("chooser.css");
        //
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setOnCloseRequest(Event::consume);
        //
        Text text = new Text(displayText);
        TextFlow textFlow = new TextFlow(text);
        //
        ListView<E> listView = new ListView<>(FXCollections.observableList(list));
        listView.getSelectionModel().setSelectionMode(selectionMode);
        listView.setCellFactory(v -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(E object) {
                return objectToStringFunction.apply(object);
            }

            @Override
            public E fromString(String string) {
                throw new UnsupportedOperationException();
            }
        }));

        //
        ObservableList<E> selectedItems = listView.getSelectionModel().getSelectedItems();

        //
        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems), minToSelect));

        button.setOnAction(event -> {
            stage.hide();
            consumer.accept(selectedItems);
        });

        //
        vbox.getChildren().addAll(textFlow, listView, button);
        //
        stage.show();
    }
}

