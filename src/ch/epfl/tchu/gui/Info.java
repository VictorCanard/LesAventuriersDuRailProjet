package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the text describing the progression of the game
 *
 * @author Victor Jean Canard-Duchene (326913)
 */
public final class Info {
    private final String playerName;


    /**
     * Info constructor: assigns a player name under which all the activity will be described
     *
     * @param playerName : the name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Gives the french name of the given card
     *
     * @param card  : the specified card to get the name from
     * @param count : the number of cards there are
     * @return message including the french name of the given card, in plural if there is more than one
     */
    public static String cardName(Card card, int count) {
        String cardFrenchName = "";

        if (card.color() == null) {
            cardFrenchName = StringsFr.LOCOMOTIVE_CARD;
        } else {
            switch (card.color()) {
                case BLACK:
                    cardFrenchName = StringsFr.BLACK_CARD;
                    break;
                case VIOLET:
                    cardFrenchName = StringsFr.VIOLET_CARD;
                    break;
                case BLUE:
                    cardFrenchName = StringsFr.BLUE_CARD;
                    break;
                case GREEN:
                    cardFrenchName = StringsFr.GREEN_CARD;
                    break;
                case YELLOW:
                    cardFrenchName = StringsFr.YELLOW_CARD;
                    break;
                case ORANGE:
                    cardFrenchName = StringsFr.ORANGE_CARD;
                    break;
                case RED:
                    cardFrenchName = StringsFr.RED_CARD;
                    break;
                case WHITE:
                    cardFrenchName = StringsFr.WHITE_CARD;
                    break;


            }
        }

        return String.format("%s%s", cardFrenchName, StringsFr.plural(count));
    }

    /**
     * Gives the message that the two players have tied at the end of the game
     *
     * @param playerNames : the list of names of the two players
     * @param points      : number of points they have earned at the end of the game
     * @return the message that the players have tied and how many points they earned
     */
    public static String draw(List<String> playerNames, int points) {
        StringBuilder players = new StringBuilder();
        for (int i = 0; i < Menu.number_of_players; i++) {
            String andSeparator = (i == Menu.number_of_players - 2) ? StringsFr.AND_SEPARATOR : "";

            players.append(playerNames.get(i))
                    .append(andSeparator);
        }

        return String.format(StringsFr.DRAW, players, points);
    }

    /**
     * Returns a string with a formatted list of cards (adds the delimiters 'and' as well as ', ' at the right points in the text)
     *
     * @param bagOfCards : cards in a sortedBag that we want the textual representation of (in French, with commas and an AND_SEPARATOR)
     * @return string of all card names formatted
     */
    public static String cardNames(SortedBag<Card> bagOfCards) {
        StringBuilder stringOfAllCardNamesToReturn = new StringBuilder();

        List<String> cardList = getListOfCards(bagOfCards);

        for (int i = 0; i < cardList.size(); i++) {
            //Only adds commas for n-2 objects
            String commaSeparator = (i < cardList.size() - 2) ? ", " : "";

            //Only adds an And Separator after the second to last object
            String andSeparator = (i == cardList.size() - 2) ? StringsFr.AND_SEPARATOR : "";

            stringOfAllCardNamesToReturn
                    //Name of the card in French
                    .append(cardList.get(i))
                    //Comma if it is needed
                    .append(commaSeparator)
                    //And separator before the last item
                    .append(andSeparator);
        }
        return stringOfAllCardNamesToReturn.toString();
    }

    /**
     * Returns a bag of cards as a list of card names in French
     *
     * @param originalBag : bag of cards we want to transform in a list of cards
     * @return a sorted bag's associated list of cards
     */
    private static List<String> getListOfCards(SortedBag<Card> originalBag) {
        List<String> stringList = new ArrayList<>();

        for (Card currentCard : originalBag.toSet()) {
            //Number of cards
            int multiplicity = originalBag.countOf(currentCard);

            String stringToAdd = multiplicity + " " + cardName(currentCard, multiplicity);
            stringList.add(stringToAdd);
        }
        return stringList;
    }

