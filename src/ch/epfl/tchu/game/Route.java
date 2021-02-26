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

    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
    }


    public enum Level{
        OVERGROUND,
        UNDERGROUND
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
        return List.of(station1,station2);
    }


    public Station stationOpposite(Station station){
        if(station.name().equals(stat))
            case station1.name():
                return station2;
            case station2.name():
                return station1;
            default:
                throw new IllegalArgumentException("The station isn't one of this route");


        }

    }

    public List<SortedBag<Card>> possibleClaimCards(){

    }

    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){}

    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }


}
