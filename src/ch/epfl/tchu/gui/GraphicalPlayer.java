package ch.epfl.tchu.gui;


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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
     * @param thisPlayer : the player the graphical interface belongs to
     * @param playerNames : the names of the players in the game
     */
    public GraphicalPlayer(PlayerId thisPlayer, Map<PlayerId, String> playerNames) {

        this.thisPlayer = thisPlayer;
        this.playerNames = playerNames;
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
     * @param publicGameState : the public game state at the point in the game
     * @param playerState : the player state at the point in the game
     */
    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    /**
     * Adds the given message to the information view of the graphical interface
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
     * @param drawTicketsHandler : the action handler corresponding to the player drawing tickets
     * @param drawCardHandler : the action handler corresponding to the player drawing cards
     * @param claimRouteHandler : the action handler corresponding to the player claiming a route
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
     * @param drawCardHandler : the action handler corresponding to the player drawing cards
     */
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        disableAllTurnActions();

        drawCardsHP.set((slot) -> {
            drawCardHandler.onDrawCards(slot);
            drawCardsHP.set(null);
        });
    }

    /**
     * Allows the player to choose tickets through the ticket button by displaying a pop up window
     * @param ticketsToChooseFrom : the tickets the player must choose at least 1 of
     * @param chooseTicketsHandler : the action handler corresponding to the player choosing ticket(s)
     */
    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        int ticketChooseSize = ticketsToChooseFrom.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(ticketsToChooseFrom.toList()));

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        /*Window<Ticket> ticketWindow = new Window<>(primaryStage, StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)), listView, ticketChooseSize);

        ticketWindow.setButtonAction(selectedItems -> chooseTicketsHandler.onChooseTickets(SortedBag.of(selectedItems)));

        ticketWindow.show();*/

        createChoiceWindow(StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)), listView, ticketChooseSize,
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

       /* Window<SortedBag<Card>> cardsWindow = new Window<>(primaryStage, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, makeSpecialView(possibleClaimCards), 1);

        cardsWindow.setButtonAction(items -> chooseCardsHandler.onChooseCards(items.get(0)));

        cardsWindow.show();*/

        createChoiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, makeSpecialView(possibleClaimCards), 1,
                items -> chooseCardsHandler.onChooseCards(items.get(0)));
    }

    /**
     * Allows the player to choose additional cards when it is necessary when attempting to claim a tunnel route.
     * The player also has an option to abandon the route by not selecting cards and clicking the choose button
     * @param possibleAdditionalCards : the possible additional cards the player can play to claim the route
     * @param chooseCardsHandler : the action handler corresponding to the player choosing cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {

        assert isFxApplicationThread();

      /*  Window<SortedBag<Card>> cardsWindow = new Window<>(primaryStage, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, makeSpecialView(possibleAdditionalCards), 0);

        cardsWindow.setButtonAction((items) -> {
            if (items.isEmpty()) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else {
                chooseCardsHandler.onChooseCards(items.get(0));
            }
        });
        cardsWindow.show();*/

        createChoiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, makeSpecialView(possibleAdditionalCards), 0,
                (items) -> {
                    if (items.isEmpty()) {
                        chooseCardsHandler.onChooseCards(SortedBag.of());
                    } else {
                        chooseCardsHandler.onChooseCards(items.get(0));
                    }
                });
    }
//this method is quite redundant...                               can just put this in the two places its used,
    private <E> ListView<E> makeNormalView(List<E> list) {
        return new ListView<>(FXCollections.observableList(list));
    }

    private ListView<SortedBag<Card>> makeSpecialView(List<SortedBag<Card>> cards) {
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(cards));

        listView.setCellFactory(view -> new TextFieldListCell<>(new CardBagStringConverter()));
        return listView;
    }

    /**
     * Represents a window for when the player needs to make a choice involving choosing cards or choosing tickets
     * @param <E> : the type (sorted bags of cards or tickets) that the player needs to choose from
     */
    /*private static class Window<E> {
        private final Button button;
        private final ObservableList<E> selectedItems;
        private final Stage primaryStage;
        private final Stage stage;

        *//**
         * Creates the infrastructure of the window
         * @param ownerStage : the stage the window belongs to
         * @param title : the title of the window
         * @param textToDisplay : the text displayed in the window, corresponding to the action the player needs to take
         * @param listView : the listView of the items the player will need to choose from
         * @param minValue : the minimum number of items that can be selected before the player can accept their choices
         *//*
        private Window(Stage ownerStage, String title, String textToDisplay, ListView<E> listView, int minValue) {

            VBox vBox = new VBox();

            this.primaryStage = ownerStage;
            this.stage = setStage(vBox, title);
            this.selectedItems = listView.getSelectionModel().getSelectedItems();
            this.button = new Button(StringsFr.CHOOSE);
           // this.stage.setOnCloseRequest(Event::consume); //already in setStage

            button.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems), minValue));

            Text text = new Text(textToDisplay);
            TextFlow textFlow = new TextFlow(text);
            vBox.getChildren().addAll(textFlow, listView, button);
        }

        private Stage setStage(VBox vBox, String title) {
            Stage stage = new Stage(StageStyle.UTILITY);
            Scene scene = new Scene(vBox);
            //
            scene.getStylesheets().add("chooser.css");
            stage.setTitle(title);
            //
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            stage.setOnCloseRequest(Event::consume);
            return stage;
        }

        private void setButtonAction(Consumer<ObservableList<E>> consumer) {
            button.setOnAction(event -> {
                stage.hide();
                consumer.accept(selectedItems);
            });
        }

        *//**
         * Shows the window.
         * This is its own method as it must be called at the end after the window is prepared,
         * and for clarity in the methods that create this window
         *//*
        private void show() {
            stage.show();
        }
    }*/

    private <E> void createChoiceWindow(String title, String displayText, ListView<E> listView, int minSelected, Consumer<ObservableList<E>> consumer){
        VBox vbox = new VBox();

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

        ObservableList<E> selectedItems = listView.getSelectionModel().getSelectedItems();

        Button button = new Button(StringsFr.CHOOSE);
        button.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems), minSelected));

        button.setOnAction(event -> {
            stage.hide();
            consumer.accept(selectedItems);
        });

        vbox.getChildren().addAll(textFlow, listView, button);
        stage.show();
    }
}

