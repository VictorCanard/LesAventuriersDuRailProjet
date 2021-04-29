package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;
import java.util.Locale;

class MapViewCreator {
    private MapViewCreator() {
    }

    public static Node createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser chooseCards) {
        Pane map = new Pane();
        map.getStylesheets().addAll("map.css", "colors.css");

        //
        ImageView mapBackground = new ImageView("map.png");

        map.getChildren().add(mapBackground);

        //

        setAllRoutes(map, gameState, claimRouteHP);


        return map;
    }

    private static void setAllRoutes(Pane map, ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP) {
        for (Route route : ChMap.routes()
        ) {
            Group routeGroup = new Group();

            routeGroup.setId(route.id());

            String routeColor = (route.color() == null) ? "NEUTRAL" : route.color().name().toUpperCase(Locale.ROOT);
            routeGroup.getStyleClass().addAll("route", route.level().name(), routeColor);


            routeGroup.disableProperty().bind(
                    claimRouteHP.isNull().or(gameState.claimable(route).not()));

            map.getChildren().add(routeGroup);

            //
            for (int currentRouteCase = 1; currentRouteCase <= route.length(); currentRouteCase++) {
                Group caseGroup = new Group();
                caseGroup.setId(route.id() + "_" + currentRouteCase);

                routeGroup.getChildren().add(caseGroup);
                //
                Rectangle trackRectangle = new Rectangle(36, 12);
                trackRectangle.getStyleClass().addAll("track", "filled");

                caseGroup.getChildren().add(trackRectangle);

                //
                Group wagonGroup = new Group();
                wagonGroup.getStyleClass().add("car");

                caseGroup.getChildren().add(wagonGroup);
                //
                Rectangle wagonRectangle = new Rectangle(36, 12);
                wagonRectangle.getStyleClass().add("filled");

                double centerX = wagonRectangle.widthProperty().get() / 2;
                double centerY = wagonRectangle.heightProperty().get() / 2;

                Circle circle1 = new Circle(centerX - 6, centerY, 3);
                Circle circle2 = new Circle(centerX + 6, centerY, 3);

                wagonGroup.getChildren().addAll(wagonRectangle, circle1, circle2);
            }

        }
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }
}
