package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class GameTest {

    public static List<Route> routes;

    private static final Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Jacob", PlayerId.PLAYER_2, "Martha");
    private static final SortedBag<Ticket> initialTickets = SortedBag.of(ChMap.tickets());


    public final static Random NON_RANDOM = new Random(){
        @Override
        public int nextInt(int i){
            return i - 1;
        }

    };

    @Test
    void playWorks10000times() {
       /* for (int i = 0; i < 0; i++) {
            playWorks100Times();
        }*/

    }

    @Test
    void playWorks100Times(){
        for (int i = 0; i < 1; i++) {
            Random realRandom = new Random(i);

            GameTest.routes = ChMap.routes().stream().filter(((route -> !route.id().endsWith("_2")))).collect(Collectors.toList());

            TestPlayer player1 = new TestPlayer(50+i, routes, playerNames.get(PlayerId.PLAYER_1), true);
            TestPlayer player2 = new TestPlayer(200000000L + i, routes, playerNames.get(PlayerId.PLAYER_2), true);

            Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);
            Game.play(players, playerNames, initialTickets, realRandom);

        }
    }

    private static class AssertAndInfo{
        private static PlayerId firstPlayer;

        private static boolean isFirstTimePrinted = true;
        private static boolean isFirstEOF = true;

        private static final boolean gameInfo = false;
        private static final boolean playerInfo = true;
        private static final boolean cardInfo = false;

        public static List<Route> routeList = new ArrayList<>();

        private static void displayInfo(int turnCounter, PublicGameState publicGameState, PublicCardState publicCardState, PlayerState playerState, String playerName){
            if(gameInfo || playerInfo || cardInfo){
                System.out.printf("\n---------------------------%s___TURN # %s----------------------------\n", playerName, turnCounter);
            }
            if(gameInfo && isFirstTimePrinted){
                displayGameState(publicGameState);
            }
            if (playerInfo){
                displayPlayerInfo(playerName, playerState);
            }
            if(cardInfo && isFirstTimePrinted){
                displayCardState(publicCardState);

            }

            isFirstEOF = true;
            isFirstTimePrinted = ! isFirstTimePrinted;
        }

        private static void displayCardState(PublicCardState publicCardState){
            System.out.printf("Taille du deck: %s     Taille de la défausse: %s\n", publicCardState.deckSize(), publicCardState.discardsSize());
        }
        private static void displayGameState(PublicGameState publicGameState) {
            System.out.printf("CanDrawCards : %s     CanDrawTickets : %s     Current Player Id : %s      LastPlayer : %s\n", publicGameState.canDrawCards(), publicGameState.canDrawTickets(), publicGameState.currentPlayerId(), publicGameState.lastPlayer());


        }
        private static void displayPlayerInfo(String playerName, PlayerState playerState){
            System.out.printf("%s:: Nombre de voitures: %s      Nombre de cartes: %s     Nombre de billets: %s  \n", playerName, playerState.carCount(), playerState.cardCount(), playerState.ticketCount());
        }

        /*private static void displayTotalNumberOfCards(PublicCardState publicCardState, PublicGameState publicGameState){
            int totalNumber = publicCardState.totalSize()+publicGameState.currentPlayerState().cardCount() + publicGameState.playerState(publicGameState.currentPlayerId().next()).cardCount();
            System.out.print("Total Number of Cards = " + (totalNumber));
            assert totalNumber == 110;

        }*/
        private static void displayEndOfGameMessage(PublicPlayerState publicPlayerState, String message){
            if (isFirstEOF){
                System.out.println(message);
                routeList.addAll(publicPlayerState.routes());
            }
            else{
                routeList.addAll(publicPlayerState.routes());
                Set<Route> routeSet = new HashSet<>(routeList);
                assert routeSet.size() == routeList.size(); //To see if there any duplicates
            }
            isFirstEOF = ! isFirstEOF;



        }
    }


    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;

        private PublicGameState currentState;
        private final Info infoGenerator;
        private SortedBag<Ticket> distributedTickets;
        private PlayerId ownId;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        private final boolean playerMessageDebug;
        private final String name;

        private  int claimRoutesTurns = 0;
        private  int drawCardsTurns = 0;
        private  int drawTicketsTurns = 0;



        public TestPlayer(long randomSeed, List<Route> allRoutes, String playerName, boolean playerMessagesWanted) {
            AssertAndInfo.routeList = new ArrayList<>();

            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            this.infoGenerator = new Info(playerName);
            this.playerMessageDebug = playerMessagesWanted;
            this.name = playerName;
        }

        @Override
        public void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames) {
            this.ownId = ownID;
            receiveInfo(ownID.name());

            playerNames.forEach(((playerId, name) -> {
                receiveInfo(playerId +" is "+ name + ".");
            }));
        }

        @Override
        public void receiveInfo(String info) {

            if(info.contains("remporte") || info.contains("ex æqo") ){
                AssertAndInfo.displayEndOfGameMessage(ownState, info);
                System.out.printf("%s:: %s ClaimRoutes\n  %s Draw Cards\n %s Draw tickets\n", name, claimRoutesTurns, drawCardsTurns, drawTicketsTurns);
            }

            if(playerMessageDebug){
                System.out.println(name + " received : " +info);
            }
        }



        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.currentState = newState;
            this.ownState = ownState;

        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.distributedTickets = tickets;
            receiveInfo("Les 5 billets qui vous ont été distribués sont " + distributedTickets.stream().map((Ticket::text)).collect(Collectors.joining(", ")));

        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            //Chooses 3 random tickets
            SortedBag.Builder<Ticket> chosenTickets =  new SortedBag.Builder<>();

            int numberOfKeptTickets = rng.nextInt(3) + 3; //Can keep 3 to 5 tickets

            for (int i = 0; i < numberOfKeptTickets; i++) {
                int randomSlot = rng.nextInt(distributedTickets.size());

                SortedBag<Ticket> chosenTicket = SortedBag.of(distributedTickets.get(randomSlot));
                chosenTickets.add(chosenTicket);
                this.distributedTickets = distributedTickets.difference(chosenTicket);

            }
            return chosenTickets.build();


        }


        @Override
        public TurnKind nextTurn() {

            //AssertAndInfo
            AssertAndInfo.displayInfo(turnCounter, currentState, currentState.cardState(), ownState, name);

            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = new ArrayList<>();

            for (Route route: allRoutes
                 ) {
                if(ownState.canClaimRoute(route) && !currentState.claimedRoutes().contains(route)){
                    claimableRoutes.add(route);
                }
            }

            if (!claimableRoutes.isEmpty()){
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = this.ownState.possibleClaimCards(route);

                routeToClaim = route;

                initialClaimCards = cards.get(0);

                claimRoutesTurns++;
                return TurnKind.CLAIM_ROUTE;
            }
            else if(currentState.canDrawCards() && turnCounter % 20 != 0){
                drawCardsTurns++;
                return TurnKind.DRAW_CARDS;
            }
            else{
                drawTicketsTurns++;
                return TurnKind.DRAW_TICKETS;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            receiveInfo("Voici les billets tirés "+ options);
            receiveInfo("Lesquels voulez-vous garder ?");

            SortedBag.Builder<Ticket> chosenTickets =  new SortedBag.Builder<>();

            int numberOfKeptTickets = rng.nextInt(3) + 1; //Can keep 1, 2 or 3 tickets
            System.out.println("Number of kept tickets: " + numberOfKeptTickets);

            for (int i = 0; i < numberOfKeptTickets; i++) {
                int randomSlot = rng.nextInt(options.size());


                SortedBag<Ticket> chosenTicket = SortedBag.of(options.get(randomSlot));
                chosenTickets.add(chosenTicket);
                options = options.difference(chosenTicket); //Not sure this will affect the var options and effectively remove these tickets from the game

            }
            return chosenTickets.build();

        }

        @Override
        public int drawSlot() {
            return rng.nextInt(6)-1; //the player chooses randomly where they take the card from
        }

        @Override
        public Route claimedRoute() {
            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            int index = rng.nextInt(options.size());
            return options.get(index);
        }
    }


}