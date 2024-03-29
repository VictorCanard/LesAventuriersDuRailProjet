package ch.epfl.tchu.game;

import java.util.List;

/**
 * Represents the types of cards that can be played
 *
 * @author Victor Jean Canard-Duchene (326913)
 * @author Anne-Marie Rusu (296098)
 */
public enum Card {
    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    /**
     * List of all the types of cards
     */
    public final static List<Card> ALL = List.of(Card.values());
    /**
     * Total number of types of cards
     */
    public final static int COUNT = ALL.size();
    /**
     * List of the car types of cards (excluding the locomotive card)
     */
    public final static List<Card> CARS = ALL.subList(0, Color.COUNT);
    private final Color color;

    /**
     * Constructs the card from the given color
     *
     * @param color : the color of the corresponding card
     */
    Card(Color color) {
        this.color = color;
    }

    /**
     * Determines the type of card from its color
     *
     * @param color : the color of a card
     * @return : the type of the card corresponding to the given color
     */
    public static Card of(Color color) {
        return ALL.get(color.ordinal());
    }

    /**
     * Determines the color of the card from its type. Null if its a locomotive card.
     *
     * @return : the color of the given card
     */
    public Color color() {
        return this.color;
    }
}
