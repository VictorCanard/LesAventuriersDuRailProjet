package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class GameV2 { //No constructor as the class is only functional; it shouldn't be instantiable
        private static Map<PlayerId, Player> players;
        private static PlayerId firstPlayer;
        private static Map<PlayerId, String> playerNames;
        private static SortedBag<Ticket> ticketOptions;
        private static Map<PlayerId, Info> infoGenerators;
        private static ch.epfl.tchu.game.GameState GameState;



        public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
            //Todo Repair the map as players is not according to the randomly generated player 1, but is in the original order
            Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

            GameV2.players = Map.copyOf(players);
            GameV2.playerNames = Map.copyOf(playerNames);
            GameV2.ticketOptions = tickets;
            GameV2.infoGenerators = new EnumMap<>(PlayerId.class);
            GameV2.GameState = GameState.initial(tickets, rng);
            GameV2.firstPlayer = GameState.currentPlayerId();

            initial();

            do {
                nextTurn();

            }while(! isLastTurn());

            endOfGameV2();

        }
        private static void initial(){
            players.forEach((k,v)->{ //Calls intiPlayers and initializes info generators
                infoGenerators.put(k, new Info(playerNames.get(k)));
                v.initPlayers(k, playerNames);
            });

            //For  both players:
            // Set initial tickets
            //updateStateForAll()
            //Choose initial tickets
            //receiveInfoForAll();
        }
        private static void nextTurn(){
            players.forEach((k,v) ->{
                //receiveInfoForAll();
                //updateStateForAll()
                Player.TurnKind playerChoice = v.nextTurn();

                switch (playerChoice){
                    case DRAW_CARDS:
                        drawCards(v);
                        break;
                    case DRAW_TICKETS:
                        drawTickets(v);
                        break;
                    case CLAIM_ROUTE:
                        claimRoute(v);
                        break;

                }

            });



        }
        private static boolean isLastTurn(){
            return GameState.lastTurnBegins();

        }
        private static void endOfGameV2(){
            //receiveInfoForAll(); It's last turn
            //One more turn

            //Calculate final points
            players.forEach(((playerId, player) -> {
                int totalPoints = calculateTotal(player);

                //Get PlayerInfoGenerator and apply method getsLongestTrailBonus();
                //receiveInfoForAll();
            }));

            //updateStateForAll()
            //receiveInfoForAll(); The winner or both ex aequo

        }


        private static void receiveInfoForAll(String infoToReceive){
            players.forEach((playerId, player) ->{
                player.receiveInfo(infoToReceive);
            });
        }
        private static void updateAllStates(PublicGameState newState, Map<PlayerId, PlayerState> bothPlayersOwnStates){
            players.forEach((playerId,player) ->{
                player.updateState(newState, bothPlayersOwnStates.get(playerId));
            });

        }

        private static void drawTickets(Player player){
            //receiveInfoForAll();
            SortedBag<Ticket> keptTickets = player.chooseTickets(ticketOptions);
            //receiveInfoForAll();
        }
        private static void drawCards(Player player){
            //receiveInfoForAll();
            for (int i = 0; i < 2; i++) {
                //withCardsDeckRecreatedIfNeeded
                int drawSlot = player.drawSlot();
                //receiveInfoForAll(); The player has chosen a card from the deck (-1) or from the FU Cards (0-4)
                //updateStateForAll()
            }


        }
        //Anne-Marie
        private static void claimRoute(Player player){ //withCardsDeckRecreatedIfNeeded
            //receiveInfoForAll(); Cartes initiales et additionnelles
            //If (tunnel){
            ////receiveInfoForAll(); It's a tunnel
            //receiveInfoForAll(); Additional Claim Cards
            //if (can't or doesn't want to capture the tunnel){
            //receiveInfoForAll();

        }

        private static int calculateTotal(Player player){
        /*PlayerState currentPlayerState = GameState.playerState();

        if(longestTrail() >= 0){

        }

        return currentPlayerState.finalPoints() + ;*/
            return 0;


        }

        //Returns a negative int if the second player has a longer trail than the first
        //Returns a positive int if the first player has a longer trail than the second
        //Returns 0 if they both have trails of equal length
        private static int longestTrail(){
            Map<PlayerId, Integer> playerIdIntegerMap = new EnumMap<>(PlayerId.class);

            players.forEach((playerId, player) -> {
                PlayerState currentPlayerState = GameState.playerState(playerId);
                int length = Trail.longest(currentPlayerState.routes()).length();
                playerIdIntegerMap.put(playerId, length);

            });

            return Integer.compare(playerIdIntegerMap.get(PlayerId.PLAYER_1), playerIdIntegerMap.get(PlayerId.PLAYER_2));
        }

    }


