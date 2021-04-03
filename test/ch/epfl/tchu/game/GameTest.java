package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

class GameTest {

    private final List<Route> routes = ChMap.routes();
    public final static Random NON_RANDOM = new Random(){
        @Override
        public int nextInt(int i){
            return i - 1;
        }

    };

    @Test
    void play() {

        Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, "Jacob", PlayerId.PLAYER_2, "Martha");

        TestPlayer player1 = new TestPlayer((long) Math.random()*1000, routes, playerNames.get(PlayerId.PLAYER_1), false);
        TestPlayer player2 = new TestPlayer((long) Math.random()*2000, routes, playerNames.get(PlayerId.PLAYER_2), false);

        Map<PlayerId, Player> players = Map.of(PlayerId.PLAYER_1, player1, PlayerId.PLAYER_2, player2);

        SortedBag<Ticket> initialTickets = SortedBag.of(ChMap.tickets());


        Random nonRandom = NON_RANDOM;



        for (int i = 0; i < 100; i++) {
            Random realRandom = new Random((long) (1000*i + Math.random()*10));
            Game.play(players, playerNames, initialTickets, realRandom);

        }

    }

    private static class AssertAndInfo{
        private static PlayerId firstPlayer;

        private static String displayCardState(PublicCardState publicCardState){
            return String.format("Taille du deck: %s     Taille de la défausse: %s\n", publicCardState.deckSize(), publicCardState.discardsSize());
        }
        private static String displayGameState(PublicGameState publicGameState) {
            if (publicGameState.currentPlayerId() == PlayerId.PLAYER_1) {
                return String.format("CanDrawCards : %s     CanDrawTickets : %s     Current Player Id : %s      LastPlayer : %s\n", publicGameState.canDrawCards(), publicGameState.canDrawTickets(), publicGameState.currentPlayerId(), publicGameState.lastPlayer());
            }
            return "";
        }
        private static String displayPlayerInfo(PlayerState playerState){
            return String.format("Nombre de voitures: %s      Nombre de cartes: %s     Nombre de tickets: %s     ", playerState.carCount(), playerState.cardCount(), playerState.ticketCount());
        }

        private static String displayTotalNumberOfCards(PublicCardState publicCardState, PublicGameState publicGameState){
            return String.format("Total Number of Cards =" + (publicCardState.totalSize()+publicGameState.currentPlayerState().cardCount() + publicGameState.playerState(publicGameState.currentPlayerId().next()).cardCount()));
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
        private Info infoGenerator;
        private SortedBag<Ticket> distributedTickets;
        private PlayerId ownId;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        private final boolean messageDebug;


        public TestPlayer(long randomSeed, List<Route> allRoutes, String playerName, boolean gameMessagesWanted) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            this.infoGenerator = new Info(playerName);
            this.messageDebug = gameMessagesWanted;
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
            if(messageDebug || (currentState.lastPlayer() != null && currentState.lastPlayer() == ownId)){
                System.out.println(ownId + " received info " +info);
            }
        }



        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.currentState = newState;
            this.ownState = ownState;

            //AssertANdINfo
            receiveInfo(AssertAndInfo.displayGameState(newState));
            receiveInfo(AssertAndInfo.displayCardState(newState.cardState()));
            receiveInfo(AssertAndInfo.displayPlayerInfo(ownState));
            receiveInfo(AssertAndInfo.displayTotalNumberOfCards(newState.cardState(), newState));
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.distributedTickets = tickets;
            receiveInfo("Les 5 billets qui vous ont été distribués sont " + distributedTickets.stream().map((ticket -> ticket.text())).collect(Collectors.joining(", ")));

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
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = new ArrayList<>();

            for (Route route: allRoutes
                 ) {
                if(ownState.canClaimRoute(route)){
                    claimableRoutes.add(route);
                }
            }

            if (!claimableRoutes.isEmpty()){
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = this.ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE; //fails here because gameState is null apparently (line 138 in Game, when setting the new gameState for tunnel)
            }
            else if(currentState.canDrawCards()){
                return TurnKind.DRAW_CARDS;
            }
            else{
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