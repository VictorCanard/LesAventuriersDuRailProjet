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

    public static Pane createMapView(ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        Pane map = new Pane();
        //map.setPrefHeight();
        map.getStylesheets().addAll("map.css", "colors.css");

        //
        ImageView mapBackground = new ImageView("map.png");

        map.getChildren().add(mapBackground);

        //

        setAllRoutes(map, gameState, claimRouteHP, cardChooser);


        return map;
    }

    private static void setAllRoutes(Pane map, ObservableGameState gameState, ObjectProperty<ClaimRouteHandler> claimRouteHP, CardChooser cardChooser) {
        for (Route route : ChMap.routes()
        ) {
            Group routeGroup = new Group();

            //Set Id, color and level to a route's group style class
            routeGroup.setId(route.id());

            String routeColor = (route.color() == null) ? "NEUTRAL" : route.color().name().toUpperCase(Locale.ROOT);
            routeGroup.getStyleClass().addAll("route", route.level().name(), routeColor);


            //When a route is clicked on, checks the cards a player could use to claim this route.
            //If he can play multiple sorted bag of cards, asks the player which one he wants to use.
            routeGroup.setOnMouseClicked((event -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);

                if (possibleClaimCards.size() == 1) {
                    claimRouteHP.get().onClaimRoute(route, possibleClaimCards.get(0));
                } else if (possibleClaimCards.size() > 1) {
                    ChooseCardsHandler chooseCardsH =
                            chosenCards -> claimRouteHP.get().onClaimRoute(route, chosenCards);

                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }

            }));

            //When a route is claimed, adds the Id of the player who claimed it to the routeGroup's style class
            gameState.getAllRoutesContainedByWhom().get(route).addListener((property, oldValue, newValue) -> {
                if (newValue != null) {
                    routeGroup.getStyleClass().add(newValue.name());
                }

            });

            //If the route isn't claimable or if the handler is null, deactivates the routeGroup
            routeGroup.disableProperty().bind(
                    claimRouteHP.isNull().or(gameState.claimable(route).not()));


            map.getChildren().add(routeGroup);

            //
            setAllBlocksOfARoute(route, routeGroup);


        }
    }

    private static void setAllBlocksOfARoute(Route route, Group routeGroup) {
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

            Circle wheel1 = new Circle(centerX - 6, centerY, 3);
            Circle wheel2 = new Circle(centerX + 6, centerY, 3);

            wagonGroup.getChildren().addAll(wagonRectangle, wheel1, wheel2);
        }
    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ChooseCardsHandler handler);
    }
}
