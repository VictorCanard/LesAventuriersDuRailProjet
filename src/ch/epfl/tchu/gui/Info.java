package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class Info {
    private final String playerName;
    private final static List<String> listOfAllCards =
            List.of(StringsFr.BLACK_CARD,
                    StringsFr.VIOLET_CARD,
                    StringsFr.BLUE_CARD,
                    StringsFr.GREEN_CARD,
                    StringsFr.YELLOW_CARD,
                    StringsFr.ORANGE_CARD,
                    StringsFr.RED_CARD,
                    StringsFr.WHITE_CARD,
                    StringsFr.LOCOMOTIVE_CARD);

    public Info(String playerName){
        this.playerName = playerName;
    }

    public static String cardName(Card card, int count){
        String cardFrenchName = listOfAllCards.get(card.ordinal());

        return String.format("%s%s",cardFrenchName, StringsFr.plural(count));


    }

    public static String draw(List<String> playerNames, int points){
        String playersMessage = String.format("%s%s%s", playerNames.get(0), StringsFr.AND_SEPARATOR, playerNames.get(1));
        String drawMessage = String.format(StringsFr.DRAW, playersMessage, points);
        return drawMessage;
    }


    public String willPlayFirst(){
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }
    public String keptTickets(int count){
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }
    public String canPlay(){
        return String.format(StringsFr.CAN_PLAY, playerName);
    }
    public String drewTickets(int count){
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }
    public String drewBlindCard(){
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }
    public String drewVisibleCard(Card card){
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
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

        if(additionalCost == 0){
            additionalCostMessage = String.format(StringsFr.NO_ADDITIONAL_COST);
        }
        else{
            additionalCostMessage = String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        }
        return String.format("%s%s", additionalCards, additionalCostMessage);

    }
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, route);
    }
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }
    public String getsLongestTrailBonus(Trail longestTrail){
        String trailName = String.format("%s%s%s", longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());
        return String.format(StringsFr.GETS_BONUS, playerName, trailName);
    }
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS, playerName, points,StringsFr.plural(points), loserPoints, StringsFr.plural(loserPoints));
    }

    private static String cardNames(SortedBag<Card> bagOfCards){
        StringBuilder stringOfAllCardNamesToReturn = new StringBuilder();
        ListIterator<Card> iterator = bagOfCards.toList().listIterator();

        while(iterator.hasNext() && iterator.nextIndex() < bagOfCards.size()-1) {
            Card nextCard = iterator.next();
            int n = bagOfCards.countOf(nextCard);

            stringOfAllCardNamesToReturn
                    .append(n)
                    .append(" ")
                    .append(cardName(nextCard, n))
                    .append(", ");


        }
        Card lastCard = iterator.next();
        int countOfLastCard = bagOfCards.countOf(lastCard);
        stringOfAllCardNamesToReturn.append("et ")
                                    .append(countOfLastCard)
                                    .append(" ")
                                    .append(cardName(lastCard, countOfLastCard));


        return stringOfAllCardNamesToReturn.toString();

    }
}
