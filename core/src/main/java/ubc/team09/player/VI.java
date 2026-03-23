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
 * purely because when you google "AI," the first thing that comes up is OpenAI
 * and that makes me cringe.
 */
public interface VI {
	/**
	 * Returns the VI's recommended move.
	 * 
	 * @see {@link ubc.cosc322.state.Move} to use the move value.
	 * 
	 * @param board The current board state.
	 * @return An integer encoding of the move.
	 */
	public int consult(State board);
}
