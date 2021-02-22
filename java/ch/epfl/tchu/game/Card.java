package ch.epfl.tchu.game;

import java.util.List;

public enum Card {
    BLACK,VIOLET,BLUE,GREEN,YELLOW,ORANGE,RED,WHITE,LOCOMOTIVE;

    private Color color = null;
    public final static List<Card> ALL = List.of(Card.values());
    public final static int COUNT = ALL.size();

    public final static List<Card> CARS = ALL.subList(0,8);

    Card(){} //Gros doute ici, fin de la partie 3.4
    Card(Color color){
        this.color = color;
    }

    public static Card of(Color color){
        return ALL.get(color.ordinal());
    }
    public Color color(){
        return this.color;
    }
}
