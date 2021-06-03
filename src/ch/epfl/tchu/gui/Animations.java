package ch.epfl.tchu.gui;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents the kinds of animations used in the game
 * @author Anne-Marie Rusu (296098)
 */
public class Animations{
    private static final Duration DURATION = Duration.seconds(0.5);
    private static final Duration PAUSE = Duration.seconds(1);
    private static final double PATH_RATE = 1.2;


    /**
     * Translates a node to a given ending position (starts in (0,0))
     * @param node : the node to be translated
     * @param x : the x coordinate to translate by
     * @param y : the y coordinate to translate by
     */
    public static void translate(Node node, double x, double y){
        node.setVisible(true);
        TranslateTransition translate  = new TranslateTransition();

        translate.setRate(PATH_RATE);
        translate.setFromX(0);
        translate.setFromY(0);
        translate.setByX(x);
        translate.setByY(y);

        translate.setNode(node);
        translate.setOnFinished(event -> node.setVisible(false));
        translate.playFromStart();

    }

    /**
     * Flips the set of 3 drawn additional cards obtained when a player wants to claim an underground route
     * @param faceDown : an array of the three face down cards
     * @param faceUp : an array of the the three face up cards
     * @param consumer : a consumer used to execute receiveInfo
     * @param nextPlayer : the next player to play after the turn is over
     * @param message : the message if the player has claimed the route or not
     * @param noAddCost : the additional cost for this turn
     * @param isLastTurn : if the last turn has arrived
     */

    public static void flip(Node [] faceDown, Node [] faceUp, Consumer<String> consumer, String nextPlayer, String message, boolean noAddCost, boolean isLastTurn){
        SequentialTransition sq = new SequentialTransition(new PauseTransition(PAUSE), simpleFlip(faceDown[0], faceUp[0]), simpleFlip(faceDown[1], faceUp[1]), simpleFlip(faceDown[2], faceUp[2]));

        if(noAddCost || !message.equals("null")) {
           sq.setOnFinished(event -> {
               consumer.accept(message);
               if(!isLastTurn) {
                   consumer.accept("FROM ANIMATION : " + String.format(StringsFr.CAN_PLAY, nextPlayer));}
           });
        }

        sq.play();
    }

    /**
     *  A private method to help create the flip animation of a single card
     * @param faceDown : the facedown side of the card
     * @param faceUp : the face up side of the card
     * @return the transition corresponding to the flip of a single card
     */

    private static SequentialTransition simpleFlip(Node faceDown, Node faceUp){
        faceUp.setVisible(true);
        int rotAngle = 90;
        RotateTransition fromFaceDown = new RotateTransition(DURATION);
        RotateTransition toFaceUp = new RotateTransition(DURATION);

        fromFaceDown.setAxis(Rotate.Y_AXIS);
        fromFaceDown.setByAngle(rotAngle);

        toFaceUp.setAxis(Rotate.Y_AXIS);
        toFaceUp.setFromAngle(rotAngle);
        toFaceUp.setByAngle(rotAngle);

        fromFaceDown.setNode(faceDown);
        toFaceUp.setNode(faceUp);

        return new SequentialTransition(fromFaceDown, toFaceUp);

    }

}
