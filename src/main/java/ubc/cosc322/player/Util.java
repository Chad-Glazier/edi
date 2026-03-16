package ubc.cosc322.player;

import java.util.ArrayList;
import java.util.Map;

import ubc.cosc322.bitboard.BitBoard;
import ubc.cosc322.state.C;
import ubc.cosc322.state.MinimalState;
import ubc.cosc322.state.Move;
import ubc.cosc322.state.State;
import ygraph.ai.smartfox.games.amazons.AmazonsGameMessage;

public class Util {
    /**
     * Generates the starting Amazons board state.
     */
    public static State initialBoard() {

        byte[] queens = new byte[8];

		// White queens
        queens[0] = 60;
        queens[1] = 93;
        queens[2] = 96;
        queens[3] = 69;

        // Black queens
        queens[4] = 30;
        queens[5] = 03;
        queens[6] = 06;
        queens[7] = 39;

        // Flag each queen's position.
        long[] occupancy = BitBoard.create();
        for (byte queen : queens) {
			BitBoard.flag(occupancy, queen);
        }

        return new State(
			occupancy, 
			queens, 
			C.WHITE, 
			0
		);
    }

	/**
	 * Parses the occupancy board and queen positions from a
	 * {@link ygraph.ai.smartfox.games.GameMessage#GAME_STATE_BOARD}
	 * or
	 * {@link ygraph.ai.smartfox.games.GameMessage#GAME_ACTION_START}
	 * message.
	 * <br /><br />
	 * If the board received from the server is incoherent in some way (e.g.,
	 * it is missing a queen), then this will return <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	public static MinimalState parseState(Map<String, Object> messageDetails) {
		ArrayList<Integer> board = (ArrayList<Integer>) 
			messageDetails.get(AmazonsGameMessage.GAME_STATE);

		long[] occupancy = BitBoard.create();
		byte[] queens = new byte[8];

		int blackIdx = 4;
		int whiteIdx = 0;

		for (int i = 1; i < 11; i++) {
			for (int j = 1; j < 11; j++) {
				byte position = (byte) (10 * (i - 1) + j - 1);
				switch (board.get(i * 11 + j)) {
				case 0:
					continue;
				case 1:
					queens[whiteIdx++] = position;
					BitBoard.flag(occupancy, position);
					break;
				case 2:
					queens[blackIdx++] = position;
					BitBoard.flag(occupancy, position);
					break;
				case 3:
					BitBoard.flag(occupancy, position);
					break;
				default:
					return null;
				}
			}
		}

		if (blackIdx < 8 || whiteIdx < 4) {
			return null;
		}

		return new MinimalState(occupancy, queens);
	}

	/**
	 * Formats the message details of a message of the type
	 * {@link ygraph.ai.smartfox.games.GameMessage#GAME_ACTION_MOVE}
	 * into the standard integer representation.
	 * <br /><br />
	 * If the <code>start</code> position in the received move does not match
	 * any of the state's queens, then the <code>player</code> will be set to
	 * <code>-1</code>.
	 */
	@SuppressWarnings("unchecked")
	public static int parseMove(
		MinimalState state, Map<String, Object> details
	) {
		ArrayList<Integer> fromCoords = 
			(ArrayList<Integer>) details.get(AmazonsGameMessage.QUEEN_POS_CURR);
		ArrayList<Integer> toCoords = 
			(ArrayList<Integer>) details.get(AmazonsGameMessage.QUEEN_POS_NEXT);
		ArrayList<Integer> arrowCoords = 
			(ArrayList<Integer>) details.get(AmazonsGameMessage.ARROW_POS);

		int fromRow = fromCoords.get(0);
		int fromCol = fromCoords.get(1);
		int toRow = toCoords.get(0);
		int toCol = toCoords.get(1);
		int arrowRow = arrowCoords.get(0);
		int arrowCol = arrowCoords.get(1);

		byte start = (byte) ((fromRow - 1) * 10 + (fromCol - 1));
		byte end = (byte) ((toRow - 1) * 10 + (toCol - 1));
		byte arrow = (byte) ((arrowRow - 1) * 10 + (arrowCol - 1));

		byte player = -1;
		for (int i = 0; i < 8; i++) {
			if (state.queens[i] == start) {
				if (i < 4) {
					player = C.WHITE;
				} else {
					player = C.BLACK;
				}
				break;
			}
		}

		return Move.encode(start, end, arrow, player);
	}

	/**
	 * Converts a standard position index [0..99] into an ArrayList position in
	 * the server's expected format ([row, column]; indexed from 1).
	 */
	private static ArrayList<Integer> positionIndexToList(byte position) {
		int row = position / 10 + 1;
		int col = position % 10 + 1;

		ArrayList<Integer> list = new ArrayList<Integer>(2);
		list.add(row);
		list.add(col);

		return list;
	}

	/**
	 * Takes a move from the standard integer encoding and returns a server-
	 * appropriate ArrayList that represents it's starting position.
	 */
	public static ArrayList<Integer> getStartPosition(int move) {
		return positionIndexToList(Move.start(move));
	}

	/**
	 * Takes a move from the standard integer encoding and returns a server-
	 * appropriate ArrayList that represents it's starting position.
	 */
	public static ArrayList<Integer> getEndPosition(int move) {
		return positionIndexToList(Move.end(move));
	}

	/**
	 * Takes a move from the standard integer encoding and returns a server-
	 * appropriate ArrayList that represents it's starting position.
	 */
	public static ArrayList<Integer> getArrowPosition(int move) {
		return positionIndexToList(Move.arrow(move));
	}
}
