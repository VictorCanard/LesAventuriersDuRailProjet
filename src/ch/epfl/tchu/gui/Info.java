package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.List;

public final class Info {
    private final String playerName;
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

    public Info(String playerName){
        this.playerName = playerName;
    }
    public static String cardName(Card card, int count){
        Color cardColor = card.color();
        String cardFrenchName = listOfAllCards.get(cardColor.ordinal());

        return String.format("%s%t",cardFrenchName, StringsFr.plural(count));
    }

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

        String additionalCostMessage;

        if(additionalCost > 0){
            additionalCostMessage = String.format(StringsFr.NO_ADDITIONAL_COST);
        }
        else{
            additionalCostMessage = String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost);
        }
        return String.format("%s%t", additionalCards, additionalCostMessage);

    }
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, route);
    }
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount);
    }
    public String getsLongestTrailBonus(Trail longestTrail){
        String trailName = String.format("%s%t%u", longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());
        return String.format(StringsFr.GETS_BONUS, playerName, trailName);
    }
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, playerName, points, loserPoints);
    }

    private static String cardNames(SortedBag<Card> bagOfCards){
        StringBuilder stringOfAllCardNamesToReturn = new StringBuilder();
        for (Card c: bagOfCards.toSet()) {
            int n = bagOfCards.countOf(c);
            stringOfAllCardNamesToReturn
                    .append(cardName(c, n))
                    .append(" ")
                    .append(n);
        }
        return stringOfAllCardNamesToReturn.toString();

    }
}
