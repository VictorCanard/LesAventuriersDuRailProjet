package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * These handlers define the code to run when the player does one of these actions.
 *
 * @author Victor Jean Canard-Duchene (326913)
 */
public interface ActionHandlers {

    /**
     * When the player draws tickets at the beginning of a turn.
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        void onDrawTickets();
    }

    /**
     * When the player draws cards at the beginning of a turn.
     */
    @FunctionalInterface
    interface DrawCardHandler {
        void onDrawCards(int slot);
    }

    /**
     * When the player claims routes at the beginning of a turn.
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        void onClaimRoute(Route route, SortedBag<Card> initialCards);
    }

    /**
     * When the player has to choose tickets at the beginning of the game or when drawing them from the deck.
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        void onChooseTickets(SortedBag<Ticket> keptTickets);
    }

    /**
     * When the player chooses cards to capture a route or additional cards for a tunnel claim.
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        void onChooseCards(SortedBag<Card> cards);
    }
}
