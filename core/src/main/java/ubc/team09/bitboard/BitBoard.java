package ubc.team09.bitboard;

/**
 * A bitboard is a means of representing a board such that each square
 * is represented by a single bit. This obviously only allows for binary state,
 * but multiple bitboards can be used to represent more nuanced board data.
 * E.g., you could have one bitboard that flags all queens, one that flags all
 * arrows, etc.
 * <br />
 * <br />
 * In typical chess games, bitboards offer a tradeoff: extremely fast bit
 * operations against increased memory consumption (due to the number of
 * bitboards necessary to represent a complex chess state). In Amazons, the
 * math is a little different. Bitboards in Amazons generally use <em>less</em>
 * memory because there's only one kind of piece and only 8 of them, but their
 * operations aren't quite as fast as chess operations because the 10x10 board
 * cannot be represented by a single <code>long</code>.
 * <br />
 * <br />
 * Since bitboards are meant to be performant, this class is not meant to
 * wrap a bitboard with a neat interface (and thereby discard sone of the
 * performance gain); instead, it simply consists of a number of static utility
 * functions useful for creating and interacting with bitboards.
 */
public class BitBoard {
	/**
	 * A mask to zero all but the least significant 36 bits. This can be used
	 * on the higher <code>long</code> of the bitboard to zero any leftover
	 * bits from a left-shift.
	 */
	private static final long LSB_MASK = (1L << 36) - 1;

	/**
	 * Creates a new bitboard for representing a 10x10 board (i.e., there are
	 * at least 100 bits).
	 */
	public static long[] create() {
		return new long[2];
	}

	/**
	 * Creates a copy of a bitboard.
	 */
	public static long[] copy(long[] original) {
		return new long[] { original[0], original[1] };
	}

	/**
	 * Copies the <code>src</code> bitboard into the <code>dst</code> bitboard.
	 */
	public static void copyTo(long[] src, long[] dst) {
		dst[0] = src[0];
		dst[1] = src[1];
	}

	/**
	 * Flags the specified index (i.e., sets it to <code>1</code> in the
	 * bitboard).
	 */
	public static void flag(long[] bitboard, byte index) {
		// `x & 63` is the same as `x % 64` under these conditions (namely,
		// because 64 is a power of 2), except it only takes one CPU cycle.
		bitboard[index >>> 6] |= (1L << (index & 63));
	}

	/**
	 * Returns a copy of the given bitboard, but with the specified index
	 * flagged.
	 */
	public static long[] flagCopy(long[] bitboard, byte index) {
		final long[] newBitboard = { bitboard[0], bitboard[1] };
		flag(newBitboard, index);
		return newBitboard;
	}

	/**
	 * Un-flags the specified index (i.e., sets it to <code>0</code> in the
	 * bitboard).
	 */
	public static void unflag(long[] bitboard, byte index) {
		bitboard[index >>> 6] &= ~(1L << (index & 63));
	}

	/**
	 * Returns a copy of the given bitboard, but with the specified index un-
	 * flagged.
	 */
	public static long[] unflagCopy(long[] bitboard, byte index) {
		final long[] newBitboard = { bitboard[0], bitboard[1] };
		unflag(newBitboard, index);
		return newBitboard;
	}

	/**
	 * Moves a flag from one position to another. This function creates and
	 * returns a copy that reflects the new board state, rather than mutating
	 * the original.
	 * 
	 * @param bitboard The original bitboard. This will be unchanged.
	 * @param src      The position index to remove the bit from.
	 * @param dst      The position index to move the bit to.
	 * @returns A copy of the original bitboard with the move applied.
	 */
	public static long[] moveCopy(long[] bitboard, byte src, byte dst) {
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

		return new long[] { lo, hi };
	}

	/**
	 * Moves a flag from one position to another. This function will reflect
	 * the updated boardstate in <code>updated</code>, rather than mutating the
	 * original bitboard.
	 * 
	 * @param initial The original bitboard. This will be unchanged.
	 * @param src     The position index to remove the bit from.
	 * @param dst     The position index to move the bit to.
	 * @param updated The bitboard that will reflect the new state.
	 */
	public static void move(
			long[] initial, byte src, byte dst,
			long[] updated) {
		long lo = initial[0];
		long hi = initial[1];

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

		updated[0] = lo;
		updated[1] = hi;
	}

	/**
	 * Returns <code>true</code> if and only if the indexed bit is flagged
	 * (i.e., if it is <code>1</code> in the bitboard).
	 */
	public static boolean flagged(long[] bitboard, byte index) {
		return (bitboard[index >>> 6] & (1L << (index & 63))) != 0;
	}

	/**
	 * Zeroes a bitboard.
	 */
	public static void clear(long[] bitboard) {
		bitboard[0] = 0L;
		bitboard[1] = 0L;
	}

	/**
	 * Inverts all bits on the board.
	 */
	public static void not(long[] bitboard) {
		bitboard[0] = ~bitboard[0];
		bitboard[1] = (~bitboard[1]) & LSB_MASK;
	}

	/**
	 * Returns a new bitboard with all the bits opposite of the original, which
	 * is left unchanged.
	 */
	public static long[] notCopy(long[] bitboard) {
		return new long[] {
				~bitboard[0],
				(~bitboard[1]) & LSB_MASK
		};
	}

	/**
	 * Returns the position index (0-99) of the least-significant bit. If there
	 * is no such bit (i.e., the bitboard is zero), then <code>-1</code> is
	 * returned.
	 */
	public static byte lsb(long[] bitboard) {
		if (bitboard[0] != 0) {
			return (byte) Long.numberOfTrailingZeros(bitboard[0]);
		}
		if (bitboard[1] != 0) {
			return (byte) (64 + Long.numberOfTrailingZeros(bitboard[1]));
		}
		return 100;
	}

	/**
	 * Returns the position index (0-99) of the most-significant bit. If there
	 * is no such bit (i.e., the bitboard is zero), then <code>-1</code> is
	 * returned.
	 */
	public static byte msb(long[] bitboard) {
		if (bitboard[1] != 0) {
			return (byte) (127 - Long.numberOfLeadingZeros(bitboard[1]));
		}
		if (bitboard[0] != 0) {
			return (byte) (63 - Long.numberOfLeadingZeros(bitboard[0]));
		}
		return 100;
	}

	/**
	 * Returns <code>true</code> if and only if the bitboard is all zeros.
	 */
	public static boolean isEmpty(long[] bitboard) {
		return bitboard[0] == 0 && bitboard[1] == 0;
	}

	/**
	 * Removes the least-significant bit from the board and returns its index.
	 * 
	 * If the bitboard is empty, this returns <code>-1</code>.
	 */
	public static byte poll(long[] bitboard) {
		byte idx = -1;
		if (bitboard[0] != 0) {
			idx = (byte) Long.numberOfTrailingZeros(bitboard[0]);
			bitboard[0] &= bitboard[0] - 1;
		} else if (bitboard[1] != 0) {
			idx = (byte) (64 + Long.numberOfTrailingZeros(bitboard[1]));
			bitboard[1] &= bitboard[1] - 1;
		}
		return idx;
	}

	/**
	 * Returns the number of flags in the bitboard. The bitboard will not
	 * be mutated.
	 */
	public static int count(long[] bitboard) {
		return Long.bitCount(bitboard[0]) + Long.bitCount(bitboard[1]);
	}
}
