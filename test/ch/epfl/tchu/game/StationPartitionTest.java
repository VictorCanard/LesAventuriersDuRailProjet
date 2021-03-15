package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationPartitionTest {

    int [] actualFlattenedArray = {3,1,3,3,4,3,13,7,7,13,7,13,12,13,14};
    StationPartition.Builder builder = new StationPartition.Builder(15);

    private static final Station BER = new Station(0, "Berne");
    private static final Station DEL = new Station(1, "Delémont");
    private static final Station FRI = new Station(2, "Fribourg");
    private static final Station INT = new Station(3, "Interlaken");
    private static final Station LCF = new Station(4, "La Chaux-de-Fonds");
    private static final Station LAU = new Station(5, "Lausanne");
    private static final Station LUC = new Station(6, "Lucerne");
    private static final Station NEU = new Station(7, "Neuchâtel");
    private static final Station OLT = new Station(8, "Olten");
    private static final Station SCZ = new Station(9, "Schwyz");
    private static final Station SOL = new Station(10, "Soleure");
    private static final Station WAS = new Station(11, "Wassen");
    private static final Station YVE = new Station(12, "Yverdon");
    private static final Station ZOU = new Station(13, "Zoug");
    private static final Station ZUR = new Station(14, "Zürich");

    @Test
    void myTest(){
        builder.connect(BER, FRI);
        builder.connect(LAU, INT);
        builder.connect(BER, INT);


        StationPartition partition = builder.build();
        assertTrue(partition.connected(BER, LAU));
        assertTrue(!(partition.connected(SOL, BER)));
    }

    @Test
    void connected() {

    }
}