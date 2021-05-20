package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Adapts an instance of type GraphicalPlayer to an instance of type Player
 * @author Victor Jean Canard-Duchene (326913)
 */
public final class GraphicalPlayerAdapter implements Player {
    private final BlockingQueue<SortedBag<Ticket>> ticketQueue;
    private final BlockingQueue<SortedBag<Card>> cardQueue;
    private final BlockingQueue<Route> routeQueue;
    private final BlockingQueue<Integer> integerQueue;
    private final BlockingQueue<TurnKind> turnKindQueue;
    //
    private GraphicalPlayer graphicalPlayer;

    /**
     * Constructs the GraphicalPlayerAdapter, to be executed on a different thread than the JavaFx thread
     */
    public GraphicalPlayerAdapter() {
        int cap = 1;

        this.ticketQueue = new ArrayBlockingQueue<>(cap);
        this.cardQueue = new ArrayBlockingQueue<>(cap);
        this.routeQueue = new ArrayBlockingQueue<>(cap);
        this.turnKindQueue = new ArrayBlockingQueue<>(cap);
        this.integerQueue = new ArrayBlockingQueue<>(cap);
    }

    /**
     * The following eleven overridden methods from the interface Player allow the adapter to manage
     * each interaction in the game, by calling the appropriate method from GraphicalPlayer, while running on the JavaFX thread.
     */

    @Override
    public void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames) {
        runLater(() -> this.graphicalPlayer = new GraphicalPlayer(ownID, playerNames));

    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketQueue::add));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {

        return withTryAndCatch(ticketQueue);
    }

    @Override
    public TurnKind nextTurn() {
        runLater(() -> graphicalPlayer.startTurn(
                () -> turnKindQueue.add(TurnKind.DRAW_TICKETS),
                (slot -> {
                    integerQueue.add(slot);
                    turnKindQueue.add(TurnKind.DRAW_CARDS);
                }),
                ((route, initialCards) -> {
                    routeQueue.add(route);
                    cardQueue.add(initialCards);
                    turnKindQueue.add(TurnKind.CLAIM_ROUTE);
                })));
        return withTryAndCatch(turnKindQueue);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {

        setInitialTicketChoice(options);
        return chooseInitialTickets();
    }

    @Override
    public int drawSlot() {
        if (integerQueue.isEmpty()) {
            runLater(() -> graphicalPlayer.drawCard(integerQueue::add));
        }
        return withTryAndCatch(integerQueue);
    }

    @Override
    public Route claimedRoute() {

        return withTryAndCatch(routeQueue);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return withTryAndCatch(cardQueue);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardQueue::add));
        return initialClaimCards();
    }

    private <E> E withTryAndCatch(BlockingQueue<E> e) {
        try {
            return e.take();
        } catch (InterruptedException error) {
            throw new Error(error);
        }
    }
}
