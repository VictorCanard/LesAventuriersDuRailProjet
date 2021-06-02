package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Menu;

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
     * @param name   : the host name
     * @param port   : the port number
     */
    public RemotePlayerClient(Player player, String name, int port) {
        //Largest number that can be represented by an unsigned 16 bit binary number
        int maxPortLength = (int) Math.pow(2, 16) - 1;

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
        try (final Socket socket = new Socket(name, port);
             final BufferedReader bufferedReader =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(),
                                     US_ASCII));
             final BufferedWriter bufferedWriter =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII))) {
            String readLine;

            while ((readLine = bufferedReader.readLine()) != null) {



                Iterator<String> arguments = NetUtils.getStringIterator(readLine, spacePattern);


                switch (MessageId.valueOf(arguments.next())) {
                    case INIT_PLAYERS:
                        PlayerId ownId = PLAYER_ID_SERDE.deserialize(arguments.next());

                        Iterator<String> nameIterator = NetUtils.getStringIterator(arguments.next(), commaPattern);

                        Map<PlayerId, String> playerNames = new HashMap<>();

                        Menu.activePlayers.forEach(playerId -> playerNames.put(playerId, STRING_SERDE.deserialize(nameIterator.next())));

                        player.initPlayers(ownId, playerNames);
                        break;

                    case RECEIVE_INFO:
                        String info = STRING_SERDE.deserialize(arguments.next());

                        player.receiveInfo(info);
                        break;

                    case UPDATE_STATE:
                        PublicGameState newState = PUBLIC_GAME_STATE_SERDE.deserialize(arguments.next());
                        PlayerState ownState = PLAYER_STATE_SERDE.deserialize(arguments.next());

                        player.updateState(newState, ownState);
                        break;

                    case SET_INITIAL_TICKETS:
                        SortedBag<Ticket> tickets = SORTED_BAG_TICKET_SERDE.deserialize(arguments.next());

                        player.setInitialTicketChoice(tickets);
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        SortedBag<Ticket> chosen = player.chooseInitialTickets();
                        writeAndFlush(bufferedWriter, SORTED_BAG_TICKET_SERDE.serialize(chosen));
                        break;

                    case NEXT_TURN:
                        Player.TurnKind turn = player.nextTurn();

                        writeAndFlush(bufferedWriter, TURN_KIND_SERDE.serialize(turn));
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> ticketOptions = SORTED_BAG_TICKET_SERDE.deserialize(arguments.next());
                        String chosenTickets = SORTED_BAG_TICKET_SERDE.serialize(player.chooseTickets(ticketOptions));

                        writeAndFlush(bufferedWriter, chosenTickets);
                        break;


                    case DRAW_SLOT:
                        int drawSlot = player.drawSlot();

                        writeAndFlush(bufferedWriter, INTEGER_SERDE.serialize(drawSlot));
                        break;

                    case ROUTE:
                        Route claimedRoute = player.claimedRoute();

                        writeAndFlush(bufferedWriter, ROUTE_SERDE.serialize(claimedRoute));
                        break;

                    case CARDS:
                        SortedBag<Card> initialClaimCards = player.initialClaimCards();

                        writeAndFlush(bufferedWriter, SORTED_BAG_CARD_SERDE.serialize(initialClaimCards));
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        List<SortedBag<Card>> cardOptions = LIST_SORTED_BAG_CARD_SERDE.deserialize(arguments.next());
                        SortedBag<Card> additionalCards = player.chooseAdditionalCards(cardOptions);
                        writeAndFlush(bufferedWriter,SORTED_BAG_CARD_SERDE.serialize(additionalCards));
                        break;
                    case THREE_DRAWN_CARDS:
                        SortedBag<Card> cards = SORTED_BAG_CARD_SERDE.deserialize(arguments.next());
                        player.tunnelDrawnCards(cards);
                        break;
                    case ADDITIONAL_COST:
                        int additionalCost = INTEGER_SERDE.deserialize(arguments.next());
                        player.additionalCost(additionalCost);

                        break;
                    case DID_OR_DIDNT_CLAIM_ROUTE:
                        String s = STRING_SERDE.deserialize(arguments.next());
                        player.didOrDidntClaimRoute(s);
                        break;
                    default:
                        throw new Error();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    private void writeAndFlush(BufferedWriter bufferedWriter, String serialized) throws IOException {
        final char lineReturn = '\n';

        bufferedWriter.write(serialized + lineReturn);
        bufferedWriter.flush();
    }
}
