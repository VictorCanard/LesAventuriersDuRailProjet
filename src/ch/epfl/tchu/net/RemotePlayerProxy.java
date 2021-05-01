package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {
    private final BufferedWriter bufferedWriter;
    private final BufferedReader bufferedReader;

    public RemotePlayerProxy(Socket socket) {
        try {
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(),
                            US_ASCII));
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(),
                            US_ASCII));

        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }


    @Override
    public void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames) {
        String playerId = PLAYER_ID_SERDE.serialize(ownID);

        String namesOfPlayers = PlayerId.ALL.stream()
                .map((playerId1 -> STRING_SERDE.serialize(playerNames.get(playerId1))))
                .collect(Collectors.joining(","));

        sendMessage(MessageId.INIT_PLAYERS, playerId, namesOfPlayers);

    }

    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO, STRING_SERDE.serialize(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String gameStateString = PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String playerStateString = PLAYER_STATE_SERDE.serialize(ownState);

        sendMessage(MessageId.UPDATE_STATE, gameStateString, playerStateString);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String initialTickets = SORTED_BAG_TICKET_SERDE.serialize(tickets);

        sendMessage(MessageId.SET_INITIAL_TICKETS, initialTickets);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS);

        return SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN);

        return TURN_KIND_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String ticketOptions = SORTED_BAG_TICKET_SERDE.serialize(options);

        sendMessage(MessageId.CHOOSE_TICKETS, ticketOptions);

        return SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT);

        return INTEGER_SERDE.deserialize(receiveMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE);
        return ROUTE_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS);

        return SORTED_BAG_CARD_SERDE.deserialize(receiveMessage());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String optionsString = LIST_SORTED_BAG_CARD_SERDE.serialize(options);
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, optionsString);

        return SORTED_BAG_CARD_SERDE.deserialize(receiveMessage());
    }

    private void sendMessage(MessageId messageId, String... allParametersOfTheMessage) {
        try {


            String message = String.join(" ", List.of(messageId.name(), String.join(" ", allParametersOfTheMessage)))
                    + '\n';

            bufferedWriter.write(message);
            System.out.println("SENT MESSAGE : " + message);

            bufferedWriter.flush();

        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }

    private String receiveMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }
}
