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
    private final Socket playerSocket;

    public RemotePlayerProxy(Socket socket){
        this.playerSocket = socket;
    }
    @Override
    public void initPlayers(PlayerId ownID, Map<PlayerId, String> playerNames) {
        String playerId = STRING_SERDE.serialize(ownID.name());

        String namesOfPlayers = playerNames.values()
                .stream()
                .map(STRING_SERDE::serialize)
                .collect(Collectors.joining(","));

        sendMessage(MessageId.INIT_PLAYERS, List.of(playerId, namesOfPlayers));

    }

    @Override
    public void receiveInfo(String info) {
        sendMessage(MessageId.RECEIVE_INFO, List.of(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String gameStateString = PUBLIC_GAME_STATE_SERDE.serialize(newState);
        String playerStateString = PLAYER_STATE_SERDE.serialize(ownState);

        sendMessage(MessageId.UPDATE_STATE, List.of(gameStateString, playerStateString));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String initialTickets = SORTED_BAG_TICKET_SERDE.serialize(tickets);

        sendMessage(MessageId.SET_INITIAL_TICKETS, List.of(initialTickets));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS);

        String receivedMessage = receiveMessage();

        return SORTED_BAG_TICKET_SERDE.deserialize(receivedMessage);
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN);

        String received = receiveMessage();

        return TURN_KIND_SERDE.deserialize(received);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String ticketOptions = SORTED_BAG_TICKET_SERDE.serialize(options);

        sendMessage(MessageId.CHOOSE_TICKETS, List.of(ticketOptions));

        String receiveMessage = receiveMessage();

        return SORTED_BAG_TICKET_SERDE.deserialize(receiveMessage);
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT);

        String received = receiveMessage();

        return INTEGER_SERDE.deserialize(received);
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE);

        String received = receiveMessage();

        return ROUTE_SERDE.deserialize(received);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS);

        String received = receiveMessage();

        return SORTED_BAG_CARD_SERDE.deserialize(received);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String optionsString = LIST_SORTED_BAG_CARD_SERDE.serialize(options);

        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS, List.of(optionsString));

        String receivedMessage = receiveMessage();

        return SORTED_BAG_CARD_SERDE.deserialize(receivedMessage);
    }

    private void sendMessage(MessageId messageId){
        sendMessage(messageId, List.of());
    }
    private void sendMessage(MessageId messageId, List<String> allParametersOfTheMessage) {
        try (playerSocket) {

            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(playerSocket.getOutputStream(),
                                    US_ASCII));

            String message = String.join(messageId.name()
                    + String.join(" ", allParametersOfTheMessage)
                    + '\n', " ");

            w.write(message);

            w.flush();


        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    private String receiveMessage(){
        try(playerSocket){
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(playerSocket.getInputStream(),
                                    US_ASCII));

            return r.readLine();
        } catch (IOException ioException) {
            throw new UncheckedIOException(ioException);
        }
    }
}
