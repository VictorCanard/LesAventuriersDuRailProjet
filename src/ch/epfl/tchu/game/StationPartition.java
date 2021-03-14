package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Random;
import java.util.stream.IntStream;

public final class StationPartition implements StationConnectivity {

    private final int[] PARTITIONS;
    private final int STATION_COUNT;

    private StationPartition(int[] repLinks) {
       PARTITIONS = new int[repLinks.length];
        for(int i= 0; i< repLinks.length; i++){
            PARTITIONS[i] = repLinks[i];
            System.out.print(PARTITIONS[i] + ", ");
        }
        System.out.println("\n*************************");
        STATION_COUNT = Builder.theStationCount;

    }

    public static final class Builder{

        private static int theStationCount;
        private int[] partitionsArray;

        public Builder(int stationCount){ //argument is what was calculated in PlayerState ticketPoints. ex if max station id is 25, gives 26
            Preconditions.checkArgument(stationCount>=0);
            theStationCount = stationCount;
            partitionsArray = IntStream.range(0, stationCount).toArray(); //ex now you have a table from 0 to 25 because 26 excluded

            //for testing only:
            for(int i = 0; i<stationCount; i++){
                System.out.print(partitionsArray[i] + ", ");
            }
            System.out.println("\n-------------------------------");

        }

        public Builder connect(Station s1, Station s2){
           int rep1 = representative(s1.id());
           int rep2 = representative(s2.id());
           Random rand = new Random();
           boolean chosen =  rand.nextBoolean();

           if(chosen == true){
               partitionsArray[rep1] = rep2;
               System.out.println("the chosen representative is: " + rep2 + " :" +s2.name());

           }else{
               partitionsArray[rep2] = rep1;
               System.out.println("the chosen representative is: " + rep1 + " :" +s1.name());
           }
            System.out.println("deep rep after connecting " + s1.name() + " to " + s2.name());
            for(int i = 0; i< partitionsArray.length; i++){
                System.out.print(partitionsArray[i] + ", ");
            }
            System.out.println("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
           return this;
        }

        public StationPartition build(){

            for(int i = 0; i< partitionsArray.length; i++){
                if(i == partitionsArray[i]){
                    continue;
                }else{
                    replaceRepresentative(i, partitionsArray[i]);
                }
             }
            return new StationPartition(partitionsArray);
            //flattens the representation
            //uses array created in builder i assume
            //method: go in each index and replace the entry: (x) with the entry at index (x)  and repeat until you reach index = entry
            //returns new StationPartition(new array created by method) which is the flattened representation

        }
        private int replaceRepresentative(int index, int entry) {
            if( partitionsArray[entry]== entry){
               return partitionsArray[index] = entry;
            }
           return replaceRepresentative(entry, representative(entry));
        }

        private int representative(int stationId){
            Preconditions.checkArgument(stationId>=0);
            return partitionsArray[stationId];
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

        if(id1>STATION_COUNT || id2>STATION_COUNT) {
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
