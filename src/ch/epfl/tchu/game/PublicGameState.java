package ch.epfl.tchu.game;

import java.util.List;
import java.util.Map;

public class PublicGameState {
    PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer){}

    public int ticketsCount(){
        return 0;
    }
    public boolean canDrawTickets(){
        return false;
    }
    public PublicCardState cardState(){
        return null;
    }
    public boolean canDrawCards(){
        return false;
    }
    public PlayerId currentPlayerId(){return null;}
    public PublicPlayerState playerState(PlayerId playerId){return null;}
    public PublicPlayerState currentPlayerState(){return null;}
    public List<Route> claimedRoutes(){return null;}
    public PlayerId lastPlayer(){return null;}
}
