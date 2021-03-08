package ch.epfl.tchu.gui;

import ch.epfl.RouteTestMap;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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




    @Test
    void cardName() {
        Card testCard = Card.BLACK;
        String expectedName = "noire";
        String actualName = Info.cardName(testCard, 1);

        assertEquals(expectedName,actualName);
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
    }

    @Test
    void claimedRoute() { //?????
        List<Card> cards = Card.ALL;
        String expected = String.format(CLAIMED_ROUTE, playerOneName, RouteTestMap.route1.toString(), "1 carte noire et 1 carte violette");
        String actual = playerOneInfo.claimedRoute(RouteTestMap.route1, SortedBag.of(cards.subList(0,2)));

        assertEquals(expected,actual);
    }

    @Test
    void attemptsTunnelClaim() {
        List<Card> cards = Card.ALL;

        String expected = String.format(ATTEMPTS_TUNNEL_CLAIM, playerOneName, RouteTestMap.route1, "1 noire, et 1 violette");
        String actual = playerOneInfo.attemptsTunnelClaim(RouteTestMap.route1, SortedBag.of(cards.subList(0,2)));


        assertEquals(expected,actual);

    }

    @Test
    void drewAdditionalCards() {
    }

    @Test
    void didNotClaimRoute() {
    }

    @Test
    void lastTurnBegins() {
    }

    @Test
    void getsLongestTrailBonus() {
    }

    @Test
    void won() {
    }
}