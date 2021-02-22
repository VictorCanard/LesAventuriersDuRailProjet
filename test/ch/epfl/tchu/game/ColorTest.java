package ch.epfl.tchu.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorTest {
    @Test
    void colorValuesAreDefinedInTheRightOrder() {
        var expectedValues = new Color[]{
                Color.BLACK, Color.VIOLET, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED, Color.WHITE
        };
        assertArrayEquals(expectedValues, Color.values());
    }

    @Test
    void colorAllIsDefinedCorrectly() {
        Assertions.assertEquals(List.of(Color.values()), Color.ALL);
    }

    @Test
    void colorCountIsDefinedCorrectly() {
        Assertions.assertEquals(8, Color.COUNT);
    }
}