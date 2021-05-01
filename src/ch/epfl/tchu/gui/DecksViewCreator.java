package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
//package private - no access mods

class DecksViewCreator {
    private DecksViewCreator() {}

    public static Node createHandView(ObservableGameState gameState) {
        HBox handPane = new HBox();

        ObservableList<Ticket> ticketList = FXCollections.observableArrayList(ChMap.tickets());
        javafx.scene.control.ListView<Ticket> tickets = new javafx.scene.control.ListView<>(ticketList);

        StackPane pane = new StackPane();
        pane.getChildren().addAll(new Rectangle(100,100), new Label("Go!"));
        handPane.getChildren().add(tickets);
        handPane.getChildren().add(pane);



        return handPane;
    }

    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets, ObjectProperty<ActionHandlers.DrawCardHandler> drawCard) {
        VBox cardsView = new VBox();


        return cardsView;
    }
}