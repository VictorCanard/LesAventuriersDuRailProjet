package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.stream.IntStream;

/**
 * Describes the player's network of claimed routes as partitions of connected stations
 * @author Anne-Marie Rusu (296098)
 */
public final class StationPartition implements StationConnectivity {
    private final int[] partitions;

    /**
     * Private Builder
     * @param repLinks : links (numbers) pointing to each station's representative
     */
    private StationPartition(int[] repLinks) {
        partitions = repLinks.clone();
    }

    /**
     * Builder for the outer class StationPartition : builds the partitions of stations
     */
    public static final class Builder{
        private int[] partitionsArray;

        /**
         * Constructs an array ranging from 0 included to the station count excluded
         * @param stationCount : the maximum id plus one of the group of stations
         */
        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount>=0);
            partitionsArray = IntStream.range(0, stationCount).toArray();
        }

        /**
         * Connects two stations by assigning to station 2's representative, station 1's representative
         * @param s1 : First station
         * @param s2 : Second station
         * @return this, with the new connection
         */
        public Builder connect(Station s1, Station s2){
            partitionsArray[representative(s2.id())] = representative(s1.id());
            return this;
        }

        /**
         * Flattens the array by having each of the stations point directly to the representative of the subset
         * @return a new StationPartition with a flattened representation of the partitions
         */
        public StationPartition build(){
            for(int i = 0; i< partitionsArray.length; i++){
                partitionsArray[i] = representative(i);
             }
            return new StationPartition(partitionsArray);
        }

        /**
         * Finds the representative associated to this station id.
         * Either it finds it directly or it employs a recursive algorithm to follow the links up to the main representative
         * @param stationId : the id of which we want to know the (in)direct representative
         * @return the id of the station's representative or calls itself again until it finds it
         */
        private int representative(int stationId){
            Preconditions.checkArgument(stationId>=0);
            if(partitionsArray[stationId] == stationId){
                return partitionsArray[stationId];
            }
            return representative(partitionsArray[stationId]);
        }
    }

    /**
     * Verifies if two stations are connected
     * @param s1 : first train station
     * @param s2 : second train station
     * @return : true if they are connected by a (single) players wagons, false otherwise
     */
    @Override
    public boolean connected(Station s1, Station s2) {
        int id1 = s1.id();
        int id2 = s2.id();
        if(id1>= partitions.length || id2>= partitions.length) { // partitions.connected(zur, ZUR);
            if (id1 == id2) { return true;}
            return false;
        }
        if(partitions[id1] == partitions[id2]){ return true;}
        return false;
    }
}
