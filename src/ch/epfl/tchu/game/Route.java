package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

public final class Route {
    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    public enum Level{
        OVERGROUND,
        UNDERGROUND
    }

    public Route(String id, Station station1, Station station2, int length, Level level, Color color, String id1, Station station11, Station station21, int length1, Level level1, Color color1){

        this.id = id1;
        this.station1 = station11;
        this.station2 = station21;
        this.length = length1;
        this.level = level1;
        this.color = color1;
    }

    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }
    public List<Station> stations(){

    }
    public Station stationOpposite(Station station){

    } //qui retourne la gare de la route qui n'est pas celle donnée,
    // ou lève IllegalArgumentException si la gare donnée n'est
    // ni la première ni la seconde gare de la route,

    public List<SortedBag<Card>> possibleClaimCards(){

    }

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){}

    public int claimPoints(){}


}
