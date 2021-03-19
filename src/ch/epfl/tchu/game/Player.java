package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

public interface Player {
    public static enum TurnKind{

    }
    void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames);
    void receiveInfo(String info);
    void updateState(PublicGameState newState, PlayerState ownState);
    void setInitialTicketChoice(SortedBag<Ticket> tickets);
    SortedBag<Ticket> chooseInitialTickets();
    TurnKind nextTurn();
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);
    int drawSlot();
    Route claimedRoute();
    SortedBag<Card> initialClaimCards();
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);
}
