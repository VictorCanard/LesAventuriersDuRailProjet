package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private <E> ObservableList<E> createWindowChoice(String title, ListView<E> listView) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        //
        VBox verticalBox = new VBox();
        //
        Scene scene = new Scene(verticalBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        //
        TextFlow textFlow = new TextFlow();
        //
        Text text = new Text();
        textFlow.getChildren().add(text);
        //
        Button button = new Button(StringsFr.CHOOSE);
        //

        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        //
        verticalBox.getChildren().addAll(textFlow, listView, button);
        //
        stage.setTitle(title);
        stage.show();

        // Also add the disable property
        ObservableList<E> chosen;
        while((chosen = listView.getSelectionModel().getSelectedItems()).size() != 5){

        }
        return chosen;
    }

    private ListView<SortedBag<Card>> makeSpecialListView(List<SortedBag<Card>> sortedBags) {
        ListView<SortedBag<Card>> listView = new ListView<>(FXCollections.observableList(sortedBags));
        listView.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));
        return listView;
    }

    private void setSceneGraph() {
        Node mapView = MapViewCreator
                .createMapView(observableGameState, claimRouteHP, this::chooseClaimCards);

        Node cardsView = DecksViewCreator
                .createCardsView(observableGameState, drawTicketsHP, drawCardsHP);

        Node handView = DecksViewCreator
                .createHandView(observableGameState);

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
        messages.add(new Text('\n' + messageToAdd));

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

    public void chooseTickets(SortedBag<Ticket> ticketsToChooseFrom, ActionHandlers.ChooseTicketsHandler chooseTicketsHandler) {
        assert isFxApplicationThread();

        ObservableList<Ticket> observableList = FXCollections.observableArrayList(ticketsToChooseFrom.toList());
        ListView<Ticket> listView = new ListView<>(observableList);

        createWindowChoice(String.format(StringsFr.CHOOSE_TICKETS, ticketsToChooseFrom, StringsFr.plural(ticketsToChooseFrom.size())), listView);

        //chooseTicketsHandler.onChooseTickets();
    }

    public void drawCard(ActionHandlers.DrawCardHandler drawCardHandler) {
        assert isFxApplicationThread();

        drawCardsHP.set(drawCardHandler);

        drawTicketsHP.set(null);
        claimRouteHP.set(null);
        chooseTicketsHP.set(null);
        chooseCardsHP.set(null);


        //drawCardHandler.onDrawCards();
    }

    public void chooseClaimCards(List<SortedBag<Card>> possibleClaimCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        createWindowChoice(StringsFr.CHOOSE_CARDS, makeSpecialListView(possibleClaimCards));
        //chooseCardsHandler.onChooseCards();

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> possibleAdditionalCards, ActionHandlers.ChooseCardsHandler chooseCardsHandler) {
        assert isFxApplicationThread();

        createWindowChoice(StringsFr.CHOOSE_ADDITIONAL_CARDS, makeSpecialListView(possibleAdditionalCards));
        //chooseCardsHandler.onChooseCards();
    }
}
