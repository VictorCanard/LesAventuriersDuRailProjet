package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Represents a remote player client
 *
 * @author Anne-Marie Rusu (296098)
 */

public class RemotePlayerClient {
    private final Player player;
    private final String name;
    private final int port;

    private final String spacePattern = Pattern.quote(" ");
    private final String commaPattern = Pattern.quote(",");

    /**
     * Constructor for the Client of a Player (who is not necessarily playing on the same machine)
     *
     * @param player : the player to take their turn
     * @param name : the host name
     * @param port : the port number
     */
    public RemotePlayerClient(Player player, String name, int port) {
        //Largest number that can be represented by an unsigned 16 bit binary number
        int maxPortLength = (int) Math.pow(2, 16) -1 ;

        this.player = player;
        this.name = Objects.requireNonNull(name);
        this.port = Objects.checkIndex(port, maxPortLength);
    }

    /**
     * Method that runs until the end of the game.
     * It tries to connect to the socket and then until it reads an empty line,
     * it keeps intercepting the messages and then running the appropriate player methods.
     * If these player methods return a value, the run() method will then serialize that value
     * and send it back onto the Socket (it will write it with the buffered writer).
     */
    public void run() {
        try (Socket socket = new Socket(name, port);
             BufferedReader bufferedReader =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(),
                                     US_ASCII));
             BufferedWriter bufferedWriter =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII))) {


            String readLine;

            while ((readLine = bufferedReader.readLine()) != null) {
                int patternLimit = -1;
                char lineReturn = '\n';

                String[] incoming = readLine.split(spacePattern, patternLimit);
                Iterator<String> stringIterator = Arrays.stream(incoming).iterator();

                switch (MessageId.valueOf(stringIterator.next())) {
                    case INIT_PLAYERS:
                        PlayerId ownId = PLAYER_ID_SERDE.deserialize(stringIterator.next());
                        String[] playerNamesSerialized = stringIterator.next().split(commaPattern, patternLimit);

                        Map<PlayerId, String> playerNames = new HashMap<>();

                        for (int i = 0; i < PlayerId.COUNT; i++) {
                            playerNames.put(PlayerId.ALL.get(i), STRING_SERDE.deserialize(playerNamesSerialized[i]));
                        }

                        player.initPlayers(ownId, playerNames);
                        break;

                    case RECEIVE_INFO:
                        String info = STRING_SERDE.deserialize(stringIterator.next());

                        player.receiveInfo(info);
                        break;

                    case UPDATE_STATE:
                        PublicGameState newState = PUBLIC_GAME_STATE_SERDE.deserialize(stringIterator.next());
                        PlayerState ownState = PLAYER_STATE_SERDE.deserialize(stringIterator.next());

                        player.updateState(newState, ownState);
                        break;

                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = SORTED_BAG_TICKET_SERDE.deserialize(stringIterator.next());

                        player.setInitialTicketChoice(tickets);
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> chosen = player.chooseInitialTickets();

                        bufferedWriter.write(SORTED_BAG_TICKET_SERDE.serialize(chosen) + lineReturn);
                        bufferedWriter.flush();
                        break;

                    case NEXT_TURN:
                        Player.TurnKind turn = player.nextTurn();

                        bufferedWriter.write(TURN_KIND_SERDE.serialize(turn) + lineReturn);
                        bufferedWriter.flush();
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> ticketOptions = SORTED_BAG_TICKET_SERDE.deserialize(stringIterator.next());
                        String chosenTickets = SORTED_BAG_TICKET_SERDE.serialize(player.chooseTickets(ticketOptions));

                        bufferedWriter.write(chosenTickets + lineReturn);
                        bufferedWriter.flush();
                        break;


                    case DRAW_SLOT:
                        int drawSlot = player.drawSlot();

                        bufferedWriter.write(INTEGER_SERDE.serialize(drawSlot) + lineReturn);
                        bufferedWriter.flush();
                        break;

                    case ROUTE:
                        Route claimedRoute = player.claimedRoute();

                        String string = ROUTE_SERDE.serialize(claimedRoute) + lineReturn;
                        bufferedWriter.write(string);
                        bufferedWriter.flush();
                        break;

                    case CARDS:
                        SortedBag<Card> initialClaimCards = player.initialClaimCards();

                        bufferedWriter.write(SORTED_BAG_CARD_SERDE.serialize(initialClaimCards) + lineReturn);
                        bufferedWriter.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> cardOptions = LIST_SORTED_BAG_CARD_SERDE.deserialize(stringIterator.next());
                        SortedBag<Card> addCards = player.chooseAdditionalCards(cardOptions);

                        bufferedWriter.write(SORTED_BAG_CARD_SERDE.serialize(addCards) + lineReturn);
                        bufferedWriter.flush();
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
