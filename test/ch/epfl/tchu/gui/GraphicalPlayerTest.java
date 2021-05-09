package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class GraphicalPlayerTest extends Application {

    private SortedBag<Card> makeAllCards() {
        SortedBag.Builder<Card> cards = new SortedBag.Builder<>();

        for (int i = 0; i < 10; i++) {
            cards.add(SortedBag.of(Card.ALL));
        }
        return cards.build();
    }

    PlayerState p1State;

    private void setState(GraphicalPlayer2 player) {

        List<Route> playerOneRoutes = new ArrayList<>(ChMap.routes().subList(0, 3));
        playerOneRoutes.add(ChMap.routes().get(16));
        p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        makeAllCards(), playerOneRoutes
                );

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);

        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);

        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        GraphicalPlayer2 p = new GraphicalPlayer2(PLAYER_1, playerNames);
        setState(p);

        ActionHandlers.DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets !");
        ActionHandlers.DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ActionHandlers.ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        ActionHandlers.ChooseTicketsHandler chooseTicketsHandler =
                (keptTickets -> p.receiveInfo("J'ai gardé "+ keptTickets));

        ActionHandlers.ChooseCardsHandler chooseAdditionalCardsHandler =  (additionalCards -> p.receiveInfo("J'ai joue " + additionalCards + " cartes additionelles"));


        p.startTurn(drawTicketsH, drawCardH, claimRouteH);
        p.receiveInfo("Hello");

        p.chooseTickets(SortedBag.of(ChMap.tickets().subList(0, 5)), chooseTicketsHandler);
        p.chooseAdditionalCards(p1State.possibleAdditionalCards(3, SortedBag.of(3, Card.ORANGE), SortedBag.of(3, Card.ORANGE)), chooseAdditionalCardsHandler);
    }
}