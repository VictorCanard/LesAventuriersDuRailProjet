package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents the view of the ticket and card draw piles, and face up cards, as well as the tickets and cards the player possesses.
 *
 * @author Anne-Marie Rusu (296098)
 */

class DecksViewCreator {
    private static final Map<Card, Double> X_HAND_CARD_POS = new EnumMap<>(Card.class) ;


    private DecksViewCreator() {
    }

    /**
     * Creates the Hand View of the player whose graphical interface this is; ie creates the tickets and cards the player possesses
     *
     * @param gameState : Observable Game State which allows the hand view to change according to the game's state
     * @return a Horizontal Box with a specific scene graph (set of children and attached nodes)
     */
    public static HBox createHandView(ObservableGameState gameState) {
        HBox mainHBox = new HBox();
        mainHBox.getStylesheets().addAll("decks.css", "colors.css", "info.css");
        //
        HBox handPane = new HBox();
        handPane.setId("hand-pane");
        //
        ObservableList<Ticket> listOfTickets = gameState.getAllPlayerTickets();
        ListView<Ticket> listView = new ListView<>(listOfTickets);
        listView.setId("tickets");
        //Score for Tickets
        Label ticketScore = new Label();
        ReadOnlyIntegerProperty points = gameState.ticketPoints();

        StringExpression string = new SimpleStringProperty("Total courant des tickets: ").concat(points);
        ticketScore.textProperty().bind(string);

        points.addListener((property, oldV, newV)-> {
            if(newV.shortValue() < 0){
                ticketScore.setId("ticket-points-negative");
            }else{
                ticketScore.setId("ticket-points-positive");
            }
        });
        //
        mainHBox.getChildren().addAll(listView, ticketScore, handPane);

        //Cards
        for (Card card : Card.ALL) {
            ReadOnlyIntegerProperty count = gameState.getNumberOfCard(card);

            StackPane stackPane = cardPane(card);

            Text text = new Text(count.getValue().toString());
            text.getStyleClass().add("count");

            text.textProperty().bind(Bindings.convert(count));
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));

            stackPane.getChildren().add(text);
            stackPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            handPane.getChildren().add(stackPane);

            X_HAND_CARD_POS.put(card, (double) (-745 + 75*Card.ALL.indexOf(card)));
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
        VBox cardPane = new VBox();
        cardPane.getStylesheets().addAll("decks.css", "colors.css");
        cardPane.setId("card-pane");

        //tickets button
        Button ticketButton = new Button(StringsFr.TICKETS);
        //
        ReadOnlyIntegerProperty ticketsPctProperty = gameState.ticketsPctLeftProperty();
        cardPane.getChildren().add(deckButton(ticketButton, ticketsPctProperty));
        //
        ticketButton.disableProperty().bind(drawTickets.isNull());
        //
        ticketButton.setOnMouseClicked(event -> drawTickets.get().onDrawTickets());

        //face up cards
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            StackPane mainStack = new StackPane();
            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("card");

            StackPane animCard = new StackPane();
            animCard.getStyleClass().add("card");


            //changes the graphics of the card according to what card is stored in the slot
            gameState.getFaceUpCard(slot).addListener((property, oldValue, newValue) -> {
                stackPane.getStyleClass().add(getCardName(newValue));

                if (oldValue != null) {
                    animCard.getStyleClass().add(getCardName(oldValue));
                    stackPane.getStyleClass().remove(getCardName(oldValue));
                    if(animCard.getStyleClass().size()>2) {
                        animCard.getStyleClass().remove(1);
                    }
                }else{
                    animCard.getStyleClass().add(getCardName(newValue));
                }
            });
            stackPane.disableProperty().bind(drawCards.isNull());

            //

