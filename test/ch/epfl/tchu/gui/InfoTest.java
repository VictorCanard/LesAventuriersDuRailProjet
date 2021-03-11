package ch.epfl.tchu.gui;

import ch.epfl.RouteTestMap;
import ch.epfl.TestMap;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.*;

class InfoTest {
    private static String playerOneName = "Ethan";
    private static Info playerOneInfo = new Info(playerOneName);
    private final static List<String> listOfAllCards =
            List.of(StringsFr.BLACK_CARD,
                    StringsFr.BLUE_CARD,
                    StringsFr.GREEN_CARD,
                    StringsFr.ORANGE_CARD,
                    StringsFr.RED_CARD,
                    StringsFr.VIOLET_CARD,
                    StringsFr.WHITE_CARD,
                    StringsFr.YELLOW_CARD,
                    StringsFr.LOCOMOTIVE_CARD);
    private final static List<Card> cards = Card.ALL;
    private final static SortedBag<Card> bagOfCards = SortedBag.of(cards.subList(0,2));




    @Test
    void cardName() {
        Card testCard = Card.BLACK;
        String expectedName = "noire";
        String actualName = Info.cardName(testCard, 1);

        assertEquals(expectedName,actualName);

        Card testCard2 = Card.LOCOMOTIVE;
        String expectedName2 = "locomotive";
        String actualName2 = Info.cardName(testCard2, 1);

        assertEquals(expectedName2,actualName2);
    }

    @Test
    void draw() {
        List<String> playerNames = List.of("Ethan","Boyde");
        Info.draw(playerNames, 50);
        String expectedMessage = "\nEthan et Boyde sont ex æqo avec 50 points !\n";
        String actualMessage = Info.draw(playerNames, 50);

        assertEquals(expectedMessage,actualMessage);
    }

    @Test
    void willPlayFirst() {
        String expected = String.format(WILL_PLAY_FIRST, playerOneName);
        String actual = playerOneInfo.willPlayFirst();

        assertEquals(expected,actual);
    }

    public static final String WILL_PLAY_FIRST =
            "%s jouera en premier.\n\n";
    public static final String KEPT_N_TICKETS =
            "%s a gardé %s billet%s.\n";
    public static final String CAN_PLAY =
            "\nC'est à %s de jouer.\n";
    public static final String DREW_TICKETS =
            "%s a tiré %s billet%s...\n";
    public static final String DREW_BLIND_CARD =
            "%s a tiré une carte de la pioche.\n";
    public static final String DREW_VISIBLE_CARD =
            "%s a tiré une carte %s visible.\n";
    public static final String CLAIMED_ROUTE =
            "%s a pris possession de la route %s au moyen de %s.\n";
    public static final String ATTEMPTS_TUNNEL_CLAIM =
            "%s tente de s'emparer du tunnel %s au moyen de %s !\n";
    public static final String ADDITIONAL_CARDS_ARE =
            "Les cartes supplémentaires sont %s. ";
    public static final String NO_ADDITIONAL_COST =
            "Elles n'impliquent aucun coût additionnel.\n";
    public static final String SOME_ADDITIONAL_COST =
            "Elles impliquent un coût additionnel de %s carte%s.\n";
    public static final String DID_NOT_CLAIM_ROUTE =
            "%s n'a pas pu (ou voulu) s'emparer de la route %s.\n";
    public static final String LAST_TURN_BEGINS =
            "\n%s n'a plus que %s wagon%s, le dernier tour commence !\n";
    public static final String GETS_BONUS =
            "\n%s reçoit un bonus de 10 points pour le plus long trajet (%s).\n";
    public static final String WINS =
            "\n%s remporte la victoire avec %s point%s, contre %s point%s !\n";
    @Test
    void keptTickets() {
        String expected = String.format(KEPT_N_TICKETS, playerOneName, 5, "s");
        String actual = playerOneInfo.keptTickets(5);

        assertEquals(expected,actual);

        String expected2 = String.format(KEPT_N_TICKETS, playerOneName, 1, "");
        String actual2 = playerOneInfo.keptTickets(1);

        assertEquals(expected2,actual2);
    }

