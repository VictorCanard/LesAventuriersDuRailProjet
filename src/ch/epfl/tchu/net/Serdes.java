package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Pattern;


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


//    public static final Serde<String> STRING_SERDE = Serde.of;
//    public static final Serde<String> STRING_SERDE = Serde.of;

      public static final Serde<Card> CARD_SERDE = Serde.oneOf(List.of(Card.values()));

//    public static final Serde<String> STRING_SERDE = Serde.of;
//    public static final Serde<String> STRING_SERDE = Serde.of;

      public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    public static final Serde<SortedBag<Card>> LIST_STRING_SERDE = Serde.bagOf(CARD_SERDE, ",");
    public static final Serde<SortedBag<Ticket>> LIST_CARD_SERDE = Serde.bagOf(TICKET_SERDE, ",");
    public static final Serde<List<SortedBag<Card>>> LIST_ROUTE_SERDE = Serde.listOf(LIST_STRING_SERDE, ";");

//    public static final Serde<String> STRING_SERDE = Serde.of;
    //    public static final Serde<String> STRING_SERDE = Serde.of;
//    public static final Serde<String> STRING_SERDE = Serde.of;


    public static final Serde<PublicCardState> CARD_STATE_SERDE = Serde.of(
            (publicCardState) -> new StringJoiner(";").add(LIST_CARD_SERDE.serialize(publicCardState.faceUpCards())
                                +INTEGER_SERDE.serialize(publicCardState.deckSize())
                                +INTEGER_SERDE.serialize(publicCardState.discardsSize())).toString(),
            (string) -> string.split(Pattern.quote(";"), -1).
    ); //Not sure on how to go from string to the public card's state attributes here

    public static final Serde<PublicGameState> GAME_STATE_SERDE = Serde.;



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
