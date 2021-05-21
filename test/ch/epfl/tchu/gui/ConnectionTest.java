package ch.epfl.tchu.gui;


import javafx.application.Platform;
import org.junit.jupiter.api.Test;

import static ch.epfl.tchu.gui.Menu.number_of_players;

public class ConnectionTest {
    @Test
    void runNClientsAndOneServer(){
        new Thread(()->ServerMain.main(new String[0])).start();
        for (int i = 1; i < number_of_players; i++) {
            Platform.runLater(()-> ClientMain.main(new String[0]));
        }
    }
}
