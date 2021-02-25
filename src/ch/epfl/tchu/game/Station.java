package ch.epfl.tchu.game;

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
     * @param name
     */
    public Station(int id, String name){
        if(id <0){
            throw new IllegalArgumentException();
        }
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
