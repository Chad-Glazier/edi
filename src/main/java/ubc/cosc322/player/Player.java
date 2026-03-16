package ubc.cosc322.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import sfs2x.client.entities.Room;
import ubc.cosc322.state.C;
import ubc.cosc322.state.MinimalState;
import ubc.cosc322.state.Move;
import ubc.cosc322.state.State;
import ubc.cosc322.view.Ansi;
import ubc.cosc322.view.Display;
import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.GameClient;
import ygraph.ai.smartfox.games.GameMessage;
import ygraph.ai.smartfox.games.GamePlayer;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

public class Player extends GamePlayer {

	//
	// Fields that have to be populated by server events.
	//

	private GameClient client = null;
	private Room room = null;
	private String opponentUsername = "Unknown";
	private byte ourColor = -1;

	//
	// Fields that have to be initialized.
	//
	
	private String username;
	private final String password = "password";
	private final VI VI;
	/** The player who should make the first move. */
	private final byte startingColor;
	/** The board state. */
	private MinimalState state = MinimalState.initial();
	/** The time limit for making a move. */
	private final int timeLimit;

	//
	// Recorded values used for displaying info or debugging.
	//
	private final LinkedList<String> systemMessages = new LinkedList<>();
	private final ArrayList<Integer> moveHistory;


	/**
	 * Creates a new game player which consults the given <code>VI</code> for
	 * decisions.
	 * 
	 * @param username The username assigned to this player.
	 * @param VI The VI to consult for move recommendations.
	 * @param startingColor Indicates which color will move first (in case,
	 * for some reason, Black is supposed to move first).
	 */
	public Player(String username, VI VI, byte startingColor, int timeLimit) {
		
		this.username = username;
		this.VI = VI;
		this.startingColor = startingColor;
		this.timeLimit = timeLimit;

		moveHistory = new ArrayList<Integer>(92);
		moveHistory.add(0);
	}

	private boolean handleBoardMessage(Map<String, Object> details) {
		
		//
		// Parse the received board state, and ensure that it's coherent.
		//
		
		MinimalState received = Util.parseState(details);
		if (received == null) {
			handleMessage(
				"Client", 
				"Incoherent board received; Ignoring."
			);
			return false;
		}

		//
		// Check that the received board state is consistent with the state we
		// have locally.
		//

		if (received.isConsistentWith(state)) {
			handleMessage(
				"Client",
				"Consistent board received."
			);
			return true;
		}

		state = received;
		Display.printBoard(
			new State(
				state.occupancy,
				state.queens,
				Move.player(moveHistory.get(moveHistory.size() - 1)) == C.WHITE 
					? C.BLACK 
					: C.WHITE,
				moveHistory.get(moveHistory.size() - 1)
			), 
			getBoardTitle(),
			ourColor == C.BLACK ? username : opponentUsername,
			ourColor == C.WHITE ? username : opponentUsername
		);
		handleMessage(
			"Client", 
			"Resolving inconsistency."
		);
		return true;
	}

	private boolean handleStartMessage(Map<String, Object> details) {

		boolean ok = true;

		// 
		// Handle the new board state given.
		//

		if (details.get(AmazonsGameMessage.GAME_STATE) != null) {
			ok = handleBoardMessage(details);
		}

		//
		// Assign player colors/usernames.
		//

		String black = (String) details.get(AmazonsGameMessage.PLAYER_BLACK);
		String white = (String) details.get(AmazonsGameMessage.PLAYER_WHITE);

		username = getGameClient().getUserName();
		if (white.equalsIgnoreCase(username)) {
			ourColor = C.WHITE;
			opponentUsername = black;
		} else if (black.equalsIgnoreCase(username)) {
			ourColor = C.BLACK;
			opponentUsername = white;
		} else {
			handleMessage(
				"Client",
				"Username not recognized by server."
			);
			return false;
		}

		//
		// We print the starting board.
		//

		State fullState = new State(
			state.occupancy, 
			state.queens,
			ourColor,
			0
		);
		Display.printBoard(
			fullState, getBoardTitle(), 
			ourColor == C.BLACK ? username : opponentUsername,
			ourColor == C.WHITE ? username : opponentUsername
		);
		handleMessage("Client", "Game start.");
		Display.printText(0, "Waiting for " + opponentUsername + "...");

		//
		// If we don't move first, then we stop here.
		//

		if (startingColor != ourColor) {
			return ok;
		}

		//
		// Otherwise, we start.
		//

		int ourMove = VI.consult(
			fullState, 
			ourColor, 
			timeLimit
		);

		// 
		// We update our local board to reflect our new move and send the 
		// move to the server.
		//

		sendMove(ourMove);
		
		return ok;
	}

