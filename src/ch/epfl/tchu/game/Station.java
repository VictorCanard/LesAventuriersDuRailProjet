package ch.epfl.tchu.game;

import java.util.Objects;

/**
 * Class Station
 * @author Victor Jean Canard-Duchene (326913)
 */

public final class Station {
    private int id;
    private String name;

    /**
     * Station Constructor
     * if the id is negative it throws an error.
     * @param id : between 0 and 50
     * @param name : station's name
     */
    public Station(int id, String name){
        if(id <0){
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) { //Généré par Intellij
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id == station.id && name.equals(station.name);
    }


    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
