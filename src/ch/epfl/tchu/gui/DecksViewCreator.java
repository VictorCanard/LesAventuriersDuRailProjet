package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Represents the view of the ticket and card draw piles, and face up cards, as well as the tickets and cards the player possesses.
 *
 * @author Anne-Marie Rusu (296098)
 * @author Victor Canard-Duchêne (326913)
 */

class DecksViewCreator {
    private static final String DECKS = "decks.css";
    private static final String CARD_STRING = "card";

    private DecksViewCreator() {
    }

    /**
     * Creates the Hand View of the player whose graphical interface this is; ie creates the tickets and cards the player possesses
     *
     * @param gameState : Observable Game State which allows the hand view to change according to the game's state
     * @return a Horizontal Box with a specific scene graph (set of children and attached nodes)
     */
    public static HBox createHandView(ObservableGameState gameState) {
        final int minNumberVisible = 1;
        final int minCardVisible = 0;


        final String handPaneString = "hand-pane";
        final String tickets = "tickets";
        final String countString = "count";

        //
        HBox mainHBox = new HBox();
        mainHBox.getStylesheets().addAll(DecksViewCreator.DECKS, GuiUtils.COLORS);
        //
        HBox handPane = new HBox();
        handPane.setId(handPaneString);
        //
        ObservableList<Ticket> listOfTickets = gameState.getAllPlayerTickets();
        ListView<Ticket> listView = new ListView<>(listOfTickets);
        listView.setId(tickets);
        //
        mainHBox.getChildren().addAll(listView, handPane);

        //Cards
        for (Card card : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.getNumberOfCard(card);
            //
            StackPane stackPane = cardPane(card);
            //
            Text text = new Text(count.getValue().toString());
            text.getStyleClass().add(countString);
            //
            text.textProperty().bind(Bindings.convert(count));
            text.visibleProperty().bind(Bindings.greaterThan(count, minNumberVisible));
            //
            stackPane.getChildren().add(text);
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, minCardVisible));
            //
            handPane.getChildren().add(stackPane);
        }
        return mainHBox;
    }

    /**
     * Creates the cards view at the right of the screen; ie the tickets deck, the cards deck, and the 5 face-up cards
     *
     * @param gameState   : observable game state that stores the information about the tickets and cards
     * @param drawTickets : an action handler for drawing tickets
     * @param drawCards   : an action handler for drawing cards
     * @return a vertical box with a specific scene graph
     */
    public static Node createCardsView(ObservableGameState gameState, ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets, ObjectProperty<ActionHandlers.DrawCardHandler> drawCards) {
        final String cardPaneString = "card-pane";

        VBox cardPane = new VBox();
        cardPane.getStylesheets().addAll(DECKS, GuiUtils.COLORS);
        cardPane.setId(cardPaneString);

        //Tickets button
        Button ticketButton = new Button(StringsFr.TICKETS);
        //
        ReadOnlyIntegerProperty ticketsPctProperty = gameState.ticketsPercentageLeftProperty();
        cardPane.getChildren().add(deckButton(ticketButton, ticketsPctProperty));
        //
        ticketButton.disableProperty().bind(drawTickets.isNull());
        //
        ticketButton.setOnMouseClicked(event -> drawTickets.get().onDrawTickets());

        //Face-Up cards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add(CARD_STRING);

            //Changes the graphics of the card according to what card is stored in the slot
            gameState.getFaceUpCard(slot).addListener((property, oldValue, newValue) -> {

                stackPane.getStyleClass().add(getCardName(newValue));

                if (oldValue != null) {
                    stackPane.getStyleClass().remove(getCardName(oldValue));
                }
            });
            stackPane.disableProperty().bind(drawCards.isNull());
            //
            stackPane.setOnMouseClicked(event -> drawCards.get().onDrawCards(slot));
            //
            cardPane.getChildren().add(cardRectangles(stackPane));
        }

        //Cards button
        Button cardButton = new Button(StringsFr.CARDS);

        ReadOnlyIntegerProperty cardsPercentageProperty = gameState.cardsPercentageLeftProperty();

        cardPane.getChildren().add(deckButton(cardButton, cardsPercentageProperty));
        //
        cardButton.disableProperty().bind(drawCards.isNull());
        //
        cardButton.setOnMouseClicked(event -> drawCards.get().onDrawCards(Constants.DECK_SLOT));

        return cardPane;
    }

    /**
     * Finds the String to associate to a specific card. Neutral is the card is a locomotive, its name in uppercase otherwise
     *
     * @param card : the card which we want to know the name of
     * @return the name of the cards (in upper case)
     */
    private static String getCardName(Card card) {
        return (card == Card.LOCOMOTIVE) ? GuiUtils.NEUTRAL : card.color().name();
    }

    /**
     * Creates a gauged button with a certain percentage and a specific button
     *
     * @param button     : button we want to add a gauge to
     * @param percentage : represents the actual value which is displayed onto the gauge
     * @return a button with a percentage bar
     */
    private static Button deckButton(Button button, ReadOnlyIntegerProperty percentage) {
        final int rectangleWidth = 50;
        final int rectangleHeight = 5;
        final double percentageMultiplier = 0.5;

        final String bg = "background";
        final String fg = "foreground";
        final String gauged = "gauged";
        //
        Group group = new Group();
        //
        Rectangle gaugeBackground = new Rectangle(rectangleWidth, rectangleHeight);
        gaugeBackground.getStyleClass().add(bg);
        //
        Rectangle gaugeForeground = new Rectangle(rectangleWidth, rectangleHeight);
        gaugeForeground.widthProperty().bind(percentage.multiply(percentageMultiplier));
        gaugeForeground.getStyleClass().add(fg);
        //
        group.getChildren().addAll(gaugeBackground, gaugeForeground);
        //
        button.setGraphic(group);
        button.getStyleClass().add(gauged);
        //
        return button;
    }

    /**
     * Makes the card pane of a specific card and returns it
     *
     * @param card : card we want to make into a stack pane
     * @return a new Stack Pane for a specific card
     */
    private static StackPane cardPane(Card card) {
        String cardName = getCardName(card);
        //
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(cardName, CARD_STRING);
        //
        return cardRectangles(stackPane);
    }

    /**
     * Makes the three card rectangles for a given stackPane
     *
     * @param stackPane : to which we add an outside, inside and train rectangle
     * @return the stack pane given as an argument with three new rectangles as children of its scene graph
     */
    private static StackPane cardRectangles(StackPane stackPane) {
        final int outWidth = 60;
        final int outHeight = 90;
        final int inWidth = 40;
        final int inHeight = 70;

        final String outsideString = "outside";
        final String insideString = "inside";
        final String trainImage = "train-image";
        //
        Rectangle outside = new Rectangle(outWidth, outHeight);
        outside.getStyleClass().add(outsideString);
        //
        Rectangle inside = new Rectangle(inWidth, inHeight);
        inside.getStyleClass().addAll(GuiUtils.FILLED, insideString);
        //
        Rectangle train = new Rectangle(inWidth, inHeight);
        train.getStyleClass().add(trainImage);
        //
        stackPane.getChildren().addAll(outside, inside, train);
        //
        return stackPane;
    }
}

