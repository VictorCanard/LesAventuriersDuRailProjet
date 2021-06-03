package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.Menu;

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
        return Menu.activePlayers.get((this.ordinal() + 1) % Menu.numberOfPlayers);
    }

    /**
     * Gets the previous player Id
     * @return the previous players Id
     */
    public PlayerId previous(){
        if(Menu.numberOfPlayers == 3) {return Menu.activePlayers.get((this.ordinal() +2) % Menu.numberOfPlayers);}
        else{ return this.next();}
    }
}