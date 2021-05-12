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

    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        drawCardsHP.set(drawCardHandler);

        drawTicketsHP.set(null);
        claimRouteHP.set(null);
        chooseTicketsHP.set(null);
        chooseCardsHP.set(null);

    }
    private void setAllNull(){
        drawCardsHP.set(null);
        drawTicketsHP.set(null);
        claimRouteHP.set(null);
        chooseTicketsHP.set(null);
        chooseCardsHP.set(null);
    }


    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();


        setAllNull();

        VBox verticalBox = new VBox();
        Stage stage = setStage(verticalBox);

        ObservableList<Ticket> observableList = FXCollections.observableArrayList(ticketsToChooseFrom.toList());
        ListView<Ticket> listView = new ListView<>(observableList);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        int ticketChooseSize = ticketsToChooseFrom.size() - Constants.DISCARDABLE_TICKETS_COUNT;
        ObservableList<Ticket> list = listView.getSelectionModel().getSelectedItems();

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, ticketChooseSize, StringsFr.plural(ticketChooseSize)));
        TextFlow textFlow = new TextFlow(text);

        Button button = new Button(StringsFr.CHOOSE);

        ObservableValue<Boolean> selectCond = Bindings.lessThan(Bindings.size(list), ticketChooseSize);
        button.disableProperty().bind(selectCond);

        verticalBox.getChildren().addAll(textFlow, listView, button);

        button.setOnAction(event -> {
            stage.hide();
            chooseTicketsHandler.onChooseTickets(SortedBag.of(list));
        });

        stage.setOnCloseRequest(Event::consume);
        stage.show();


    }

    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        createCardWindow(StringsFr.CHOOSE_CARDS, possibleClaimCards, chooseCardsHandler);
    }

    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {

        createCardWindow(StringsFr.CHOOSE_ADDITIONAL_CARDS, possibleAdditionalCards, chooseCardsHandler);
    }

    private Stage setStage(VBox vBox) {
        Stage stage = new Stage(StageStyle.UTILITY);
        Scene scene = new Scene(vBox);
        //
        scene.getStylesheets().add("chooser.css");
        stage.setTitle(StringsFr.CARDS_CHOICE);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setOnCloseRequest(Event::consume);
        return stage;
    }

    private void createCardWindow(String chooseThis, List<SortedBag<Card>> cards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();


        setAllNull();

        VBox verticalBox = new VBox();
        Stage stage = setStage(verticalBox);

        //
        ListView<SortedBag<Card>> listView = makeSpecialListView(cards);
        ObservableList<SortedBag<Card>> selectedItems = listView.getSelectionModel().getSelectedItems();
        //
        Text text = new Text(chooseThis);
        TextFlow textFlow = new TextFlow(text);

        //
        Button button = new Button(StringsFr.CHOOSE);
        verticalBox.getChildren().addAll(textFlow, listView, button);
        //

        if (chooseThis.equals(StringsFr.CHOOSE_CARDS)) {
            ObservableValue<Boolean> selectCond = Bindings.lessThan(Bindings.size(selectedItems), 1);
            button.disableProperty().bind(selectCond);
        }

        button.setOnAction(event -> {
            stage.hide();

            if (selectedItems.isEmpty()) {
                chooseCardsHandler.onChooseCards(SortedBag.of());
            } else {
                chooseCardsHandler.onChooseCards(selectedItems.get(0));
            }

        });


        stage.show();

    }

    private ListView<SortedBag<Card>> makeSpecialListView(List<SortedBag<Card>> sortedBags) {
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(sortedBags));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        return listView;
    }

}

