package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class Serdes {
    private Serdes() {
    }

    private static final String SEMI_COLON = ";";
    private static final String COMMA = ",";
    private static final String COLON = ":";

    private static final String SEMI_COLON_PATTERN = Pattern.quote(";");
    private static final String COLON_PATTERN = Pattern.quote(":");

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Serde of an integer
     */
    public static final Serde<Integer> INTEGER_SERDE = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    /**
     * Serde of a string
     */
    public static final Serde<String> STRING_SERDE = Serde.of(
            (string) -> Base64.getEncoder().encodeToString(string.getBytes(UTF_8)),

            serializedString -> new String(
                    Base64.getDecoder().decode(serializedString),
                    UTF_8)
    );
    /**
     * Serde of a player id
     */
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde of the kind of action a player can take on their turn
     */
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);
    /**
     * Serde of a card
     */
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);
    /**
     * Serde of a route
     */
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    /**
     * Serde of a ticket
     */
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    //-------------------------------------------

    /**
     * Serde of a list of strings
     */
    public static final Serde<List<String>> LIST_STRING_SERDE = Serde.listOf(STRING_SERDE, COMMA);
    /**
     * Serde of a list of cards
     */
    public static final Serde<List<Card>> LIST_CARD_SERDE = Serde.listOf(CARD_SERDE, COMMA);
    /**
     * Serde of a list of routes
     */
    public static final Serde<List<Route>> LIST_ROUTE_SERDE = Serde.listOf(ROUTE_SERDE, COMMA);
    /**
     * Serde of a sorted bag of cards
     */
    public static final Serde<SortedBag<Card>> SORTED_BAG_CARD_SERDE = Serde.bagOf(CARD_SERDE, COMMA);
    /**
     * Serde of a sorted bag of tickets
     */
    public static final Serde<SortedBag<Ticket>> SORTED_BAG_TICKET_SERDE = Serde.bagOf(TICKET_SERDE, COMMA);
    /**
     * Serde of a list of sorted bags of cards
     */
    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(SORTED_BAG_CARD_SERDE, SEMI_COLON);

    //--------------------------------------------
    /**
     * Serde of a public card state
     */
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(

            (publicCardState) -> new StringJoiner(SEMI_COLON)
                    .add(LIST_CARD_SERDE.serialize(publicCardState.faceUpCards()))
                    .add(INTEGER_SERDE.serialize(publicCardState.deckSize()))
                    .add(INTEGER_SERDE.serialize(publicCardState.discardsSize()))
                    .toString(),

            (string) -> {
                String[] splitString = string.split(SEMI_COLON_PATTERN, -1);
                return new PublicCardState(
                        LIST_CARD_SERDE.deserialize(splitString[0]),
                        INTEGER_SERDE.deserialize(splitString[1]),
                        INTEGER_SERDE.deserialize(splitString[2]));
            });

    /**
     * Serde of a public player state
     */
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(
            (publicPlayerState) -> new StringJoiner(SEMI_COLON)
                    .add(INTEGER_SERDE.serialize(publicPlayerState.ticketCount()))
                    .add(INTEGER_SERDE.serialize(publicPlayerState.cardCount()))
                    .add(LIST_ROUTE_SERDE.serialize(publicPlayerState.routes()))
                    .toString(),

            (string) -> {
                String[] splitString = string.split(SEMI_COLON_PATTERN, -1);
                return new PublicPlayerState(
                        INTEGER_SERDE.deserialize(splitString[0]),
                        INTEGER_SERDE.deserialize(splitString[1]),
                        LIST_ROUTE_SERDE.deserialize(splitString[2]));
            }
    );
    /**
     * Serde of a player state
     */
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(

            (playerState) -> new StringJoiner(SEMI_COLON)
                    .add(SORTED_BAG_TICKET_SERDE.serialize(playerState.tickets()))
                    .add(SORTED_BAG_CARD_SERDE.serialize((playerState.cards())))
                    .add(LIST_ROUTE_SERDE.serialize(playerState.routes()))
                    .toString(),

            (string) -> {
                String[] splitString = string.split(SEMI_COLON_PATTERN, -1);
                return new PlayerState(
                        SORTED_BAG_TICKET_SERDE.deserialize(splitString[0]),
                        SORTED_BAG_CARD_SERDE.deserialize(splitString[1]),
                        LIST_ROUTE_SERDE.deserialize(splitString[2]));
            });
    /**
     * Serde of a public game state
     */
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            (publicGameState) -> new StringJoiner(COLON)
                    .add(INTEGER_SERDE.serialize(publicGameState.ticketsCount()))
                    .add(PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()))
                    .add(PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()))
                    .add(PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)))
                    .add(PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)))
                    .add(PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer()))
                    .toString(),

            (serializedString) -> {
                String[] splitString = serializedString.split(COLON_PATTERN, -1);

                PlayerId currentPlayer = PLAYER_ID_SERDE.deserialize(splitString[2]);

                return new PublicGameState(
                        INTEGER_SERDE.deserialize(splitString[0]),
                        PUBLIC_CARD_STATE_SERDE.deserialize(splitString[1]),
                        currentPlayer,
                        Map.of(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(splitString[3]),
                                PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE_SERDE.deserialize(splitString[4])),
                        PLAYER_ID_SERDE.deserialize(splitString[5]));
            }
    );
}
