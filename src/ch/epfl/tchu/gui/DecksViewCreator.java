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

import java.util.Locale;


class DecksViewCreator {
    private DecksViewCreator() {
    }

    /**
     * Creates the Hand View of tickets
     * @param gameState
     * @return
     */
    public static HBox createHandView(ObservableGameState gameState) {

        HBox handView = new HBox();
        handView.getStylesheets().addAll("decks.css", "colors.css");

        //tickets : issue with showing them. In ObservableGameState, list returned in getAllPlayerTickets
        // is null even though setTickets does set the tickets
        //
        ObservableList<String> ticketList = FXCollections.observableArrayList();
        ObservableList<Ticket> listOfTickets = gameState.getAllPlayerTickets();
        listOfTickets.forEach(ticket -> ticketList.add(ticket.toString()));

        ListView<String> listView = new ListView<>(ticketList);
        listView.setId("tickets");

        handView.getChildren().add(listView);

        //
        HBox handPane = new HBox();
        handPane.setId("hand-pane");

        handView.getChildren().add(handPane);



        //cards
        for (Card card : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.getNumberOfEachCard().get(card);

            StackPane cardPane = cardPane(card);

            Text text = new Text(count.getValue().toString());
            text.getStyleClass().add("count");

            text.textProperty().bind(Bindings.convert(count));
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));

            cardPane.getChildren().add(text);
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

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
        //

        ReadOnlyIntegerProperty ticketsPctProperty = gameState.ticketsPercentageLeftProperty();
        cardsView.getChildren().add(deckButtons(ticketButton, ticketsPctProperty));

        //
        ticketButton.disableProperty().bind(drawTickets.isNull());

        //
        ticketButton.setOnMouseClicked(event -> drawTickets.get().onDrawTickets());


        //face up cards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("card");

            //changes the graphics of the card according to what card is stored in the slot
            gameState.getFaceUpCards().get(slot).addListener((property, oldValue, newValue) -> {

                stackPane.getStyleClass().add(getCardName(newValue));
                if(oldValue != null){
                    stackPane.getStyleClass().remove(getCardName(oldValue));
                }

            });
            stackPane.disableProperty().bind(drawCards.isNull());

            //
            stackPane.setOnMouseClicked(event -> drawCards.getValue().onDrawCards(slot));

            cardsView.getChildren().add(cardRectangles(stackPane));
        }

        //cards button
        Button cardButton = new Button("Cartes");

        ReadOnlyIntegerProperty cardsPctProperty = gameState.cardsPercentageLeftProperty();

        cardsView.getChildren().add(deckButtons(cardButton, cardsPctProperty));

        //
        cardButton.disableProperty().bind(drawCards.isNull());

        //
        cardButton.setOnMouseClicked(event -> drawCards.get().onDrawCards(Constants.DECK_SLOT));


        return cardsView;
    }

    private static String getCardName(Card card) {
        return (card == Card.LOCOMOTIVE) ? "NEUTRAL" : card.name().toUpperCase(Locale.ROOT);
    }

    private static Button deckButtons(Button button, ReadOnlyIntegerProperty percentage) {
        Group gauge = new Group();

        Rectangle gaugeBackground = new Rectangle(50, 5);
        gaugeBackground.getStyleClass().add("background");

        Rectangle gaugeForeground = new Rectangle(50, 5);
        gaugeForeground.getStyleClass().add("foreground");
        gaugeForeground.widthProperty().bind(percentage.multiply(0.5));

        gauge.getChildren().addAll(gaugeBackground, gaugeForeground);

        button.setGraphic(gauge);
        button.getStyleClass().add("gauged");

        return button;
    }


    private static StackPane cardPane(Card card) {
        String cardName = getCardName(card);

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(cardName, "card");

        return cardRectangles(stackPane);
    }

    private static StackPane cardRectangles(StackPane stackPane) {
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");

        Rectangle inside = new Rectangle(40, 70);
        inside.getStyleClass().addAll("filled", "inside");

        Rectangle train = new Rectangle(40, 70);
        train.getStyleClass().add("train-image");

        stackPane.getChildren().addAll(outside, inside, train);
        return stackPane;

    }
}

