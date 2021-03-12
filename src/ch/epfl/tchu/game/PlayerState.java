package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

public final class PlayerState extends PublicPlayerState {
    public PlayerState(int ticketCount, int cardCount, List<Route> routes) {
        super(ticketCount, cardCount, routes);
    }
    public PlayerState initial(SortedBag<Card> initialCards){}
}
