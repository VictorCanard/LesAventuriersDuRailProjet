package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;
import ch.epfl.tchu.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;


public class RemotePlayerClient {
    private final Player player;
    private final String name;
    private final int port;
    private final String spacePattern = Pattern.quote(" ");
    private final String commaPattern = Pattern.quote(",");

    /**
     * Constructor for the Client of a Player (not necessarily playing on the same machine)
     *
     * @param player : the player to take their turn
     * @param name   : the host name
     * @param port   : the port number
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;

    }


    /**
     * Method that runs until the end of the game.
     * It tries to connect to the socket and then until it reads an empty line,
     * it keeps intercepting the messages and then running the appropriate player methods.
     */
    public void run(){
        try(Socket s = new Socket(name, port);
            BufferedReader r =
                    new BufferedReader(
                            new InputStreamReader(s.getInputStream(),
                                    US_ASCII));
            BufferedWriter w =
                    new BufferedWriter(
                            new OutputStreamWriter(s.getOutputStream(),
                                    US_ASCII))) {


            String read;

            while ((read = r.readLine()) != null) {
                System.out.println(read);

               String[] incoming = read.split(spacePattern, -1);
               String type = incoming[0];

               switch (MessageId.valueOf(type)) {
                   case INIT_PLAYERS:
                       PlayerId ownId = PLAYER_ID_SERDE.deserialize(incoming[1]);
                       String[] players12 = incoming[2].split(commaPattern, -1); //QWRh,Q2hhcmxlcw==

                       Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, STRING_SERDE.deserialize(players12[0]),
                                                                  PlayerId.PLAYER_2, STRING_SERDE.deserialize(players12[1]));

                       player.initPlayers(ownId, playerNames);
                       break;

                   case RECEIVE_INFO:
                       String info = STRING_SERDE.deserialize(incoming[1]);

                       player.receiveInfo(info);
                       break;

                   case UPDATE_STATE:
                       PublicGameState newState = PUBLIC_GAME_STATE_SERDE.deserialize(incoming[1]);
                       PlayerState ownState = PLAYER_STATE_SERDE.deserialize(incoming[2]);

                       player.updateState(newState, ownState);
                       break;

                   case SET_INITIAL_TICKETS:
                       SortedBag<Ticket> tickets = SORTED_BAG_TICKET_SERDE.deserialize(incoming[1]);

                       player.setInitialTicketChoice(tickets);
                       break;

                   case CHOOSE_INITIAL_TICKETS:
                      SortedBag<Ticket> chosen =  player.chooseInitialTickets();
                       w.write(SORTED_BAG_TICKET_SERDE.serialize(chosen));
                      w.flush();
                      break;

                  case NEXT_TURN:
                       Player.TurnKind turn = player.nextTurn();
                       w.write(TURN_KIND_SERDE.serialize(turn));
                       w.flush();
                       break;

                   case CHOOSE_TICKETS:
                       SortedBag<Ticket> ticketOptions = SORTED_BAG_TICKET_SERDE.deserialize(incoming[1]);
                       String chosenTickets = SORTED_BAG_TICKET_SERDE.serialize(player.chooseTickets(ticketOptions));
                       w.write(chosenTickets);
                       w.flush();
                       break;

                   case DRAW_SLOT:
                       int drawSlot = player.drawSlot();
                       w.write(INTEGER_SERDE.serialize(drawSlot));
                       w.flush();
                       break;

                   case ROUTE:
                       Route claimedRoute = player.claimedRoute();
                       w.write(ROUTE_SERDE.serialize(claimedRoute));
                       w.flush();
                       break;

                   case CARDS:
                       SortedBag<Card> initialClaimCards = player.initialClaimCards();
                       w.write(SORTED_BAG_CARD_SERDE.serialize(initialClaimCards));
                       w.flush();
                       break;

                   case CHOOSE_ADDITIONAL_CARDS:
                       List<SortedBag<Card>> cardOptions = LIST_SORTED_BAG_CARD_SERDE.deserialize(incoming[1]);
                       SortedBag<Card> addCards = player.chooseAdditionalCards(cardOptions);
                       w.write(SORTED_BAG_CARD_SERDE.serialize(addCards));
                       w.flush();
                       break;
               }
            }
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