            stackPane.setOnMouseClicked(event -> {
                String source = event.getSource().toString();
                String bracketPattern = Pattern.quote("[");
                String[] sourceTab = source.split(bracketPattern, -1);

                String cardname = sourceTab[1].substring(16, sourceTab[1].length()-1);

                double posx;
                if (cardname.equals("NEUTRAL")) {
                    posx = X_HAND_CARD_POS.get(Card.LOCOMOTIVE);
                } else {
                    Color color = Color.valueOf(cardname);
                    Card cardType = Card.of(color);
                    posx = X_HAND_CARD_POS.get(cardType);
                }
                if(animCard.getStyleClass().size()>1){ animCard.getStyleClass().remove(1);}
                animCard.getStyleClass().add(cardname);
                // Animations.arcTranslate(animCard, 0, 250,400, 200, posx, 575-(slot*100));
                Animations.translate(animCard, posx,575-(slot*100));

                drawCards.get().onDrawCards(slot);

            });

            mainStack.getChildren().addAll(cardRectangles(animCard), cardRectangles(stackPane));
            cardPane.getChildren().addAll(mainStack);
        }

        //cards button
        Button cardButton = new Button(StringsFr.CARDS);

        ReadOnlyIntegerProperty cardsPctProperty = gameState.cardsPctLeftProperty();

        cardPane.getChildren().add(deckButton(cardButton, cardsPctProperty));
        //
        cardButton.disableProperty().bind(drawCards.isNull());
        //
        cardButton.setOnMouseClicked(event -> drawCards.get().onDrawCards(Constants.DECK_SLOT));

        return cardPane;
    }
//todo: get rid of info related to drawn cards ? (spoiler)
    public static Node createDrawnCards2(SortedBag<Card> cards){
        HBox hbox = new HBox();
        hbox.getStylesheets().addAll("decks.css", "colors.css", "additional-cards.css");
        hbox.setId("drawCards");
        System.out.println(cards);

            for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                StackPane mainStack = new StackPane();
                mainStack.getStyleClass().add("card");

                StackPane frontOfCard = new StackPane();
                frontOfCard.getStyleClass().add(getCardName(cards.get(i)));


                StackPane back = new StackPane();
                StackPane backOfCard = backOfCardRectangle(back);

                mainStack.getChildren().addAll(cardRectangles(frontOfCard), backOfCard);

                hbox.getChildren().add(mainStack);
                Animations.flip(backOfCard, frontOfCard);
            }

        return hbox;
    }

    /**
     * Finds the String to associate to a specific card. Neutral is the card is a locomotive, its name in uppercase otherwise
     *
     * @param card : the card which we want to know the name of
     * @return the name of the cards (in upper case)
     */
    private static String getCardName(Card card) {
        return (card == Card.LOCOMOTIVE) ? "NEUTRAL" : card.color().name();
    }

    /**
     * Creates a gauged button with a certain percentage and a specific button
     *
     * @param button     : button we want to add a gauge to
     * @param percentage : represents the actual value which is displayed onto the gauge
     * @return a button with a percentage bar
     */
    private static Button deckButton(Button button, ReadOnlyIntegerProperty percentage) {
        Group group = new Group();

        Rectangle gaugeBackground = new Rectangle(50, 5);
        gaugeBackground.getStyleClass().add("background");

        Rectangle gaugeForeground = new Rectangle(50, 5);
        gaugeForeground.getStyleClass().add("foreground");
        gaugeForeground.widthProperty().bind(percentage.multiply(0.5));

        group.getChildren().addAll(gaugeBackground, gaugeForeground);

        button.setGraphic(group);
        button.getStyleClass().add("gauged");

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

        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(cardName, "card");

        return cardRectangles(stackPane);
    }

    /**
     * Makes the three card rectangles for a given stackPane
     *
     * @param stackPane : to which we add an outside, inside and train rectangle
     * @return the stack pane given as an argument with three new rectangles as children of its scene graph
     */
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

    private static StackPane backOfCardRectangle(StackPane backOfCard){
        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("tunnel-card");
        Rectangle train = new Rectangle(40, 70);
        train.getStyleClass().add("train-image");

        backOfCard.getChildren().addAll(outside, train);
        return backOfCard;
    }
}

