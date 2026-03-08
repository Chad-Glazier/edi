package ubc.cosc322;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import sfs2x.client.entities.Room;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.GameMessage;

/**
 * Main Game Client & Server Communication
 * Supports interactive dynamic time allocation via console.
 */
public class COSC322Test extends GamePlayer {

    private GameClient gameClient = null; 
    private BaseGameGUI gamegui = null;
    
    private String userName = null;
    private String passwd = null;
    private int myColor = -1; 
    
    private BoardState boardState = new BoardState();

    // Time Management Variables (in milliseconds)
    private long remainingTimeBankMs;
    private long hardLimitPerMoveMs;
 
    public static void main(String[] args) {                 
        String user = (args.length > 0) ? args[0] : "spectator";
        String pwd  = (args.length > 1) ? args[1] : "pwd";

        long timeBankSec = 1800; 
        long hardLimitSec = 30;  

        // If time args are passed via command line, use them
        if (args.length > 3) {
            try { 
                timeBankSec = Long.parseLong(args[2]); 
                hardLimitSec = Long.parseLong(args[3]);
            } catch (Exception e) { 
                System.out.println("Invalid args. Falling back to manual input."); 
            }
        } 
        // Otherwise, prompt the user interactively in the console
        else {
            Scanner scanner = new Scanner(System.in);
            System.out.println("===================================");
            System.out.println("   AMAZONS ONLINE CLIENT SETUP");
            System.out.println("===================================");
            
            System.out.print("Enter Total Time Bank in seconds (e.g., 1800 for 30m): ");
            timeBankSec = scanner.nextLong();
            
            System.out.print("Enter Hard Limit per move in seconds (e.g., 30): ");
            hardLimitSec = scanner.nextLong();
        }

        COSC322Test player = new COSC322Test(user, pwd, timeBankSec, hardLimitSec);
        
        if(player.getGameGUI() == null) {
            player.Go();
        } else {
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(() -> player.Go());
        }
    }
    
    public COSC322Test(String userName, String passwd, long timeBankSec, long hardLimitSec) {
        this.userName = userName;
        this.passwd = passwd;
        this.gamegui = new BaseGameGUI(this);

        this.remainingTimeBankMs = timeBankSec * 1000L;
        this.hardLimitPerMoveMs = hardLimitSec * 1000L;
        
        System.out.printf("\nTime Config Saved - Bank: %ds, Hard Limit: %ds\n", timeBankSec, hardLimitSec);
    }
 
    @Override
    public void onLogin() {
        System.out.println("Login successful.");
        this.userName = gameClient.getUserName();
        if (gamegui != null) gamegui.setRoomInformation(gameClient.getRoomList());

        String targetRoom = "Okanagan Lake"; 
        for (Room room : gameClient.getRoomList()) {
            if (room.getName().equals(targetRoom)) {
                gameClient.joinRoom(targetRoom); 
                break;
            }
        }
    }

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
        
        if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {
            if (gamegui != null) {
                @SuppressWarnings("unchecked")
                ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state"); 
                if (state != null) {
                    gamegui.setGameState(state);
                    boardState.setupLocalBoard(state); 
                }
            }
            return true;
        }

        if (messageType.equals(GameMessage.GAME_ACTION_START)) {
            String whiteUser = (String) msgDetails.get("player-white");
            String blackUser = (String) msgDetails.get("player-black");
            
            if (whiteUser.equals(this.userName)) {
                this.myColor = 1; 
                System.out.println("I am WHITE (First move).");
                thinkAndMakeMove(); 
            } else if (blackUser.equals(this.userName)) {
                this.myColor = 2;
                System.out.println("I am BLACK.");
            }
            return true;
        }

        if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {
            if (gamegui != null) gamegui.updateGameState(msgDetails);
            
            @SuppressWarnings("unchecked")
            ArrayList<Integer> queenPosCurr = (ArrayList<Integer>) msgDetails.get("queen-position-current");
            @SuppressWarnings("unchecked")
            ArrayList<Integer> queenPosNext = (ArrayList<Integer>) msgDetails.get("queen-position-next");
            @SuppressWarnings("unchecked")
            ArrayList<Integer> arrowPos = (ArrayList<Integer>) msgDetails.get("arrow-position");
            
            boardState.updateLocalBoard(queenPosCurr, queenPosNext, arrowPos);
            
            int movingQueenColor = boardState.board[queenPosNext.get(0) - 1][queenPosNext.get(1) - 1];
            if (movingQueenColor != this.myColor) {
                thinkAndMakeMove(); 
            }
            return true;
        }
        return true;    
    }

    /**
     * Dynamic Time Manager & Move Execution
     */
    private void thinkAndMakeMove() {
        System.out.println("\nAI is calculating...");
        
        // 1. Calculate dynamic time allocation (10% of remaining time)
        long allocatedMs = remainingTimeBankMs / 10;
        
        // 2. Cap at hard limit minus 4 seconds (network safety buffer)
        long maxSafeMs = Math.max(1000L, hardLimitPerMoveMs - 4000L);
        allocatedMs = Math.min(allocatedMs, maxSafeMs);
        allocatedMs = Math.max(allocatedMs, 1000L); // Minimum 1 second

        System.out.printf("[Time Manager] Bank: %ds | Allocated: %ds\n", remainingTimeBankMs/1000, allocatedMs/1000);
        
        long startTime = System.currentTimeMillis();
        
        // 3. Run AI
        GameAI ai = new GameAI(allocatedMs);
        AmazonMove bestMove = ai.getBestMove(this.boardState, this.myColor);
        
        // 4. Update Time Bank
        long elapsedMs = System.currentTimeMillis() - startTime;
        this.remainingTimeBankMs = Math.max(0, this.remainingTimeBankMs - elapsedMs);
        
        // 5. Send Move
        if (bestMove != null) {
            gameClient.sendMoveMessage(bestMove.getServerQStart(), bestMove.getServerQEnd(), bestMove.getServerArrow());
        } else {
            System.out.println("No moves available. Game Over.");
        }
    }

    // =========================================================
    // SFS2X REQUIRED METHODS
    // =========================================================

    @Override
    public String userName() { return userName; }

    @Override
    public GameClient getGameClient() { return this.gameClient; }
    
    @Override
    public BaseGameGUI getGameGUI() { return this.gamegui; }

    @Override
    public void connect() { gameClient = new GameClient(userName, passwd, this); }
}