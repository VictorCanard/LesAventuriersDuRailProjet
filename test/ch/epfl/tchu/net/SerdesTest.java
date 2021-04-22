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
    void integerSerdeWorks(){
    int initial = 12345;
    String ser = "12345";
    String serialized = Serdes.INTEGER_SERDE.serialize(initial);
    int deserialized = Serdes.INTEGER_SERDE.deserialize(serialized);
    assertEquals(initial, deserialized);
    assertEquals(serialized, ser);
    }

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
    void stringSerdeWorksWithEmptyString() {
        String originalString = "";
        String expectedSerial = "";

        String serialized = Serdes.STRING_SERDE.serialize(originalString);
        String deserialized = Serdes.STRING_SERDE.deserialize(serialized);

        assertEquals(expectedSerial, serialized);
        assertEquals(originalString, deserialized);
    }

    @Test
    void nullPlayerIdSerdeWorks(){
        PlayerId original = null;
        String ser = "" ;
        String serialized = Serdes.PLAYER_ID_SERDE.serialize(original);
        PlayerId deserialized = Serdes.PLAYER_ID_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(serialized, ser);
    }

    @Test
    void playerIdSerdeWorks(){
        for (PlayerId playerId: PlayerId.values()) {
            String serialized = Serdes.PLAYER_ID_SERDE.serialize(playerId);

            PlayerId deserialized = Serdes.PLAYER_ID_SERDE.deserialize(serialized);

            assertEquals(serialized, String.valueOf(playerId.ordinal()));
            assertEquals(playerId, deserialized);
        }
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
    void cardSerdeWorks(){
        Serde<Card> cardSerde = Serdes.CARD_SERDE;

        for (Card card: Card.values()
             ) {
            String serialized = cardSerde.serialize(card);

            Card deserialized = cardSerde.deserialize(serialized);

            assertEquals(serialized, String.valueOf(card.ordinal()));
            assertEquals(deserialized, card);
        }


    }

    @Test
    void routeSerdeWorks(){
        Serde<Route> routeSerde = Serdes.ROUTE_SERDE;

        for (Route route: ChMap.routes()
        ) {
            String serialized = routeSerde.serialize(route);

            Route deserialized = routeSerde.deserialize(serialized);

            assertEquals(serialized, String.valueOf(ChMap.routes().indexOf(route)));
            assertEquals(deserialized, route);
        }

    }

    @Test
    void ticketSerdeWorks(){
        Serde<Ticket> ticketSerde = Serdes.TICKET_SERDE;

        for (Ticket ticket : ChMap.tickets()
        ) {
            String serialized = ticketSerde.serialize(ticket);

            Ticket deserialized = ticketSerde.deserialize(serialized);

            assertEquals(serialized, String.valueOf(ChMap.tickets().indexOf(ticket)));
            assertEquals(deserialized, ticket);
        }

    }

    @Test
    void routeSerdeWorks(){
        Route original = ChMap.routes().get(0);
        String serialized = Serdes.ROUTE_SERDE.serialize(original);
        Route deserialized = Serdes.ROUTE_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);

    }
    @Test
    void ticketSerdeWorks(){
        Ticket original = ChMap.tickets().get(41); //index (38,39) -> 38, (40,41) -> 40 etc up to 45 (country tickets where there are 2 of each kind)
        String ser = "40";
        String serialized = Serdes.TICKET_SERDE.serialize(original);
        Ticket deserialized = Serdes.TICKET_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);

    }
    @Test
    void listStringSerdeWorks(){
        List<String> original = List.of("Charles", "Charles", "Charles");
        String ser = "Q2hhcmxlcw==,Q2hhcmxlcw==,Q2hhcmxlcw==";
        String serialized = Serdes.LIST_STRING_SERDE.serialize(original);
        List<String> deserialized = Serdes.LIST_STRING_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);
    }
    @Test
    void emptyListStringSerdeWorks(){
        List<String> original = List.of();
        String ser = "";
        String serialized = Serdes.LIST_STRING_SERDE.serialize(original);
        List<String> deserialized = Serdes.LIST_STRING_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);
    }

    @Test
    void listCardSerdeWorks(){
        List<Card> original = List.of(RED, WHITE, BLUE, BLACK, RED);
        String ser = "6,7,2,0,6";
        String serialized = Serdes.LIST_CARD_SERDE.serialize(original);
        List<Card> deserialized = Serdes.LIST_CARD_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);
    }
    @Test
    void emptyListCardSerdeWorks(){
        List<Card> original = List.of();
        String ser = "";
        String serialized = Serdes.LIST_CARD_SERDE.serialize(original);
        List<Card> deserialized = Serdes.LIST_CARD_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);
    }
    @Test
    void listRouteSerdeWorks(){
        List<Route> original = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(5));
        String ser = "0,1,5";
        String serialized = Serdes.LIST_ROUTE_SERDE.serialize(original);
        List<Route> deserialized = Serdes.LIST_ROUTE_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);
    }
    @Test
    void emptyListRouteSerdeWorks(){
        List<Route> original = List.of();
        String ser = "";
        String serialized = Serdes.LIST_ROUTE_SERDE.serialize(original);
        List<Route> deserialized = Serdes.LIST_ROUTE_SERDE.deserialize(serialized);
        assertEquals(original, deserialized);
        assertEquals(ser, serialized);
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
    void ticketSortedBagSerdeWorks(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(5)));

        String serialized = Serdes.SORTED_BAG_TICKET_SERDE.serialize(tickets);
        SortedBag<Ticket> deserialized = Serdes.SORTED_BAG_TICKET_SERDE.deserialize(serialized);
//todo: why serialized as 5,0,1 and not 0,1,5 ?
        assertEquals(tickets, deserialized);

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

    @Test
    void playerStateSerdeWorks(){
        SortedBag<Ticket> tickets = SortedBag.of(List.of(ChMap.tickets().get(0), ChMap.tickets().get(1), ChMap.tickets().get(5)));
        SortedBag<Card> cards = SortedBag.of(List.of(RED, WHITE, BLUE, BLACK, RED));
        List<Route> routes = List.of(ChMap.routes().get(0));

        PlayerState original = new PlayerState(tickets, cards, routes);
        String ser = "5,0,1;0,2,6,6,7;0";
//todo: see ticketSortedBagSerdeWorks
        String serialized = Serdes.PLAYER_STATE_SERDE.serialize(original);
        PlayerState deserialized = Serdes.PLAYER_STATE_SERDE.deserialize(serialized);

        assertEquals(ser, serialized);
        assertEquals(original.tickets(), deserialized.tickets());
        assertEquals(original.cards(), deserialized.cards());
        assertEquals(original.routes(), deserialized.routes());
}

}