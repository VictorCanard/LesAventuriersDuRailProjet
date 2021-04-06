package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


/**
 * Train stations used in the game
 * @author Victor Jean Canard-Duchene (326913)
 */

public final class Station {
    /**
     * Unique identifier (positive)
     */
    private final int id;
    /**
     * Name of the station
     */
    private final String name;

    /**
     * A train station, defined by its id and name
     * @param id between 0 and 50
     * @param name Station's name
     * @throws IllegalArgumentException if id is strictly negative
     */
    public Station(int id, String name){
        Preconditions.checkArgument(id>=0);

        this.id = id;
        this.name = name;
    }

    /**
     * Getter for the id associated to the train station
     * @return the id of a station
     */
    public int id() {
        return id;
    }

    /**
     * Getter for the name associated to the train station
     * @return name of the train station
     */
    public String name() {
        return name;
    }

    /**
     * Redefines the textual representation of a station to be its name
     * @return the station's name
     */
    @Override
    public String toString(){
        return name();
    }
}
