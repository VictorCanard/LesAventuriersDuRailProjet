package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Color;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;


public class Serdes {
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(
            i -> Integer.toString(i),
            Integer::parseInt);


    public static final Serde<String> STRING_SERDE = Serde.of(
            (string) -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
            (serializedString) -> new String(
                    Arrays.toString(Base64.getDecoder().decode(serializedString)).getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8)
    );


//    Serde<Color> color = Serde.oneOf(Color.ALL);
//    Serde<List<Color>> listOfColor = Serde.listOf(color, "+");
//    Serde<SortedBag<Color>> bagOfColor = Serde.bagOf(color, "+");

    //Type	Séparateur	Liste de valeurs
    //String
    //PlayerId	 	PlayerId.ALL
    //TurnKind	 	TurnKind.ALL
    //Card	 	Card.ALL
    //Route	 	ChMap.routes()
    //Ticket	 	ChMap.tickets()
    //List<String>	virgule (,)
    //List<Card>	virgule (,)
    //List<Route>	virgule (,)
    //SortedBag<Card>	virgule (,)
    //SortedBag<Ticket>	virgule (,)
    //List<SortedBag<Card>>	point-virgule (;)
    //PublicCardState	point-virgule (;)
    //PublicPlayerState	point-virgule (;)
    //PlayerState	point-virgule (;)
    //PublicGameState	deux-points (:)
}
