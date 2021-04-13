package ch.epfl.tchu.game;

import java.util.List;

/**
 * The players partaking in the game
 * @author Anne-Marie Rusu (296098)
 */

public enum PlayerId {
    PLAYER_1, PLAYER_2;


    public final static List<PlayerId> ALL = List.of(PlayerId.values());

    public final static int COUNT = ALL.size();

    /**
     *Getter for the next player's id, different from this
     * @return the next player's id
     */
    public PlayerId next(){
        return (this.equals(PLAYER_1)) ? PLAYER_2 : PLAYER_1;
    }
}
