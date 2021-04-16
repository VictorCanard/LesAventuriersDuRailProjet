package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Color;

import java.util.List;

public class Serdes {
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);

//    Serde<Color> color = Serde.oneOf(Color.ALL);
//    Serde<List<Color>> listOfColor = Serde.listOf(color, "+");
//    Serde<SortedBag<Color>> bagOfColor = Serde.bagOf(color, "+");
}