	private boolean handleMoveMessage(Map<String, Object> details) {
		
		// 
		// Parse the move and determine whether or not it is legal. 
		// If it isn't, then it is logged and ignored.
		//

		int move = Util.parseMove(state, details);
		if (!Move.isLegal(state, move)) {
			handleMessage(
				"Client", 
				"Incoherent move received; Ignoring."
			);
			return false;
		}

		//
		// If the move is legal, then the current board state is updated. If
		// the board state is now terminal, then the game is ended.
		//

		moveHistory.add(move);
		state = new MinimalState(state, move);
		State fullState = new State(
			state.occupancy,
			state.queens,
			ourColor,
			move
		);
		Display.printBoard(
			fullState, getBoardTitle(), 
			ourColor == C.BLACK ? username : opponentUsername,
			ourColor == C.WHITE ? username : opponentUsername
		);
		handleMessage(
			"Client",
			"Move received. Board updated."
		);

		if (state.isTerminal(ourColor)) {
			Display.printText(0, opponentUsername + " wins.\n");
			System.exit(0);
			return true;
		}

		//
		// If the new state isn't terminal, then the VI is consulted for a
		// response.
		//

		int ourMove = VI.consult(
			fullState, 
			ourColor, 
			timeLimit
		);

		sendMove(ourMove);

		return true;
	}

	private void sendMove(int move) {
		// 
		// We update our local board to reflect our new move and send the 
		// move to the server.
		//

		moveHistory.add(move);
		state = new MinimalState(state, move);
		Display.printBoard(
			new State(
				state.occupancy,
				state.queens,
				ourColor == C.WHITE ? C.BLACK : C.WHITE,
				move
			), 
			getBoardTitle(), 
			ourColor == C.BLACK ? username : opponentUsername,
			ourColor == C.WHITE ? username : opponentUsername
		);
		Display.printText(
			0, 
			"Waiting for " + opponentUsername + "..."
		);
		handleMessage(
			"Client",
			"Sending move to server."
		);
		getGameClient().sendMoveMessage(
			Util.getStartPosition(move),
			Util.getEndPosition(move),
			Util.getArrowPosition(move)		
		);

		// 
		// Check if we won.
		//

		if (state.isTerminal(ourColor == C.WHITE ? C.BLACK : C.WHITE)) {
			Display.printText(0, username + " wins.");
		}
	}

	private String getBoardTitle() {
		return this.username + " vs " + opponentUsername;
	}

	//
	// Methods to implement GamePlayer.
	//

	@Override
	public void connect() {
		client = new GameClient(username, password, this);
	}

	@Override
	public GameClient getGameClient() {
		return client;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return null;
	}

	@Override
	public boolean handleMessage(String msgType, String msg) {
		// the server spams this message for some reason, so we just ignore it.
		if (msgType.equals("userCountChange")) {
			return true;
		}

		systemMessages.push(msgType + (msg != null ? ": " + msg : ""));
		Display.printSystemMessages(systemMessages);		

		return true;
	}

	@Override
	public boolean handleGameMessage(
		String msgType, Map<String, Object> details
	) {
		switch (msgType) {
		case GameMessage.GAME_STATE_BOARD:
			return handleBoardMessage(details);
		case GameMessage.GAME_ACTION_START:
			return handleStartMessage(details);
		case GameMessage.GAME_ACTION_MOVE:
			return handleMoveMessage(details);
		default:
			handleMessage("Unhandled", msgType);
			return true;
		}
	}

	@Override
	public void onLogin() {
		Display.printText(0, "Logged in.");

		List<Room> rooms = client.getRoomList();
		List<String> roomNames = new LinkedList<String>();
		for (Room room : rooms) {
			roomNames.add(
				room.getName() + 
				Ansi.FG_BRIGHT_BLACK +
				" (" + Integer.toString(
					room.getUserCount() - room.getSpectatorCount()
				) + 
				"/" + Integer.toString(
					room.getMaxUsers() - room.getMaxSpectators()
				) +
				")" +
				Ansi.RESET
			);
		}

		System.out.println(roomNames);

		String roomName = Display.prompt("Choose a room.", roomNames);	

		for (Room room : rooms) {
			if (roomName.startsWith(room.getName())) {
				this.room = room;
			}
		}

		Display.clear();
		Display.printText(0, "Joining room " + room.getName() + "...");

		client.joinRoom(room.getName());
	}

	@Override
	public String userName() {
		return getGameClient().getUserName();
	}	
}
