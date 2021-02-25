package ch.epfl.tchu.game;

/**
 * Interface StationConnectivity
 * @author Anne-Marie Rusu (296098)
 */
public interface StationConnectivity {

    /**
     * Verifies if two stations are connected
     * @param s1 : first train station
     * @param s2 : second train station
     * @return : true if they are connected by a (single) players wagons, false otherwise
     */
    boolean connected(Station s1, Station s2);
}
