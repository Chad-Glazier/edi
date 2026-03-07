package ubc.cosc322.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import ubc.cosc322.eval.HeuristicMethod;

/**
 * This class is used to handle moves on the board. Use this class to:<br />
 * - encode moves as <code>int</code>s,<br />
 * - compute the board state after a move is made, and<br />
 * - generate possible moves.<br />
 * <br /><br />
 * The section below describes how moves are encoded as <code>int</code>s, but
 * you don't need to understand those details to use the class.
 * <br /><br />
 * <strong>Representation of Moves</strong>
 * <br /><br />
 * A move can be represented with three values:<br />
 * - The position of the queen we want to move,<br />
 * - The queen's new position, and<br />
 * - The position of the queen's arrow.<br />
 * <br /><br />
 * Since these are all position indices, they can be represented by something
 * as small as a <code>byte</code>. To ensure we have the convenience of using
 * primitive values, we will represent these three values with a single 
 * <code>int</code>, where the bits are used like so:<br />
 * - The first (least significant) 8 bits are the starting position of the 
 * queen.<br />
 * - The next 8 bits are the queen's destination.<br />
 * - The next 8 bits are the target for the queen's arrow.<br />
 * - The remaining (most significant) 8 bits indicate which player made the
 * move. This could be deduced from other board state information (i.e.,
 * checking which queen position array the starting position belongs to),
 * but we're not using these bits for anything else.<br />
 */
public class Move {
	// private static final int START_OFFSET = 0;
	private static final int END_OFFSET = 8;
	private static final int ARROW_OFFSET = 16;
	private static final int PLAYER_OFFSET = 24;

	private static final int START_MASK = 0x000000ff;
	private static final int END_MASK = 0x0000ff00;
	private static final int ARROW_MASK = 0x00ff0000;
	private static final int PLAYER_MASK = 0xff000000;

	private static final int WHITE = 0;

	private static final int QUEENS = 4;

	/**
	 * Encodes a move as an integer.
	 * 
	 * @param start The starting position index (0-99) of the queen to move.
	 * @param end The position index the queen will move to.
	 * @param arrow The position index where the queen will fire her arrow
	 * after moving.
	 * @param player The player making the move (<code>0</code> for White, and
	 * <code>1</code> for Black).
	 * @return An integer representation of the move.
	 */
	public static int encode(byte start, byte end, byte arrow, byte player) {
		return (int) start | 
			(int) end << END_OFFSET |
			(int) arrow << ARROW_OFFSET |
			(int) player << PLAYER_OFFSET;
	}

	/**
	 * Reads the starting position (of the queen) from a move.
	 * 
	 * @param move The integer representation of a move. See {@link #encode}
	 * @return A position index.
	 */
	public static byte start(int move) {
		return (byte) (move & START_MASK);
	}

	/**
	 * Reads the ending position (of the queen) from a move.
	 * 
	 * @param move The integer representation of a move. See {@link #encode}
	 * @return A position index.
	 */
	public static byte end(int move) {
		return (byte) ((move & END_MASK) >>> END_OFFSET);
	}

	/**
	 * Reads the position where the queen fires an arrow from a move.
	 * 
	 * @param move The integer representation of a move. See {@link #encode}
	 * @return A position index.
	 */
	public static byte arrow(int move) {
		return (byte) ((move & ARROW_MASK) >>> ARROW_OFFSET);
	}

	/**
	 * Reads the active player from a move.
	 * 
	 * @param move The integer representation of a move. See {@link #encode}
	 * @return <code>0</code> for White, and <code>1</code> for Black.
	 */
	public static byte player(int move) {
		return (byte) ((move & PLAYER_MASK) >>> PLAYER_OFFSET);
	}

