package ch.epfl.tchu.game;

import java.util.List;

/**
 * The players partaking in the game
 *
 * @author Anne-Marie Rusu (296098)
 */

public enum PlayerId {
    PLAYER_1, PLAYER_2;

    /**
     * List of all the players
     */
    public final static List<PlayerId> ALL = List.of(PlayerId.values());

    /**
     * Number of players
     */
    public final static int COUNT = ALL.size();

    /**
     * Getter for the next player's id
     *
     * @return the next player's id
     */
    public PlayerId next() {
        return PlayerId.ALL.get((this.ordinal() + 1) % PlayerId.COUNT);
    }
}
