package ubc.cosc322.gametree;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import ubc.cosc322.eval.HeuristicMethod;
import ubc.cosc322.util.BitBoard;
import ubc.cosc322.util.Move;

/**
 * Represents a game state and facilitates game tree traversal.
 */
public class State {
	private static final byte WHITE = 0;
	private static final byte BLACK = 1;	
	private static final byte UNKNOWN = 2;

	public final State parent;

	public final long[] empty = new long[2]; 
	public final byte[] white = new byte[4];
	public final byte[] black = new byte[4];
	public final int move;

	/**
	 * Creates a new game state. 
	 * 
	 * @param parent The previous game state. Never set this to 
	 * <code>null</code>, instead see the other constructor signature that
	 * lets you create a root node.
	 * @param move The move associated with this new game state. E.g., if you
	 * have a given state <code>s</code> and you want to represent the state
	 * that would arise from applying a move <code>m</code>, you would call 
	 * <code>new State(s, m)</code>.
	 */
	public State(State parent, int move) {
		this.parent = parent;
		Move.apply(
			parent.empty, parent.white, parent.black, 
			move, 
			this.empty, this.white, this.black
		);
		this.move = move;
	}

	/**
	 * Use this initializer to create a root node in a game tree.
	 * 
	 * @param empty A bitboard where each empty square is flagged. This will be
	 * not be mutated.
	 * @param white The position indices of each white queen. This will not be
	 * mutated.
	 * @param black The position indices of each black queen. This will not be
	 * mutated.
	 * @param activePlayer The player who can make a move from this state, 
	 * where <code>0</code> is for White and <code>1</code> is for Black. I.e.,
	 * if white is to make the next move from this board state, set this
	 * argument to <code>0</code>.
	 */
	public State(
		long[] empty, byte[] white, byte[] black, byte activePlayer
	) {
		this.parent = null;
		BitBoard.copyTo(empty, this.empty);
		for (int i = 0; i < 4; i++) {
			this.white[i] = white[i];
			this.black[i] = black[i];
		}
		this.move = Move.encode(
			(byte) 0, 
			(byte) 0, 
			(byte) 0, 
			activePlayer == WHITE ? BLACK : WHITE
		);
	}

	/**
	 * Evaluates this board position from the perspective of the player who
	 * made the most recent move.
	 * 
	 * @param heuristic The heuristic used to evaluate the position.
	 * @return A numeric score, from 0 to 1, that represents the quality of the
	 * board state for the player who made the most recent move.
	 */
	public double value(HeuristicMethod heuristic) {
		heuristic.setBoard(empty, white, black);
		return heuristic.evaluate(Move.player(move));
	}

	/**
	 * Evaluates this board position from the perspective of the specified
	 * player.
	 * 
	 * @param heuristic The heuristic used to evaluate the position.
	 * @param player The player whos position we want to evaluate.
	 * @return A numeric score, from 0 to 1, that represents the quality of the
	 * board state for the specified player.
	 */
	public double value(HeuristicMethod heuristic, byte player) {
		heuristic.setBoard(empty, white, black);
		return heuristic.evaluate(player);
	}

	/**
	 * Returns the moves that could be made from this position.
	 */
	public IntArrayList moves() {
		return Move.getAll(
			empty, 
			activePlayer() == WHITE ? white : black, 
			activePlayer()
		);		
	}

	/**
	 * Indicates whether or not this game state is terminal.
	 */
	public boolean isLeaf() {
		byte[] activeQueens = activePlayer() == WHITE ? white : black;
		return Move.impossible(empty, activeQueens);
	}

	/**
	 * If this game state is terminal, then this returns the winning player;
	 * <code>0</code> represents White, <code>1</code> represents Black, and
	 * <code>2</code> indicates that the game isn't over.
	 */
	public byte winner() {
		if (isLeaf()) {
			return Move.player(move);
		}
		return UNKNOWN;
	}

	/**
	 * Returns the active player; i.e., the next one to move.
	 */
	public byte activePlayer() {
		return Move.player(move) == WHITE ? BLACK : WHITE;
	}
}
