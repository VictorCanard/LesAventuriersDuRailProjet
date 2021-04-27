package ch.epfl.tchu;

import ch.epfl.tchu.game.*;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.tchu.PlayerMethod.*;


public final class TestPlayer implements Player {

    private static final class TooManyCallsError extends Error {
    }

    private static final int CALLS_LIMIT = 10_000;
    private static final int MIN_CARD_COUNT = 16;
    private static final int DRAW_TICKETS_ODDS = 15;
    private static final int ABANDON_TUNNEL_ODDS = 10;
    private static final int DRAW_ALL_TICKETS_TURN = 30;

    private final Random rng;
    private final List<Route> allRoutes;

    private final Deque<PlayerMethod> calls = new ArrayDeque<>();

    private final Deque<TurnKind> allTurns = new ArrayDeque<>();
    private final Deque<String> allInfos = new ArrayDeque<>();
    private final Deque<PublicGameState> allGameStates = new ArrayDeque<>();
    private final Deque<PlayerState> allOwnStates = new ArrayDeque<>();
    private final Deque<SortedBag<Ticket>> allTicketsSeen = new ArrayDeque<>();

    private Route routeToClaim;
    private SortedBag<Card> initialClaimCards;

    private void registerCall(PlayerMethod key) {
        calls.add(key);
        if (calls.size() >= CALLS_LIMIT)
            throw new TestPlayer.TooManyCallsError();
    }

    private Map<PlayerMethod, Integer> callSummary() {
        var summary = new EnumMap<PlayerMethod, Integer>(PlayerMethod.class);
        calls.forEach(c -> summary.merge(c, 1, Integer::sum));
        return summary;
    }

    private PublicGameState gameState() {
        return allGameStates.getLast();
    }

    private PlayerState ownState() {
        return allOwnStates.getLast();
    }


    public TestPlayer(long randomSeed, List<Route> allRoutes) {
        this.rng = new Random(randomSeed);
        this.allRoutes = List.copyOf(allRoutes);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.printf("ownId: %s\n", ownId);

        System.out.println("Player Names: " + playerNames);

        registerCall(PlayerMethod.INIT_PLAYERS);
        Map<PlayerId, String> playerNames1 = Map.copyOf(playerNames);
    }

    @Override
    public void receiveInfo(String info) {
        System.out.printf("Received Info: %s\n", info);

        registerCall(RECEIVE_INFO);
        allInfos.addLast(info);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        registerCall(UPDATE_STATE);
        allGameStates.addLast(newState);
        allOwnStates.addLast(ownState);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        registerCall(SET_INITIAL_TICKET_CHOICE);
        allTicketsSeen.addLast(tickets);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        registerCall(PlayerMethod.CHOOSE_INITIAL_TICKETS);
        return allTicketsSeen.peekFirst();
    }

    @Override
    public TurnKind nextTurn() {
        registerCall(PlayerMethod.NEXT_TURN);

        var turn = doNextTurn();
        allTurns.addLast(turn);
        return turn;
    }

    private TurnKind doNextTurn() {
        var gameState = gameState();
        if (gameState.canDrawTickets()
                && (allTurns.size() >= DRAW_ALL_TICKETS_TURN
                || rng.nextInt(DRAW_TICKETS_ODDS) == 0))
            return TurnKind.DRAW_TICKETS;

        var ownState = ownState();
        var claimedRoutes = new HashSet<>(gameState.claimedRoutes());
        var claimableRoutes = allRoutes.stream()
                .filter(r -> !claimedRoutes.contains(r))
                .filter(ownState::canClaimRoute)
                .collect(Collectors.toCollection(ArrayList::new));
        if (claimableRoutes.isEmpty() || ownState.cardCount() < MIN_CARD_COUNT) {
            return TurnKind.DRAW_CARDS;
        } else {
            var route = claimableRoutes.get(rng.nextInt(claimableRoutes.size()));
            for (int i = 0; i < 3 && route.level() == Route.Level.OVERGROUND; i++) {
                // slightly favor tunnels
                route = claimableRoutes.get(rng.nextInt(claimableRoutes.size()));
            }

            var cards = ownState.possibleClaimCards(route);

            routeToClaim = route;
            initialClaimCards = cards.isEmpty() ? null : cards.get(0);
            return TurnKind.CLAIM_ROUTE;
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        registerCall(PlayerMethod.CHOOSE_TICKETS);

        allTicketsSeen.addLast(options);

        var shuffledOptions = new ArrayList<>(options.toList());
        Collections.shuffle(shuffledOptions, rng);
        var ticketsToKeep = 1 + rng.nextInt(options.size());
        return SortedBag.of(shuffledOptions.subList(0, ticketsToKeep));
    }

    @Override
    public int drawSlot() {
        registerCall(PlayerMethod.DRAW_SLOT);
        return rng.nextInt(6) - 1;
    }

    @Override
    public Route claimedRoute() {
        registerCall(PlayerMethod.CLAIMED_ROUTE);
        return routeToClaim;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        registerCall(PlayerMethod.INITIAL_CLAIM_CARDS);
        return initialClaimCards;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        registerCall(PlayerMethod.CHOOSE_ADDITIONAL_CARDS);

        return rng.nextInt(ABANDON_TUNNEL_ODDS) == 0
                ? SortedBag.of()
                : options.get(rng.nextInt(options.size()));
    }
}


