package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Serdes {
    private Serdes(){}

    public static final Serde<Integer> INTEGER_SERDE = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    public static final Serde<String> STRING_SERDE = Serde.of(
            (string) -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8)),
            (serializedString) -> new String(
                    Arrays.toString(Base64.getDecoder().decode(serializedString)).getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8)
    );

    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    //-------------------------------------------

    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, ",");

    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, ",");

    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, ",");

    public static final Serde<SortedBag<Card>> SB_CARD_SERDE = Serde.bagOf(CARD_SERDE, ",");

    public static final Serde<SortedBag<Ticket>> SB_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, ",");

    public static final Serde<List<SortedBag<Card>>> LIST_SB_CARD_SERDE = Serde.listOf(SB_CARD_SERDE, ";");

    //--------------------------------------------
    public static final Serde<PublicCardState> CARD_STATE_SERDE = Serde.of(
            (publicCardState) -> new StringJoiner(";").add(LIST_CARD_SERDE.serialize(publicCardState.faceUpCards())
                                +INTEGER_SERDE.serialize(publicCardState.deckSize())
                                +INTEGER_SERDE.serialize(publicCardState.discardsSize())).toString(),
            (string) -> string.split(Pattern.quote(";"), -1).
    ); //Not sure on how to go from string to the public card's state attributes here

   public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE;


   public static final Serde<PlayerState> PLAYER_STATE_SERDE;


   public static final Serde<PublicGameState> GAME_STATE_SERDE;



//    Serde<Color> color = Serde.oneOf(Color.ALL);
//    Serde<List<Color>> listOfColor = Serde.listOf(color, "+");
//    Serde<SortedBag<Color>> bagOfColor = Serde.bagOf(color, "+");


}
