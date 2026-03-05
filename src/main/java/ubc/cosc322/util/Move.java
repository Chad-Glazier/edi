package ubc.cosc322.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;

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
 * - The remaining (most significant) 8 bits are left empty.<br />
 */
public class Move {
	// private static final int START_OFFSET = 0;
	private static final int END_OFFSET = 8;
	private static final int ARROW_OFFSET = 16;

	private static final int START_MASK = 0x000000ff;
	private static final int END_MASK = 0x0000ff00;
	private static final int ARROW_MASK = 0x00ff0000;

	private static final int QUEENS = 4;

	/**
	 * Encodes a move as an integer.
	 * 
	 * @param start The starting position index (0-99) of the queen to move.
	 * @param end The position index the queen will move to.
	 * @param arrow The position index where the queen will fire her arrow
	 * after moving.
	 * @return An integer representation of the move.
	 */
	public static int encode(byte start, byte end, byte arrow) {
		return (int) start | 
			(int) end << END_OFFSET |
			(int) arrow << ARROW_OFFSET;
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
	 * Given a board state, this function returns all possible moves that
	 * could be made by a queen at the specified position.
	 * 
	 * @param empty A bitboard where each empty square is flagged.
	 * @param queen The position index of the queen to generate moves for.
	 * @return A list of integers representing moves. See the other methods of
	 * this class for ways to use such moves.
	 */
	public static IntArrayList getAll(long[] empty, byte queen) {
		IntArrayList moves = new IntArrayList();

		for (byte end : Graph.neighbors(empty, queen)) {
			long[] newEmpty = BitBoard.flagCopy(empty, queen);
			for (byte arrow : Graph.neighbors(newEmpty, end)) {
				moves.add(encode(queen, end, arrow));
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
	 * @return A list of integers representing moves. See the other methods of
	 * this class for ways to use such moves.
	 */
	public static IntArrayList getAll(long[] empty, byte[] queens) {
		IntArrayList moves = new IntArrayList();

		for (byte queen : queens) {
			for (byte end : Graph.neighbors(empty, queen)) {
				long[] newEmpty = BitBoard.flagCopy(empty, queen);
				for (byte arrow : Graph.neighbors(newEmpty, end)) {
					moves.add(encode(queen, end, arrow));
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

		BitBoard.flag(empty, start);	// the original position is now empty
		BitBoard.unflag(empty, end); 	// the new position is not empty
		BitBoard.unflag(empty, arrow);  // the arrow's position is not empty

		// update the position of the queen
		for (int i = 0; i < QUEENS; i++) {
			if (white[i] == start) {
				white[i] = end;
				return;
			}
			if (black[i] == start) {
				black[i] = end;
				return;
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

		BitBoard.copyTo(empty, newEmpty);

		BitBoard.flag(newEmpty, start);	  // the original position is now empty
		BitBoard.unflag(newEmpty, end);   // the new position is not empty
		BitBoard.unflag(newEmpty, arrow); // the arrow's position is not empty

		// update the position of the queen
		for (int i = 0; i < QUEENS; i++) {
			if (white[i] == start) {
				newWhite[i] = end;
				return;
			}
			if (black[i] == start) {
				newBlack[i] = end;
				return;
			}
		}
	}
}
