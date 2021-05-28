package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * Represents the view of the information panel
 *
 * @author Anne-Marie (296098)
 * @author Victor Canard-Duchêne (326913)
 */

public class InfoViewCreator {
    private InfoViewCreator() {
    }

    /**
     * Creates the Information Panel on the left side of each player's graphical interface.
     *
     * @param playerId    : the player whose graphical interface this is.
     * @param playerNames : the name of each player.
     * @param gameState   : the state of the game to display.
     * @param infos       : the messages that appear in the bottom-left corner giving information on the sequence of events of the game.
     * @return a Vertical Box containing the messages and each player's stats.
     */
    public static VBox createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> infos) {
        final String info = "info.css";
        final String gameInfoString = "game-info";

        VBox infoPane = new VBox();
        infoPane.getStylesheets().addAll(info, GuiUtils.COLORS);

        //Adds the players' statistics to their windows
        PlayerId currentId = playerId;
        for (int i = 0; i < PlayerId.COUNT; i++) {
            infoPane.getChildren().add(playerStats(currentId, playerNames, gameState));
            currentId = currentId.next();
        }

        Separator separator = new Separator();
        infoPane.getChildren().add(separator);

        TextFlow gameInfo = new TextFlow();
        gameInfo.setId(gameInfoString);
        infoPane.getChildren().add(gameInfo);

        Bindings.bindContent(gameInfo.getChildren(), infos);

        return infoPane;
    }

    /**
     * Creates the view on each player's stats in the top-left corner of the graphical display.
     *
     * @param playerId    : the player whose graphical interface this is.
     * @param playerNames : the name of each player.
     * @param gameState   : the state of the game to display.
     * @return a Vertical Box with all stats concerning this player and all stats concerning the other player underneath it.
     */
    private static VBox playerStats(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState) {
        final int circleRadius = 5;
        final String playerStatsString = "playerStats";

        VBox playerStats = new VBox();
        playerStats.setId(playerStatsString);
        playerStats.getStyleClass().add(playerId.name());

        Circle circle = new Circle(circleRadius);
        circle.getStyleClass().add(GuiUtils.FILLED);
        circle.getStyleClass().add("Circle");
        //Creates the text of the player's statistics
        Text stats = new Text();
        stats.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,
                playerNames.get(playerId),
                gameState.getTicketCount(playerId),
                gameState.getCardCount(playerId),
                gameState.getCarCount(playerId),
                gameState.getConstructionPoints(playerId)));

        TextFlow textFlow = new TextFlow(circle, stats);
        playerStats.getChildren().addAll(textFlow);

        return playerStats;
    }
}
