package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Random;
class GameTest {

    @Test
    void play() {
    }




    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        private int turnCounter;
        private PlayerState ownState;
        private GameState gameState;
        private PublicGameState currentState;
        private Info infoGenerator;
        private SortedBag<Ticket> distributedTickets;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;


        public TestPlayer(long randomSeed, List<Route> allRoutes, String playerName) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;
            this.infoGenerator = new Info(playerName);
        }

        @Override
        public void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames) {
            receiveInfo(ownID.name());

            playerNames.forEach(((playerId, name) -> {
                receiveInfo(playerId +" is "+ name + ".");
            }));
        }

        @Override
        public void receiveInfo(String info) {
            System.out.println(info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.currentState = newState;
            this.ownState = ownState;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.distributedTickets = tickets;
            receiveInfo("Les 5 billets qui vont été distribués sont " + tickets);

        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            //Chooses 3 random tickets
            SortedBag.Builder<Ticket> chosenTickets =  new SortedBag.Builder<>();

            int numberOfKeptTickets = rng.nextInt(3) + 3; //Can keep 3 to 5 tickets

            for (int i = 0; i < numberOfKeptTickets; i++) {
                int randomSlot = rng.nextInt(5);

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
            List<Route> claimableRoutes = List.of();

            PlayerState playerState = gameState.currentPlayerState();

            for (Route route: allRoutes
                 ) {
                if(playerState.canClaimRoute(route)){
                    claimableRoutes.add(route);
                }
            }


            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
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
                int randomSlot = rng.nextInt(3);

                SortedBag<Ticket> chosenTicket = SortedBag.of(options.get(randomSlot));
                chosenTickets.add(chosenTicket);
                options = options.difference(chosenTicket); //Not sure this will affect the var options and effectively remove these tickets from the game

            }
            return chosenTickets.build();

        }

        @Override
        public int drawSlot() {
            return 0;
        }

        @Override
        public Route claimedRoute() {
            return null;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return null;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return null;
        }
    }


}