package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.GameMessage;

/**
 * 
 */
public class Game extends GamePlayer {

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
    
    private String userName = null;
    private String passwd = null;
 
    
    public static void main(String[] args) {                 
        // TODO: Implement
    }

    @Override
    public void onLogin() {
        // TODO: Implement
    }

    @Override
    public boolean handleGameMessage(
		String messageType, 
		Map<String, Object> msgDetails
	) {
        // TODO: Implement

		return false;
    }

    // =========================================================
    // SFS2X REQUIRED METHODS
    // =========================================================

    @Override
    public String userName() {
        return userName;
    }

    @Override
    public GameClient getGameClient() {
        return this.gameClient;
    }
    
    @Override
    public BaseGameGUI getGameGUI() {
        return this.gamegui; 
    }

    @Override
    public void connect() {
        gameClient = new GameClient(userName, passwd, this);            
    }

}