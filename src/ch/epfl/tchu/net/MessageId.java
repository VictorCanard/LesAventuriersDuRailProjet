package ch.epfl.tchu.net;

public enum MessageId {
    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE, //is for claimedRoute()
    CARDS, //is for initialClaimCards()
    CHOOSE_ADDITIONAL_CARDS;


}
