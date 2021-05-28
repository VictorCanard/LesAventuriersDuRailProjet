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
 * Represents the graphical interface of a playerId of the game
 *
 * @author Victor Jean Canard-Duchene (326913)
 * @author Anne-Marie Rusu (296098)
 */
public final class GraphicalPlayer {
    private final PlayerId playerId;
    private final ObservableGameState observableGameState;
    private final ObservableList<Text> messages = FXCollections.observableArrayList();
    private final Stage primaryStage;

    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP = new SimpleObjectProperty<>(null);

    /**
     * Creates the graphical interface of the perspective of the given playerId
     *
     * @param playerId    : the playerId the graphical interface belongs to
     * @param playerNames : the names of the players in the game
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        Preconditions.checkArgument(playerNames.size() == PlayerId.COUNT);

        this.playerId = Objects.requireNonNull(playerId);
        this.observableGameState = new ObservableGameState(playerId);
        this.primaryStage = new Stage();
        //sets the scene
        setSceneGraph(Objects.requireNonNull(playerNames));
    }

    private void setSceneGraph(Map<PlayerId, String> playerNames) {
        final char dash = '—';
        final String title = "tCHu";

        Node mapView = MapViewCreator
                .createMapView(observableGameState, claimRouteHP, this::chooseClaimCards);

        Node cardsView = DecksViewCreator
                .createCardsView(observableGameState, drawTicketsHP, drawCardsHP);

        Node handView = DecksViewCreator
                .createHandView(observableGameState);

        Node infoView = InfoViewCreator
                .createInfoView(playerId, playerNames, observableGameState, messages);


        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

        Scene scene = new Scene(mainPane);

        primaryStage.setTitle(title + dash + playerNames.get(playerId));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Sets the state of the game on the javafx thread
     *
     * @param publicGameState : the public game state at the point in the game
     * @param playerState     : the playerId state at the point in the game
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();

        observableGameState.setState(publicGameState, playerState);
    }

    /**
     * Adds the given message to the information view of the graphical interface
     *
     * @param messageToAdd : the information to be displayed to the playerId
     */
    public void receiveInfo(String messageToAdd) {
        assert isFxApplicationThread();

        Preconditions.checkArgument(!messageToAdd.isEmpty());

        final int maxToAddMessage = 4;

        if (messages.size() > maxToAddMessage) {
            messages.remove(0);
        }
        messages.add(new Text(messageToAdd));
    }

    /**
     * Starts the playerId's turn on the javafx thread, where they can initially do 3 actions
     *
     * @param drawTicketsHandler : the action handler corresponding to the playerId drawing tickets
     * @param drawCardHandler    : the action handler corresponding to the playerId drawing cards
     * @param claimRouteHandler  : the action handler corresponding to the playerId claiming a route
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
     * Disables all the actions for the playerId
     */
    private void disableAllTurnActions() {
        drawCardsHP.set(null);
        drawTicketsHP.set(null);
        claimRouteHP.set(null);
    }

    /**
     * Allows the playerId to draw cards, through the 5 face up cards or the cards button
     *
     * @param drawCardHandler : the action handler corresponding to the playerId drawing cards
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();
        //disables all the other actions when the player decides to draw a card
        disableAllTurnActions();

        drawCardsHP.set((slot) -> {
            drawCardHandler.onDrawCards(slot);
            drawCardsHP.set(null);
        });
    }

    /**
     * Allows the playerId to choose tickets through the ticket button by displaying a pop up window
     *
     * @param ticketsToChooseFrom  : the tickets the playerId must choose at least 1 of
     * @param chooseTicketsHandler : the action handler corresponding to the playerId choosing ticket(s)
     */
    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!ticketsToChooseFrom.isEmpty());

        final int ticketChooseSize = ticketsToChooseFrom.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        createChoiceWindow(StringsFr.TICKETS_CHOICE,
                String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)),
                ticketsToChooseFrom.toList(),
                SelectionMode.MULTIPLE,
                Object::toString,
                ticketChooseSize,
                selectedItems -> chooseTicketsHandler.onChooseTickets(SortedBag.of(selectedItems)));
    }

    /**
     * Allows the playerId to choose the initial claim cards they want to use when attempting to claim a route
     *
     * @param possibleClaimCards : the list of groups of claim cards the playerId can use for the route
     * @param chooseCardsHandler : the action handler corresponding to the playerId choosing cards
     */
    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!possibleClaimCards.isEmpty());

        final int minToSelect = 1;

        createChoiceWindow(StringsFr.CARDS_CHOICE,
                StringsFr.CHOOSE_CARDS,
                List.copyOf(possibleClaimCards),
                SelectionMode.SINGLE,
                Info::cardNames,
                minToSelect,
                items -> chooseCardsHandler.onChooseCards(items.get(0)));
    }

    /**
     * Allows the playerId to choose additional cards when it is necessary when attempting to claim a tunnel route.
     * The playerId also has an option to abandon the route by not selecting cards and clicking the choose button
     *
     * @param possibleAdditionalCards : the possible additional cards the playerId can play to claim the route
     * @param chooseCardsHandler      : the action handler corresponding to the playerId choosing cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(!possibleAdditionalCards.isEmpty());

        final int minToSelect = 0;

        createChoiceWindow(StringsFr.CARDS_CHOICE,
                StringsFr.CHOOSE_ADDITIONAL_CARDS,
                List.copyOf(possibleAdditionalCards),
                SelectionMode.SINGLE,
                Info::cardNames,
                minToSelect,
                items -> {
                    if (items.isEmpty()) {
                        chooseCardsHandler.onChooseCards(SortedBag.of());
                    } else {
                        chooseCardsHandler.onChooseCards(items.get(0));
                    }
                });
    }

    /**
     * Creates the pop up window for which the player can select options of the given type, specific to the action they have taken in the game
     *
     * @param title : the title of the window
     * @param displayText : the text to be displayed in the window
     * @param list : the list of type E to which the player must select options from
     * @param selectionMode : the selection mode of the list (how many options the player can select)
     * @param objectToStringFunction : functional interface which will perform the given operation (in this case to transform the given objects into Strings)
     * @param minToSelect : minimum number of options the player must select
     * @param consumer : functional interface which will perform the given operation via the accept method
     * @param <E> : the type of object the player will pick options of
     */
    private <E> void createChoiceWindow(String title, String displayText, List<E> list, SelectionMode selectionMode, Function<E, String> objectToStringFunction, int minToSelect, Consumer<ObservableList<E>> consumer) {
        final String chooser = "chooser.css";

        VBox vbox = new VBox();

        Scene scene = new Scene(vbox);
        scene.getStylesheets().add(chooser);

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.setTitle(title);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setOnCloseRequest(Event::consume);

        Text text = new Text(displayText);
        TextFlow textFlow = new TextFlow(text);

        ListView<E> listView = new ListView<>(FXCollections.observableList(list));
        listView.getSelectionModel().setSelectionMode(selectionMode);

        //transforms the objects to Strings
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

        ObservableList<E> selectedItems = listView.getSelectionModel().getSelectedItems();

        //Configures the choose button
        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems), minToSelect));

        button.setOnAction(event -> {
            stage.hide();
            consumer.accept(selectedItems);
        });

        vbox.getChildren().addAll(textFlow, listView, button);
        stage.show();
    }
}

