package ubc.team09.player;

import ubc.team09.state.State;

/**
 * This interface encloses the decision-making part of the program; i.e., one
 * that receives information about the board state and is capable of deciding
 * a move.
 * <br />
 * <br />
 * This interface is named "VI," for "virtual intelligence," (borrowing the
 * term from
 * <a href="https://masseffect.fandom.com/wiki/Virtual_Intelligence">a
 * videogame</a>)
 * purely because the term "AI" has become increasingly ambiguous following the
 * obnoxious marketing from tech companies.
 */
public interface VI {
	/**
	 * Returns the VI's recommended move.
	 * <br /><br />
	 * See {@link ubc.team09.state.Move Move} to use the move value.
	 * 
	 * @param board The current board state.
	 * @return An integer encoding of the move.
	 */
	public int consult(State board);

	/**
	 * Tells the VI which color they should make decisions for; i.e., which
	 * player they should try to maximize the score for.
	 * <br /><br />
	 * See {@link ubc.team09.state.C C}
	 * 
	 * @param color <code>0</code> for White, <code>1</code> for black.
	 * @return The configured VI.
	 */
	public VI setColor(byte color);

	/**
	 * Imposes a time limit for each turn that the VI takes.
	 * 
	 * @param seconds The number of seconds allotted.
	 * @return The configured VI.
	 */
	public VI setTimeLimit(int seconds);
}
