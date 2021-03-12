package ch.epfl.tchu.game;

import java.util.List;

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
     *Getter for the next player to have a turn
     * @return the next player
     */
    public PlayerId next(){

        if(this.equals(PLAYER_1)){
            return PLAYER_2;
        }else{
            return PLAYER_1;
        }
    }
}