    @Test
    void canPlay() {
        String expected = String.format(CAN_PLAY, playerOneName);
        String actual = playerOneInfo.canPlay();

        assertEquals(expected,actual);
    }

    @Test
    void drewTickets() {
        String expected = String.format(DREW_TICKETS, playerOneName, 5, "s");
        String actual = playerOneInfo.drewTickets(5);

        assertEquals(expected,actual);
        String expected2 = String.format(DREW_TICKETS, playerOneName, 1, "");
        String actual2 = playerOneInfo.drewTickets(1);

        assertEquals(expected2,actual2);
    }

    @Test
    void drewBlindCard() {
        String expected = String.format(DREW_BLIND_CARD, playerOneName);
        String actual = playerOneInfo.drewBlindCard();

        assertEquals(expected,actual);
    }

    @Test
    void drewVisibleCard() {

        String expected = String.format(DREW_VISIBLE_CARD, playerOneName, listOfAllCards.get(0));
        String actual = playerOneInfo.drewVisibleCard(Card.BLACK);

        assertEquals(expected,actual);

        String expected2 = String.format(DREW_VISIBLE_CARD, playerOneName, listOfAllCards.get(8));
        String actual2 = playerOneInfo.drewVisibleCard(Card.LOCOMOTIVE);

        assertEquals(expected2,actual2);
    }

    @Test
    void claimedRoute() {
        List<Card> cards = Card.ALL;
        String expected = String.format(CLAIMED_ROUTE, playerOneName, RouteTestMap.route1.toString(), "1 noire, et 1 violette");
        String actual = playerOneInfo.claimedRoute(RouteTestMap.route1, SortedBag.of(cards.subList(0,2)));

        assertEquals(expected,actual);
    }

    @Test
    void attemptsTunnelClaim() {
        List<Card> cards = Card.ALL;

        String expected = String.format(ATTEMPTS_TUNNEL_CLAIM, playerOneName, RouteTestMap.route1, "1 noire, 1 violette, et 1 bleue");
        String actual = playerOneInfo.attemptsTunnelClaim(RouteTestMap.route1, SortedBag.of(cards.subList(0,3)));


        assertEquals(expected,actual);

    }

    @Test
    void drewAdditionalCards() {

        String expected1 = String.format(ADDITIONAL_CARDS_ARE, "1 noire, et 1 violette");
        String expected2 = String.format(SOME_ADDITIONAL_COST, 2, "s");
        String expectedFinal = String.format("%s%s", expected1,expected2);
        String actual = playerOneInfo.drewAdditionalCards(bagOfCards, 2);

        assertEquals(expectedFinal,actual);

        String expected3 = String.format(NO_ADDITIONAL_COST);
        String expectedFinal2 = String.format("%s%s", expected1,expected3);
        String actual2 = playerOneInfo.drewAdditionalCards(bagOfCards, 0);

        assertEquals(expectedFinal2,actual2);
    }

    @Test
    void didNotClaimRoute() {
        String expected = String.format(DID_NOT_CLAIM_ROUTE, playerOneName, RouteTestMap.route1);

        String actual = playerOneInfo.didNotClaimRoute(RouteTestMap.route1);

        assertEquals(expected,actual);
    }

    @Test
    void lastTurnBegins() {
        String expected = String.format(LAST_TURN_BEGINS, playerOneName, 5, "s");

        String actual = playerOneInfo.lastTurnBegins(5);

        assertEquals(expected,actual);
    }

    @Test
    void getsLongestTrailBonus() {
        String expected = String.format(GETS_BONUS, playerOneName, "Yverdon"+ StringsFr.EN_DASH_SEPARATOR+"Lucerne");

        String actual = playerOneInfo.getsLongestTrailBonus(TestMap.testTrail1);

        assertEquals(expected,actual);
    }

    @Test
    void won() {
        String expected = String.format(WINS, playerOneName, 15,"s",  10,"s");

        String actual = playerOneInfo.won(15, 10);

        assertEquals(expected,actual);
        String expected2 = String.format(WINS, playerOneName, 1,"",  0,"s");

        String actual2 = playerOneInfo.won(1, 0);

        assertEquals(expected2,actual2);
    }
}