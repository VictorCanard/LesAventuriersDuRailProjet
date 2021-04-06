package ch.epfl.tchu.game;

import java.util.List;

/**
 * Types of colors the cards and routes can be
 * @author Victor Canard-Duchêne (326913)
 */
public enum Color {
    BLACK,VIOLET,BLUE,GREEN,YELLOW,ORANGE,RED,WHITE;

    /**
     * List of all the colors
     */
    public final static List<Color> ALL = List.of(Color.values());

    /**
     * Total number of colors
     */
    public final static int COUNT = ALL.size();
}
