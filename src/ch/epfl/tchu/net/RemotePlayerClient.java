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
     *
     * @param player : the player to take their turn
     * @param name : the host name
     * @param port : the port number
     */
    public RemotePlayerClient(Player player, String name, int port){
        this.player = player;
        this.name = name;
        this.port = port;
    }

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
   //todo: big sad
            //String reads = r.readLine(); //this readLine is as expected

            while (r.readLine() != null) { //then it becomes null

               String read = r.readLine();

               String[] incoming = read.split(spacePattern, -1);
               String type = incoming[0];

               switch (MessageId.valueOf(type)) {
                   case INIT_PLAYERS:
                       PlayerId ownId = PLAYER_ID_SERDE.deserialize(incoming[1]);
                       String[] players12 = incoming[2].split(commaPattern, -1);

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
                      String ts = SORTED_BAG_TICKET_SERDE.serialize(chosen);
                      w.write(ts);
                      w.flush();
                      break;

                  case NEXT_TURN:
                       Player.TurnKind turn = player.nextTurn();
                       String tks = TURN_KIND_SERDE.serialize(turn);
                       w.write(tks);
                       w.flush();
                       break;

                   case CHOOSE_TICKETS:
                       SortedBag<Ticket> ticketOptions = SORTED_BAG_TICKET_SERDE.deserialize(incoming[1]);
                       String cts = SORTED_BAG_TICKET_SERDE.serialize(player.chooseTickets(ticketOptions));
                       w.write(cts);
                       w.flush();
                       break;

                   case DRAW_SLOT:
                       int drawSlot = player.drawSlot();
                       String dss = INTEGER_SERDE.serialize(drawSlot);
                       w.write(dss);
                       w.flush();
                       break;

                   case ROUTE:
                       Route claimedRoute = player.claimedRoute();
                       String crs = ROUTE_SERDE.serialize(claimedRoute);
                       w.write(crs);
                       w.flush();
                       break;

                   case CARDS:
                       SortedBag<Card> initialClaimCards = player.initialClaimCards();
                       String iccs = SORTED_BAG_CARD_SERDE.serialize(initialClaimCards);
                       w.write(iccs);
                       w.flush();
                       break;

                   case CHOOSE_ADDITIONAL_CARDS:
                       List<SortedBag<Card>> cardOptions = LIST_SORTED_BAG_CARD_SERDE.deserialize(incoming[1]);
                       SortedBag<Card> addCards = player.chooseAdditionalCards(cardOptions);
                       String acs = SORTED_BAG_CARD_SERDE.serialize(addCards);
                       w.write(acs);
                       w.flush();
                       break;
               }
            }
        }catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }
}
