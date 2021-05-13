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
import java.util.function.Function;

import static javafx.application.Platform.isFxApplicationThread;

public final class GraphicalPlayer {

    private final PlayerId thisPlayer;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState observableGameState;
    private final ObservableList<Text> messages = FXCollections.observableArrayList();
    private final Stage primaryStage;

    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHP = new SimpleObjectProperty<>(null);

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

        mainPane.setPrefSize(500, 250); //Todo change this line
        Scene scene = new Scene(mainPane);


        primaryStage.setTitle("tCHu" + " \u2014 " + playerNames.get(thisPlayer));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setState(PublicGameState publicGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        observableGameState.setState(publicGameState, playerState);
    }

    public void receiveInfo(String messageToAdd) {
        assert isFxApplicationThread();

        if (messages.size() > 4) {
            messages.remove(0);
        }
        messages.add(new Text(messageToAdd));

    }

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

    private void disableAllTurnActions() {
        drawCardsHP.set(null);
        drawTicketsHP.set(null);
        claimRouteHP.set(null);
    }

    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        disableAllTurnActions();

        drawCardsHP.set((slot) -> {
            drawCardHandler.onDrawCards(slot);
            drawCardsHP.set(null);
        });

    }


    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        int ticketChooseSize = ticketsToChooseFrom.size() - Constants.DISCARDABLE_TICKETS_COUNT;

        ListView<Ticket> listView = new ListView<>(FXCollections.observableList(ticketsToChooseFrom.toList()));

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Window<Ticket> ticketWindow = new Window<>(primaryStage, StringsFr.TICKETS_CHOICE, String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)), listView);

        ticketWindow.setButtonAction(selectedItems -> {
            chooseTicketsHandler.onChooseTickets(SortedBag.of(selectedItems));
            return null;
        });

        ticketWindow.setButtonDP(ticketChooseSize);
        ticketWindow.show();
    }

    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        Window<SortedBag<Card>> cardsWindow = new Window<>(primaryStage, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, makeSpecialView(possibleClaimCards));

        cardsWindow.setButtonAction((items) -> {

            chooseCardsHandler.onChooseCards(items.get(0));

            return null;
        });

        cardsWindow.setButtonDP(1);
        cardsWindow.show();

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {

        assert isFxApplicationThread();

        Window<SortedBag<Card>> cardsWindow = new Window<>(primaryStage, StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, makeSpecialView(possibleAdditionalCards));

        cardsWindow.setButtonAction((items) -> {
            if (items.isEmpty()) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else {
                chooseCardsHandler.onChooseCards(items.get(0));
            }
            return null;
        });
        cardsWindow.show();
    }

    private ListView<SortedBag<Card>> makeSpecialView(List<SortedBag<Card>> cards) {
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(cards));

        listView.setCellFactory(view -> new TextFieldListCell<>(new CardBagStringConverter()));
        return listView;
    }


    private static class Window<E> {
        private final Button button;
        private final ObservableList<E> selectedItems;
        private final Stage primaryStage;
        private final Stage stage;


        private Window(Stage ownerStage, String title, String textToDisplay, ListView<E> listView) {

            VBox vBox = new VBox();

            this.primaryStage = ownerStage;
            this.stage = setStage(vBox, title);
            this.selectedItems = listView.getSelectionModel().getSelectedItems();
            this.button = new Button(StringsFr.CHOOSE);
            this.stage.setOnCloseRequest(Event::consume);


            Text text = new Text(textToDisplay);
            TextFlow textFlow = new TextFlow(text);
            vBox.getChildren().addAll(textFlow, listView, button);

        }


        private void show() {
            stage.show();
        }

        private void setButtonAction(Function<ObservableList<E>, Void> function) {
            button.setOnAction(event -> {
                stage.hide();
                function.apply(selectedItems);
            });
        }

        private void setButtonDP(int minValue) {

            button.disableProperty().bind(Bindings.lessThan(Bindings.size(selectedItems), minValue));
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
    }

}

