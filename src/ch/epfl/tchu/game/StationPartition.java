package ch.epfl.tchu.game;

public final class StationPartition implements StationConnectivity {

    private StationPartition(int[] repLinks) {
        repLinks = new int[ChMap.stations().size()];
    }

    public final class Builder{

        public Builder(int stationCount){

        }

        public Builder connect(Station s1, Station s2){

        }

        public StationPartition build(){

        }
        private int representative(int station){

           // return representative id of subset that station is in
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



        return false;
    }


}
