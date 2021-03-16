package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationPartitionTest {


    StationPartition.Builder builder = new StationPartition.Builder(15);
    StationPartition.Builder builderEmpty = new StationPartition.Builder(0);
    StationPartition.Builder builderOne = new StationPartition.Builder(1);


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
    private static final Station FR4 = new Station(50, "France");


    @Test
    void connected() {
        builder.connect(BER, FRI);
        builder.connect(LAU, INT);
        builder.connect(BER, INT);
        builder.connect(ZUR, SOL);
        builder.connect(WAS, SOL);

        StationPartition partition = builder.build();
        int [] expectedArray = {0, 1, 0, 0, 4, 0, 6, 7, 8, 9, 10, 11 , 12, 11, 11};

        assertTrue(partition.connected(BER, LAU));
        assertTrue(!(partition.connected(SOL, BER)));
        assertTrue(partition.connected(FR4, FR4));
        assertTrue(!(partition.connected(FR4, LAU)));
        assertTrue(partition.connected(WAS, ZUR));
    }

    @Test
    void connectedEmpty(){
//you can make an empty array, but then you cant connect anything so whats the point? we dont even have
        //an exception to be thrown in the connect method......
        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            builderEmpty.connect(BER, BER);
        });
        StationPartition partition = builderEmpty.build();
        //expected empty array;
        assertTrue(partition.connected(FRI, FRI));
        assertTrue(!(partition.connected(BER, FRI)));
    }

    @Test
    void connectedOne(){
        builderOne.connect(BER,BER);

        assertThrows(IndexOutOfBoundsException.class, () ->
        {
            builderOne.connect(FRI, BER);
        });

        StationPartition partition = builderOne.build();

        assertTrue(partition.connected(BER, BER));
        assertTrue(partition.connected(ZUR, ZUR));
        assertTrue(!(partition.connected(LAU, SOL)));
    }



}