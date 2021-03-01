package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

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
        Preconditions.checkArgument(id>=0);

        this.id = id;
        this.name = name;
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
