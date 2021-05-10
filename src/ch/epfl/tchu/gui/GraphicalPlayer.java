package ch.epfl.tchu.gui;


import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
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
    private final ObjectProperty<ActionHandlers.ChooseTicketsHandler> chooseTicketsHP = new SimpleObjectProperty<>(null);
    private final ObjectProperty<ActionHandlers.ChooseCardsHandler> chooseCardsHP = new SimpleObjectProperty<>(null);

    public GraphicalPlayer(PlayerId thisPlayer, Map<PlayerId, String> playerNames) {

        this.thisPlayer = thisPlayer;
        this.playerNames = playerNames;
        this.observableGameState = new ObservableGameState(thisPlayer);

        this.primaryStage = new Stage();
        setSceneGraph();
    }

    private void setSceneGraph() {
        Node mapView = MapViewCreator.createMapView(observableGameState, claimRouteHP, this::chooseClaimCards);

        Node cardsView = DecksViewCreator.createCardsView(observableGameState, drawTicketsHP, drawCardsHP);

        Node handView = DecksViewCreator.createHandView(observableGameState);

        Node infoView = InfoViewCreator.createInfoView(thisPlayer, playerNames, observableGameState, messages);

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
            messages.remove(4);
        }
        messages.add(0, new Text('\n' + messageToAdd));
    }

    public void startTurn(ActionHandlers.DrawTicketsHandler drawTicketsHandler, ActionHandlers.DrawCardHandler drawCardHandler, ActionHandlers.ClaimRouteHandler claimRouteHandler) {
        assert isFxApplicationThread();

        if (observableGameState.canDrawTickets()) {
            drawTicketsHP.set(drawTicketsHandler);
        } else {
            drawTicketsHP.set(null);
        }

        if (observableGameState.canDrawCards()) {
            drawCardsHP.set(drawCardHandler);
        } else {
            drawCardsHP.set(null);
        }

        claimRouteHP.set(claimRouteHandler);
    }

    //Todo: check this method
    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        drawCardsHP.set(drawCardHandler);

        drawTicketsHP.set(null);
        claimRouteHP.set(null);
        chooseTicketsHP.set(null);
        chooseCardsHP.set(null);
    }

    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();
        chooseTicketsHP.set(chooseTicketsHandler);
        chooseCardsHP.set(null);
        ObservableList<Ticket> observableList = FXCollections.observableArrayList(ticketsToChooseFrom.toList());
        ListView<Ticket> listView = new ListView<>(observableList);

        createChoiceWindow(StringsFr.TICKETS_CHOICE, StringsFr.CHOOSE_TICKETS, listView);
    }

    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        chooseCardsHP.set(chooseCardsHandler);
        chooseTicketsHP.set(null);
        ListView<SortedBag<Card>> listView = makeSpecialListView(possibleClaimCards);

        createChoiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_CARDS, listView);
    }

    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();
        chooseCardsHP.set(chooseCardsHandler);
        chooseTicketsHP.set(null);
        ListView<SortedBag<Card>> listView = makeSpecialListView(possibleAdditionalCards);

        createChoiceWindow(StringsFr.CARDS_CHOICE, StringsFr.CHOOSE_ADDITIONAL_CARDS, listView);
    }

    private <E> void createChoiceWindow(String typeChoice, String chooseThis, ListView<E> listView) {
        Stage stage = new Stage(StageStyle.UTILITY);
        VBox verticalBox = new VBox();
        Scene scene = new Scene(verticalBox);
        scene.getStylesheets().add("chooser.css");

        stage.setTitle(typeChoice);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);

        switch (typeChoice) {
            case StringsFr.TICKETS_CHOICE:

                //need to hold down shift to select multiple -> normal?
                listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

                int ticketChooseSize = listView.getItems().size() - Constants.DISCARDABLE_TICKETS_COUNT;
                ObservableList<E> list = listView.getSelectionModel().getSelectedItems();

                Text ticketText = new Text(String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)));
                TextFlow ticketTextFlow = new TextFlow(ticketText);

                Button ticketButton = new Button(StringsFr.CHOOSE);

                ObservableValue<Boolean> selectCond = Bindings.lessThan(Bindings.size(list), ticketChooseSize);
                ticketButton.disableProperty().bind(selectCond);

                verticalBox.getChildren().addAll(ticketTextFlow, listView, ticketButton);
                //how to get type
                  /*  ticketButton.setOnAction(event -> {
                        stage.hide();
                        chooseTicketsHP.getValue().onChooseTickets(SortedBag.of(list));
                    });*/
                break;

            case StringsFr.CARDS_CHOICE:

                ObservableList<E> selectedItems = listView.getSelectionModel().getSelectedItems();
                E chosen = listView.getSelectionModel().getSelectedItem();

                Text cardText = new Text(chooseThis);
                TextFlow cardTextFlow = new TextFlow(cardText);

                Button cardButton = new Button(StringsFr.CHOOSE);
                verticalBox.getChildren().addAll(cardTextFlow, listView, cardButton);

                if (chooseThis.equals(StringsFr.CHOOSE_CARDS)) {
                    ObservableValue<Boolean> cardSelectCond = Bindings.lessThan(Bindings.size(selectedItems), 1);
                    cardButton.disableProperty().bind(cardSelectCond);
                }
                   /* //how to get type
                    cardButton.setOnAction(event -> {
                        stage.hide();
                        chooseCardsHP.getValue().onChooseCards(chosen);
                    });*/
                break;
        }
        stage.setOnCloseRequest(Event::consume);
        stage.show();
    }

    private ListView<SortedBag<Card>> makeSpecialListView(List<SortedBag<Card>> sortedBags) {
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(sortedBags));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        return listView;
    }
}