	/**
	 * Returns <code>true</code> if and only if there are no possible moves
	 * in the given board state.
	 * 
	 * @param empty A bitboard where each empty square is flagged.
	 * @param queens The position indices of each queen that could be moved 
	 * next. E.g., if you want to know all possible moves that White could 
	 * make, you should set this argument to the list of white queen positions.
	 * @return <code>true</code> if and only if none of the specified queens
	 * can move in the given board state.
	 */
	public static boolean impossible(long[] empty, byte[] queens) {
		for (byte queen : queens) {
			if (!Graph.neighbors(empty, queen).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Given a board state, this function returns all possible moves that
	 * could be made by a queen at the specified position.
	 * 
	 * @param empty A bitboard where each empty square is flagged.
	 * @param queen The position index of the queen to generate moves for.
	 * @param player The player moving the queen. <code>0</code> for White, and
	 * <code>1</code> for Black.
	 * @return A list of integers representing moves. See the other methods of
	 * this class for ways to use such moves.
	 */
	public static IntArrayList getAll(long[] empty, byte queen, byte player) {
		IntArrayList moves = new IntArrayList();

		for (byte end : Graph.neighbors(empty, queen)) {
			long[] newEmpty = BitBoard.flagCopy(empty, queen);
			for (byte arrow : Graph.neighbors(newEmpty, end)) {
				moves.add(encode(queen, end, arrow, player));
			}
		}

		return moves;
	}

	/**
	 * Given a board state and the positions of all queens that could move 
	 * next, this function returns a collection that includes all possible
	 * moves.
	 *
	 * @param empty A bitboard where each empty square is flagged.
	 * @param queens The position indices of each queen that could be moved 
	 * next. E.g., if you want to know all possible moves that White could 
	 * make, you should set this argument to the list of white queen positions.
	 * @param player The player making the move (<code>0</code> for White, and
	 * <code>1</code> for Black).
	 * @return A list of integers representing moves. See the other methods of
	 * this class for ways to use such moves.
	 */
	public static IntArrayList getAll(
		long[] empty, byte[] queens, byte player
	) {
		IntArrayList moves = new IntArrayList();

		for (byte queen : queens) {
			for (byte end : Graph.neighbors(empty, queen)) {
				long[] newEmpty = BitBoard.flagCopy(empty, queen);
				for (byte arrow : Graph.neighbors(newEmpty, end)) {
					moves.add(encode(queen, end, arrow, player));
				}
			}			
		}

		return moves;
	}

	/**
	 * Given a board state and a move, this function applies the move and then
	 * updates the inputs (in place) to reflect the new state.
	 * 
	 * @param empty A bitboard where each empty square is flagged. This will be
	 * mutated to reflect the new boardstate.
	 * @param white The position indices of each white queen. This will be
	 * mutated to reflect the new boardstate.
	 * @param black The position indices of each black queen. This will be
	 * mutated to reflect the new boardstate.
	 * @param move The move to apply.
	 */
	public static void apply(
		long[] empty, byte[] white, byte[] black, int move
	) {
		final byte start = start(move);
		final byte end = end(move);
		final byte arrow = arrow(move);
		final byte player = player(move);

		BitBoard.flag(empty, start);	// the original position is now empty
		BitBoard.unflag(empty, end); 	// the new position is not empty
		BitBoard.unflag(empty, arrow);  // the arrow's position is not empty

		// update the position of the queen
		if (player == WHITE) {
			for (int i = 0; i < QUEENS; i++) {
				if (white[i] == start) {
					white[i] = end;
					return;
				}
			}
		} else {
			for (int i = 0; i < QUEENS; i++) {
				if (black[i] == start) {
					black[i] = end;
					return;
				}
			}
		}
	}

	/**
	 * Given a board state and a move, this function applies the move and then
	 * writes the new state into the specified "new" objects.
	 * 
	 * @param empty A bitboard where each empty square is flagged. This will be
	 * not be mutated.
	 * @param white The position indices of each white queen. This will not be
	 * mutated.
	 * @param black The position indices of each black queen. This will not be
	 * mutated.
	 * @param move The move to apply.
	 * @param newEmpty The bitboard in which the updated state will be stored.
	 * @param newWhite The array in which the updated positions of the white
	 * queens will be stored.
	 * @param newBlack The array in which the updated positions of the black
	 * queens will be stored.
	 */
	public static void apply(
		long[] empty, byte[] white, byte[] black, int move,
		long[] newEmpty, byte[] newWhite, byte[] newBlack
	) {
		final byte start = start(move);
		final byte end = end(move);
		final byte arrow = arrow(move);
		final byte player = player(move);

		BitBoard.copyTo(empty, newEmpty);

		BitBoard.flag(newEmpty, start);	  // the original position is now empty
		BitBoard.unflag(newEmpty, end);   // the new position is not empty
		BitBoard.unflag(newEmpty, arrow); // the arrow's position is not empty

		for (int i = 0; i < QUEENS; i++) {
			newWhite[i] = white[i];
			newBlack[i] = black[i];
		}

		// update the position of the queen
		if (player == WHITE) {
			for (int i = 0; i < QUEENS; i++) {
				if (white[i] == start) {
					newWhite[i] = end;
					return;
				}
			}
		} else {
			for (int i = 0; i < QUEENS; i++) {
				if (black[i] == start) {
					newBlack[i] = end;
					return;
				}
			}
		}
	}

	/**
	 * Given a board state, a sequence of moves, and a heuristic evaluation
	 * function, this method evaluates the board state that would result from
	 * applying that sequence of moves.
	 * 
	 * @param empty A bitboard where each empty square is flagged. This will be
	 * not be mutated.
	 * @param white The position indices of each white queen. This will not be
	 * mutated.
	 * @param black The position indices of each black queen. This will not be
	 * mutated.
	 * @param moves The sequence of moves to be applied before evaluating the
	 * new board state. Moves are applied in the order of low to high indices 
	 * (left-to-right).
	 * @param heuristic The heuristic method used to evaluate the new board
	 * state.
	 * @param player The player whose position we are evaluating; 
	 * <code>0</code> for White, and <code>1</code> for Black.
	 */
	public static double evaluateSequence(
		long[] empty, byte[] white, byte[] black,
		IntArrayList moves,
		HeuristicMethod heuristic,
		byte player
	) {
		// make copies so that we can do in-place operations without mutating
		// the original board state.
		empty = BitBoard.copy(empty);
		white = new byte[]{ white[0], white[1], white[2], white[3] };
		black = new byte[]{ black[0], black[1], black[2], black[3] };

		for (int move : moves) {
			apply(empty, white, black, move);
		}

		heuristic.setBoard(empty, white, black);
		return heuristic.evaluate(player);
	}

	/**
	 * Evaluates the board state after making a specific move. This function
	 * doesn't mutate the original board state.
	 * 
	 * @param empty A bitboard where each empty square is flagged. This will be
	 * not be mutated.
	 * @param white The position indices of each white queen. This will not be
	 * mutated.
	 * @param black The position indices of each black queen. This will not be
	 * mutated.
	 * @param move The move to be applied before evaluating the new board
	 * state.
	 * @param heuristic The heuristic method used to evaluate the new board
	 * state.
	 * @param player The player whose position we are evaluating; 
	 * <code>0</code> for White, and <code>1</code> for Black.
	 * @return
	 */
	public static double evaluate(
		long[] empty, byte[] white, byte[] black,
		int move, HeuristicMethod heuristic, byte player
	) {
		// make copies so that we can do in-place operations without mutating
		// the original board state.
		empty = BitBoard.copy(empty);
		white = new byte[]{ white[0], white[1], white[2], white[3] };
		black = new byte[]{ black[0], black[1], black[2], black[3] };

		apply(empty, white, black, move);

		heuristic.setBoard(empty, white, black);
		return heuristic.evaluate(player);
	}
}
