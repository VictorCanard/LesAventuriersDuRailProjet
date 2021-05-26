package ch.epfl.tchu.gui;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

//organizing all the animation possibilities
public class Animations extends Application{
    private static final Duration ONE_S = Duration.millis(1000);
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
//works
    public static void flip(Node faceDown, Node faceUp){ //maybe do all three at the same time in sequence to do setOnFinished to hide the three
        int rotAngle = 90;
        RotateTransition fromFaceDown = new RotateTransition(ONE_S);
        RotateTransition toFaceUp = new RotateTransition(ONE_S);

        fromFaceDown.setAxis(Rotate.Y_AXIS);
        fromFaceDown.setByAngle(rotAngle);

        toFaceUp.setAxis(Rotate.Y_AXIS);
        toFaceUp.setFromAngle(rotAngle);
        toFaceUp.setByAngle(rotAngle);

        fromFaceDown.setNode(faceDown);
        toFaceUp.setNode(faceUp);

        SequentialTransition sq = new SequentialTransition(new PauseTransition(ONE_S), fromFaceDown, toFaceUp);
        sq.play();
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
        pt.setDuration(ONE_S);
        pt.setPath(arc);
        pt.setNode(node);

        TranslateTransition tt = new TranslateTransition(ONE_S);
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
            root.getChildren().addAll(cir, cir2);
            Scene scene = new Scene(root,1500,700,Color.WHITE);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Animation Test");
            primaryStage.show();

        }
        public static void main(String[] args) {
            launch(args);
        }





}
