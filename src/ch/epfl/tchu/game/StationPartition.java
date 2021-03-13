package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.stream.IntStream;

public final class StationPartition implements StationConnectivity {

//partitions array initialised with their own ids (0-50)
    private final int[] PARTITIONS;




    private StationPartition(int[] repLinks) { //array already with the representatives in the entries (idk if flattened or not)
       PARTITIONS = new int[repLinks.length];
        for(int i= 0; i< repLinks.length; i++){
            PARTITIONS[i] = repLinks[i];
        }
    }

    public final class Builder{

        public Builder(int stationCount){ //argument is what was calculated in PlayerState ticketPoints. ex if max station id is 25, gives 26
            Preconditions.checkArgument(stationCount>=0);

            int [] idArray = IntStream.range(0, stationCount).toArray(); //ex now you have a table from 0 to 25 because 26 excluded
        }

        public Builder connect(Station s1, Station s2){ //je ne comprends pas comment choisir "aleatoirement" le representant, cest nous qui choisisons ou cest Random
           int id1 = s1.id();
           int id2 = s2.id();

           //connect one representative to the other (which has to automatically change the representative of all the elements of the subset)

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
    //table 0-50, the largest id is 50????????
    @Override
    public boolean connected(Station s1, Station s2) {
        int id1 = s1.id();
        int id2 = s2.id();

        if(/*id 1 hors tableau ou id2 hors tableau*/){
            if(id1 ==id2){
                return true; //meaning the stations are the same
            }
            return false;
        }else{
            //if representative of id1 and id2 are the same return true
        }

    }


}
