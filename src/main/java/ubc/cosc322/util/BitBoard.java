package ubc.cosc322.util;

/**
 * A bitboard is a means of representing a board such that each square 
 * is represented by a single bit. This obviously only allows for binary state,
 * but multiple bitboards can be used to represent more nuanced board data.
 * E.g., you could have one bitboard that flags all queens, one that flags all
 * arrows, etc. 
 * <br /><br />
 * Apart from the obvious point of saving memory, bitboards can also take
 * advantage of bitwise operators to perform certain actions much faster than
 * could be done with a more intuitive memory representation.
 * <br /><br />
 * Since bitboards are meant to be performant, this class is not meant to
 * wrap a bitboard with a neat interface (and thereby discard a ton of
 * performance gain); instead, it simply consists of a number of static utility
 * functions useful for creating and interacting with bitboards.
 */
public class BitBoard {
	/**
	 * Creates a new bitboard with at least <code>size</code> bits. 
	 */
	public static long[] create(int size) {
		// there are 64 bits in a `long`.
		return new long[size / 64 + 1];
	}
	/**
	 * Creates a new bitboard for representing a 10x10 board (i.e., there are
	 * at least 100 bits).
	 */
	public static long[] create() {
		return new long[2];
	}

	/**
	 * Flags the specified index (i.e., sets it to <code>1</code> in the
	 * bitboard).
	 */
	public static void flag(long[] bitboard, int index) {
		// `x & 63` is the same as `x % 64` under these conditions (namely,
		// because 64 is a power of 2), except it only takes one CPU cycle.
		bitboard[index >>> 6] |= (1L << (index & 63));
	}

	/**
	 * Un-flags the specified index (i.e., sets it to <code>0</code> in the
	 * bitboard).
	 */
	public static void unflag(long[] bitboard, int index) {
		bitboard[index >>> 6] &= ~(1L << (index & 63));
	}

	/**
	 * Moves a flag from one position to another. Unlike the other bitboard
	 * operations defined on this utility class, this function creates and
	 * returns a copy, leaving the original bitboard unchanged.
	 * 
	 * To help performance, this function assumes that the original bitboard
	 * is representing a normal 10x10 board.
	 * 
	 * @param bitboard The original bitboard. This will be unchanged.
	 * @param src The position index to remove the bit from.
	 * @param dst The position index to move the bit to.
	 * @returns A copy of the original bitboard with the move applied.
	 */
	public static long[] move(long[] bitboard, int src, int dst) {
		long lo = bitboard[0];
		long hi = bitboard[1];

		if (src < 64) {
			lo ^= (1L << src);
		} else {
			hi ^= (1L << (src - 64));
		}

		if (dst < 64) {
			lo ^= (1L << dst);
		} else {
			hi ^= (1L << (dst - 64));
		}

		return new long[]{ lo, hi };
	}

	/**
	 * Returns <code>true</code> if and only if the indexed bit is flagged
	 * (i.e., if it is <code>1</code> in the bitboard).
	 */
	public static boolean flagged(long[] bitboard, int index) {
		return (bitboard[index >>> 6] & (1L << (index & 63))) != 0;
	}

	/**
	 * Zeroes a bitboard.
	 */
	public static void clear(long[] bitboard) {
		for (int i = 0; i < bitboard.length; i++) {
			bitboard[i] = 0L;
		}
	}
}
