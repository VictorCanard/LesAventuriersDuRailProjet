package ch.epfl.tchu.game;

import java.util.List;

/**
 * The players partaking in the game
 *
 * @author Anne-Marie Rusu (296098)
 */


public enum PlayerId {
    PLAYER_1, PLAYER_2, PLAYER_3;

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
        if(this.equals(PLAYER_1)){
            return PLAYER_2;
        }else if(this.equals(PLAYER_2)){
            return PLAYER_3;
        }
        return PLAYER_1;
    }



}
