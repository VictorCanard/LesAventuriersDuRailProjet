package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

class SerdesTest {




    @Test
    void stringSerdeWorks(){
        String originalString = "Charles";
        String expectedSerial = "Q2hhcmxlcw==";

        String serialized = Serdes.STRING_SERDE.serialize(originalString);
        String deserialized = Serdes.STRING_SERDE.deserialize(serialized);

        assertEquals(expectedSerial, serialized);
        assertEquals(originalString, deserialized);


    }

    @Test
    void turnKindSerdeWorks(){
        Serde<Player.TurnKind> turnKindSerde = Serdes.TURN_KIND_SERDE;

        for (Player.TurnKind turnKind : Player.TurnKind.values()) {
            String serialized = turnKindSerde.serialize(turnKind);

            assertEquals(turnKind.ordinal(),Integer.valueOf(serialized));

            Player.TurnKind deserialized = turnKindSerde.deserialize(serialized);
            assertEquals(turnKind, deserialized);
        }
    }

    @Test
    void cardSortedBagWorks(){
        SortedBag<Card> sortedBag = SortedBag.of(List.of(RED, WHITE, BLUE, BLACK, RED));

        String serialized = Serdes.SORTED_BAG_CARD_SERDE.serialize(sortedBag);

        SortedBag<Card> deserialized = Serdes.SORTED_BAG_CARD_SERDE.deserialize(serialized);

        assertEquals("0,2,6,6,7", serialized);
        assertEquals(sortedBag.toList(), deserialized.toList());

    }

    @Test
    void listSortedBagCardsWorks(){
        SortedBag<Card> sortedBag1 = SortedBag.of(List.of(RED, WHITE, BLUE, BLACK, RED));
        SortedBag<Card> sortedBag2 = SortedBag.of(List.of(YELLOW, BLUE, BLUE, BLACK, RED));
        SortedBag<Card> sortedBag3 = SortedBag.of(List.of(RED, WHITE, LOCOMOTIVE, BLACK, LOCOMOTIVE));

        List<SortedBag<Card>> sortedBags = List.of(sortedBag1, sortedBag2, sortedBag3 );

        String serialized = Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(sortedBags);

        List<SortedBag<Card>> deserialized = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(serialized);

        assertEquals("0,2,6,6,7;0,2,2,4,6;0,6,7,8,8", serialized);
        assertEquals(sortedBags, deserialized);
    }

    @Test
    void serdesWorksWithTeacherExample(){
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PLAYER_2, ps, null);

        String serialized = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs);

        PublicGameState deserialized = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(serialized);

        assertEquals("40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;:", serialized);

        boolean isTheSame = Arrays.stream(gs.getClass().getDeclaredFields()).allMatch(field -> Arrays.asList(GameState.class.getDeclaredFields()).contains(field));
        assertTrue(isTheSame);


    }

}