package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

public final class Info {
    public Info(String playerName){}
    public static String cardName(Card card, int count){}
            //(utilise BLACK_CARD, BLUE_CARD, etc.),
    public static String draw(List<String> playerNames, int points){}
            //(utilise DRAW).

    public String willPlayFirst(){}
    public String keptTickets(int count){}
    public String canPlay(){}
    public String drewTickets(int count){}
    public String drewBlindCard(){}
    public String drewVisibleCard(Card card){}
    public String claimedRoute(Route route, SortedBag<Card> cards){}
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){}
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){}
    public String didNotClaimRoute(Route route){}
    public String lastTurnBegins(int carCount){}
    public String getsLongestTrailBonus(Trail longestTrail){}
    public String won(int points, int loserPoints){}
}
