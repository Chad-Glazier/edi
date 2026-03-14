package ubc.cosc322.state;

import java.util.Arrays;

import ubc.cosc322.bitboard.BitBoard;

/**
 * Represents a board state using bitboards.
 */
public class State {
	/**
	 * An occupancy board; i.e., a bitboard that has a flag wherever there is
	 * a queen or an arrow. If a position is not flagged on this board, then 
	 * it means that position is empty.
	 */
	public final long[] occupancy = new long[2];
	/**
	 * The position indices of the queens on the board. The first four are
	 * White's queens, and the last four are Black's.
	 */
	public final byte[] queens = new byte[8];
	/**
	 * The player who can make a move from this position.
	 */
	public final byte activePlayer;
	/**
	 * The move that created this board state.
	 * 
	 * @see {@link ubc.cosc322.state.Move}
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
}
