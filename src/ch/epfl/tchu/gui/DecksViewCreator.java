package ch.epfl.tchu.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

class DecksViewCreator {
    private DecksViewCreator() {
    }

    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets, ObjectProperty<ActionHandlers.DrawCardHandler> drawCard) {
        return null;
    }

    public static Node createHandView(ObservableGameState gameState) {
        return null;
    }
}
