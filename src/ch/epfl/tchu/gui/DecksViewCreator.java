package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


class DecksViewCreator {
    private DecksViewCreator() {
    }

    public static HBox createHandView(ObservableGameState gameState) {

        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");
        handView.setId("hand-pane");

        //tickets : issue with showing them. In ObservableGameState, method getAllPlayerTickets is updated, but doesnt carry here, even with ticketList

        ObservableList<String> tlString = FXCollections.observableArrayList();
        ObservableList<Ticket> listOfTickets = gameState.getAllPlayerTickets();
        listOfTickets.forEach(ticket -> tlString.add(ticket.toString()));

        ListView<String> tl = new ListView<>(tlString);
        tl.setId("tickets");

        handView.getChildren().add(tl);

        //cards
        for (Card c : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.getNumberOfEachCard().get(c); //map in ObsvGameState is always null. But if you use the count of an individual card it works

            StackPane cardPane = cardPane(c);

            if (count != null) {
                Text text = new Text(count.getValue().toString());
                text.getStyleClass().addAll("count");
                text.textProperty().bind(Bindings.convert(count));
                text.visibleProperty().bind(Bindings.greaterThan(count, 0));

                cardPane.getChildren().add(text);
                cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));
            }

            handView.getChildren().addAll(cardPane);

        }


        return handView;
    }


    public static VBox createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets, ObjectProperty<ActionHandlers.DrawCardHandler> drawCards) {
        VBox cardsView = new VBox();
        cardsView.getStylesheets().addAll("decks.css", "colors.css");
        cardsView.setId("card-pane");

        //tickets button
        Button ticketButton = new Button("Billets");
        ticketButton.getStyleClass().add("gauged");
        Group ticketGauge = new Group();
        ReadOnlyIntegerProperty ticketsPctProperty = gameState.ticketsPercentageLeftProperty();
        ticketButton.disableProperty().bind(drawTickets.isNull());

        ticketButton.setOnMouseClicked(event -> drawTickets.getValue().onDrawTickets());

        cardsView.getChildren().add(deckButtons(ticketButton, ticketsPctProperty, ticketGauge));

        //face up cards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane card = new StackPane();
            card.getStyleClass().addAll("card");

            //changes the graphics of the card according to what card is stored in the slot
            gameState.getFaceUpCards().get(slot).addListener((property, oldValue, newValue) -> {
                card.getStyleClass().add(newValue.name());
            });
            card.disableProperty().bind(drawCards.isNull());

            card.setOnMouseClicked(event -> drawCards.getValue().onDrawCards(slot));

            cardsView.getChildren().add(cardRectangles(card));
        }

        //cards button
        Button cardButton = new Button("Cartes");
        cardButton.getStyleClass().add("gauged");
        Group cardGauge = new Group();
        ReadOnlyIntegerProperty cardsPctProperty = gameState.cardsPercentageLeftProperty();

        cardButton.disableProperty().bind(drawCards.isNull());

        cardButton.setOnMouseClicked(event -> drawCards.getValue().onDrawCards(Constants.DECK_SLOT));

        cardsView.getChildren().add(deckButtons(cardButton, cardsPctProperty, cardGauge));
        return cardsView;
    }

    private static Button deckButtons(Button button, ReadOnlyIntegerProperty percentage, Group gauge) {
        Rectangle gaugeBackground = new Rectangle(50, 5);
        gaugeBackground.getStyleClass().add("background");

        Rectangle gaugeForeground = new Rectangle(50, 5);
        gaugeForeground.getStyleClass().add("foreground");
        gaugeForeground.widthProperty().bind(percentage.multiply(50).divide(100));

        gauge.getChildren().addAll(gaugeBackground, gaugeForeground);
        button.setGraphic(gauge);
        return button;
    }


    private static StackPane cardPane(Card c) {
        String cardName;
        if (c == Card.LOCOMOTIVE) {
            cardName = "NEUTRAL";
        } else {
            cardName = c.name();
        }
        StackPane card = new StackPane();
        card.getStyleClass().addAll(cardName, "card");

        return cardRectangles(card);
    }

    private static StackPane cardRectangles(StackPane card) {
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");
        Rectangle inside = new Rectangle(40, 70);
        inside.getStyleClass().addAll("filled", "inside");
        Rectangle train = new Rectangle(40, 70);
        train.getStyleClass().add("train-image");

        card.getChildren().addAll(outside, inside, train);
        return card;

    }
}

