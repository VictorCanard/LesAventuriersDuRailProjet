package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import java.util.stream.IntStream;

public final class StationPartition implements StationConnectivity {
    private final int[] partitions;

    private StationPartition(int[] repLinks) {
        partitions = repLinks.clone();
    }

    public static final class Builder{
        private int[] partitionsArray;

        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount>=0);
            partitionsArray = IntStream.range(0, stationCount).toArray();
        }

        public Builder connect(Station s1, Station s2){
            partitionsArray[representative(s2.id())] = representative(s1.id());
            return this;
        }

        public StationPartition build(){
            for(int i = 0; i< partitionsArray.length; i++){
                partitionsArray[i] = representative(i);
             }
            return new StationPartition(partitionsArray);
        }

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
        if(id1>= partitions.length || id2>= partitions.length) {
            if (id1 == id2) { return true;}
        }
        if(partitions[id1] == partitions[id2]){ return true;}
        return false;
    }
}
