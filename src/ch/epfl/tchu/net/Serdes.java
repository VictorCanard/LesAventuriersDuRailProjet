package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Serdes {
    private Serdes(){} //TODO: Do we need a constructor, we didn't use one in Game.java?

    private static final String SEMI_COLON = ";";
    private static final String COMMA = ",";
    private static final String COLON = ":";

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

    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, COMMA);

    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, COMMA);

    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, COMMA);

    public static final Serde<SortedBag<Card>> SB_CARD_SERDE = Serde.bagOf(CARD_SERDE, COMMA);

    public static final Serde<SortedBag<Ticket>> SB_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, COMMA);

    public static final Serde<List<SortedBag<Card>>> LIST_SB_CARD_SERDE = Serde.listOf(SB_CARD_SERDE, SEMI_COLON);

    //--------------------------------------------

    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(

            (publicCardState) -> new StringJoiner(SEMI_COLON).add(LIST_CARD_SERDE.serialize(publicCardState.faceUpCards())
                                                                +INTEGER_SERDE.serialize(publicCardState.deckSize())
                                                                +INTEGER_SERDE.serialize(publicCardState.discardsSize()))
                                                            .toString(),
    //Im assuming we need to create the object??
    //todo: if i understood correctly abcd;efgh;ijk; -> [0] = abcd, [1] = efgh, [2] = ijk  using split
            (string) -> new PublicCardState(LIST_CARD_SERDE.deserialize(string.split(SEMI_COLON, -1)[0]),
                                            INTEGER_SERDE.deserialize(string.split(SEMI_COLON, -1)[1]),
                                            INTEGER_SERDE.deserialize(string.split(SEMI_COLON, -1)[2]))
    );

   public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
           (publicPlayerState) -> new StringJoiner(SEMI_COLON).add(INTEGER_SERDE.serialize(publicPlayerState.ticketCount())
                                                                   +INTEGER_SERDE.serialize(publicPlayerState.cardCount())
                                                                   +LIST_ROUTE_SERDE.serialize(publicPlayerState.routes()))
                                                              .toString(),

           (string) -> new PublicPlayerState(INTEGER_SERDE.deserialize(string.split(SEMI_COLON, -1)[0]),
                                           INTEGER_SERDE.deserialize(string.split(SEMI_COLON, -1)[1]),
                                           LIST_ROUTE_SERDE.deserialize(string.split(SEMI_COLON, -1)[2]))
   );

   public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(

           (playerState) -> new StringJoiner(SEMI_COLON).add(SB_TICKET_SERDE.serialize(playerState.tickets())
                                                            +SB_CARD_SERDE.serialize((playerState.cards()))
                                                            +LIST_ROUTE_SERDE.serialize(playerState.routes()))
                                                        .toString(),

           (string) -> new PlayerState(SB_TICKET_SERDE.deserialize(string.split(SEMI_COLON, -1)[0]),
                                        SB_CARD_SERDE.deserialize(string.split(SEMI_COLON, -1)[1]),
                                        LIST_ROUTE_SERDE.deserialize(string.split(SEMI_COLON, -1)[2]))
           );

   public static final Serde<PublicGameState> GAME_STATE_SERDE = Serde.of(
           (publicGameState) -> new StringJoiner(COLON).add(INTEGER_SERDE.serialize(publicGameState.ticketsCount())
                                                            +PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState())
                                                            +PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId())
                                                            +PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId().next())
                                                            +PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.currentPlayerState())
                                                            +PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(publicGameState.currentPlayerId().next()))
                                                            +PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()))
                                                        .toString(),
//todo: i have doubts about the map
           (string) -> new PublicGameState(INTEGER_SERDE.deserialize(string.split(COLON, -1)[0]),
                                            PUBLIC_CARD_STATE_SERDE.deserialize(string.split(COLON, -1)[1]),
                                            PLAYER_ID_SERDE.deserialize(string.split(COLON, -1)[2]),
                                            Map.of(PLAYER_ID_SERDE.deserialize(string.split(COLON, -1)[2]), PUBLIC_PLAYER_STATE_SERDE.deserialize(string.split(COLON, -1)[4]),
                                                    PLAYER_ID_SERDE.deserialize(string.split(COLON, -1)[3]), PUBLIC_PLAYER_STATE_SERDE.deserialize(string.split(COLON, -1)[5])),
                                            PLAYER_ID_SERDE.deserialize(string.split(COLON, -1)[5]))
           );

//    Serde<Color> color = Serde.oneOf(Color.ALL);
//    Serde<List<Color>> listOfColor = Serde.listOf(color, "+");
//    Serde<SortedBag<Color>> bagOfColor = Serde.bagOf(color, "+");


}
