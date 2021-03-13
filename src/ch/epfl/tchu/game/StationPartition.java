package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.stream.IntStream;

public final class StationPartition implements StationConnectivity {

    private final int[] PARTITIONS;
    private final int STATION_COUNT;


    private StationPartition(int[] repLinks) { //array already with the representatives in the entries (idk if flattened or not)
       PARTITIONS = new int[repLinks.length];
        for(int i= 0; i< repLinks.length; i++){
            PARTITIONS[i] = repLinks[i];
        }

        STATION_COUNT = Builder.theStationCount;
    }


    public static final class Builder{

        private static int theStationCount;

        public Builder(int stationCount){ //argument is what was calculated in PlayerState ticketPoints. ex if max station id is 25, gives 26
            Preconditions.checkArgument(stationCount>=0);
            theStationCount = stationCount;
            int [] idArray = IntStream.range(0, stationCount).toArray(); //ex now you have a table from 0 to 25 because 26 excluded


            //but what if you have stations 0, 24, 25... what about all the other stations and their representatives?

        }


        public Builder connect(Station s1, Station s2){ //je ne comprends pas comment choisir "aleatoirement" le representant, cest nous qui choisisons ou cest Random
           int id1 = s1.id();
           int id2 = s2.id();

           //connect one representative to the other (which has to automatically change the representative of all the elements of the subset)

            return this;
        }

        public StationPartition build(){
            //flattens the representation
            //uses array created in builder i assume
            //method: go in each index and replace the entry: (x) with the entry at index (x)  and repeat until you reach index = entry
            //returns new StationPartition(new array created by method) which is the flattened representation
        }
        private int representative(int stationId){
        //manipulates the deep representation apparently


           return
        }
    }

    /**
     * Verifies if two stations are connected
     * @param s1 : first train station
     * @param s2 : second train station
     * @return : true if they are connected by a (single) players wagons, false otherwise
     */
    //Prenez garde au fait que la méthode connected doit également accepter des gares dont l'identité est hors des
    // bornes du tableau passé à son constructeur. Lorsqu'au moins une des gares qu'on lui passe est ainsi hors bornes,
    // elle ne retourne vrai que si les deux gares ont la même identité.

    @Override
    public boolean connected(Station s1, Station s2) {
        int id1 = s1.id();
        int id2 = s2.id();

        if(id1> STATION_COUNT || id2>STATION_COUNT) {
            if (id1 == id2) {
                return true; //meaning the stations are the same
            }
        }
        if(PARTITIONS[id1] == PARTITIONS[id2]){
                return true;
        }
        return false;
    }
}
