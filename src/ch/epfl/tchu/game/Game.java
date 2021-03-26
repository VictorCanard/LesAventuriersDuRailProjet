package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Game implements Player { //No constructor as the class is only functional; it shouldn't be instantiable
    private static Map<PlayerId, Player> players;


    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng){
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        players = Map.copyOf(players);

        for (Player player: players.values() // Call initPlayers()
             ) {

        }
        //ReceiveInfo

    }

    private void receiveInfoForAll(String infoToReceive){
        for (Player player: players.values()
             ) {
            player.receiveInfo(infoToReceive);
        }
    }
    private void updateAllStates(PublicGameState newState, Map<PlayerId, PlayerState> bothPlayersOwnStates){
        PlayerId playerOne = PlayerId.PLAYER_1;

        for (Player player: players.values()
        ) {
            player.updateState(newState, bothPlayersOwnStates.get(playerOne));
            playerOne = playerOne.next(); //To call the method updateState for playerTwo as well
        }
    }

    @Override
    public void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames) {

    }

    @Override
    public void receiveInfo(String info) {

    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {

    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return null;
    }

    @Override
    public TurnKind nextTurn() {
        return null;
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        return null;
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
