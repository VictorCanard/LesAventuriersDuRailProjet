package ch.epfl.tchu.game;

import java.util.List;

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

    private Color color;

    public final static List<Card> ALL = List.of(Card.values());
    public final static int COUNT = ALL.size();
    public final static List<Card> CARS = ALL.subList(0,8);

    /**
     * Constructor for Card
     * @param color : the color of the corresponding card
     */
    Card(Color color){
        this.color = color;
    }

    /**
     * Determines the type of card from its color
     * @param color : the color of a card
     * @return : the type of the card corresponding to the given color
     */
    public static Card of(Color color){
        return ALL.get(color.ordinal());
    }

    /**
     * Determines the color of the card from its type. Null if its a locomotive card
     * @return : the color of the given card
     */
    public Color color(){
        if(this.color == null){
            return null;
        }else {
            return this.color;
        }
    }
}
