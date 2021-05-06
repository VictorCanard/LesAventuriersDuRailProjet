package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

public class InfoViewCreator {
    private InfoViewCreator(){}

    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> infos){
        VBox infoPane = new VBox();
        infoPane.getStylesheets().addAll("info.css", "colors.css");

        VBox player1Stats = playerStats(playerId, playerNames, gameState);

        VBox player2Stats = playerStats(playerId.next(), playerNames, gameState);

        Separator separator = new Separator();

        infoPane.getChildren().addAll(player1Stats, player2Stats, separator);

        TextFlow gameInfo = new TextFlow();
        gameInfo.setId("game-info");
        infoPane.getChildren().add(gameInfo);

        Bindings.bindContent(gameInfo.getChildren(), infos); // i think?
        return infoPane;
    }

    private static VBox playerStats(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState){
        String playerName = playerNames.get(playerId);
        String playerStyle = playerId.name();
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");
        playerStats.getStyleClass().add(playerStyle);

        Circle circle = new Circle(5);
        circle.getStyleClass().add("filled");

        Text stats = new Text();
        stats.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,
                playerName,
                gameState.getTicketCount(playerId).getValue(),
                gameState.getCardCount(playerId).getValue(),
                gameState.getCarCount(playerId).getValue(),
                gameState.getConstructionPoints(playerId).getValue()));

        TextFlow textFlow = new TextFlow(circle, stats);
        playerStats.getChildren().addAll(textFlow);

        return playerStats;
    }

}
