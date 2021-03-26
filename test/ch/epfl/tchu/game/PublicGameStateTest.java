package ch.epfl.tchu.game;

import ch.epfl.tchu.ChMapTest;

import ch.epfl.tchu.RouteTestMap;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class PublicGameStateTest implements ChMapTest, RouteTestMap {

//Everything ok
    List<Card> faceUpCards1 = new ArrayList<>(List.of(Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.BLUE, Card.RED));
    List<Card> pioche = new ArrayList<>(List.of(Card.BLUE, Card.BLACK, Card.LOCOMOTIVE,Card.BLACK, Card.BLUE, Card.LOCOMOTIVE, Card.BLUE, Card.RED ));
    List<Card> discards = new ArrayList<>(List.of(Card.BLUE));
    PublicCardState cardState1 = new PublicCardState(faceUpCards1, pioche.size(), discards.size());

    PublicPlayerState playerState1= new PublicPlayerState(1, 4, List.of(RouteTestMap.route1, RouteTestMap.route8));
    PublicPlayerState playerState2= new PublicPlayerState(2, 5, List.of(RouteTestMap.route3, RouteTestMap.route4));

    Map<PlayerId, PublicPlayerState> playerSM1 = new TreeMap<>(){{
        put(PlayerId.PLAYER_1, playerState1);
        put(PlayerId.PLAYER_2, playerState2);
    }};
    Map<PlayerId, PublicPlayerState> playerSMFAIL = new TreeMap<>(){{
        put(PlayerId.PLAYER_1, playerState1);
    }};

    PublicGameState publicGameStateOK = new PublicGameState(6, cardState1, PlayerId.PLAYER_1,playerSM1, null);

    PublicCardState emptyPiocheCS = new PublicCardState(faceUpCards1, 0, 3);//because condition is pioche and discards to have at least 5 between them
    PublicGameState pGSEmptyPioche = new PublicGameState(6, emptyPiocheCS, PlayerId.PLAYER_1,playerSM1, null);

    @Test
    void ConstructorWorks(){
    //tickets are negative
        assertThrows(IllegalArgumentException.class, ()-> new PublicGameState(-5, cardState1, PlayerId.PLAYER_1,playerSM1, null));
    //Map has only 1 pair
        assertThrows(IllegalArgumentException.class, ()-> new PublicGameState(2, cardState1, PlayerId.PLAYER_1,playerSMFAIL, null));
    //NULL
        assertThrows(NullPointerException.class, ()-> new PublicGameState(0, null, PlayerId.PLAYER_1,playerSM1, null));
        assertThrows(NullPointerException.class, ()-> new PublicGameState(0, cardState1, null,playerSM1, null));
        assertThrows(NullPointerException.class, ()-> new PublicGameState(0, cardState1, PlayerId.PLAYER_1,null, null));
    }
    @Test
    void ticketsCount() {
        assertEquals(6, publicGameStateOK.ticketsCount());
    }

    @Test
    void canDrawTickets() {
        assertTrue(publicGameStateOK.canDrawTickets());
        assertFalse(new PublicGameState(0, emptyPiocheCS, PlayerId.PLAYER_1,playerSM1, null).canDrawTickets());

    }

    @Test
    void cardState() {
       PublicCardState publicCardState =  publicGameStateOK.cardState();
       assertEquals(Card.BLACK, publicCardState.faceUpCard(0));
       assertEquals(8, publicCardState.deckSize());
       assertEquals(1, publicCardState.discardsSize());
    }

    @Test
    void canDrawCards() {
        assertTrue(publicGameStateOK.canDrawCards());
        assertFalse(pGSEmptyPioche.canDrawCards());
    }

    @Test
    void currentPlayerId() {
        assertEquals(PlayerId.PLAYER_1, publicGameStateOK.currentPlayerId());
    }

    @Test
    void playerState() {
        PublicPlayerState ps = publicGameStateOK.playerState(PlayerId.PLAYER_1);
        assertEquals(List.of(RouteTestMap.route1, RouteTestMap.route8), ps.routes());
        assertEquals(1, ps.ticketCount());
        assertEquals(4, ps.cardCount());
    }

    @Test
    void currentPlayerState() {
        PublicPlayerState ps = publicGameStateOK.currentPlayerState();
        assertEquals(List.of(RouteTestMap.route1, RouteTestMap.route8), ps.routes());
        assertEquals(1, ps.ticketCount());
        assertEquals(4, ps.cardCount());
    }

    @Test
    void claimedRoutes() {
       List<Route> claimedRoutes =  publicGameStateOK.claimedRoutes();
        assertEquals(4, claimedRoutes.size());

    }

    @Test
    void lastPlayer() {
        assertNull(publicGameStateOK.lastPlayer());
    }
}