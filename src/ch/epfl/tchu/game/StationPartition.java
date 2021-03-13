package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.stream.IntStream;

public final class StationPartition implements StationConnectivity {

//partitions array initialised with their own ids (0-50)
    private int[] partitions = IntStream.range(0, 51).toArray();




    private StationPartition(int[] repLinks) {
        repLinks = IntStream.range(0, repLinks.length).toArray();  ///???????????

    }

    public final class Builder{





        public Builder(int stationCount){
            Preconditions.checkArgument(stationCount>=0);

            int [] idArray = new int[stationCount];

        }

        public Builder connect(Station s1, Station s2){

        }

        public StationPartition build(){

        }
        private int representative(int stationId){

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
                return true;
            }
            return false;
        }

        return true; //because id in bounds of tableau means forcement they're connected????
    }


}
