package ubc.cosc322.util;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * A move can be represented with three values:
 * - The starting queen position,
 * - The queen's new position, and
 * - The position of the queen's arrow.
 * 
 * Since these are all position indices, they can be represented by something
 * as small as a <code>byte</code>. To ensure we have the convenience of using
 * primitive values, we will represent these three values with a single `int`,
 * where the bits are used like so:
 * - The first (least significant) 8 bits are the starting position of the 
 * queen.
 * - The next 8 bits are the queen's destination.
 * - The next 8 bits are the target for the queen's arrow.
 * - The remaining (most significant) 8 bits are left empty.
 */
public class Move {
	/**
	 * Given a board state, this function returns a collection of all possible
	 * board states after white makes a move.
	 *
	 * @param empty A bitboard where each empty square is flagged.
	 * @param black The position indices of each black queen.
	 * @param white The position indices of each white queen.
	 */
	public IntArrayList all(long[] empty, int[] white, int[] black) {

	}

	/**
	 * 
	 */
}
