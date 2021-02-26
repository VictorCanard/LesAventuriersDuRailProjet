package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

public final class Route {
    private final String ID;
    private final Station STATION1;
    private final Station STATION2;
    private final int LENGTH;
    private final Level LEVEL;
    private final Color COLOR;

    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {


        if(station1.equals(station2)|| length<Constants.MIN_ROUTE_LENGTH || length > Constants.MAX_ROUTE_LENGTH) {
            throw new IllegalArgumentException();
        }else if(id == null || station1 == null || station2 == null || level == null) {
            throw new NullPointerException();
        }else {
            this.ID = id;
            this.STATION1 = station1;
            this.STATION2 = station2;
            this.LENGTH = length;
            this.LEVEL = level;
            this.COLOR = color;
        }


    }


    public enum Level{
        OVERGROUND,
        UNDERGROUND
    }

    public String id() {
        return ID;
    }

    public Station station1() {
        return STATION1;
    }

    public Station station2() {
        return STATION2;
    }

    public int length() {
        return LENGTH;
    }

    public Level level() {
        return LEVEL;
    }

    public Color color() {
        return COLOR;
    }
    public List<Station> stations(){
        return List.of(STATION1,STATION2);
    }


    public Station stationOpposite(Station station){
        if(station.name().equals(STATION1.name())) {
            return STATION2;
        }
        else if(station.name().equals(STATION2.name())) {
            return STATION1;
        }else {
            throw new IllegalArgumentException("The station isn't one of this route");
        }
    }



    public List<SortedBag<Card>> possibleClaimCards(){
        return null;
    }

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        return 0;
    }

    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(LENGTH);
    }


}
