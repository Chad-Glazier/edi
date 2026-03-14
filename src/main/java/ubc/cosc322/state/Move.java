package ubc.cosc322.state;

/**
 * This class is used to handle moves on the board. Use this class to: encode
 * or decode moves as <code>int</code>s.
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
}
