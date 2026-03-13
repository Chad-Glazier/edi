package ubc.cosc322.bitboard;

import java.util.Arrays;

/**
 * Represents a board state using bitboards.
 */
class State {
	/**
	 * An occupancy board; i.e., a bitboard that has a flag wherever there is
	 * a queen or an arrow. If a position is not flagged on this board, then 
	 * it means that position is empty.
	 */
	public final long[] occupancy = new long[2];
	public final byte[] queens = new byte[8];
	public final byte activePlayer;

	/**
	 * The move that created this board state.
	 * 
	 * @see {@link ubc.cosc322.util.Move}
	 */
	public final int move;

	public State(
		long[] occupancy, byte[] queens, byte activePlayer, int move
	) {
		this.activePlayer = activePlayer;
		this.occupancy[0] = occupancy[0];
		this.occupancy[1] = occupancy[1];	
		this.queens[0] = queens[0];
		this.queens[1] = queens[1];
		this.queens[2] = queens[2];
		this.queens[3] = queens[3];
		this.queens[4] = queens[4];
		this.queens[5] = queens[5];
		this.queens[6] = queens[6];
		this.queens[7] = queens[7];
		this.move = move;
	}

	/**
	 * Returns a generator which yields all possible subsequent states.
	 */
	public StateGenerator children() {
		return new StateGenerator(this);
	}

	/**
	 * Returns a copy of this state.
	 */
	public State copy() {
		return new State(
			BitBoard.copy(occupancy), 
			Arrays.copyOf(queens, 8), 
			activePlayer, 
			move
		);
	}

	/**
	 * Returns <code>true</code> if, and only if, two boards are equivalent.
	 * 
	 * @param other The board to compare this one to.
	 */
	@Override
	public boolean equals(Object obj) {

		// TODO: Implement a faster check.

		if (this == obj) return true;
		if (!(obj instanceof State)) return false;

		State other = (State) obj;

		byte[] thisWhite = Arrays.copyOfRange(queens, 0, 4);
		byte[] otherWhite = Arrays.copyOfRange(other.queens, 0, 4);
		byte[] thisBlack = Arrays.copyOfRange(queens, 4, 8);
		byte[] otherBlack = Arrays.copyOfRange(other.queens, 4, 8);

		for (byte thisQueen : thisWhite) {
			boolean anyMatch = false;
			for (byte otherQueen : otherWhite) {
				if (thisQueen == otherQueen) {
					anyMatch = true;
					break;
				}
			}
			if (!anyMatch) {
				return false;
			}
		}

		for (byte thisQueen : thisBlack) {
			boolean anyMatch = false;
			for (byte otherQueen : otherBlack) {
				if (thisQueen == otherQueen) {
					anyMatch = true;
					break;
				}
			}
			if (!anyMatch) {
				return false;
			}
		}

		return 
			activePlayer == other.activePlayer &&
			occupancy[0] == other.occupancy[0] &&
			occupancy[1] == other.occupancy[1];
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {

		// TODO: Implement a faster hashing.
		
		return super.hashCode();
	}
}
