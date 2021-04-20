package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static org.junit.jupiter.api.Assertions.*;

class SerdesTest {

    //Todo test Empty possibilities


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
    void stringSerdeWorksWithEmptyString(){
        String originalString = "";
        String expectedSerial = "";

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
    void cardSortedBagWorksWithNullArgs(){
        SortedBag<Card> sortedBag = SortedBag.of();

        String serialized = Serdes.SORTED_BAG_CARD_SERDE.serialize(sortedBag);

        SortedBag<Card> deserialized = Serdes.SORTED_BAG_CARD_SERDE.deserialize(serialized);

        assertEquals("", serialized);
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
    void listSortedBagCardsWorksWithNullArguments(){
        SortedBag<Card> sortedBag1 = SortedBag.of();
        SortedBag<Card> sortedBag2 = SortedBag.of();
        SortedBag<Card> sortedBag3 = SortedBag.of();

        List<SortedBag<Card>> sortedBags = List.of(sortedBag1, sortedBag2, sortedBag3 );

        String serialized = Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(sortedBags);

        List<SortedBag<Card>> deserialized = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(serialized);

        assertEquals(";;", serialized);
        assertEquals(sortedBags, deserialized);
    }

    @Test
    void gameStateSerdeWorks20Times(){


        for (int i = 0; i < 20; i++) {
            List<Card> fu = randomFUCards();
            PublicCardState cs = new PublicCardState(fu, ((int) Math.round(Math.random()*50)), ((int) Math.round(Math.random()*30)));

            int firstIndex = ((int) Math.round(Math.random()*80));
            List<Route> rs1 = ChMap.routes().subList(firstIndex, firstIndex + 10);
            Map<PlayerId, PublicPlayerState> ps = Map.of(
                    PLAYER_1, new PublicPlayerState(((int) Math.round(Math.random()*15)), ((int) Math.round(Math.random()*9)), rs1),
                    PLAYER_2, new PublicPlayerState(((int) Math.round(Math.random()*20)), ((int) Math.round(Math.random()*8)), List.of()));
            PublicGameState gs =
                    new PublicGameState(((int) Math.round(Math.random()*40)), cs, PLAYER_2, ps, null);


            String serialized = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs);

            PublicGameState deserialized = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(serialized);

            assertTrue(sameGameState(gs, deserialized));
        }




    }
    private List<Card> randomFUCards(){
        List<Card> cardList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            cardList.add(ALL.get((int) Math.round(Math.random()*8)));
        }
        return cardList;
    }

    @Test
    void gameStateWorksWithNullAttributes(){
        List<Card> fu = List.of(RED, WHITE, BLUE, BLACK, RED);
        PublicCardState cs = new PublicCardState(fu, 0, 0);
        List<Route> rs1 = List.of();
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PLAYER_1, new PublicPlayerState(0, 0, rs1),
                PLAYER_2, new PublicPlayerState(0, 0, List.of()));
        PublicGameState gs =
                new PublicGameState(0, cs, PLAYER_2, ps, null);

        String serialized = Serdes.PUBLIC_GAME_STATE_SERDE.serialize(gs);


        PublicGameState deserialized = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(serialized);

        assertEquals("0:6,7,2,0,6;0;0:1:0;0;:0;0;:", serialized);

        assertEquals(gs.ticketsCount(), deserialized.ticketsCount());
        assertTrue(sameCardState(gs.cardState(), deserialized.cardState()));
        assertEquals(gs.currentPlayerId(), deserialized.currentPlayerId());

        Map<PlayerId, PublicPlayerState> deserializedPs = Map.of(PLAYER_1, deserialized.playerState(PLAYER_1),
                PLAYER_2, deserialized.playerState(PLAYER_2));


        assertTrue(sameGameState(gs, deserialized));
    }
    boolean sameCardState(PublicCardState publicCardState, PublicCardState otherCardState){

        return publicCardState.deckSize() == otherCardState.deckSize()
                && publicCardState.discardsSize() == otherCardState.discardsSize()
                && publicCardState.faceUpCards().equals(otherCardState.faceUpCards());
    }

    boolean samePlayerState(PublicPlayerState first, PublicPlayerState second){
        return first.ticketCount() == second.ticketCount()
                && first.cardCount() == second.cardCount()
                && first.routes().equals(second.routes());
    }
    boolean sameGameState(PublicGameState one, PublicGameState two){
        return sameCardState(one.cardState(), two.cardState())
                && one.ticketsCount() == two.ticketsCount()
                && one.currentPlayerId() == two.currentPlayerId()
                && samePlayerState(one.playerState(PLAYER_1), two.playerState(PLAYER_1))
                && samePlayerState(one.playerState(PLAYER_2), two.playerState(PLAYER_2))
                && one.lastPlayer() == two.lastPlayer();
    }

}