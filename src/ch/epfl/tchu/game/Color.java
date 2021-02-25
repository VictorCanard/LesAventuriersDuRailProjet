package ch.epfl.tchu.game;

import java.util.List;

/**
 * @author Victor Canard-Duchêne
 */

public enum Color {
    BLACK,VIOLET,BLUE,GREEN,YELLOW,ORANGE,RED,WHITE;


    public final static List<Color> ALL = List.of(Color.values());
    public final static int COUNT = ALL.size();
}
