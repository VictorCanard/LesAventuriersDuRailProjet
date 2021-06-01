package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.ServerMain;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerIdTest {
    @Test
    void playerIdAllIsDefinedCorrectly() {
        assertEquals(List.of(PlayerId.PLAYER_1, PlayerId.PLAYER_2), ServerMain.activePlayers);
    }

    @Test
    void playerIdNextWorks() {
        assertEquals(PlayerId.PLAYER_2, PlayerId.PLAYER_1.next());
        assertEquals(PlayerId.PLAYER_1, PlayerId.PLAYER_2.next());
    }
}