    /**
     * Assigns which player will play first
     *
     * @return message including the name of the player that will take the first turn
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Gives the message of the number of tickets that the given player has kept
     *
     * @param count : the number of tickets to keep
     * @return message including the number of tickets the player has kept
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Gives the message of the name of the player whose turn it is
     *
     * @return message including the name of the player who can now play
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Gives the message of the number of tickets the player has drawn
     *
     * @param count : number of tickets to draw
     * @return message including how many tickets the player has drawn
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    /**
     * Gives message that the player drew a card from the draw pile
     *
     * @return message declaring the player drew a card from the draw pile
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Gives the message that the player drew a card from the 5 face up cards
     *
     * @param card : the card that was drawn
     * @return message including the card name that was drawn from the face-up cards
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Gives the message that a route was claimed by the player
     *
     * @param route : route claimed by player
     * @param cards : cards used to claim route
     * @return message including the route that was claimed by the player and the cards used to claim it
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, toString(route), cardNames(cards));
    }

    /**
     * Gives the message announcing that the player wants to take a tunnel route
     *
     * @param route        : the tunnel route to be claimed
     * @param initialCards : the cards the player has put down initially to claim the tunnel route
     * @return message including the tunnel to be attempted and the initial cards played by the player
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, toString(route), cardNames(initialCards));
    }

    /**
     * Gives the message that the player has drawn 3 additional cards from the draw pile
     *
     * @param drawnCards     : the three drawn cards
     * @param additionalCost : the additional cost from the three cards
     * @return message including the names of the additional cards and the additional cost
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCards = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardNames(drawnCards));

        String additionalCostMessage;

        if (additionalCost == 0) {
            additionalCostMessage = StringsFr.NO_ADDITIONAL_COST;
        } else {
            additionalCostMessage = String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        }

        return String.format("%s%s", additionalCards, additionalCostMessage);
    }

    /**
     * Gives the message that the player failed, or did not want to claim the route
     *
     * @param route : the route that was abandoned
     * @return message including the route that was not claimed by the player
     */
    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, toString(route));
    }

    /**
     * Gives the message that the last turn of the game is beginning
     *
     * @param carCount : the number of car cards less or equal to 2, signaling the final round
     * @return message including how many cars the player has, and the last turn of the game is beginning
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Gives the message of the player who received the bonus points from claiming the longest trail
     *
     * @param longestTrail : the longest trail in the game
     * @return message including the player who won the bonus and on what trail, and the number of points they get
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        String trailName = String.format("%s%s%s", longestTrail.station1(), StringsFr.EN_DASH_SEPARATOR, longestTrail.station2());

        return String.format(StringsFr.GETS_BONUS, playerName, trailName);
    }

    /**
     * Gives the message that the player has won
     *
     * @param points : number of points the player has won with
     * @return message including the number of points of the winning and losing player
     */
    public String won(List<String> winnersToLosers, List<Integer> points) {
        StringBuilder sb = new StringBuilder();

        sb
                .append("\n")
                .append(String.format(StringsFr.WINS, playerName, points.get(0), StringsFr.plural(points.get(0))));

        for (int i = 1; i < Menu.number_of_players; i++) {
            String contre = (i == 1) ? StringsFr.CONTRE : "";
            String and = (i == Menu.number_of_players -2) ? StringsFr.AND_SEPARATOR : "";
            String comma = (i < Menu.number_of_players -2) ? ", " : "";
            sb
                    .append(comma)
                    .append(and)
                    .append(contre)
                    .append(String.format(StringsFr.POINTS, points.get(i), StringsFr.plural(points.get(i)), winnersToLosers.get(i)));
        }
        sb.append(".\n");

        return sb.toString();


    }


    /**
     * Transforms a route into a textual representation
     *
     * @param route : route we want the text formatting of
     * @return the route's textual representation
     */
    private String toString(Route route) {
        return String.format("%s%s%s", route.station1(), StringsFr.EN_DASH_SEPARATOR, route.station2());
    }
}