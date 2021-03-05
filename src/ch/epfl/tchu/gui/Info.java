package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

public final class Info {
    private final String playerName;

    public Info(String playerName){
        this.playerName = playerName;
    }
    public static String cardName(Card card, int count){
        Color cardColor = card.color();
        String cardFrenchName = StringsFr.cardColor;
        return StringsFr.
    }
            //(utilise BLACK_CARD, BLUE_CARD, etc.),
    public static String draw(List<String> playerNames, int points){
        String playersMessage = String.format("%s%t%u", playerNames.get(0), StringsFr.AND_SEPARATOR, playerNames.get(1));
        String drawMessage = String.format(StringsFr.DRAW, playersMessage, points);
        return drawMessage;
    }


    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count);
    }
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, playerName);
    }
    public String drewTickets(int count){
        return String.format(StringsFr.DREW_TICKETS, playerName, count);
    }
    public String drewBlindCard(){
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, cardName(card, 1));
    }
    public String claimedRoute(Route route, SortedBag<Card> cards){
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, route, cardNames(cards));
    }
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards){
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, route, cardNames(initialCards));
    }
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost){
        String additionalCards = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardNames(drawnCards));

        if()
        String additionalCost = String.format(StringsFr.ADD);
    }
    public String didNotClaimRoute(Route route){}
    public String lastTurnBegins(int carCount){}
    public String getsLongestTrailBonus(Trail longestTrail){}
    public String won(int points, int loserPoints){}

    private static String cardNames(SortedBag<Card> bagOfCards){
        StringBuilder stringOfAllCardNamesToReturn = new StringBuilder();
        for (Card c: bagOfCards.toSet()) {
            int n = bagOfCards.countOf(c);
            stringOfAllCardNamesToReturn
                    .append(cardName(c, 1)) //Fix the count issue
                    .append(" ")
                    .append(n);
        }
        return stringOfAllCardNamesToReturn.toString();

    }
}
