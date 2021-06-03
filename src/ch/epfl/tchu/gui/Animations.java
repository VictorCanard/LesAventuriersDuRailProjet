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

//organizing all the animation possibilities
public class Animations extends Application{
    private static final Duration DURATION = Duration.seconds(0.5);
    private static final Duration PAUSE = Duration.seconds(1);
    private static final double PATH_RATE = 1.2;


//works
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

    public static void arcTranslate(Node node, double centerx, double centery, double radx, double rady, double finalx, double finaly){
        Arc arc = new Arc();
        arc.setLength(90);
        arc.setRadiusX(radx);
        arc.setRadiusY(rady);
        arc.setStartAngle(90);
        arc.setType(ArcType.OPEN);
        arc.setCenterX(centerx);
        arc.setCenterY(centery);

        PathTransition pt = new PathTransition();
        pt.setDuration(DURATION);
        pt.setPath(arc);
        pt.setNode(node);

        TranslateTransition tt = new TranslateTransition(DURATION);
        tt.setToY(finaly);
        tt.setToX(finalx);
        tt.setNode(node);


        SequentialTransition sq = new SequentialTransition(pt, tt);
        sq.setOnFinished(event -> node.setVisible(false));
        sq.play();

    }


//for testing

        @Override
        public void start(Stage primaryStage) throws Exception {
            // TODO Auto-generated method stub
            //Creating the circle
            Circle cir = new Circle(600,250,50);

            //setting color and stroke of the cirlce
            cir.setFill(Color.RED);
            cir.setStroke(Color.BLACK);


            Circle cir2 = new Circle(600, 250, 50);
            cir2.setFill(Color.BLUE);
            cir2.setStroke(Color.BLACK);





            //translate(cir, 400, 0, 0);
            //flip(cir, cir2);
            //arcTranslate(cir, 600, 250, 200, 200);

            //Configuring Group and Scene
            Group root = new Group();
            root.getStylesheets().add("decks.css");

            StackPane stackPane = new StackPane();
            stackPane.getStyleClass().add("card");
            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("tunnel-card");

            Rectangle train = new Rectangle(40, 70);
            train.getStyleClass().add("train-image");

            stackPane.getChildren().addAll(outside, train);

            StackPane sp = new StackPane();
            stackPane.getStyleClass().add("card");
            Rectangle out = new Rectangle(60, 90);
            out.getStyleClass().add("outside");

            Rectangle tr = new Rectangle(40, 70);
            tr.getStyleClass().add("train-image");

            sp.getChildren().addAll(out, tr);



            root.getChildren().addAll(sp, stackPane);
            //flip(stackPane, sp);
            Scene scene = new Scene(root,1500,700,Color.WHITE);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Animation Test");
            primaryStage.show();

        }
        public static void main(String[] args) {
            launch(args);
        }





}
