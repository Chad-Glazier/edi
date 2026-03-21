package ubc.team09.state;

import ubc.team09.bitboard.BitBoard;

public class MinimalState {
	/**
	 * An occupancy board; i.e., a bitboard that has a flag wherever there is
	 * a queen or an arrow. If a position is not flagged on this board, then
	 * it means that position is empty.
	 */
	public final long[] occupancy;
	/**
	 * The position indices of the queens on the board. The first four are
	 * White's queens, and the last four are Black's.
	 */
	public final byte[] queens;

	/**
	 * Creates a new minimal state. The arguments given are directly given to
	 * the state's fields, without being copied, so it's important that they
	 * are not mutated.
	 * 
	 * @param occupancy
	 * @param queens
	 */
	public MinimalState(long[] occupancy, byte[] queens) {
		this.occupancy = occupancy;
		this.queens = queens;
	}

	/**
	 * Creates a new state by applying the given move to an old state.
	 */
	public MinimalState(MinimalState prev, int move) {
		this.occupancy = new long[2];
		this.queens = new byte[8];

		occupancy[0] = prev.occupancy[0];
		occupancy[1] = prev.occupancy[1];
		queens[0] = prev.queens[0];
		queens[1] = prev.queens[1];
		queens[2] = prev.queens[2];
		queens[3] = prev.queens[3];
		queens[4] = prev.queens[4];
		queens[5] = prev.queens[5];
		queens[6] = prev.queens[6];
		queens[7] = prev.queens[7];

		for (byte i = 0; i < 8; i++) {
			if (queens[i] == Move.start(move)) {
				BitBoard.unflag(occupancy, queens[i]);
				queens[i] = Move.end(move);
				BitBoard.flag(occupancy, queens[i]);
				break;
			}
		}

		BitBoard.flag(occupancy, Move.arrow(move));
	}

	/**
	 * Returns <code>true</code> if and only if the given state has the same
	 * occupancy board and queen positions as this object.
	 * 
	 * @param state The full state to consider.
	 */
	public boolean isConsistentWith(State state) {

		if (this.occupancy[0] != state.occupancy[0]
				|| this.occupancy[1] != state.occupancy[1]) {
			return false;
		}

		// check white queens
		for (int i = 0; i < 4; i++) {
			boolean matchFound = false;
			for (int j = 0; j < 4; j++) {
				if (this.queens[i] == state.queens[j]) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				return false;
			}
		}

		// check black queens
		for (int i = 4; i < 8; i++) {
			boolean matchFound = false;
			for (int j = 4; j < 8; j++) {
				if (this.queens[i] == state.queens[j]) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if and only if the given state has the same
	 * occupancy board and queen positions as this object.
	 * 
	 * @param state The state to consider.
	 */
	public boolean isConsistentWith(MinimalState state) {

		if (this.occupancy[0] != state.occupancy[0]
				|| this.occupancy[1] != state.occupancy[1]) {
			return false;
		}

		// check white queens
		for (int i = 0; i < 4; i++) {
			boolean matchFound = false;
			for (int j = 0; j < 4; j++) {
				if (this.queens[i] == state.queens[j]) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				return false;
			}
		}

		// check black queens
		for (int i = 4; i < 8; i++) {
			boolean matchFound = false;
			for (int j = 4; j < 8; j++) {
				if (this.queens[i] == state.queens[j]) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns <code>true</code> if and only if there are no moves that could
	 * be made from this position.
	 */
	public boolean isTerminal(byte activePlayer) {
		for (int i = activePlayer * 4; i < activePlayer * 4 + 4; i++) {
			if (!BitBoard.isEmpty(QGraph.neighbors(queens[i], occupancy))) {
				return false;
			}
		}
		return true;
	}

	public static MinimalState initial() {

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

		return new MinimalState(occupancy, queens);
	}
}
