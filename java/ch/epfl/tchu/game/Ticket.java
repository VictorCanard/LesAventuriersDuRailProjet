package ch.epfl.tchu.game;

import java.util.List;

/**
 * Class Ticket
 * @author Anne-Marie Rusu (296098)
 */
public final class Ticket  implements Comparable<Ticket>{

    private final String TEXT;

    Ticket(List<Trip> trips){

    }
    Ticket(Station from, Station to, int points){

    }

    /**
     * Textual representation of the trip ticket
     * @return : text of the ticket
     */
    public String text(){
        return TEXT;
    }

    private static String computeText(){

    }

    public int points(StationConnectivity connectivity){

    }

    @Override
    public int compareTo(Ticket that) {
        return 0;
    }


}
